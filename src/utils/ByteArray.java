package utils;

import java.nio.ByteBuffer;

public class ByteArray {
	public static byte[] sum(byte[] array1, byte[] array2) {
		ByteBuffer buffer = ByteBuffer.allocate(array1.length + array2.length);
		buffer.put(array1);
		buffer.put(array2);
		return buffer.array();
	}
}
