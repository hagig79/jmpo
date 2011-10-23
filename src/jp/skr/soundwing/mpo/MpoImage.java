package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;

/**
 * @author mudwell
 * 
 */
public class MpoImage {

	private BufferedImage image;
	private MpExtension ext;

	public MpoImage(BufferedImage image, MpExtension ext) {
		this.image = image;
		this.ext = ext;
	}

	/**
	 * @return
	 */
	public BufferedImage getBufferedImage() {
		return image;
	}

	public boolean isFirst() {
		return ext.isFirst();
	}

	public MpIndexFields getIndexIfd() {
		return ext.getMpIndexIfd();
	}

	public MpAttributeFields getAttributeIfd() {
		return ext.getAttributeIfd();
	}

}
