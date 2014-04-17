package git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Util;

public class Commit extends GitObject
{
	public static HashMap<String, Commit> oldCommits = new HashMap<String, Commit>();
	//do we need this?
	public static HashMap<Commit, Commit> oldToNewCommits = new HashMap<Commit, Commit>();

	private Tree tree;
	private List<Commit> parents;
	private String footer;

	//@formatter:off
	/**
	 * 	commit 353\0tree 46e5884aceee28ae699ff876ddb97b398fcf8a54
	 *	parent 83a3b8020286330905fa5aec8c28c2ccdbee28ee
	 *	author Todd Klasik <tklasik@gblsys.com> 1397078217 -0700
	 *	committer Todd Klasik <tklasik@gblsys.com> 1397078217 -0700
	 *
	 *	TUCA-298 This actually finishes resolving that issue so sourcedocs are actually displayed (and dont attempt to query access on no sourcedoc)
     *
	 * @param commitNumber
	 */
	//@formatter:on
	private Commit(String commitNumber)
	{
		if (commitNumber.length() != 40)
		{
			System.out.println("Commit: Invalid hash length");
		}
		List<Commit> parents = new ArrayList<Commit>();
		oldCommits.put(commitNumber, this);
		try
		{
			String path = getSourcePath(commitNumber);
			File file = new File(path);

			String commitString = loadFileFromHash(file);
			String [] splitString = commitString.split("\n");

			int startOfHash = commitString.indexOf('\0') + 6;
			tree = new Tree(splitString[0].substring(startOfHash));
			int parentsIndex = 1;
			while (splitString[parentsIndex].startsWith("parent"))
			{
				parents.add(getCommit(splitString[parentsIndex++].substring(7)));
			}

			StringBuilder remainder = new StringBuilder();
			for (int i = parentsIndex; i < splitString.length; i++)
			{
				remainder.append(splitString[i] + "\n");
			}
			footer = remainder.toString();

			System.out.println("Loaded Commit: " + Util.digestObjectFile(file));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static Commit getCommit(String commitNumber)
	{
		if (oldCommits.containsKey(commitNumber))
		{
			return oldCommits.get(commitNumber);
		}
		else
		{
			return new Commit(commitNumber);
		}
	}

	@Override
	public String reSave()
	{
		String newTreeHash = tree.reSave();
		String [] newParentHashes = new String [parents.size()];
		return null;
	}
}
