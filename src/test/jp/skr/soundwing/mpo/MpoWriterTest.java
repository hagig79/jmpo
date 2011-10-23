package jp.skr.soundwing.mpo;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import org.junit.Test;

public class MpoWriterTest {

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

}
