package jp.skr.soundwing.mpo;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * MPOファイルを表す.
 * 
 * 内部に複数のMPOImageを持つ.
 * 
 * @author mudwell
 * 
 */
public class MPOFile {

	private List<MPOImage> images;

	MPOFile(BufferedImage left, BufferedImage right) {
		this(new MPOImage[] { new MPOImage(left, null),
				new MPOImage(right, null) });
	}

	MPOFile(MPOImage left, MPOImage right) {
		this(new MPOImage[] { left, right });
	}

	MPOFile(MPOImage[] images) {
		this.images = new ArrayList<MPOImage>();
		for (MPOImage image : images) {
			this.images.add(image);
		}
	}

	public MPOImage getLeftImage() {
		return images.get(0);
	}

	public MPOImage getRightImage() {
		return images.get(1);
	}

	public int getNumberOfImages() {
		return images.size();
	}
}
