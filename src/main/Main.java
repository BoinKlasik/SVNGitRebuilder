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

		if (!Util.validHash(args[0]))
		{
			System.out.println("invalid SHA1 hash provided");
			return;
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
}
