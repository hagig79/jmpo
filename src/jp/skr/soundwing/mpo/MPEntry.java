package jp.skr.soundwing.mpo;

public class MPEntry {
	/**
	 * 個別画像種別管理情報.
	 */
	private int info;
	/**
	 * 個別画像サイズ.
	 */
	private int size;
	/**
	 * 個別画像データオフセット.
	 */
	private int offset;
	/**
	 * 従属画像のエントリ番号.
	 */
	private int image1EntryNumber;
	private int image2EntryNumber;

	public MPEntry(byte[] buffer, int mpentryHead) {
		System.out.printf("MPEntry Head: %x\n", mpentryHead);
		this.info = MPOLoader.getInt(buffer, mpentryHead);
		this.size = MPOLoader.getInt(buffer, mpentryHead + 4);
		this.offset = MPOLoader.getInt(buffer, mpentryHead + 8);
	}

	/**
	 * 個別画像のサイズを取得する.
	 * 
	 * @return 個別画像のサイズ
	 */
	public int getSize() {
		return size;
	}

	public int getOffset() {
		return offset;
	}
}
