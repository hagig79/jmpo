package jp.skr.soundwing.mpo;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class MpoWriterTest {

	@Test
	public void testConvertByteArray() throws IOException {
		BufferedImage image = ImageIO.read(new File("testdata/HNI_0031.MPO.jpg"));
		byte[] imageData = MpoWriter.convertByteArray(image);
		for (int i = 0; i < imageData.length;i++) {
			System.out.printf("%02x ", imageData[i]);
		}
		assertNotNull(imageData);
	}

}
