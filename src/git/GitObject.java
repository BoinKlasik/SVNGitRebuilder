package git;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import main.Main;
import main.Util;

import org.apache.commons.io.IOUtils;

public abstract class GitObject
{
	protected String oldCommitNumber;

	protected GitObject(String commitNumber)
	{
		this.oldCommitNumber = commitNumber;
		if (!Util.validHash(commitNumber))
		{
			System.out.println("Invalid hash: " + commitNumber);
		}
	}

	protected static String getSourcePath(String commitNum)
	{
		return Main.sourceRepo + "/.git/objects/" + commitNum.substring(0, 2) + "/" + commitNum.substring(2);
	}

	protected static String getTargetPAth(String hash)
	{
		return Main.targetRepo + "/.git/objects/" + hash.substring(0, 2) + "/" + hash.substring(2);
	}

	protected static String loadFileFromHash(String commitNum) throws IOException
	{
		File f = new File(getSourcePath(commitNum));
		final InputStream in = new InflaterInputStream(new FileInputStream(f));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		IOUtils.copy(in, out);
		in.close();
		out.close();
		return out.toString("UTF-8");
	}

	protected static void saveFileFromHash(String newHash, String contents) throws IOException
	{
		File f = new File(getSourcePath(newHash));
		final OutputStream out = new DeflaterOutputStream(new FileOutputStream(f));
		final ByteArrayInputStream in = new ByteArrayInputStream(contents.getBytes());
		IOUtils.copy(in, out);
		out.close();
		in.close();
	}

	//needs a better name
	protected abstract String reSave();

	public abstract String getNewHash();

	public abstract String getOldHash();
}
