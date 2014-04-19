package git;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import main.Util;

import org.apache.commons.codec.binary.Hex;

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
			if (!knownExtensions.contains(fileExtension))
			{
				askAboutExtension(fileExtension);
				knownExtensions.add(fileExtension);
			}
			byte [] resultBytes;
			if (binaryExtensions.contains(fileExtension))
			{
				resultBytes = loadBytesfromHash(oldCommitNumber);
			}
			else
			{
				String fileLines = loadFileFromHash(oldCommitNumber);
				fileLines.replace("\r\n", "\n");
				resultBytes = fileLines.getBytes();
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

	private void askAboutExtension(String fileExtension)
	{
		int result = JOptionPane.showConfirmDialog(null, "Is " + fileExtension + " (" + name + ") a binary file?");
		if (result == JOptionPane.YES_OPTION)
		{
			binaryExtensions.add(fileExtension);
		}
	}
}
