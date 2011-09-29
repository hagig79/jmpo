package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MPOImage {

	private List<BufferedImage> images;

	MPOImage(BufferedImage left, BufferedImage right) {
		this(new BufferedImage[] { left, right });
	}

	MPOImage(BufferedImage[] images) {
		this.images = new ArrayList<BufferedImage>();
		for (BufferedImage image : images) {
			this.images.add(image);
		}
	}

	public BufferedImage getLeftImage() {
		return images.get(0);
	}

	public BufferedImage getRightImage() {
		return images.get(1);
	}

	public int getNumberOfImages() {
		return images.size();
	}
}
