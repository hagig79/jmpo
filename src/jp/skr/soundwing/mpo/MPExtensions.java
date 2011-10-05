package jp.skr.soundwing.mpo;

import static jp.skr.soundwing.mpo.MPOLoader.ENDIAN_OFFSET_SIZE;

import java.util.HashMap;
import java.util.Map;

import static jp.skr.soundwing.mpo.MPIndexFields.startsWith;

public class MPExtensions {
	MPIndexFields indexIFD;
	MPAttributeFields individualIFD;

	static final int COUNT_LENGTH = 2;
	static final int VERSION_LENGTH = 12;
	static final int NUMBER_OF_IMAGES_LENGTH = 12;
	static final int ENTRY_LENGTH = 12;
	static final int UNIQUE_LENGTH = 12;
	static final int KOMA_LENGTH = 12;
	static final int OFFSET_OF_NEXT_IFD_LENGTH = 4;
	static final int MP_INDIVIDUAL_NUM_LENGTH = 12;
	static final int BASE_VIEWPOINT_NUM_LENGTH = 12;
	static final int CONVERGENCE_ANGLE_LENGTH = 12;
	static final int BASELINE_LENGTH_LENGTH = 12;

	static final byte[] VERSION_TAG_DATA = { (byte) 0xb0, 0x00 };
	static final byte[] NUMBER_OF_IMAGES_TAG_DATA = { (byte) 0xb0, 0x01 };
	static final byte[] ENTRY_TAG_DATA = { (byte) 0xb0, 0x02 };
	static final byte[] KOMA_TAG_DATA = { (byte) 0xb0, 0x03 };
	static final byte[] UNIQUE_TAG_DATA = { (byte) 0xb0, 0x04 };
	static final byte[] MP_INDIVIDUAL_NUM_TAG_DATA = { (byte) 0xb1, 0x01 };
	static final byte[] BASE_VIEWPOINT_NUM_TAG_DATA = { (byte) 0xb2, 0x04 };
	static final byte[] CONVERGENCE_ANGLE_TAG_DATA = { (byte) 0xb2, 0x05 };
	static final byte[] BASELINE_LENGTH_TAG_DATA = { (byte) 0xb2, 0x06 };

	static final MPTag VERSION_TAG = new MPTag(VERSION_TAG_DATA, VERSION_LENGTH);
	static final MPTag NUMBER_OF_IMAGES_TAG = new MPTag(
			NUMBER_OF_IMAGES_TAG_DATA, NUMBER_OF_IMAGES_LENGTH);
	static final MPTag ENTRY_TAG = new MPTag(ENTRY_TAG_DATA, ENTRY_LENGTH);
	static final MPTag KOMA_TAG = new MPTag(KOMA_TAG_DATA, VERSION_LENGTH);
	static final MPTag UNIQUE_TAG = new MPTag(UNIQUE_TAG_DATA, KOMA_LENGTH);
	static final MPTag MP_INDIVIDUAL_NUM_TAG = new MPTag(
			MP_INDIVIDUAL_NUM_TAG_DATA, MP_INDIVIDUAL_NUM_LENGTH);
	static final MPTag BASE_VIEWPOINT_NUM_TAG = new MPTag(
			BASE_VIEWPOINT_NUM_TAG_DATA, BASE_VIEWPOINT_NUM_LENGTH);
	static final MPTag CONVERGENCE_ANGLE_TAG = new MPTag(
			CONVERGENCE_ANGLE_TAG_DATA, CONVERGENCE_ANGLE_LENGTH);
	static final MPTag BASELINE_LENGTH_TAG = new MPTag(
			BASELINE_LENGTH_TAG_DATA, BASELINE_LENGTH_LENGTH);

	static final MPTag[] INDEX_TAGS = { VERSION_TAG, NUMBER_OF_IMAGES_TAG,
			ENTRY_TAG, KOMA_TAG, UNIQUE_TAG, MP_INDIVIDUAL_NUM_TAG };
	static final MPTag[] INDIVIDUAL_TAGS = { VERSION_TAG,MP_INDIVIDUAL_NUM_TAG,
			BASE_VIEWPOINT_NUM_TAG, CONVERGENCE_ANGLE_TAG, BASELINE_LENGTH_TAG };

	/**
	 * @param indexIFD
	 * @param attr
	 */
	public MPExtensions(MPIndexFields indexIFD, MPAttributeFields attr) {
		this.indexIFD = indexIFD;
		this.individualIFD = attr;
	}

	public static MPExtensions create(byte[] fileData, int app2) {
		int offsetBase = app2 + ENDIAN_OFFSET_SIZE;
		int indexIFDOffset = getIFDOffset(fileData, offsetBase);

		int pos = indexIFDOffset + offsetBase;
		System.out.printf("indexIFDOffset = %x\n", indexIFDOffset + offsetBase);
		pos += COUNT_LENGTH;
		boolean first = false;

		Map<MPTag, byte[]> tagValues = new HashMap<MPTag, byte[]>();
		// 記録画像数のタグが存在すれば先頭画像
		for (MPTag tag : INDEX_TAGS) {
			if (startsWith(fileData, pos, tag.tagData)) {
				System.out.println(tag);
				byte[] buffer = new byte[tag.dataLength];
				System.arraycopy(fileData, pos, buffer, 0, tag.dataLength);
				tagValues.put(tag, buffer);
				pos += tag.dataLength;
				if (tag == NUMBER_OF_IMAGES_TAG) {
					// 先頭画像
					first = true;
				}
			}
		}
		MPIndexFields indexIFD = null;
		if (first) {
			indexIFD = new MPIndexFields();
			indexIFD.numberOfImages = tagValues.get(NUMBER_OF_IMAGES_TAG);
			indexIFD.entry = tagValues.get(ENTRY_TAG);
			// MPエントリーの解析

			int offsetOfNextIFD = MPOLoader.getInt(fileData, pos);
			pos = offsetOfNextIFD + offsetBase + COUNT_LENGTH;
			System.out.printf("attr head %x\n", pos);
		}
		for (MPTag tag : INDIVIDUAL_TAGS) {
			if (startsWith(fileData, pos, tag.tagData)) {
				byte[] buffer = new byte[tag.dataLength];
				System.arraycopy(fileData, pos, buffer, 0, tag.dataLength);
				tagValues.put(tag, buffer);
				pos += tag.dataLength;
			}

		}

		MPAttributeFields attr = new MPAttributeFields();
		attr.mpIndividualNum = tagValues.get(MP_INDIVIDUAL_NUM_TAG);
		attr.convergenceAngle = tagValues.get(CONVERGENCE_ANGLE_TAG);
		attr.baselineLength = tagValues.get(BASELINE_LENGTH_TAG);

		return new MPExtensions(indexIFD, attr);
	}

	/**
	 * @param buffer
	 * @param offsetBase
	 *            オフセットの基準点
	 * @return
	 */
	private static int getIFDOffset(byte[] buffer, int offsetBase) {
		int offset = 0;
		offset |= (buffer[offsetBase + 4] & 0xff) << 24;
		offset |= (buffer[offsetBase + 4 + 1] & 0xff) << 16;
		offset |= (buffer[offsetBase + 4 + 2] & 0xff) << 8;
		offset |= (buffer[offsetBase + 4 + 3] & 0xff);
		return offset;
	}

	public MPIndexFields getMPIndexIFD() {
		return indexIFD;
	}

	public boolean isFirst() {
		return indexIFD != null;
	}

	static class MPTag {
		byte[] tagData;
		int dataLength;

		MPTag(byte[] tag, int dataLength) {
			this.tagData = tag;
			this.dataLength = dataLength;
		}
	}
}
