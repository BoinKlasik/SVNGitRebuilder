package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import main.Util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

public class Blob extends Commitable
{
	public static HashMap<String, Blob> oldBlobs = new HashMap<>();
	public static List<String> binaryExtensions = new ArrayList<>();
	public static List<String> knownExtensions = new ArrayList<>();

	private Blob(String commitNumber, int mode, String fileName)
	{
		super(commitNumber);
		this.mode = mode;
		this.name = fileName;
		oldBlobs.put(commitNumber, this);
	}

	public static Blob getBlob(String commitNumber, int mode, String fileName)
	{
		if (!oldBlobs.containsKey(commitNumber))
		{
			return new Blob(commitNumber, mode, fileName);
		}
		else
		{
			return oldBlobs.get(commitNumber);
		}
	}

	private String getFileExtension()
	{
		int lastIndexOf = name.lastIndexOf('.');
		if (lastIndexOf != -1)
		{
			return name.substring(lastIndexOf);
		}
		return name;
	}

	@Override
	protected byte [] reSave()
	{
		try
		{
			String fileExtension = getFileExtension();
			byte [] resultBytes;
			resultBytes = loadBytesfromHash(oldCommitNumber);
			if (!knownExtensions.contains(fileExtension))
			{
				int nullIndex = ArrayUtils.indexOf(resultBytes, (byte) 0) + 1;
				askAboutExtension(fileExtension, new String(resultBytes, nullIndex, resultBytes.length >= 300 + nullIndex ? 300 : resultBytes.length
						- nullIndex));
				knownExtensions.add(fileExtension);
			}
			boolean backupBinaryCheck = gitLikeBinaryCheck(resultBytes);
			if (!binaryExtensions.contains(fileExtension))
			{
				if (backupBinaryCheck)
				{
					System.out.println("Binary check match FAIL: expected FALSE was TRUE " + name);
				}
				String fileLines = new String(resultBytes);
				fileLines = fileLines.substring(fileLines.indexOf('\0') + 1);
				fileLines = fileLines.replace("\r\n", "\n");
				String headerPlus = "blob " + fileLines.length() + "\0" + fileLines;
				resultBytes = headerPlus.getBytes();
			}
			else
			{
				if (!backupBinaryCheck)
				{
					System.out.println("Binary check match FAIL: expected TRUE was FALSE " + name);
				}
			}
			byte [] newHash = Util.digestToBytes(resultBytes);
			saveFileFromHash(Hex.encodeHexString(newHash), resultBytes);
			return newHash;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Attempts to determine if a file is binary based on the presense of a null character in the first 8k characters. Since we pass the entire blob in
	 * including headers, we ignore the first null we find. based on: <code>
	 * #define FIRST_FEW_BYTES 8000
	 * int buffer_is_binary(const char *ptr, unsigned long size)
	 * {
	 * 	if (FIRST_FEW_BYTES < size)
	 * 		size = FIRST_FEW_BYTES;
	 * 	return !!memchr(ptr, 0, size);
	 * }
	 * </code>
	 * 
	 * @formatter:
	 * @param contents
	 * @return true if file contains a null chracter past the first, false otherwise
	 */
	private boolean gitLikeBinaryCheck(byte [] contents)
	{
		//return ArrayUtils.indexOf(contents, (byte) 0, ArrayUtils.indexOf(contents, (byte) 0) + 1) != -1;
		boolean firstNull = false;
		int length = contents.length < 8000 ? contents.length : 8000;
		for (int i = 0; i < length; i++)
		{
			if (contents[i] == 0)
			{
				if (firstNull)
				{
					return true;
				}
				else
				{
					firstNull = true;
				}
			}
		}
		return false;
	}

	private void askAboutExtension(String fileExtension, String contents)
	{
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("Is %s (%s) a binary file?\r\n", fileExtension, name));
		builder.append("\"");
		for (int i = 0; i < contents.length(); i += 100)
		{
			int endOfLine = contents.length() < i + 100 ? contents.length() : i + 100;
			builder.append(contents.substring(i, endOfLine) + "\r\n");
		}
		builder.append("\"");
		int result = JOptionPane.showConfirmDialog(null, builder.toString(), "Binary File?", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION)
		{
			binaryExtensions.add(fileExtension);
		}
	}
}
