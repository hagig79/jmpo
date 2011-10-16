package jp.skr.soundwing.mpo;

import jp.skr.soundwing.exif.ByteArrayReader;
import jp.skr.soundwing.exif.ByteArrayReaderBigEndian;
import jp.skr.soundwing.exif.ByteArrayReaderLittleEndian;

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
		ByteArrayReader reader = createByteArrayReader(fileData, offsetBase);
		int attrIFDOffset = getIFDOffset(reader);
		System.out.println("indexIFDOffset:" + attrIFDOffset);
		reader.skip(attrIFDOffset - 8);

		MpAttributeFields attr = MpAttributeFields.create(reader, offsetBase);
		return new MpExtensions(null, attr);
	}

	public static MpExtensions createFirst(byte[] fileData, int offsetBase) {

		ByteArrayReader reader = createByteArrayReader(fileData, offsetBase);

		int indexIFDOffset = getIFDOffset(reader);
		System.out.println("indexIFDOffset:" + indexIFDOffset);
		reader.skip(indexIFDOffset - 8);

		MpIndexFields indexField = MpIndexFields.create(reader, offsetBase);
		System.out.printf("%x", offsetBase + indexField.getOffsetOfNextIFD());

		reader.setPosition(offsetBase + indexField.getOffsetOfNextIFD());
		MpAttributeFields attr = MpAttributeFields.create(reader, offsetBase);
		return new MpExtensions(indexField, attr);
	}

	private static int getIFDOffset(ByteArrayReader reader) {
		reader.skip(4);
		return reader.getInt();
	}

	private static ByteArrayReader createByteArrayReader(byte[] buffer,
			int offsetBase) {
		if ((buffer[offsetBase] & 0xff) == 0x49) {
			System.out.println("little");
			return new ByteArrayReaderLittleEndian(buffer, offsetBase);
		} else {
			System.out.println("big");
			return new ByteArrayReaderBigEndian(buffer, offsetBase);

		}
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

	public MpEntry getMpEntry(int index) {
		return indexIFD.getMpEntry(index);
	}

	public int getNumberOfMpEntry() {
		return indexIFD.getNumberOfMpEntry();
	}
}
