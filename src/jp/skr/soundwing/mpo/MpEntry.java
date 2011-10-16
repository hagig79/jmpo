package jp.skr.soundwing.mpo;

import jp.skr.soundwing.exif.ByteArrayReader;

/**
 * MPエントリー.
 * 
 * @author mudwell
 * 
 */
public class MpEntry {
	private static final int TYPE_MASK = 0x0fff;
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


	
	public MpEntry(ByteArrayReader reader) {
		info = reader.getInt();
		size = reader.getInt();
		offset = reader.getInt();
		reader.getInt();
	}

	/**
	 * @return
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 個別画像へのデータオフセットを返す.
	 * 
	 * 先頭画像の場合は0を返す.
	 * 
	 * @return 個別画像へのデータオフセット(先頭画像の場合は0)
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * 種別コードを返す.
	 * 
	 * @return 種別コード
	 */
	public int getTypeCode() {
		return info & TYPE_MASK;
	}
}
