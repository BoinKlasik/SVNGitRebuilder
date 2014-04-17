package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.InflaterInputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

public class Util
{
	private static MessageDigest SHA1_DIGEST;

	static
	{
		try
		{
			SHA1_DIGEST = MessageDigest.getInstance("SHA-1");
		}
		catch (NoSuchAlgorithmException e)
		{
			System.err.println("No SHA-1 Message Digest Found");
			System.exit(1);
		}
	}

	public static String digest(String s)
	{
		return Hex.encodeHexString(SHA1_DIGEST.digest(s.getBytes()));
	}

	public static String digest(ByteArrayOutputStream s)
	{
		return digest(s.toString());
	}

	public static String digestObjectFile(File f) throws IOException
	{
		final InputStream in = new InflaterInputStream(new FileInputStream(f));
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		IOUtils.copy(in, out);

		return Util.digest(out);

	}

	public static boolean validHash(String h)
	{
		try
		{
			return h.matches("[0-9a-f]{40}");
		}
		catch (RuntimeException re)
		{
			return false;
		}
	}
}
