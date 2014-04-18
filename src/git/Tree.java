package git;

import java.util.List;

public class Tree extends Commitable
{
	List<Commitable> thingsInTree;

	public Tree(String commitNumber)
	{
		super(commitNumber);
		System.out.println("Tree: " + commitNumber);
	}

	@Override
	public String reSave()
	{
		// TODO Auto-generated method stub
		return "0";
	}

	@Override
	public String getNewHash()
	{
		// TODO Auto-generated method stub
		return "0";
	}

	@Override
	public String getOldHash()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
