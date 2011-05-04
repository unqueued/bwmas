package starcraftbot.proxybot.buildorders;

import java.io.Serializable;
import starcraftbot.proxybot.khasbot.unitma.Unit;

public class BuildUnit implements BuildOrder, Serializable{
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
