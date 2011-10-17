package jp.skr.soundwing.exif;

public class ByteArrayReaderBigEndian extends ByteArrayReader {

	public ByteArrayReaderBigEndian(byte[] buffer, int offset) {
		super(buffer, offset);
	}

	@Override
	public int getInt() {
		int value = ((buffer[current] & 0xff) << 24)
				| ((buffer[current + 1] & 0xff) << 16)
				| ((buffer[current + 2] & 0xff) << 8)
				| ((buffer[current + 3] & 0xff));
		current += 4;
		return value;
	}

	@Override
	public short getShort() {
		short value = (short) (((buffer[current] & 0xff) << 8) | ((buffer[current + 1] & 0xff)));
		current += 2;
		return value;
	}

	@Override
	public boolean startsWith(byte[] subarray) {
		for (int i = 0; i < subarray.length; i++) {
			if (buffer[i + current] != subarray[i]) {
				return false;
			}
		}
		return true;

	}
}
