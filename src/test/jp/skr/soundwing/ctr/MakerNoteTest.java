package jp.skr.soundwing.ctr;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class MakerNoteTest {

	@Test
	public void testGetMakerNote() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		byte[] buffer = readFile(is);
		// exercise
		byte[] makernote = MakerNoteReader.readMakerNote(buffer, 0);
//		for (int i = 0; i < makernote.length; i++) {
//			System.out.printf("%02x ", makernote[i] & 0xff);
//		}
		// verify
		assertNotNull(makernote);
	}

	@Test
	public void testLengthOfMakerNote() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		byte[] buffer = readFile(is);
		// exercise
		byte[] makernote = MakerNoteReader.readMakerNote(buffer, 0);
		// verify
		assertEquals(174, makernote.length);
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
}
