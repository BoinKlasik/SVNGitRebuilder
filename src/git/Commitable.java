package git;

public abstract class Commitable extends GitObject
{
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
