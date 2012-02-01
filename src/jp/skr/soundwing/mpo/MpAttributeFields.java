package jp.skr.soundwing.mpo;

import jp.skr.soundwing.exif.ByteArrayReader;

/**
 * MP個別情報IFD.
 * 
 * @author mudwell
 * 
 */
public class MpAttributeFields {
	static final byte[] VERSION_TAG = { (byte) 0xb0, 0x00 };
	static final byte[] MP_INDIVIDUAL_NUM_TAG = { (byte) 0xb1, 0x01 };
	static final byte[] BASE_VIEWPOINT_NUM_TAG = { (byte) 0xb2, 0x04 };
	static final byte[] CONVERGENCE_ANGLE_TAG = { (byte) 0xb2, 0x05 };
	static final byte[] BASELINE_LENGTH_TAG = { (byte) 0xb2, 0x06 };
	static final byte[] AXIS_DISTANCE_Z_TAG = { (byte) 0xb2, 0x0a };

	static final int COUNT_LENGTH = 2;
	static final int VERSION_LENGTH = 12;
	static final int MP_INDIVIDUAL_NUM_LENGTH = 12;
	static final int BASE_VIEWPOINT_NUM_LENGTH = 12;
	static final int CONVERGENCE_ANGLE_LENGTH = 12;
	static final int BASELINE_LENGTH_LENGTH = 12;
	static final int OFFSET_OF_NEXT_IFD_LENGTH = 4;

	byte[] count;
	byte[] version;
	int mpIndividualNum;
	int baseViewpointNum;
	private Rational convergenceAngle;
	private Rational baselineLength;
	private Rational axisDistanceZ;
	int offsetOfNextIFD;

	public MpAttributeFields() {
		count = new byte[COUNT_LENGTH];
	}

	public static MpAttributeFields create(ByteArrayReader reader,
			int offsetBase) {
		MpAttributeFields mpf = new MpAttributeFields();
		int count = reader.getShort();

		for (int i = 0; i < count; i++) {
			if (reader.startsWith(VERSION_TAG)) {
				mpf.version = new byte[VERSION_LENGTH];
				reader.arraycopy(mpf.version, 0, mpf.version.length);
			} else if (reader.startsWith(MP_INDIVIDUAL_NUM_TAG)) {
				reader.skip(8);
				mpf.mpIndividualNum = reader.getInt();
			} else if (reader.startsWith(BASE_VIEWPOINT_NUM_TAG)) {
				reader.skip(8);
				mpf.baseViewpointNum = reader.getInt();
			} else if (reader.startsWith(CONVERGENCE_ANGLE_TAG)) {
				reader.skip(8);
				int offset = reader.getInt();
				int tmpPos = reader.getPosition();
				reader.setPosition(offset + offsetBase);
				int n = reader.getInt();
				int d = reader.getInt();
				mpf.convergenceAngle = new Rational(n, d);
				reader.setPosition(tmpPos);
			} else if (reader.startsWith(BASELINE_LENGTH_TAG)) {
				reader.skip(8);
				int offset = reader.getInt();
				int tmpPos = reader.getPosition();
				reader.setPosition(offset + offsetBase);
				int n = reader.getInt();
				int d = reader.getInt();
				mpf.baselineLength = new Rational(n, d);
				reader.setPosition(tmpPos);
			} else if (reader.startsWith(AXIS_DISTANCE_Z_TAG)) {
				reader.skip(8);
				int offset = reader.getInt();
				int tmpPos = reader.getPosition();
				reader.setPosition(offset + offsetBase);
				int n = reader.getInt();
				int d = reader.getInt();
				mpf.axisDistanceZ = new Rational(n, d);
				reader.setPosition(tmpPos);
			}
		}
		mpf.offsetOfNextIFD = reader.getInt();
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
		return mpIndividualNum;
	}

	/**
	 * 輻輳角を返す.
	 * 
	 * @return 輻輳角(-180〜180)[degree]
	 */
	public Rational getConvergenceAngle() {
		return convergenceAngle;
	}

	/**
	 * 基線長を返す.
	 * 
	 * @return 基線長[m]
	 */
	public Rational getBaselineLength() {
		return baselineLength;
	}
	
	public void setBaselineLength(Rational rational) {
		this.baselineLength = rational;
	}

	public void setConvergenceAngle(Rational rational) {
		this.convergenceAngle = rational;

	}

	public int getOffsetOfNextIFD() {
		return offsetOfNextIFD;
	}

	public Rational getAxisDistanceZ() {
		return axisDistanceZ;
	}

	public int getBaseViewpointNum() {
		return baseViewpointNum;
	}
}
