package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;

/**
 * @author mudwell
 *
 */
public class MpoImage {

	private BufferedImage image;

	public MpoImage(BufferedImage image, MpExtensions ext) {
		this.image = image;
	}

	/**
	 * @return
	 */
	public BufferedImage getBufferedImage() {
		return image;
	}

}
