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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

public abstract class GitObject
{
	protected String oldCommitNumber;
	protected String newCommitNumber;
	protected byte [] newHashRaw = null;

	protected GitObject(String commitNumber)
	{
		this.oldCommitNumber = commitNumber;
		if (!Util.validHash(commitNumber))
		{
			throw new RuntimeException("Invalid hash: \'" + commitNumber + "\'");
		}
	}

	public String getNewHash()
	{
		if (newCommitNumber == null)
		{
			newCommitNumber = Hex.encodeHexString(getNewRawHash());
		}
		return newCommitNumber;
	}

	public byte [] getNewRawHash()
	{
		if (newHashRaw == null)
		{
			newHashRaw = reSave();
		}
		return newHashRaw;
	}

	public String getOldHash()
	{
		return oldCommitNumber;
	}

	protected static String getSourcePath(String commitNum)
	{
		return Main.sourceRepo + "/.git/objects/" + commitNum.substring(0, 2) + "/" + commitNum.substring(2);
	}

	protected static String getTargetPath(String hash)
	{
		return Main.targetRepo + "/.git/objects/" + hash.substring(0, 2) + "/" + hash.substring(2);
	}

	protected static String loadFileFromHash(String commitNum) throws IOException
	{
		return new String(loadBytesfromHash(commitNum));
	}

	protected static byte [] loadBytesfromHash(String commitNum) throws IOException
	{
		File f = new File(getSourcePath(commitNum));
		final InputStream in = new InflaterInputStream(new FileInputStream(f));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		IOUtils.copy(in, out);
		in.close();
		out.close();
		return out.toByteArray();
	}

	protected static void saveFileFromHash(String newHash, byte [] contents) throws IOException
	{
		File f = new File(getTargetPath(newHash));
		if (!f.getParentFile().exists() && !f.getParentFile().mkdirs())
		{
			throw new IOException("Unable to create " + f.getParentFile());
		}
		final OutputStream out = new DeflaterOutputStream(new FileOutputStream(f));
		final ByteArrayInputStream in = new ByteArrayInputStream(contents);
		IOUtils.copy(in, out);
		out.close();
		in.close();
	}

	//needs a better name
	protected abstract byte [] reSave();
}
