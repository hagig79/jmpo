package jp.skr.soundwing.mpo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.skr.soundwing.exif.ByteArrayReader;

/**
 * MPインデックスIFD.
 * 
 * @author mudwell
 * 
 */
public class MpIndexFields {
	byte[] count;
	byte[] version;
	int numberOfImages;
	int entry;
	byte[] unique;
	byte[] koma;
	int offsetOfNextIFD;
	List<MpEntry> mpEntries;

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
		mpEntries = new ArrayList<MpEntry>();
	}

	public static MpIndexFields create(ByteArrayReader reader, int offsetBase) {
		MpIndexFields mpf = new MpIndexFields();
		int count = reader.getShort();
		for (int i = 0; i < count; i++) {
			if (reader.startsWith(VERSION_TAG)) {
				reader.arraycopy(mpf.version, 0, mpf.version.length);
			} else if (reader.startsWith(NUMBER_OF_IMAGES_TAG)) {
				reader.skip(8);
				mpf.numberOfImages = reader.getInt();
			} else if (reader.startsWith(ENTRY_TAG)) {
				reader.skip(8);
				mpf.entry = reader.getInt();
				int tmpPos = reader.getPosition();
				reader.setPosition(mpf.entry + offsetBase);
				for (int j = 0; j < mpf.numberOfImages; j++) {
					MpEntry entry = new MpEntry(reader);
					mpf.mpEntries.add(entry);
				}
				reader.setPosition(tmpPos);

			} else if (reader.startsWith(UNIQUE_TAG)) {
				mpf.unique = new byte[UNIQUE_LENGTH];
				reader.arraycopy(mpf.unique, 0, mpf.unique.length);

			} else if (reader.startsWith(KOMA_TAG)) {
				mpf.koma = new byte[KOMA_LENGTH];
				reader.arraycopy(mpf.koma, 0, mpf.koma.length);

			} else {
				reader.skip(12);
			}

		}

		mpf.offsetOfNextIFD = reader.getInt();
		return mpf;

	}

	/**
	 * 記録画像数を取得する.
	 * 
	 * @return 記録画像数
	 */
	public int getNumberOfImages() {
		return numberOfImages;
	}

	/**
	 * MPエントリへのオフセットを取得する.
	 * 
	 * @return MPエントリへのオフセット
	 */
	public int getMPEntryOffset() {
		return entry;
	}

	/**
	 * MP個別情報IFDのオフセットアドレスを返す.
	 * 
	 * @return MP個別情報IFDのオフセットアドレス
	 */
	public int getOffsetOfNextIFD() {
		return offsetOfNextIFD;
	}

	public void setNumberOfImages(int int1) {
		this.numberOfImages = int1;
	}

	public boolean isFirst() {

		return numberOfImages != 0;
	}

	public MpEntry getMpEntry(int index) {
		return mpEntries.get(index);
	}

	public int getNumberOfMpEntry() {

		return mpEntries.size();
	}

	public void setEntries(MpEntry[] array) {
		mpEntries.clear();
		mpEntries.addAll(Arrays.asList(array));
	}
}
