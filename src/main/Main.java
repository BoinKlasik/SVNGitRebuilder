package main;

import git.Blob;
import git.Commit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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

		getSourceRepoLeaves();

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
			writeNewRefFile(hash.getKey(), Commit.getCommit(hash.getValue()).getNewHash());
		}
		System.out.println("binary files: " + Blob.binaryExtensions);
		System.out.println("known files: " + Blob.knownExtensions);
		saveProperties();
	}

	private static void saveProperties() throws IOException
	{
		FileOutputStream stream = new FileOutputStream("extensions.prop");
		Properties prop = new Properties();
		for (String known : Blob.knownExtensions)
		{
			String type = "ascii";
			if (Blob.binaryExtensions.contains(known))
			{
				type = "binary";
			}
			prop.put(known, type);
		}
		prop.store(stream, "Known GitRebuilder file extensions.");
	}

	/**
	 * reads the sourcerepo directory and calls "git update-server-info" which pulls the info/refs file which contains an easy listing of all branches currently
	 * in the system. This file is processed for everything that isnt a tag.
	 * 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private static void getSourceRepoLeaves() throws InterruptedException, IOException
	{
		Runtime time = Runtime.getRuntime();
		//this ensures that .git/info/refs file exists (easier than traversing the whole damn .git/refs folder
		time.exec("git update-server-info", null, new File(sourceRepo)).waitFor();

		List<String> lines = FileUtils.readLines(new File(sourceRepo + "/.git/info/refs"), "UTF-8");
		leaves = new HashMap<>();
		for (String line : lines)
		{
			//tags dont hold up the git tree

			String hash = line.substring(0, 40);
			String branchName = line.substring(line.indexOf('\t') + 1);
			if (line.contains("remote"))
			{
				line = line.replace("remotes", "heads/remotes");
			}
			if (!leaves.containsKey(branchName))
			{
				leaves.put(branchName, hash);
			}
		}
		System.out.println(leaves);
	}

	/**
	 * This method writes all the old branches out to your .git/refs/<whatever> folder allowing git to "see" all the new branches that were created.
	 * 
	 * @param path
	 * @param hash
	 * @throws IOException
	 */
	private static void writeNewRefFile(String path, String hash) throws IOException
	{
		//whacky "tag commit" name endings.
		if (path.endsWith("^{}"))
		{
			return;
		}
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
