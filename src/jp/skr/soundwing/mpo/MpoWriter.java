package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class MpoWriter {
	private static final int MP_HEADER_LENGTH = 8;
	private static final byte[] SOI = { (byte) 0xff, (byte) 0xd8 };

	public static void write(MpoFile mpoFile, OutputStream stream)
			throws IOException {
		int[] dataOffset = new int[2];// 個別画像データのオフセット
		dataOffset[0] = 0; // 先頭画像は0
		byte[][] imageData = new byte[2][];
		imageData[0] = convertByteArray(mpoFile.getLeftImage()
				.getBufferedImage());
		deleteSoi(imageData[0]);
		imageData[1] = convertByteArray(mpoFile.getRightImage()
				.getBufferedImage());
		deleteSoi(imageData[1]);
		int[] mpIndividualIfdSize = new int[2];
		mpIndividualIfdSize[0] = calcMpIndividualIfdSize(mpoFile.getLeftImage()
				.getAttributeIfd(), true);
		mpIndividualIfdSize[1] = calcMpIndividualIfdSize(mpoFile
				.getRightImage().getAttributeIfd(), false);
		dataOffset[1] = MP_HEADER_LENGTH
				+ calcMpIndexIfdSize(mpoFile.getLeftImage().getIndexIfd())
				+ mpIndividualIfdSize[0] + imageData[0].length;

		int[] imageSize = new int[2];
		imageSize[0] = SOI.length + 6 + MP_HEADER_LENGTH
				+ calcMpIndexIfdSize(mpoFile.getLeftImage().getIndexIfd())
				+ mpIndividualIfdSize[0] + imageData[0].length;
		imageSize[1] = SOI.length + 6 + MP_HEADER_LENGTH
				+ mpIndividualIfdSize[1] + imageData[1].length;

		stream.write(SOI);
		writeApp2Header(stream, 2 + 4 + MP_HEADER_LENGTH
				+ calcMpIndexIfdSize(mpoFile.getLeftImage().getIndexIfd())
				+ mpIndividualIfdSize[0]);
		writeMpHeader(stream, 8);
		writeIndexIfd(stream, mpoFile.getLeftImage().getIndexIfd(), imageSize,
				dataOffset);
		writeIndividualIfd(stream, mpoFile.getLeftImage().getAttributeIfd(),
				8 + calcMpIndexIfdSize(mpoFile.getLeftImage().getIndexIfd()),
				true);
		stream.write(imageData[0]);

		stream.write(SOI);
		writeApp2Header(stream, 2 + 4 + MP_HEADER_LENGTH
				+ mpIndividualIfdSize[1]);
		writeMpHeader(stream, 8);
		writeIndividualIfd(stream, mpoFile.getRightImage().getAttributeIfd(),
				8, false);
		stream.write(imageData[1]);

	}

	static void deleteSoi(byte[] jpegData) {
		System.arraycopy(jpegData, 2, jpegData, 0, jpegData.length - 2);
	}

	private static void writeIndexIfd(OutputStream stream,
			MpIndexFields indexIfd, int[] imageSize, int[] dataOffset)
			throws IOException {
		write(stream, (short) 3);
		writeVersion(stream);
		writeNumberOfImages(stream, indexIfd.getNumberOfImages());
		int valueOffset = MP_HEADER_LENGTH + 2 + 12 * 3 + 4;
		writeOffsetOfMpEntry(stream, indexIfd.getNumberOfImages(), valueOffset);
		write(stream, valueOffset + 16 * indexIfd.getNumberOfImages());
		for (int i = 0; i < indexIfd.getNumberOfImages(); i++) {
			writeMpEntry(stream, indexIfd.getMpEntry(i), imageSize[i],
					dataOffset[i]);
		}
	}

	private static void writeIndividualIfd(OutputStream stream,
			MpAttributeFields attributeIfd, int offset, boolean b)
			throws IOException {
		write(stream, (short) 3);
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
		write(stream, 0);
		writeConvergenceAngle(stream, attributeIfd.getConvergenceAngle());
		writeBaselineLength(stream, attributeIfd.getBaselineLength());
	}

	private static void writeConvergenceAngle(OutputStream stream,
			Rational convergenceAngle) throws IOException {
		write(stream, convergenceAngle.getNumerator());
		write(stream, convergenceAngle.getDecominator());
	}

	private static void writeOffsetOfConvergenceAngle(OutputStream stream, int i)
			throws IOException {
		stream.write(0xb2);
		stream.write(0x05);
		write(stream, TYPE_TAG.SRATIONAL.getValue());
		write(stream, 1);
		write(stream, i);
	}

	private static void writeOffsetOfBaselineLength(OutputStream stream, int i)
			throws IOException {
		stream.write(0xb2);
		stream.write(0x06);
		write(stream, TYPE_TAG.RATIONAL.getValue());
		write(stream, 1);
		write(stream, i);
	}

	private static void writeBaselineLength(OutputStream stream,
			Rational baselineLength) throws IOException {
		write(stream, baselineLength.getNumerator());
		write(stream, baselineLength.getDecominator());
	}

	private static void writeBaseViewPoint(OutputStream stream,
			int baseViewpointNum) throws IOException {
		stream.write(0xb2);
		stream.write(0x04);
		write(stream, TYPE_TAG.LONG.getValue());
		write(stream, 1);
		write(stream, baseViewpointNum);
	}

	private static void writeIndividualNum(OutputStream stream,
			int mpIndividualNum) throws IOException {
		stream.write(0xb1);
		stream.write(0x01);
		write(stream, TYPE_TAG.LONG.getValue());
		write(stream, 1);
		write(stream, mpIndividualNum);
	}

	private static void writeApp2Header(OutputStream stream, int i)
			throws IOException {
		stream.write(0xff);
		stream.write(0xe2);
		write(stream, (short) i);
		stream.write('M');
		stream.write('P');
		stream.write('F');
		stream.write(0x00);
	}

	private static void writeMpHeader(OutputStream stream, int i)
			throws IOException {
		stream.write(0x4d);
		stream.write(0x4d);
		stream.write(0x00);
		stream.write(0x2a);
		stream.write(i >> 24);
		stream.write((i >> 16) & 0xff);
		stream.write((i >> 8) & 0xff);
		stream.write(i & 0xff);
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

	static int calcMpIndexIfdSize(MpIndexFields ifd) {
		int size = 2;
		size += 12; // バージョン
		size += 12; // 記録画像数
		size += 12; // MPエントリー
		size += ifd.getNumberOfImages() * 16;
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
		write(stream, mpEntry.getIndividualImageAttribute());
		write(stream, size);
		write(stream, offset);
		write(stream, (short) 0);
		write(stream, (short) 0);
	}

	static void write(OutputStream os, short value) throws IOException {
		os.write(value >> 8);
		os.write(value & 0xff);
	}

	static void write(OutputStream os, int value) throws IOException {
		os.write(value >> 24);
		os.write(value >> 16);
		os.write(value >> 8);
		os.write(value & 0xff);

	}

	static void writeVersion(OutputStream stream) throws IOException {
		stream.write(0xb0);
		stream.write(0x00);
		write(stream, TYPE_TAG.UNDEFINED.getValue());
		write(stream, 4);
		stream.write('0');
		stream.write('1');
		stream.write('0');
		stream.write('0');
	}

	static void writeNumberOfImages(OutputStream stream, int num)
			throws IOException {
		stream.write(0xb0);
		stream.write(0x01);
		write(stream, TYPE_TAG.LONG.getValue());
		write(stream, 1);
		write(stream, num);

	}

	static void writeOffsetOfMpEntry(OutputStream stream, int numOfMpEntry,
			int valueOffset) throws IOException {
		stream.write(0xb0);
		stream.write(0x02);
		write(stream, TYPE_TAG.UNDEFINED.getValue());
		write(stream, 16 * numOfMpEntry);
		write(stream, valueOffset);
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
