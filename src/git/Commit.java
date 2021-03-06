package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Main;
import main.Util;

import org.apache.commons.codec.binary.Hex;

public class Commit extends GitObject
{
	public static HashMap<String, Commit> oldCommits = new HashMap<String, Commit>();
	//do we need this?
	public static HashMap<Commit, Commit> oldToNewCommits = new HashMap<Commit, Commit>();

	private GitObject object;
	private List<Commit> parents;
	private String footer;
	private String treeOrObject;

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

		int startOfTreeOrObject = commitString.indexOf('\0');
		int startOfHash = commitString.indexOf(' ', startOfTreeOrObject);
		treeOrObject = commitString.substring(startOfTreeOrObject + 1, startOfHash);
		String treeOrObjectHash = splitString[0].substring(startOfHash + 1);
		if (treeOrObject.equals("tree"))
		{
			object = Tree.getTree(treeOrObjectHash);
		}
		else if (treeOrObject.equals("object"))
		{
			object = Commit.getCommit(treeOrObjectHash);
		}
		else
		{
			throw new RuntimeException("Commit did not contain a tree or an object");
		}
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
	protected byte [] reSave()
	{
		StringBuilder sb = new StringBuilder();
		String newTreeHash = object.getNewHash();
		String [] newParentHashes = new String [parents.size()];
		for (int i = 0; i < parents.size(); i++)
		{
			try
			{
				newParentHashes[i] = parents.get(i).getNewHash();
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
		sb.append(treeOrObject + " " + newTreeHash + "\n");
		for (String s : newParentHashes)
		{
			sb.append("parent " + s + "\n");
		}
		sb.append(footer);
		//TODO: actually make a Tag class for gods sake.
		if (treeOrObject.equals("object"))
		{
			sb.insert(0, "tag " + sb.length() + '\0');
		}
		else
		{
			sb.insert(0, "commit " + sb.length() + '\0');
		}
		String newCommitContents = sb.toString();
		byte [] newHash = Util.digestToBytes(newCommitContents.getBytes());
		System.out.println(oldCommitNumber + " -> " + Hex.encodeHexString(newHash));
		if (Main.leaves.containsValue(oldCommitNumber))
		{
			System.out.println(newCommitContents);
		}
		try
		{
			saveFileFromHash(Hex.encodeHexString(newHash), newCommitContents.getBytes());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return newHash;
	}
}
