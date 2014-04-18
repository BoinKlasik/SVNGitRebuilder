package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Main;
import main.Util;

public class Commit extends GitObject
{
	public static HashMap<String, Commit> oldCommits = new HashMap<String, Commit>();
	//do we need this?
	public static HashMap<Commit, Commit> oldToNewCommits = new HashMap<Commit, Commit>();

	private Tree tree;
	private List<Commit> parents;
	private String footer;
	private String newCommitNumber;

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
		super(commitNumber);
		oldCommitNumber = commitNumber;
		parents = new ArrayList<Commit>();
		oldCommits.put(commitNumber, this);
		String commitString;
		try
		{
			commitString = loadFileFromHash(commitNumber);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
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

		System.out.println("Loaded Commit: " + commitNumber);
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
	protected String reSave()
	{
		StringBuilder sb = new StringBuilder();
		String newTreeHash = tree.reSave();
		String [] newParentHashes = new String [parents.size()];
		for (int i = 0; i < parents.size(); i++)
		{
			String string = parents.get(i).getNewHash();
			newParentHashes[i] = string;
		}
		sb.append("tree " + newTreeHash + "\n");
		for (String s : newParentHashes)
		{
			sb.append("parent " + s + "\n");
		}
		sb.append(footer);
		sb.insert(0, "commit " + sb.length() + '\0');
		String newCommitContents = sb.toString();
		String newHash = Util.digest(newCommitContents);
		System.out.println(oldCommitNumber + " -> " + newHash);
		if (Main.leaves.contains(oldCommitNumber))
		{
			System.out.println(newCommitContents);
		}
		//saveFileFromHash(newHash, newCommitContents);
		return newHash;
	}

	@Override
	public String getNewHash()
	{
		if (newCommitNumber == null)
		{
			newCommitNumber = reSave();
		}
		return newCommitNumber;
	}

	@Override
	public String getOldHash()
	{
		return oldCommitNumber;
	}
}
