package jp.skr.soundwing.mpo;

/**
 * MPインデックスIFD.
 * 
 * @author mudwell
 * 
 */
public class MpIndexFields {
	byte[] count;
	byte[] version;
	byte[] numberOfImages;
	byte[] entry;
	byte[] unique;
	byte[] koma;
	byte[] offsetOfNextIFD;

	static final int COUNT_LENGTH = 2;
	static final int VERSION_LENGTH = 12;
	static final int N_LENGTH = 12;
	static final int ENTRY_LENGTH = 12;
	static final int UNIQUE_LENGTH = 12;
	static final int KOMA_LENGTH = 12;
	static final int OFFSET_OF_NEXT_IFD_LENGTH = 4;
	static final int MPF_LENGTH = COUNT_LENGTH + VERSION_LENGTH + N_LENGTH
			+ ENTRY_LENGTH + UNIQUE_LENGTH + KOMA_LENGTH
			+ OFFSET_OF_NEXT_IFD_LENGTH;
	static final int INTEGER_SIZE = 4;

	static final byte[] VERSION_TAG = { (byte) 0xb0, 0x00 };
	private static final byte[] NUMBER_OF_IMAGES_TAG = { (byte) 0xb0, 0x01 };
	private static final byte[] ENTRY_TAG = { (byte) 0xb0, 0x02 };
	private static final byte[] KOMA_TAG = { (byte) 0xb0, 0x03 };
	private static final byte[] UNIQUE_TAG = { (byte) 0xb0, 0x04 };

	public MpIndexFields() {
		count = new byte[COUNT_LENGTH];
		version = new byte[VERSION_LENGTH];
		numberOfImages = new byte[N_LENGTH];
		entry = new byte[ENTRY_LENGTH];
		offsetOfNextIFD = new byte[OFFSET_OF_NEXT_IFD_LENGTH];
	}

	/**
	 * MPIndexFieldsを作成する.
	 * 
	 * @param buffer
	 * @param offset
	 *            MPインデックスIFDの先頭位置.
	 * @return
	 */
	public static MpIndexFields create(byte[] buffer, int offset) {
		MpIndexFields mpf = new MpIndexFields();
		int pos = offset;
		System.arraycopy(buffer, offset, mpf.count, 0, mpf.count.length);

		pos += COUNT_LENGTH;
		if (startsWith(buffer, pos, VERSION_TAG)) {

			System.arraycopy(buffer, pos, mpf.version, 0, mpf.version.length);
			pos += VERSION_LENGTH;
		}

		if (startsWith(buffer, pos, NUMBER_OF_IMAGES_TAG)) {

			System.arraycopy(buffer, pos, mpf.numberOfImages, 0,
					mpf.numberOfImages.length);
			pos += N_LENGTH;
		}

		if (startsWith(buffer, pos, ENTRY_TAG)) {

			System.arraycopy(buffer, offset + COUNT_LENGTH + VERSION_LENGTH
					+ N_LENGTH, mpf.entry, 0, mpf.entry.length);
			pos += ENTRY_LENGTH;
		}

		if (startsWith(buffer, pos, UNIQUE_TAG)) {
			mpf.unique = new byte[UNIQUE_LENGTH];
			System.arraycopy(buffer, pos, mpf.unique, 0, mpf.unique.length);
			pos += UNIQUE_LENGTH;

		}

		if (startsWith(buffer, pos, KOMA_TAG)) {
			mpf.koma = new byte[KOMA_LENGTH];
			System.arraycopy(buffer, pos, mpf.koma, 0, mpf.koma.length);
			pos += KOMA_LENGTH;
		}

		System.arraycopy(buffer, pos, mpf.offsetOfNextIFD, 0,
				mpf.offsetOfNextIFD.length);
		return mpf;
	}

	static boolean startsWith(byte[] array, int offset, byte[] subarray) {
		for (int i = 0; i < subarray.length; i++) {
			if (array[i + offset] != subarray[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 記録画像数を取得する.
	 * 
	 * @return 記録画像数
	 */
	public int getNumberOfImages() {
		return MpoLoader.getInt(numberOfImages, numberOfImages.length
				- INTEGER_SIZE);
	}

	/**
	 * MPエントリへのオフセットを取得する.
	 * 
	 * @return MPエントリへのオフセット
	 */
	public int getMPEntryOffset() {
		return MpoLoader.getInt(entry, entry.length - INTEGER_SIZE);
	}

	/**
	 * MP個別情報IFDのオフセットアドレスを返す.
	 * 
	 * @return MP個別情報IFDのオフセットアドレス
	 */
	public int getOffsetOfNextIFD() {
		return MpoLoader.getInt(offsetOfNextIFD, offsetOfNextIFD.length
				- INTEGER_SIZE);
	}
}
