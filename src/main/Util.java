package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util
{
	private static MessageDigest SHA1_DIGEST;

	static
	{
		try
		{
			SHA1_DIGEST = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			System.err.println("No SHA-1 Message Digest Found");
			System.exit(1);
		}
	}

	public static String digest(String s)
	{
		return binToHex(SHA1_DIGEST.digest(s.getBytes()));
	}

	/**
	 * Converts a byte array to its readable string equivalent, useful for the results of hashes performed or converting Tree hash values into usable directory
	 * strings.
	 *
	 * @param b byte array to convert
	 * @return byte array converted to its string "equivalent"
	 */
	public static String binToHex(byte [] b)
	{
		final char [] digits = "0123456789abcdef".toCharArray();
		final StringBuilder result = new StringBuilder();
		for (byte element : b)
		{
			result.append(digits[(element & 0xF0) >>> 4]);
			result.append(digits[element & 0xF]);
		}
		return result.toString();
	}

	public static String asciiToHex(String s)
	{
		return binToHex(s.getBytes());
	}

	public static byte [] hexToBin(String hex)
	{
		if (hex.length() % 2 != 0)
		{
			throw new RuntimeException("Invalid hex length: " + hex.length());
		}

		final byte [] output = new byte [hex.length() >> 1];
		final char [] hchars = hex.toCharArray();

		for (int i = 0, j = 0; j < hchars.length; i++)
		{
			output[i] = (byte) ((Character.digit(hchars[j++], 16) << 4 | Character.digit(hchars[j++], 16)) & 0xFF);
		}

		return output;
	}

	public static String hexToAscii(String ascii)
	{
		return new String(hexToBin(ascii));
	}
}
