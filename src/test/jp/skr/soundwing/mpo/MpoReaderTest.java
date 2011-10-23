package jp.skr.soundwing.mpo;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import static org.junit.Assert.*;

import jp.skr.soundwing.mpo.MpoReader;

public class MpoReaderTest {
	@Test
	public void testReadMPOFile() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		// exercise
		MpoFile image = MpoReader.read(is);
		// verify
		assertNotNull(image);
	}

	@Test
	public void testGetLeftImage() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		MpoFile mpoFile = MpoReader.read(is);
		// exercise
		MpoImage image = mpoFile.getLeftImage();
		// verify
		assertNotNull(image);

	}

	@Test
	public void testGetRightImage() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		MpoFile mpoFile = MpoReader.read(is);
		// exercise
		MpoImage image = mpoFile.getRightImage();
		// verify
		assertNotNull(image);

	}

	@Test
	public void testGetIndexIfd() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		MpoFile mpoFile = MpoReader.read(is);
		MpoImage image = mpoFile.getLeftImage();
		// exercise
		MpIndexFields ifd = image.getIndexIfd();
		// verify
		assertNotNull(ifd);

	}
}
