package jp.skr.soundwing.mpo;

/**
 * MP個別情報IFD.
 * 
 * @author mudwell
 * 
 */
public class MPAttributeFields {
	static final byte[] MP_INDIVIDUAL_NUM_TAG = { (byte) 0xb1, 0x01 };
	static final byte[] BASE_VIEWPOINT_NUM_TAG = { (byte) 0xb2, 0x04 };
	static final byte[] CONVERGENCE_ANGLE_TAG = { (byte) 0xb2, 0x05 };
	static final byte[] BASELINE_LENGTH_TAG = { (byte) 0xb2, 0x06 };

	static final int COUNT_LENGTH = 2;
	static final int MP_INDIVIDUAL_NUM_LENGTH = 12;
	static final int BASE_VIEWPOINT_NUM_LENGTH = 12;
	static final int CONVERGENCE_ANGLE_LENGTH = 12;
	static final int BASELINE_LENGTH_LENGTH = 12;

	byte[] count;
	byte[] mpIndividualNum;
	byte[] baseViewpointNum;
	byte[] convergenceAngle;
	byte[] baselineLength;

	public MPAttributeFields() {
		count = new byte[COUNT_LENGTH];
	}

	public static MPAttributeFields create(byte[] fileData, int attrHead) {
		MPAttributeFields mpf = new MPAttributeFields();
		int pos = attrHead;
		System.arraycopy(fileData, attrHead, mpf.count, 0, mpf.count.length);

		pos += COUNT_LENGTH;

		if (MPIndexFields.startsWith(fileData, pos, MP_INDIVIDUAL_NUM_TAG)) {
			mpf.mpIndividualNum = new byte[MP_INDIVIDUAL_NUM_LENGTH];
			System.arraycopy(fileData, pos, mpf.mpIndividualNum, 0,
					mpf.mpIndividualNum.length);
			pos += MP_INDIVIDUAL_NUM_LENGTH;
		}
		if (MPIndexFields.startsWith(fileData, pos, BASE_VIEWPOINT_NUM_TAG)) {
			mpf.baseViewpointNum = new byte[BASE_VIEWPOINT_NUM_LENGTH];
			System.arraycopy(fileData, pos, mpf.baseViewpointNum, 0,
					mpf.baseViewpointNum.length);
			pos += BASE_VIEWPOINT_NUM_LENGTH;
		}
		if (MPIndexFields.startsWith(fileData, pos, CONVERGENCE_ANGLE_TAG)) {
			mpf.convergenceAngle = new byte[CONVERGENCE_ANGLE_LENGTH];
			System.arraycopy(fileData, pos, mpf.convergenceAngle, 0,
					mpf.convergenceAngle.length);
			pos += CONVERGENCE_ANGLE_LENGTH;
		}
		if (MPIndexFields.startsWith(fileData, pos, BASELINE_LENGTH_TAG)) {
			mpf.baselineLength = new byte[BASELINE_LENGTH_LENGTH];
			System.arraycopy(fileData, pos, mpf.baselineLength, 0,
					mpf.baselineLength.length);
			pos += BASELINE_LENGTH_LENGTH;
		}
		return mpf;
	}

	/**
	 * 個別画像番号を取得する.
	 * 
	 * 個別画像番号が存在していなかった場合は-1を返す.
	 * 
	 * @return 個別画像番号(存在しない場合は-1)
	 */
	public int getMPIndividualNum() {
		if (mpIndividualNum != null) {
			return MPOLoader.getInt(mpIndividualNum, mpIndividualNum.length
					- MPIndexFields.INTEGER_SIZE);
		} else {
			return -1;
		}
	}

	/**
	 * 輻輳角を返す.
	 * 
	 * @return
	 */
	public Rational getConvergenceAngle() {
		int n = MPOLoader.getInt(convergenceAngle, 4);
		int d = MPOLoader.getInt(convergenceAngle,
				4 + MPIndexFields.INTEGER_SIZE);
		return new Rational(n, d);
	}

	/**
	 * 基線長を返す.
	 * 
	 * @return 基線長[m]
	 */
	public Rational getBaselineLength() {
		int n = MPOLoader.getInt(baselineLength, 4);
		int d = MPOLoader
				.getInt(baselineLength, 4 + MPIndexFields.INTEGER_SIZE);
		return new Rational(n, d);
	}
}
