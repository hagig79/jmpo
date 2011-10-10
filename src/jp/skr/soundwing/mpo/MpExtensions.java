package jp.skr.soundwing.mpo;

public class MpExtensions {
	MpIndexFields indexIFD;
	MpAttributeFields individualIFD;

	/**
	 * @param indexIFD
	 * @param attr
	 */
	public MpExtensions(MpIndexFields indexIFD, MpAttributeFields attr) {
		this.indexIFD = indexIFD;
		this.individualIFD = attr;
	}

	/**
	 * @param fileData
	 * @param app2
	 * @return
	 */
	public static MpExtensions create(byte[] fileData, int offsetBase) {
		System.out.printf("offsetBase %x\n", offsetBase);
		int indexIFDOffset = getIFDOffset(fileData, offsetBase);

		System.out.printf("indexIFDOffset = %x\n", indexIFDOffset + offsetBase);
		MpAttributeFields attr = MpAttributeFields.create(fileData,
				indexIFDOffset + offsetBase, offsetBase);
		return new MpExtensions(null, attr);
	}

	public static MpExtensions createFirst(byte[] fileData, int offsetBase) {

		int indexIFDOffset = getIFDOffset(fileData, offsetBase);
		MpIndexFields indexField = MpIndexFields.create(fileData,
				indexIFDOffset + offsetBase);
		MpAttributeFields attr = MpAttributeFields.create(fileData, offsetBase
				+ indexField.getOffsetOfNextIFD(), offsetBase);
		return new MpExtensions(indexField, attr);
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

	public MpIndexFields getMPIndexIFD() {
		return indexIFD;
	}

	public int getOffsetOfNextIFD() {
		return individualIFD.getOffsetOfNextIFD();
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
