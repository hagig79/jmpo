package jp.skr.soundwing.mpo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;

public class MpExtensionTest {
	@Test
	public void testAnalyzeFirstExtension() throws Exception {
		// setup
		byte[] fileData = readFile();
		int app2 = MpoReader.findAPP2Tag(fileData, 0);
		// exercise
		MpExtension ext = MpExtension.create(fileData, app2 + 8);
		// verify
		assertNotNull(ext);
	}

	private byte[] readFile() throws FileNotFoundException, IOException {
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		byte[] fileData = MpoReader.readFile(is);
		return fileData;
	}

	@Test
	public void testFirstExtension() throws Exception {
		// setup
		byte[] fileData = readFile();
		int app2 = MpoReader.findAPP2Tag(fileData, 0);
		MpExtension ext = MpExtension.createFirst(fileData, app2 + 8);
		// exercise
		boolean actual = ext.isFirst();
		// verify
		assertTrue(actual);

	}

	@Test
	public void testMPIndividualNum() throws Exception {
		// setup
		byte[] fileData = readFile();
		int app2 = MpoReader.findAPP2Tag(fileData, 0);
		MpExtension ext = MpExtension.create(fileData, app2 + 8);
		// exercise
		int num = ext.individualIFD.getMPIndividualNum();
		// verify
		assertNotSame(num, -1);
	}

	@Test
	public void testGetAttributeIfd() throws Exception {

	}

}
