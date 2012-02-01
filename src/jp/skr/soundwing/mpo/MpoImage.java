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

	public static MpoImage createMpoImage(BufferedImage image, boolean first) {
		MpIndexFields indexIFD = null;
		if (first) {
			indexIFD = new MpIndexFields();
		}
		MpAttributeFields attr = new MpAttributeFields();
		attr.setConvergenceAngle(new Rational(0xffffffff, 0xffffffff));
		attr.setBaselineLength(new Rational(0xffffffff, 0xffffffff));
		MpExtension ext = new MpExtension(indexIFD, attr);
		return new MpoImage(image, ext);
	}

	public void setIndexIfd(MpIndexFields object) {
		ext.indexIFD = object;

	}
}
