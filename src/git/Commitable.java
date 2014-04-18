package git;

public abstract class Commitable extends GitObject
{
	protected Commitable(String commitNumber)
	{
		super(commitNumber);
	}

	@Override
	public abstract String reSave();
}
