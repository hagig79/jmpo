package jp.skr.soundwing.mpo;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;
import static org.junit.Assert.*;

import jp.skr.soundwing.mpo.MpoLoader;

public class MPOLoaderTest {
	@Test
	public void testReadMPOFile() throws Exception {
		// setup
		String path = "testdata/HNI_0021.MPO.jpg";
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		// exercise
		MpoFile image = MpoLoader.read(is);
		// verify
		assertNotNull(image);
	}

}
