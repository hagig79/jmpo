package jp.skr.soundwing.mpo;

import jp.skr.soundwing.exif.ByteArrayReader;

/**
 * MPエントリー.
 * 
 * @author mudwell
 * 
 */
public class MpEntry {
	private static final int TYPE_MASK = 0x00ffffff;
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
		image1EntryNumber = reader.getShort();
		image2EntryNumber = reader.getShort();
	}

	public MpEntry(int info, int size, int offset, int entryNo1, int entryNo2) {
		this.info = info;
		this.size = size;
		this.offset = offset;
		this.image1EntryNumber = entryNo1;
		this.image1EntryNumber = entryNo2;
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

	/**
	 * 個別画像種別管理情報を返す.
	 * 
	 * @return 個別画像種別管理情報
	 */
	public int getIndividualImageAttribute() {
		return info;
	}
}
