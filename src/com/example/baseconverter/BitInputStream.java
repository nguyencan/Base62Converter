package com.example.baseconverter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 
 * @author can.nm
 * 
 */
public class BitInputStream {

	private byte[] buffer;
	private int length;
	private int byteOffset;
	private int bitOffset;

	/**
	 * 
	 * @param bytes
	 */
	public BitInputStream(byte[] bytes) {
		this.buffer = bytes;
		this.length = bytes.length;
		this.byteOffset = 0;
		this.bitOffset = 0;
	}

	/**
	 * 
	 * @return
	 */
	synchronized public boolean hasMore() {
		return ((byteOffset * 8) + bitOffset) < (length * 8);
	}

	/**
	 * 
	 * @param nbit
	 * @return
	 */
	synchronized public int seekBit(int pos) {
		int nbyte = (bitOffset + pos) / 8;
		int nbit = (bitOffset + pos) % 8;

		if (nbit < 0 || (byteOffset + nbyte) < 0
				|| (((byteOffset + nbyte) * 8) + nbit) > (length * 8)) {
			throw new IndexOutOfBoundsException();
		}

		bitOffset = nbit;
		byteOffset += nbyte;

		return pos > 0 ? pos : pos * -1;
	}

	/**
	 * Just for UnitTest
	 * 
	 * @param args
	 */
	public static void main2(String[] args) {
		byte[] bytes = { 67, 68 };

		BitInputStream in = new BitInputStream(bytes);
		int bit = in.seekBit(17);

		System.out.println("seek: " + bit);
		System.out.println(in.readBit());
	}

	/**
	 * 
	 * @param out
	 * @param length
	 * @return
	 * @throws IOException
	 */
	synchronized public int readBits(byte[] out, int offset, int length) {
		if (offset < 0 || (out.length - offset) * 8 < length
				|| out.length * 8 < length || length >= 32) {
			throw new IndexOutOfBoundsException();
		}

		int bitCount = 0;
		int byteIndex = offset;

		for (int i = length - 1; i >= 0;) {
			if (!hasMore()) {
				return bitCount;
			}

			int count = 0;
			int value = 0;

			do {
				int bit = readBit();
				if (bit == -1) {
					break;
				}
				value |= bit << count;
				count++;
				bitCount++;
				i--;
			} while (i >= 0 && count < 8);

			out[byteIndex++] = (byte) (value & 0xff);
		}

		return bitCount;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	synchronized public int readBit() {
		if (!hasMore()) {
			return -1;
		}
		if (bitOffset == 8) {
			byteOffset++;
			bitOffset = 0;
		}
		int bit = buffer[byteOffset] & (1 << bitOffset++);
		return bit > 0 ? 1 : 0;
	}

	/**
	 * UnitTest
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		byte[] bytes = { 109, 99, -5, -22, -65, 105, 64, 0, 83, 98, 79, 105,
				61, 58, 109, -112, -34, -112, -123, -29, 111, 48, -84, -101,
				-11, 62, -67, -31, -58, -100, 88, -125, 38, -124, -100, 8, 15,
				50, -121, -19, 40, 28, -28, 107, -124, -68, 116, -32, 72, -52,
				87, 105, -73, 25, 28, -124, 84, 61, 118, 25, -25, 44, 32, 26,
				-124, -14, 22, 86, -110, 92, -105, -110, 105, -114, -14, -67,
				126, 66, 40, 13, -81, -67, -63, -124, 76, -61, 44, -68, 69,
				-124, -34, 31, -31, -92, -16, 63, -61, 117, -77, -122, 20, 15,
				-93, -88 };

		BitInputStream in = new BitInputStream(bytes);
		ByteArrayOutputStream _out = new ByteArrayOutputStream();
		BitOutputStream out = new BitOutputStream(_out);

		while (true) {
			try {
				byte[] b = new byte[4];
				int readBits = in.readBits(b, 0, 6);
				out.writeBits(b[0], readBits);
				if (readBits < 6) {
					System.out.println(readBits + " - " + b[0]);
					break;
				}
				System.out.println(readBits + " - " + b[0]);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}

		System.out.println(_out.toByteArray());
	}
}
