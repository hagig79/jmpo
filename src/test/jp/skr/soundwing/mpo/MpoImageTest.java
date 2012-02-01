package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import static org.junit.Assert.*;

import org.junit.Test;

public class MpoImageTest {
	@Test
	public void testCreateMpoImage() throws Exception {
		BufferedImage left = ImageIO.read(new File("testdata/HNI_0021l.jpg"));
		BufferedImage right = ImageIO.read(new File("testdata/HNI_0021r.jpg"));
		// exercise
		MpoFile mpoFile = new MpoFile(MpoImage.createMpoImage(left, true),
				MpoImage.createMpoImage(right, false));
		// verify
		assertEquals(2, mpoFile.getNumberOfImages()); // 記録画像数
		assertNotNull(mpoFile.getLeftImage().getIndexIfd());
		assertNull(mpoFile.getRightImage().getIndexIfd());
	}
}
