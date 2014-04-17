package git;

public class Tree extends GitObject
{

	public Tree(String substring)
	{
		if (substring.length() != 40)
		{
			System.out.println("Invalid hash");
		}
		System.out.println("Tree: " + substring);
	}

	@Override
	public String reSave()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
