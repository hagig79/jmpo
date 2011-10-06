package jp.skr.soundwing.ctr;

import jp.skr.soundwing.mpo.MpoLoader;

public class MakerNoteReader {
	
	public static byte[] readMakerNote(byte[] buffer, int offset) {
		int tag = findMakerNoteTag(buffer, offset);
		if(tag < 0) {
			return null;
		}
		int size = MpoLoader.getInt(buffer, tag + 2 + 2);
		int offsetBase = findOffsetBase(buffer, offset);
		int dataOffset = MpoLoader.getInt(buffer, tag + 2 + 2 + 4);
		byte[] result = new byte[size];
		System.arraycopy(buffer, offsetBase + dataOffset, result, 0, size);
		return result;
	}
	
	private static int findOffsetBase(byte[] buffer, int offset) {
		for (int i = offset; i < buffer.length; i++) {
			if (((buffer[i] & 0xff) == 0xff)
					&& ((buffer[i + 1] & 0xff) == 0xe1)
					&& ((buffer[i + 4] & 0xff) == 'E')
					&& ((buffer[i + 5] & 0xff) == 'x')
					&& ((buffer[i + 6] & 0xff) == 'i')
					&& ((buffer[i + 7] & 0xff) == 'f')) {
				return i + 10;
			}
		}
		return -1;
	}

	private static int findMakerNoteTag(byte[] buffer, int offset) {

		for (int i = offset; i < buffer.length; i++) {
			if (((buffer[i] & 0xff) == 0x92)
					&& ((buffer[i + 1] & 0xff) == 0x7c)) {
				return i;
			}
		}
		return -1;
	}
}
