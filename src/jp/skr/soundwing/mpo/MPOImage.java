package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;

public class MPOImage {

	/**
	 * 左側の画像.
	 */
	private BufferedImage left;
	/**
	 * 右側の画像.
	 */
	private BufferedImage right;

	MPOImage(BufferedImage left, BufferedImage right) {
		this.left = left;
		this.right = right;
	}

	public BufferedImage getLeftImage() {
		return left;
	}

	public BufferedImage getRightImage() {
		return right;
	}
}
