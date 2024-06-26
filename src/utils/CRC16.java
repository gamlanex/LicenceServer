package utils;


/**
 * This class is designed to calculate CRC checksum for a byte array data
 * 
 * @author cyw
 **/

public class CRC16 {
	// Table of combining numbers to quickly calculate a 16-bit CRC using
	// the CCITT polynomial as the generating function.
	private static final short crc_ccitt[] = { (short) 0x0000, (short) 0x1189,
			(short) 0x2312, (short) 0x329B, (short) 0x4624, (short) 0x57AD,
			(short) 0x6536, (short) 0x74BF,

			(short) 0x8C48, (short) 0x9DC1, (short) 0xAF5A, (short) 0xBED3,
			(short) 0xCA6C, (short) 0xDBE5, (short) 0xE97E, (short) 0xF8F7,

			(short) 0x1081, (short) 0x0108, (short) 0x3393, (short) 0x221A,
			(short) 0x56A5, (short) 0x472C, (short) 0x75B7, (short) 0x643E,

			(short) 0x9CC9, (short) 0x8D40, (short) 0xBFDB, (short) 0xAE52,
			(short) 0xDAED, (short) 0xCB64, (short) 0xF9FF, (short) 0xE876,

			(short) 0x2102, (short) 0x308B, (short) 0x0210, (short) 0x1399,
			(short) 0x6726, (short) 0x76AF, (short) 0x4434, (short) 0x55BD,

			(short) 0xAD4A, (short) 0xBCC3, (short) 0x8E58, (short) 0x9FD1,
			(short) 0xEB6E, (short) 0xFAE7, (short) 0xC87C, (short) 0xD9F5,

			(short) 0x3183, (short) 0x200A, (short) 0x1291, (short) 0x0318,
			(short) 0x77A7, (short) 0x662E, (short) 0x54B5, (short) 0x453C,

			(short) 0xBDCB, (short) 0xAC42, (short) 0x9ED9, (short) 0x8F50,
			(short) 0xFBEF, (short) 0xEA66, (short) 0xD8FD, (short) 0xC974,

			(short) 0x4204, (short) 0x538D, (short) 0x6116, (short) 0x709F,
			(short) 0x0420, (short) 0x15A9, (short) 0x2732, (short) 0x36BB,

			(short) 0xCE4C, (short) 0xDFC5, (short) 0xED5E, (short) 0xFCD7,
			(short) 0x8868, (short) 0x99E1, (short) 0xAB7A, (short) 0xBAF3,

			(short) 0x5285, (short) 0x430C, (short) 0x7197, (short) 0x601E,
			(short) 0x14A1, (short) 0x0528, (short) 0x37B3, (short) 0x263A,

			(short) 0xDECD, (short) 0xCF44, (short) 0xFDDF, (short) 0xEC56,
			(short) 0x98E9, (short) 0x8960, (short) 0xBBFB, (short) 0xAA72,

			(short) 0x6306, (short) 0x728F, (short) 0x4014, (short) 0x519D,
			(short) 0x2522, (short) 0x34AB, (short) 0x0630, (short) 0x17B9,

			(short) 0xEF4E, (short) 0xFEC7, (short) 0xCC5C, (short) 0xDDD5,
			(short) 0xA96A, (short) 0xB8E3, (short) 0x8A78, (short) 0x9BF1,

			(short) 0x7387, (short) 0x620E, (short) 0x5095, (short) 0x411C,
			(short) 0x35A3, (short) 0x242A, (short) 0x16B1, (short) 0x0738,

			(short) 0xFFCF, (short) 0xEE46, (short) 0xDCDD, (short) 0xCD54,
			(short) 0xB9EB, (short) 0xA862, (short) 0x9AF9, (short) 0x8B70,

			(short) 0x8408, (short) 0x9581, (short) 0xA71A, (short) 0xB693,
			(short) 0xC22C, (short) 0xD3A5, (short) 0xE13E, (short) 0xF0B7,

			(short) 0x0840, (short) 0x19C9, (short) 0x2B52, (short) 0x3ADB,
			(short) 0x4E64, (short) 0x5FED, (short) 0x6D76, (short) 0x7CFF,

			(short) 0x9489, (short) 0x8500, (short) 0xB79B, (short) 0xA612,
			(short) 0xD2AD, (short) 0xC324, (short) 0xF1BF, (short) 0xE036,

			(short) 0x18C1, (short) 0x0948, (short) 0x3BD3, (short) 0x2A5A,
			(short) 0x5EE5, (short) 0x4F6C, (short) 0x7DF7, (short) 0x6C7E,

			(short) 0xA50A, (short) 0xB483, (short) 0x8618, (short) 0x9791,
			(short) 0xE32E, (short) 0xF2A7, (short) 0xC03C, (short) 0xD1B5,

			(short) 0x2942, (short) 0x38CB, (short) 0x0A50, (short) 0x1BD9,
			(short) 0x6F66, (short) 0x7EEF, (short) 0x4C74, (short) 0x5DFD,

			(short) 0xB58B, (short) 0xA402, (short) 0x9699, (short) 0x8710,
			(short) 0xF3AF, (short) 0xE226, (short) 0xD0BD, (short) 0xC134,

			(short) 0x39C3, (short) 0x284A, (short) 0x1AD1, (short) 0x0B58,
			(short) 0x7FE7, (short) 0x6E6E, (short) 0x5CF5, (short) 0x4D7C,

			(short) 0xC60C, (short) 0xD785, (short) 0xE51E, (short) 0xF497,
			(short) 0x8028, (short) 0x91A1, (short) 0xA33A, (short) 0xB2B3,

			(short) 0x4A44, (short) 0x5BCD, (short) 0x6956, (short) 0x78DF,
			(short) 0x0C60, (short) 0x1DE9, (short) 0x2F72, (short) 0x3EFB,

			(short) 0xD68D, (short) 0xC704, (short) 0xF59F, (short) 0xE416,
			(short) 0x90A9, (short) 0x8120, (short) 0xB3BB, (short) 0xA232,

			(short) 0x5AC5, (short) 0x4B4C, (short) 0x79D7, (short) 0x685E,
			(short) 0x1CE1, (short) 0x0D68, (short) 0x3FF3, (short) 0x2E7A,

			(short) 0xE70E, (short) 0xF687, (short) 0xC41C, (short) 0xD595,
			(short) 0xA12A, (short) 0xB0A3, (short) 0x8238, (short) 0x93B1,

			(short) 0x6B46, (short) 0x7ACF, (short) 0x4854, (short) 0x59DD,
			(short) 0x2D62, (short) 0x3CEB, (short) 0x0E70, (short) 0x1FF9,

			(short) 0xF78F, (short) 0xE606, (short) 0xD49D, (short) 0xC514,
			(short) 0xB1AB, (short) 0xA022, (short) 0x92B9, (short) 0x8330,

			(short) 0x7BC7, (short) 0x6A4E, (short) 0x58D5, (short) 0x495C,
			(short) 0x3DE3, (short) 0x2C6A, (short) 0x1EF1, (short) 0x0F78

	};

	/**
	 * Calculate the two byte CRC checksum for a byte array
	 * 
	 * @param buf
	 *            A byte array to be checksummed
	 **/
	public static short calcChecksum(byte[] buf) {
		return calcChecksum(buf, 0, buf.length - 1);
	}

	/**
	 * Calculate the two byte CRC checksum for a byte array from a given offset
	 * to the end of the array
	 **/
	public static short calcChecksum(byte[] buf, int start_offset) {
		return calcChecksum(buf, start_offset, buf.length - 1);
	}

	/**
	 * Calculate the two byte CRC checksum for a byte array from a starting
	 * offset to an ending offset
	 **/
	public static short calcChecksum(byte[] buf, int start_offset, int end_offset) {
		int indx;
		byte t1, t2, t4;
		short t3;
		short accum = 0;

		if (end_offset > buf.length - 1)
			end_offset = buf.length - 1;

		for (indx = start_offset; indx <= end_offset; indx++) {
			t4 = buf[indx];
			t1 = (byte) ((accum ^ t4) & 0xFF);
			t2 = (byte) ((accum >> 8) & 0xFF);
			t3 = crc_ccitt[(t1 & 0xFF)];
			accum = (short) ((t2 & 0xFF) ^ t3);
		}

		return accum;
	}

	public static short calcChecksum(byte[] buf, int start_offset, int end_offset, short accum) {
		int indx;
		byte t1, t2, t4;
		short t3;

		if (end_offset > buf.length - 1)
			end_offset = buf.length - 1;

		for (indx = start_offset; indx <= end_offset; indx++) {
			t4 = buf[indx];
			t1 = (byte) ((accum ^ t4) & 0xFF);
			t2 = (byte) ((accum >> 8) & 0xFF);
			t3 = crc_ccitt[(t1 & 0xFF)];
			accum = (short) ((t2 & 0xFF) ^ t3);
		}
		
		return accum;
	}
}