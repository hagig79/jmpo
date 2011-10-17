package jp.skr.soundwing.exif;

/**
 * バイト配列を読み込むを補助するクラス.
 * 
 * @author mudwell
 * 
 */
public abstract class ByteArrayReader {
	byte[] buffer;
	int startOffset;
	int current;

	public ByteArrayReader(byte[] buffer, int offset) {
		this.buffer = buffer;
		this.startOffset = offset;
		current = startOffset;
	}

	public abstract int getInt();

	public abstract short getShort();

	public void skip(int n) {
		if (current + n >= buffer.length) {
			throw new IllegalArgumentException();
		}
		current += n;
	}

	public byte[] getBytes() {
		return buffer;
	}

	public int getPosition() {
		return current;
	}

	public void setPosition(int newPosition) {
		this.current = newPosition;
	}

	public abstract boolean startsWith(byte[] subarray);

	public void arraycopy(byte[] dest, int destPos, int length) {
		System.arraycopy(buffer, current, dest, destPos, length);
		current += length;
	}
}
