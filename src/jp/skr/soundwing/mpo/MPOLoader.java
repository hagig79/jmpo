package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 * @author mudwell
 * 
 */
public class MPOLoader {

	private static final int BUFFER_SIZE = 1024;
	private static final int ENDIAN_OFFSET_SIZE = 8;

	// public static MPOImage read(File input) {
	//
	// }
	//
	// public static MPOImage read(URL input) {
	//
	// }

	/**
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	public static MPOImage read(InputStream stream) throws IOException {
		// バイナリでファイルをすべて読み込む
		byte[] fileData = readFile(stream);

		System.out.println("file size: " + fileData.length);
		int app2 = findAPP2Tag(fileData, 0);
		System.out.printf("%x\n", app2);
		// オフセットの基準点
		int offsetBase = app2 + ENDIAN_OFFSET_SIZE;
		System.out.printf("Offset Base: %x\n", offsetBase);

		// 先頭IFDへのオフセットを取得
		int offset = getIFDOffset(fileData, offsetBase);
		MPIndexFields indexIFD = MPIndexFields.create(fileData, offsetBase
				+ offset);
		System.out.printf("%x\n", MPIndexFields.MPF_LENGTH + offsetBase
				+ offset);
		System.out.println("記録画像数:" + indexIFD.getNumberOfImages());

		MPEntry entry = indexIFD.getMPEntry();
		System.out.printf("%d\n", entry.getSize());
		System.out.printf("%x\n", entry.getOffset());

		int individualOffset = indexIFD.getOffsetOfNextIFD();
		System.out.printf("%x\n", individualOffset);
		System.out.printf("%x\n", individualOffset + offsetBase);

		MPEntry entry1 = new MPEntry(fileData, entry.getOffset() + offsetBase);
		System.out.printf("%x\n", entry1.getOffset());

		int jpegHead = findJpegHead(fileData, 0);
		int jpegHead2 = findJpegHead(fileData, jpegHead + 1);

		final BufferedImage image1 = createImage(fileData, jpegHead, jpegHead2
				- jpegHead);
		final BufferedImage image2 = createImage(fileData, jpegHead2,
				fileData.length - jpegHead2);

		return new MPOImage(image1, image2);
	}

	private static byte[] readFile(InputStream stream) throws IOException {
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
	private static int findAPP2Tag(byte[] buffer, int offset) {
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
	 * @param buffer
	 * @param offsetBase
	 *            オフセットの基準点
	 * @return
	 */
	private static int getIFDOffset(byte[] buffer, int offsetBase) {
		int offset = 0;
		offset |= (buffer[offsetBase + 4] & 0xff) << 24;
		offset |= (buffer[offsetBase + 4 + 1] & 0xff) << 16;
		offset |= (buffer[offsetBase + 4 + 2] & 0xff) << 8;
		offset |= (buffer[offsetBase + 4 + 3] & 0xff);
		return offset;
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
	static BufferedImage createImage(byte[] buffer, int offset, int length)
			throws IOException {

		byte[] imageByte1 = new byte[length];
		System.arraycopy(buffer, offset, imageByte1, 0, imageByte1.length);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte1);
		ImageInputStream iis = ImageIO.createImageInputStream(bis);
		Iterator readers = (Iterator) ImageIO
				.getImageReadersByFormatName("jpeg");
		ImageReader reader = (ImageReader) (readers.next());
		reader.setInput(iis, true);
		return reader.read(0);
	}

	static int getInt(byte[] buffer, int offset) {
		return ((buffer[offset] & 0xff) << 24)
				| ((buffer[offset + 1] & 0xff) << 16)
				| ((buffer[offset + 2] & 0xff) << 8)
				| ((buffer[offset + 3] & 0xff));
	}
}
