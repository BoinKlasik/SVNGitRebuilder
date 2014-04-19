package main;

import git.Blob;
import git.Commit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class Main
{
	public static String sourceRepo;
	public static String targetRepo;
	public static List<String> leaves;

	/**
	 * git update-server-info is required to ennsure .git/info/refs file exists
	 * 
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String [] args) throws IOException, InterruptedException
	{
		if (args.length < 2)
		{
			usage();
			return;
		}

		sourceRepo = args[0];
		targetRepo = args[1];

		if (args.length > 2)
		{
			loadKnownExtensions(args[2]);
		}

		Runtime time = Runtime.getRuntime();
		//this ensures that .git/info/refs file exists (easier than traversing the whole damn .git/refs folder
		time.exec("git update-server-info", null, new File(sourceRepo)).waitFor();

		List<String> lines = FileUtils.readLines(new File(sourceRepo + "/.git/info/refs"), "UTF-8");
		leaves = new ArrayList<>();
		for (String line : lines)
		{
			//tags dont hold up the git tree
			if (line.contains("tags"))
			{
				continue;
			}
			String hash = line.substring(0, 40);
			if (!leaves.contains(hash))
			{
				leaves.add(hash);
			}
		}
		System.out.println(leaves);
		try
		{
			for (String hash : leaves)
			{
				String newHash = Commit.getCommit(hash).getNewHash();
				System.out.println(newHash);
			}
			System.out.println("Loaded " + Commit.oldCommits.size() + " commits total");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("binary files: " + Blob.binaryExtensions);
		System.out.println("known files: " + Blob.knownExtensions);
	}

	private static void loadKnownExtensions(String propertiesFile)
	{
		final File f = new File(propertiesFile);
		final Properties props = new Properties();

		try (InputStream in = new FileInputStream(f))
		{
			props.load(in);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
			return;
		}

		for (Entry<Object, Object> entry : props.entrySet())
		{
			Blob.knownExtensions.add(entry.getKey().toString());

			if (entry.getValue().equals("binary"))
			{
				Blob.binaryExtensions.add(entry.getKey().toString());
			}
		}
	}

	private static void usage()
	{
		System.out.println("1) Path to source repo");
		System.out.println("2) Path to target repo");
	}
}
