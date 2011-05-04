package starcraftbot.proxybot.buildorders;

import starcraftbot.proxybot.khasbot.unitma.Unit;

public class BuildUnit implements BuildOrder{
	private int _uid;
	
	public BuildUnit(int uid){
		_uid = uid;
	}
	public int getID(){
		return _uid;
	}
	public String toString(){
		return "Unit Name: "+Unit.getUnit(_uid).toString();
	}
}
