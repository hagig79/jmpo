package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class MpoWriter {
	private static final int MP_HEADER_LENGTH = 8;
	private static final byte[] SOI = { (byte) 0xff, (byte) 0xd8 };

	/**
	 * MPOファイルをストリームに書き込む.
	 * 
	 * @param mpoFile
	 * @param stream
	 * @throws IOException
	 */
	public static void write(MpoFile mpoFile, OutputStream stream)
			throws IOException {
		int[] dataOffset = new int[2];// 個別画像データのオフセット
		byte[][] imageData = new byte[2][];
		int[] mpIndividualIfdSize = new int[2];
		int[] imageSize = new int[2];
		
		mpoFile.getMpoImage(0).getIndexIfd().setNumberOfImages(mpoFile.getNumberOfImages());

		// バイト配列に変換する
		for (int i = 0; i < mpoFile.getNumberOfImages(); i++) {
			MpoImage mpoImage = mpoFile.getMpoImage(i);
			imageData[i] = convertByteArray(mpoImage.getBufferedImage());
			deleteSoi(imageData[i]);
			if (i == 0) {
				mpIndividualIfdSize[i] = calcMpIndividualIfdSize(
						mpoImage.getAttributeIfd(), true);
				dataOffset[i] = 0; // 先頭画像は0
				imageSize[i] = SOI.length + 6 + MP_HEADER_LENGTH
						+ calcMpIndexIfdSize(mpoFile.getNumberOfImages())
						+ mpIndividualIfdSize[i] + imageData[i].length;
			} else if (i == 1) {
				mpIndividualIfdSize[i] = calcMpIndividualIfdSize(
						mpoImage.getAttributeIfd(), false);
				dataOffset[i] = MP_HEADER_LENGTH
						+ calcMpIndexIfdSize(mpoFile.getNumberOfImages())
						+ mpIndividualIfdSize[i - 1] + imageData[i - 1].length;
				imageSize[i] = SOI.length + 6 + MP_HEADER_LENGTH
						+ mpIndividualIfdSize[i] + imageData[i].length;
			} else {
				mpIndividualIfdSize[i] = calcMpIndividualIfdSize(
						mpoImage.getAttributeIfd(), false);
				dataOffset[i] = MP_HEADER_LENGTH + mpIndividualIfdSize[i - 1]
						+ imageData[i - 1].length;
				imageSize[i] = SOI.length + 6 + MP_HEADER_LENGTH
						+ mpIndividualIfdSize[i] + imageData[i].length;
			}
		}

		// データの更新
		// MPエントリを作成する
		List<MpEntry> entries = new ArrayList<MpEntry>();
		for (int i = 0; i < mpoFile.getNumberOfImages(); i++) {
			entries.add(new MpEntry(0x020002, imageSize[i], dataOffset[i], 0, 0));
		}
		mpoFile.getMpoImage(0).getIndexIfd().setEntries(entries.toArray(new MpEntry[0]));

		// ストリームに書き込む
		for (int i = 0; i < mpoFile.getNumberOfImages(); i++) {
			stream.write(SOI);
			if (i == 0) {
				writeApp2Header(stream, 2 + 4 + MP_HEADER_LENGTH
						+ calcMpIndexIfdSize(mpoFile.getNumberOfImages())
						+ mpIndividualIfdSize[i]);
			} else {
				writeApp2Header(stream, 2 + 4 + MP_HEADER_LENGTH
						+ mpIndividualIfdSize[i]);
			}
			writeMpHeader(stream, 8);
			if (i == 0) {
				writeIndexIfd(stream, mpoFile.getMpoImage(i).getIndexIfd(),
						imageSize, dataOffset);
				writeIndividualIfd(stream, mpoFile.getMpoImage(i)
						.getAttributeIfd(),
						8 + calcMpIndexIfdSize(mpoFile.getNumberOfImages()),
						true);
			} else {
				writeIndividualIfd(stream, mpoFile.getMpoImage(i)
						.getAttributeIfd(), 8, false);
			}
			stream.write(imageData[i]);
		}

	}

	/**
	 * SOIマーカーを削除する
	 * 
	 * @param jpegData
	 */
	static void deleteSoi(byte[] jpegData) {
		System.arraycopy(jpegData, 2, jpegData, 0, jpegData.length - 2);
	}

	/**
	 * インデックスIFDをストリームに書き込む.
	 * 
	 * @param stream
	 * @param indexIfd
	 * @param imageSize
	 * @param dataOffset
	 * @throws IOException
	 */
	private static void writeIndexIfd(OutputStream stream,
			MpIndexFields indexIfd, int[] imageSize, int[] dataOffset)
			throws IOException {
		writeShort(stream, (short) 3);
		writeVersion(stream);
		writeNumberOfImages(stream, indexIfd.getNumberOfImages());
		int valueOffset = MP_HEADER_LENGTH + 2 + 12 * 3 + 4;
		writeOffsetOfMpEntry(stream, indexIfd.getNumberOfImages(), valueOffset);
		writeInt(stream, valueOffset + 16 * indexIfd.getNumberOfImages());
		for (int i = 0; i < indexIfd.getNumberOfImages(); i++) {
			writeMpEntry(stream, indexIfd.getMpEntry(i), imageSize[i],
					dataOffset[i]);
		}
	}

	private static void writeIndividualIfd(OutputStream stream,
			MpAttributeFields attributeIfd, int offset, boolean b)
			throws IOException {
		writeShort(stream, (short) 3);
		if (!b) {
			writeVersion(stream);
		}
		int valueOffset = 0;
		if (b) {
			valueOffset = offset + 2 + 4 * 12 + 4;
		} else {
			valueOffset = offset + 2 + 5 * 12 + 4;

		}
		writeIndividualNum(stream, attributeIfd.getMPIndividualNum());
		writeBaseViewPoint(stream, attributeIfd.getBaseViewpointNum());
		writeOffsetOfConvergenceAngle(stream, valueOffset);
		writeOffsetOfBaselineLength(stream, valueOffset + 4 + 8);
		writeInt(stream, 0);
		writeConvergenceAngle(stream, attributeIfd.getConvergenceAngle());
		writeBaselineLength(stream, attributeIfd.getBaselineLength());
	}

	/**
	 * @param stream
	 * @param convergenceAngle
	 * @throws IOException
	 */
	private static void writeConvergenceAngle(OutputStream stream,
			Rational convergenceAngle) throws IOException {
		writeInt(stream, convergenceAngle.getNumerator());
		writeInt(stream, convergenceAngle.getDecominator());
	}

	private static void writeOffsetOfConvergenceAngle(OutputStream stream, int i)
			throws IOException {
		stream.write(0xb2);
		stream.write(0x05);
		writeShort(stream, TYPE_TAG.SRATIONAL.getValue());
		writeInt(stream, 1);
		writeInt(stream, i);
	}

	private static void writeOffsetOfBaselineLength(OutputStream stream, int i)
			throws IOException {
		stream.write(0xb2);
		stream.write(0x06);
		writeShort(stream, TYPE_TAG.RATIONAL.getValue());
		writeInt(stream, 1);
		writeInt(stream, i);
	}

	private static void writeBaselineLength(OutputStream stream,
			Rational baselineLength) throws IOException {
		writeInt(stream, baselineLength.getNumerator());
		writeInt(stream, baselineLength.getDecominator());
	}

	private static void writeBaseViewPoint(OutputStream stream,
			int baseViewpointNum) throws IOException {
		stream.write(0xb2);
		stream.write(0x04);
		writeShort(stream, TYPE_TAG.LONG.getValue());
		writeInt(stream, 1);
		writeInt(stream, baseViewpointNum);
	}

	private static void writeIndividualNum(OutputStream stream,
			int mpIndividualNum) throws IOException {
		stream.write(0xb1);
		stream.write(0x01);
		writeShort(stream, TYPE_TAG.LONG.getValue());
		writeInt(stream, 1);
		writeInt(stream, mpIndividualNum);
	}

	private static void writeApp2Header(OutputStream stream, int lengthOfApp2)
			throws IOException {
		stream.write(0xff);
		stream.write(0xe2);
		writeShort(stream, (short) lengthOfApp2);
		stream.write('M');
		stream.write('P');
		stream.write('F');
		stream.write(0x00);
	}

	private static void writeMpHeader(OutputStream stream, int offset)
			throws IOException {
		stream.write(0x4d);
		stream.write(0x4d);
		stream.write(0x00);
		stream.write(0x2a);
		stream.write(offset >> 24);
		stream.write((offset >> 16) & 0xff);
		stream.write((offset >> 8) & 0xff);
		stream.write(offset & 0xff);
	}

	static byte[] convertByteArray(BufferedImage image) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageOutputStream ios = null;
		ios = ImageIO.createImageOutputStream(baos);
		Iterator<ImageWriter> writers = ImageIO
				.getImageWritersByFormatName("jpeg");
		ImageWriter writer = writers.next();
		ImageWriteParam param = writer.getDefaultWriteParam();
		param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		param.setCompressionQuality(1.0f);
		writer.setOutput(ios);
		writer.write(image);
		return baos.toByteArray();
	}

	static int calcMpIndexIfdSize(int numberOfImages) {
		int size = 2;
		size += 12; // バージョン
		size += 12; // 記録画像数
		size += 12; // MPエントリー
		size += numberOfImages * 16;
		size += 4;
		return size;
	}

	static int calcMpIndividualIfdSize(MpAttributeFields ifd, boolean first) {
		int size = 2;
		size += 12 * 4 + 8 * 2 + 4;
		if (!first) {
			size += 12;
		}
		return size;
	}

	private static void writeMpEntry(OutputStream stream, MpEntry mpEntry,
			int size, int offset) throws IOException {
		writeInt(stream, mpEntry.getIndividualImageAttribute());
		writeInt(stream, size);
		writeInt(stream, offset);
		writeShort(stream, (short) 0);
		writeShort(stream, (short) 0);
	}

	static void writeShort(OutputStream os, short value) throws IOException {
		os.write(value >> 8);
		os.write(value & 0xff);
	}

	static void writeInt(OutputStream os, int value) throws IOException {
		os.write(value >> 24);
		os.write(value >> 16);
		os.write(value >> 8);
		os.write(value & 0xff);

	}

	static void writeVersion(OutputStream stream) throws IOException {
		stream.write(0xb0);
		stream.write(0x00);
		writeShort(stream, TYPE_TAG.UNDEFINED.getValue());
		writeInt(stream, 4);
		stream.write('0');
		stream.write('1');
		stream.write('0');
		stream.write('0');
	}

	static void writeNumberOfImages(OutputStream stream, int num)
			throws IOException {
		stream.write(0xb0);
		stream.write(0x01);
		writeShort(stream, TYPE_TAG.LONG.getValue());
		writeInt(stream, 1);
		writeInt(stream, num);

	}

	static void writeOffsetOfMpEntry(OutputStream stream, int numOfMpEntry,
			int valueOffset) throws IOException {
		stream.write(0xb0);
		stream.write(0x02);
		writeShort(stream, TYPE_TAG.UNDEFINED.getValue());
		writeInt(stream, 16 * numOfMpEntry);
		writeInt(stream, valueOffset);
	}

	enum TYPE_TAG {
		LONG((short) 0x0004), UNDEFINED((short) 0x0007), SRATIONAL(
				(short) 0x000A), RATIONAL((short) 0x0005);
		private short value;

		private TYPE_TAG(short value) {
			this.value = value;
		}

		public short getValue() {
			return value;
		}
	}
}
