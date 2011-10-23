package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * MPOファイル読み込み機能を提供する.
 * 
 * @author mudwell
 * 
 */
public class MpoReader {

	private static final int BUFFER_SIZE = 1024;
	static final int ENDIAN_OFFSET_SIZE = 8;

	/**
	 * 指定されたFileを復号化した結果としてMpoFileを返す.
	 * 
	 * @param input
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 *             読み込み中にエラーが発生した場合
	 */
	public static MpoFile read(File input) throws FileNotFoundException,
			IOException {
		InputStream in = new FileInputStream(input);
		MpoFile mpoFile = read(in);
		in.close();
		return mpoFile;
	}

	/**
	 * 指定されたURLを復号化した結果としてMpoFileを返す.
	 * 
	 * @param input
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 *             読み込み中にエラーが発生した場合
	 */
	public static MpoFile read(URL input) throws FileNotFoundException,
			IOException {
		InputStream in = new FileInputStream(input.getPath());
		MpoFile mpoFile = read(in);
		in.close();
		return mpoFile;
	}

	/**
	 * 指定されたInputStreamを復号化した結果としてMpoFileを返す.
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 *             読み込み中にエラーが発生した場合
	 */
	public static MpoFile read(InputStream stream) throws IOException {
		// バイナリでファイルをすべて読み込む
		byte[] fileData = readFile(stream);

		// MPヘッダの解析
		int app2 = findAPP2Tag(fileData, 0);
		if (app2 < 0) {
			throw new IOException("Not MPO File");
		}
		// オフセットの基準点
		int offsetBase = app2 + ENDIAN_OFFSET_SIZE;

		MpExtension firstExt = MpExtension.createFirst(fileData, offsetBase);

		List<MpEntry> entries = new ArrayList<MpEntry>();
		List<MpExtension> exts = new ArrayList<MpExtension>();

		for (int i = 0; i < firstExt.getMpIndexIfd().getNumberOfImages(); i++) {

			MpEntry entry = firstExt.getMpEntry(i);
			entries.add(entry);
			if (i == 0) {
				exts.add(firstExt);
			} else {
				MpExtension ext = MpExtension.create(fileData, findAPP2Tag(
						fileData, entry.getOffset() + offsetBase)
						+ ENDIAN_OFFSET_SIZE);
				exts.add(ext);
			}
		}
		int jpegHead = 0;
		int jpegHead2 = 0;
		if (firstExt.getNumberOfMpEntry() > 0) {
			jpegHead2 = firstExt.getMpEntry(1).getOffset() + offsetBase;

		} else {
			jpegHead2 = findJpegHead(fileData, 10);
		}

		BufferedImage image1 = createImage(fileData, jpegHead, jpegHead2
				- jpegHead);
		MpoImage imageLeft = new MpoImage(image1, firstExt);
		BufferedImage image2 = createImage(fileData, jpegHead2, fileData.length
				- jpegHead2);
		MpoImage imageRight = new MpoImage(image2, exts.get(1));

		return new MpoFile(imageLeft, imageRight);
	}

	static byte[] readFile(InputStream stream) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();

		int buff = 0;
		while ((buff = stream.read()) != -1) {
			output.write(buff);
		}
		byte[] fileData = output.toByteArray();
		output.close();
		return fileData;
	}

	private static final int SOI_1 = 0xff;
	private static final int SOI_2 = 0xd8;

	/**
	 * Jpeg画像かどうか確かめる.
	 * 
	 * @param buffer
	 *            ファイルバイト列
	 * @return
	 */
	private static boolean isJpeg(byte[] buffer) {
		// SOIを確かめる
		return (buffer[0] == SOI_1) && (buffer[1] == SOI_2);
	}

	/**
	 * @param buffer
	 * @param offset
	 * @return
	 */
	static int findAPP2Tag(byte[] buffer, int offset) {
		for (int i = offset; i < buffer.length; i++) {
			if (((buffer[i] & 0xff) == 0xff)
					&& ((buffer[i + 1] & 0xff) == 0xe2)
					&& ((buffer[i + 4] & 0xff) == 'M')
					&& ((buffer[i + 5] & 0xff) == 'P')
					&& ((buffer[i + 6] & 0xff) == 'F')
					&& ((buffer[i + 7] & 0xff) == '\0')) {
				return i;
			}
		}
		return -1;
	}

	private static int findJpegHead(byte[] buffer, int offset) {

		for (int i = offset; i < buffer.length; i++) {
			if (((buffer[i] & 0xff) == 0xff)
					&& ((buffer[i + 1] & 0xff) == 0xd8)
					&& ((buffer[i + 2] & 0xff) == 0xff)
					&& ((buffer[i + 3] & 0xff) == 0xe1)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * バイト列からBufferedImageインスタンスを作成する.
	 * 
	 * @param buffer
	 * @param offset
	 * @param length
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage createImage(byte[] buffer, int offset,
			int length) throws IOException {

		byte[] imageByte1 = new byte[length];
		System.arraycopy(buffer, offset, imageByte1, 0, imageByte1.length);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte1);
		ImageInputStream iis = ImageIO.createImageInputStream(bis);
		Iterator<ImageReader> readers = ImageIO
				.getImageReadersByFormatName("jpeg");
		ImageReader reader = readers.next();
		reader.setInput(iis, true);
		return reader.read(0);
	}

	public static int getInt(byte[] buffer, int offset) {
		return ((buffer[offset] & 0xff) << 24)
				| ((buffer[offset + 1] & 0xff) << 16)
				| ((buffer[offset + 2] & 0xff) << 8)
				| ((buffer[offset + 3] & 0xff));
	}

}
