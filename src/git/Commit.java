package git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.InflaterInputStream;

import main.Main;

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
			InputStream in = new InflaterInputStream(new FileInputStream(new File(path)));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte [] buffer = new byte [1000];
			int len;
			while ((len = in.read(buffer)) > 0)
			{
				out.write(buffer, 0, len);
			}
			System.out.println(out.toString("UTF-8"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
