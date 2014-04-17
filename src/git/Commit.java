package git;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import main.Main;
import main.Util;

public class Commit
{
	public static HashMap<String, Commit> oldCommits = new HashMap<String, Commit>();
	public static HashMap<Commit, Commit> oldToNewCommits = new HashMap<Commit, Commit>();

	private Tree tree;
	private List<Commit> parents;
	private String author, committer, comment;

	public Commit(String commitNumber)
	{
		try
		{
			String path = Main.sourceRepo + "/.git/objects/" + commitNumber.substring(0, 2) + "/" + commitNumber.substring(2);
			File file = new File(path);

			System.out.println(Util.digestObjectFile(file));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
