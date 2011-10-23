package jp.skr.soundwing.mpo;

import jp.skr.soundwing.exif.ByteArrayReader;
import jp.skr.soundwing.exif.ByteArrayReaderBigEndian;
import jp.skr.soundwing.exif.ByteArrayReaderLittleEndian;

public class MpExtension {
	MpIndexFields indexIFD;
	MpAttributeFields individualIFD;

	/**
	 * @param indexIFD
	 * @param attr
	 */
	public MpExtension(MpIndexFields indexIFD, MpAttributeFields attr) {
		this.indexIFD = indexIFD;
		this.individualIFD = attr;
	}

	/**
	 * @param fileData
	 * @param app2
	 * @return
	 */
	public static MpExtension create(byte[] fileData, int offsetBase) {
		ByteArrayReader reader = createByteArrayReader(fileData, offsetBase);
		int attrIFDOffset = getIFDOffset(reader);
		reader.skip(attrIFDOffset - 8);

		MpAttributeFields attr = MpAttributeFields.create(reader, offsetBase);
		return new MpExtension(null, attr);
	}

	public static MpExtension createFirst(byte[] fileData, int offsetBase) {

		ByteArrayReader reader = createByteArrayReader(fileData, offsetBase);

		int indexIFDOffset = getIFDOffset(reader);
		reader.skip(indexIFDOffset - 8);

		MpIndexFields indexField = MpIndexFields.create(reader, offsetBase);

		reader.setPosition(offsetBase + indexField.getOffsetOfNextIFD());
		MpAttributeFields attr = MpAttributeFields.create(reader, offsetBase);
		return new MpExtension(indexField, attr);
	}

	private static int getIFDOffset(ByteArrayReader reader) {
		reader.skip(4);
		return reader.getInt();
	}

	private static ByteArrayReader createByteArrayReader(byte[] buffer,
			int offsetBase) {
		if ((buffer[offsetBase] & 0xff) == 0x49) {
			return new ByteArrayReaderLittleEndian(buffer, offsetBase);
		} else {
			return new ByteArrayReaderBigEndian(buffer, offsetBase);

		}
	}

	public MpIndexFields getMpIndexIfd() {
		return indexIFD;
	}

	public int getOffsetOfNextIFD() {
		return individualIFD.getOffsetOfNextIFD();
	}

	public boolean isFirst() {
		return indexIFD != null;
	}

	public MpEntry getMpEntry(int index) {
		return indexIFD.getMpEntry(index);
	}

	public int getNumberOfMpEntry() {
		return indexIFD.getNumberOfMpEntry();
	}

	public MpAttributeFields getAttributeIfd() {
		return individualIFD;
	}
}
