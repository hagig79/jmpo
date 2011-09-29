package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MPOImage {

	private BufferedImage image;

	MPOImage(BufferedImage image, MPExtensions ext) {
		this.image = image;
	}

	public BufferedImage getBufferedImage() {
		return image;
	}

}
