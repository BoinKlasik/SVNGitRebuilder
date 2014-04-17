package main;

import git.Commit;

public class Main
{
	public static String sourceRepo;
	public static String targetRepo;

	/**
	 * @param args
	 */
	public static void main(String [] args)
	{
		if (args.length < 3)
		{
			usage();
			return;
		}
		if (args[0].length() != 40)
		{
			System.out.println("invalid SHA1 hash provided");
		}
		String firstHash = args[0];
		sourceRepo = args[1];
		targetRepo = args[2];
		new Commit(firstHash);
	}

	private static void usage()
	{
		System.out.println("1) Full name of a root commit as an argument.");
		System.out.println("2) Path to source repo");
		System.out.println("3) Path to target repo");
	}

	/**
	 * Converts a byte array to its readable string equivalent, useful for the results of hashes performed or converting Tree hash values into usable directory
	 * strings.
	 * 
	 * @param b byte array to convert
	 * @return byte array converted to its string "equivalent"
	 */
	public static String byteArrayToHexString(byte [] b)
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
}
