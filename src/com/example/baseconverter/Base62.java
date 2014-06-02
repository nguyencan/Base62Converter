package com.example.baseconverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class Base62 {

	private static String Base62CodingSpace = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String BASE62_ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/** default constructor prevents util class from being created. */
	private Base62() {
	}

	/**
	 * 
	 * @param original
	 * @return
	 * @throws IOException
	 */
	public static String toBase62(byte[] original) throws IOException {
		StringBuilder sb = new StringBuilder();
		BitInputStream stream = new BitInputStream(original);
		byte[] read = new byte[1]; // only read 6-bit at a time

		while (stream.hasMore()) {
			int length = stream.readBits(read, 0, 6); // try to read 5 bits
			if (length <= 0) {
				break;
			}
			if ((read[0] >> 1) == 0x1f) { // first 5-bit is 11111
				sb.append(Base62CodingSpace.charAt(61));
				stream.seekBit(-1);
			} else if ((read[0] >> 1) == 0x1e) { // first 5-bit is 11110
				sb.append(Base62CodingSpace.charAt(60));
				stream.seekBit(-1);
			} else {
				sb.append(Base62CodingSpace.charAt(read[0]));
			}
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param base62
	 * @return
	 * @throws IOException
	 */
	public static byte[] fromBase62(String base62) throws IOException {
		ByteArrayOutputStream _out = new ByteArrayOutputStream();
		BitOutputStream out = new BitOutputStream(_out);

		for (int i = 0; i < base62.length(); i++) {
			int index = Base62CodingSpace.indexOf(base62.charAt(i));

			if (i == base62.length() - 1) {
				int mod = out.getiBitCount() % 8;
				if (mod != 0) {
					out.writeBits(index, 8 - mod);
				}
			} else {
				if (index == 60) {
					out.writeBits(0x1e, 5);
				} else if (index == 61) {
					out.writeBits(0xf8, 5);
				} else {
					out.writeBits(index, 6);
				}
			}
		}
		_out.flush();
		return _out.toByteArray();
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {

//			byte[] bytes = { 109, 99, -5, -22, -65, 105, 64, 0, 83, 98, 79,
//					105, 61, 58, 109, -112, -34, -112, -123, -29, 111, 48, -84,
//					-101, -11, 62, -67, -31, -58, -100, 88, -125, 38, -124,
//					-100, 8, 15, 50, -121, -19, 40, 28, -28, 107, -124, -68,
//					116, -32, 72, -52, 87, 105, -73, 25, 28, -124, 84, 61, 118,
//					25, -25, 44, 32, 26, -124, -14, 22, 86, -110, 92, -105,
//					-110, 105, -114, -14, -67, 126, 66, 40, 13, -81, -67, -63,
//					-124, 76, -61, 44, -68, 69, -124, -34, 31, -31, -92, -16,
//					63, -61, 117, -77, -122, 20, 15, -93, -88 };

			StringBuilder sb = new StringBuilder();
			Random r = new Random();

			while (true) {
				sb.append((char) (48 + r.nextInt(47)));
				System.out.println(sb.toString());
				String base62 = Base62.toBase62(sb.toString().getBytes());
				System.out.println(base62);
			}

			// byte[] fromBase62 = Base62.fromBase62(base62);

			// System.out.println(fromBase62);
			// while (true) {
			// String rand = ObjectId.get().toStringMongod();
			// String base62 = Base62.toBase62(rand.getBytes());
			// System.out.println(base62);
			// byte[] fromBase62 = Base62.fromBase62(base62);
			// String string = new String(fromBase62);
			// System.out.println(string);
			// if (!rand.equals(string)) {
			// break;
			// }
			// }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
