package git;

public abstract class Commitable extends GitObject
{
	/**
	 * WARNING: this is NOT the final name of the file the Tree uses! This is simply used by getfileextension (im not sure why I have it in commitable at this
	 * point) for asking the user if a file is binary.
	 */
	protected String name;
	protected int mode;

	protected Commitable(String commitNumber)
	{
		super(commitNumber);
	}

	public String getName()
	{
		return name;
	}

	public int getMode()
	{
		return mode;
	}

	@Override
	protected abstract byte [] reSave();
}
