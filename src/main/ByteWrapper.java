package main;

public class ByteWrapper
{
	byte [] theBytes;

	public ByteWrapper(byte [] bytes)
	{
		theBytes = bytes;
	}

	public int indexOf(char ch)
	{
		return indexOf(ch, 0);
	}

	public int indexOf(char ch, int startIndex)
	{
		byte b = (byte) ch;
		for (int i = startIndex; i < theBytes.length; i++)
		{
			if (theBytes[i] == b)
			{
				return i;
			}
		}
		return -1;
	}

	public int getLength()
	{
		return theBytes.length;
	}

	public byte [] subBytes(int start, int end)
	{
		int resultLength = end - start;
		byte [] result = new byte [resultLength];
		for (int i = 0; i < resultLength; i++)
		{
			result[i] = theBytes[i + start];
		}
		return result;
	}
}
