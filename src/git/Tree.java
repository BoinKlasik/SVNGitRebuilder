package git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import main.ByteWrapper;
import main.Util;

import org.apache.commons.codec.binary.Hex;

public class Tree extends Commitable
{
	public static HashMap<String, Tree> oldTrees = new HashMap<String, Tree>();
	public static final int TREE_MODE = 40000;

	Map<String, Commitable> thingsInTree;

	/*
	 * tree 500\0<mode> <filename>\0hash{20}mode...
	 */
	public Tree(String commitNumber)
	{
		super(commitNumber);
		if (commitNumber.equals("e18de54072dd6ee7f717ca6166ad0a7c51677ea5"))
		{
			System.out.println("lksjaerlyjasdlkjrasd");
		}
		thingsInTree = new LinkedHashMap<>();
		mode = TREE_MODE; //trees are always mode 40000 unless they are commit Trees (which will never reference this number)
		oldTrees.put(commitNumber, this);

		byte [] treeString;
		try
		{
			//gotta load bytes because Strings dont work with the decoded hashes in tree files \o/ encoding
			treeString = loadBytesfromHash(commitNumber);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return;
		}
		String actualTreeString = new String(treeString);
		ByteWrapper byteWrapper = new ByteWrapper(treeString);

		//ignore header, read commitables after that
		int readPosition = byteWrapper.indexOf('\0') + 1;
		int spacePosition;

		while ((spacePosition = byteWrapper.indexOf(' ', readPosition)) != -1 && spacePosition > readPosition)
		{
			Commitable commitable;
			String modeString = new String(byteWrapper.subBytes(readPosition, spacePosition));
			int commitableMode = Integer.valueOf(modeString);
			readPosition = byteWrapper.indexOf('\0', spacePosition);
			String name = new String(byteWrapper.subBytes(spacePosition + 1, readPosition));
			byte [] hashCode = byteWrapper.subBytes(readPosition + 1, readPosition + 21);
			if (commitNumber.equals("e18de54072dd6ee7f717ca6166ad0a7c51677ea5"))
			{
				System.out.println("boogieboo");
			}

			String hashString = new String(Hex.encodeHex(hashCode));
			if (commitableMode == TREE_MODE)
			{
				commitable = getTree(hashString, name);
			}
			else
			{
				commitable = Blob.getBlob(hashString, commitableMode, name);
			}

			readPosition += 21;
			thingsInTree.put(name, commitable);
		}
	}

	public Tree(String commitNumber, String folderName)
	{
		this(commitNumber);
		name = folderName;
	}

	private static final byte [] treeWordBytes = { 't', 'r', 'e', 'e', ' ' };

	@Override
	protected byte [] reSave()
	{
		//tree 500\0<mode> <filename>\0hash{20}mode...
		try
		{
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			for (Entry<String, Commitable> thing : thingsInTree.entrySet())
			{
				//mode<space>name<null>hexHash
				stream.write(Integer.toString(thing.getValue().mode).getBytes());
				stream.write(' ');
				stream.write(thing.getKey().getBytes());
				stream.write(0);
				stream.write(thing.getValue().getNewRawHash());
			}
			//length+tree<space>+length integer+nullbyte
			ByteArrayOutputStream prependStream = new ByteArrayOutputStream(stream.size() + ("tree " + stream.size()).length() + 1);
			prependStream.write(treeWordBytes, 0, treeWordBytes.length);
			prependStream.write(Integer.toString(stream.size()).getBytes());
			prependStream.write(0);
			prependStream.write(stream.toByteArray());
			byte [] finalContents = prependStream.toByteArray();
			byte [] newHash = Util.digestToBytes(finalContents);
			String encodeHexString = Hex.encodeHexString(newHash);
			if (encodeHexString.startsWith("00f61509448678aec"))
			{
				System.out.println("breaker break 10-20 good buddy");
			}
			saveFileFromHash(encodeHexString, finalContents);
			return newHash;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Tree getTree(String commitNumber)
	{
		if (oldTrees.containsKey(commitNumber))
		{
			return oldTrees.get(commitNumber);
		}
		else
		{
			return new Tree(commitNumber);
		}
	}

	public static Tree getTree(String commitNumber, String folderName)
	{
		if (oldTrees.containsKey(commitNumber))
		{
			return oldTrees.get(commitNumber);
		}
		else
		{
			return new Tree(commitNumber, folderName);
		}
	}
}
