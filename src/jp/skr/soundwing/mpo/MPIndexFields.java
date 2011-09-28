package jp.skr.soundwing.mpo;

/**
 * MPインデックスIFD.
 * 
 * @author mudwell
 * 
 */
public class MPIndexFields {
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
	private static final int INTEGER_SIZE = 4;

	public MPIndexFields() {
		count = new byte[COUNT_LENGTH];
		version = new byte[VERSION_LENGTH];
		numberOfImages = new byte[N_LENGTH];
		entry = new byte[ENTRY_LENGTH];
		unique = new byte[UNIQUE_LENGTH];
		koma = new byte[KOMA_LENGTH];
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
	public static MPIndexFields create(byte[] buffer, int offset) {
		MPIndexFields mpf = new MPIndexFields();
		System.arraycopy(buffer, offset, mpf.count, 0, mpf.count.length);
		System.arraycopy(buffer, offset + COUNT_LENGTH, mpf.version, 0,
				mpf.version.length);
		System.arraycopy(buffer, offset + COUNT_LENGTH + VERSION_LENGTH,
				mpf.numberOfImages, 0, mpf.numberOfImages.length);
		System.arraycopy(buffer, offset + COUNT_LENGTH + VERSION_LENGTH
				+ N_LENGTH, mpf.entry, 0, mpf.entry.length);
		System.arraycopy(buffer, offset + COUNT_LENGTH + VERSION_LENGTH
				+ N_LENGTH + ENTRY_LENGTH, mpf.unique, 0, mpf.unique.length);
		System.arraycopy(buffer, offset + COUNT_LENGTH + VERSION_LENGTH
				+ N_LENGTH + ENTRY_LENGTH + UNIQUE_LENGTH, mpf.koma, 0,
				mpf.koma.length);
		System.arraycopy(buffer, offset + COUNT_LENGTH + VERSION_LENGTH
				+ N_LENGTH + ENTRY_LENGTH + UNIQUE_LENGTH + KOMA_LENGTH,
				mpf.offsetOfNextIFD, 0, mpf.offsetOfNextIFD.length);
		return mpf;
	}

	/**
	 * 記録画像数を取得する.
	 * 
	 * @return 記録画像数
	 */
	public int getNumberOfImages() {
		return MPOLoader.getInt(numberOfImages, numberOfImages.length
				- INTEGER_SIZE);
	}
}
