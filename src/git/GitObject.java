package git;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import main.Main;

import org.apache.commons.io.IOUtils;

public abstract class GitObject
{
	protected static String getSourcePath(String commitNum)
	{
		return Main.sourceRepo + "/.git/objects/" + commitNum.substring(0, 2) + "/" + commitNum.substring(2);
	}

	protected static String loadFileFromHash(File f) throws IOException
	{
		final InputStream in = new InflaterInputStream(new FileInputStream(f));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		IOUtils.copy(in, out);
		return out.toString("UTF-8");
	}

	//needs a better name
	public abstract String reSave();
}
