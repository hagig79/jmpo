package jp.skr.soundwing.mpo;

/**
 * MP個別情報IFD.
 * 
 * @author mudwell
 * 
 */
public class MPAttributeFields {
	static final byte[] MP_INDIVIDUAL_NUM_TAG = { (byte) 0xb1, 0x01 };

	static final int COUNT_LENGTH = 2;
	static final int MP_INDIVIDUAL_NUM_LENGTH = 12;

	byte[] count;
	byte[] mpIndividualNum;

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

}
