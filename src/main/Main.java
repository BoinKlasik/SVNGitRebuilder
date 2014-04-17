package main;

import git.Commit;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class Main
{
	public static String sourceRepo;
	public static String targetRepo;

	/**
	 * git update-server-info is required to ennsure .git/info/refs file exists
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String [] args) throws IOException, InterruptedException
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

		Runtime time = Runtime.getRuntime();
		//this ensures that .git/info/refs file exists (easier than traversing the whole damn .git/refs folder
		time.exec("git update-server-info", null, new File(sourceRepo)).waitFor();

		List<String> lines = FileUtils.readLines(new File(sourceRepo + ".git/info/refs"), "UTF-8");

		Commit.getCommit(firstHash);
		System.out.println("Loaded " + Commit.oldCommits.size() + " commits total");
	}

	private static void usage()
	{
		System.out.println("1) Full name of a root commit as an argument.");
		System.out.println("2) Path to source repo");
		System.out.println("3) Path to target repo");
	}
}
