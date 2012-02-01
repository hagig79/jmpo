package jp.skr.soundwing.mpo;

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
public class MpoFile {

	private List<MpoImage> images;

	/**
	 * @param left
	 * @param right
	 */
	MpoFile(MpoImage left, MpoImage right) {
		this(new MpoImage[] { left, right });
	}

	/**
	 * @param images
	 */
	MpoFile(MpoImage[] images) {
		this.images = new ArrayList<MpoImage>();
		for (MpoImage image : images) {
			this.images.add(image);
		}
		for (int i = 1; i < images.length; i++) {
			images[i].setIndexIfd(null);
		}
	}

	/**
	 * @return
	 */
	public MpoImage getLeftImage() {
		return images.get(0);
	}

	/**
	 * @return
	 */
	public MpoImage getRightImage() {
		return images.get(1);
	}

	/**
	 * このMPOFileが持つMPOImageの数を返す.
	 * 
	 * @return MPOFileの数
	 */
	public int getNumberOfImages() {
		return images.size();
	}

	/**
	 * @param i
	 * @return
	 */
	public MpoImage getMpoImage(int i) {
		return images.get(i);
	}
}
