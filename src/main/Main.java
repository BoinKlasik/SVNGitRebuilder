package main;

import git.Blob;
import git.Commit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

public class Main
{
	public static String sourceRepo;
	public static String targetRepo;
	public static Map<String, String> leaves;

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
		leaves = new HashMap<>();
		for (String line : lines)
		{
			//tags dont hold up the git tree
			if (line.contains("tags"))
			{
				continue;
			}
			String hash = line.substring(0, 40);
			String branchName = line.substring(line.indexOf('\t') + 1);
			if (!leaves.containsKey(branchName))
			{
				leaves.put(branchName, hash);
			}
		}
		System.out.println(leaves);
		try
		{
			for (String hash : leaves.values())
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
		for (Entry<String, String> hash : leaves.entrySet())
		{
			System.out.println(hash.getKey() + " : " + Commit.getCommit(hash.getValue()).getNewHash());
			writeNewRefFile(hash.getKey(), hash.getValue());
		}
		System.out.println("binary files: " + Blob.binaryExtensions);
		System.out.println("known files: " + Blob.knownExtensions);
	}

	private static void writeNewRefFile(String path, String hash) throws IOException
	{
		File f = new File(targetRepo + "/.git/" + path);
		if (!f.getParentFile().exists() && !f.getParentFile().mkdirs())
		{
			throw new IOException("Unable to create " + f.getParentFile());
		}
		FileUtils.write(f, hash);
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
