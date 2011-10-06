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
	 * 分母.
	 */
	private int decominator;

	public Rational(int n, int d) {
		this.numerator = n;
		this.decominator = d;
	}

	/**
	 * このRationalが表す値をdouble型で取得する.
	 * 
	 * @return
	 */
	public double getDouble() {
		return numerator / (double) decominator;
	}
}
