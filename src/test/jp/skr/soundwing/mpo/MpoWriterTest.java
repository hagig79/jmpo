package jp.skr.soundwing.mpo;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.junit.Test;

public class MpoWriterTest {

	private static final byte[] SOI = { (byte) 0xff, (byte) 0xd8 };

	@Test
	public void testConvertByteArray() throws IOException {
		BufferedImage image = ImageIO
				.read(new File("testdata/HNI_0031.MPO.jpg"));
		byte[] imageData = MpoWriter.convertByteArray(image);
		assertNotNull(imageData);
	}

	@Test
	public void testWrite() throws Exception {
		MpoFile mpoFile = MpoReader.read(new File("testdata/HNI_0031.MPO.jpg"));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MpoWriter.write(mpoFile, os);
		os.close();
		byte[] fileData = os.toByteArray();
		ByteArrayInputStream is = new ByteArrayInputStream(fileData);
		MpoReader.read(is);
		is.close();
	}

	@Test
	public void testWrite2() throws Exception {
		BufferedImage left = ImageIO.read(new File("testdata/HNI_0021l.jpg"));
		BufferedImage right = ImageIO.read(new File("testdata/HNI_0021r.jpg"));
		MpoFile mpoFile = new MpoFile(MpoImage.createMpoImage(left, true),
				MpoImage.createMpoImage(right, false));
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		MpoWriter.write(mpoFile, os);
		os.close();

		byte[] fileData = os.toByteArray();
		// SOI
		byte[] actual = new byte[SOI.length];
		System.arraycopy(fileData, 0, actual, 0, SOI.length);
		assertArrayEquals(SOI, actual);

		// APP2ヘッダを探す
		int app2 = MpoReader.findAPP2Tag(fileData, 0);
		assertTrue(app2 > 0);

		int base = app2 + 8;
		// byte[] LITTLE = { 0x49, 0x49, 0x2A, 0x00 };
		byte[] BIG = { 0x4D, 0x4D, 0x00, 0x2A };
		actual = new byte[BIG.length];
		System.arraycopy(fileData, base, actual, 0, BIG.length);
		assertArrayEquals(BIG, actual);

		// インデックスIFDのオフセット
		int offset = (fileData[base + 4] << 24) + (fileData[base + 5] << 16)
				+ (fileData[base + 6] << 8) + fileData[base + 7];
		assertEquals(8, offset);

		// 記録画像数
		int numberOfImages = (fileData[base + 8 + 14 + 8] << 24)
				+ (fileData[base + 8 + 14 + 8 + 1] << 16)
				+ (fileData[base + 8 + 14 + 8 + 2] << 8)
				+ fileData[base + 8 + 14 + 8 + 3];
		assertEquals(2, numberOfImages);

		ByteArrayInputStream is = new ByteArrayInputStream(fileData);
		MpoReader.read(is);
		is.close();
	}
}
