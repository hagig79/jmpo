package jp.skr.soundwing.ctr;

import jp.skr.soundwing.mpo.MPOLoader;

public class MakerNoteReader {
	
	public byte[] readMakerNote(byte[] buffer, int offset) {
		int tag = findMakerNoteTag(buffer, offset);
		if(tag < 0) {
			return null;
		}
		int size = MPOLoader.getInt(buffer, tag + 2 + 2);
//		int offsetBase = findOffsetBase();
		int dataOffset = MPOLoader.getInt(buffer, tag + 2 + 2 + 4);
		byte[] result = new byte[size];
		System.arraycopy(buffer, dataOffset, result, 0, size);
		return result;
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
