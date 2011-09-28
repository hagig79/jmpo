package jp.skr.soundwing.mpo;

/**
 * @author mudwell
 *
 */
public class Rational {
	/**
	 * 分子.
	 */
	private int numerator;
	/**
	 * 分母
	 */
	private int decominator;
	
	/**
	 * @return
	 */
	public double getDouble() {
		return numerator/(double)decominator;
	}
}
