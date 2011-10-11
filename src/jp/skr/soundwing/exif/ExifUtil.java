package jp.skr.soundwing.exif;

/**
 * Exifを解析するための関数を持つクラス.
 * 
 * @author mudwell
 * 
 */
public class ExifUtil {

	public static boolean startsWith(byte[] array, int offset, byte[] subarray) {
		for (int i = 0; i < subarray.length; i++) {
			if (array[i + offset] != subarray[i]) {
				return false;
			}
		}
		return true;
	}
}
