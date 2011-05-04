package starcraftbot.proxybot.buildorders;

import java.io.Serializable;
import starcraftbot.proxybot.khasbot.unitma.Unit;

public class BuildStructure implements BuildOrder, Serializable {
	private int _sid;
	private String _location;
	public BuildStructure(int sid, String location){
		_sid = sid;
		_location = location;
	}
	public int getID(){
		return _sid;
	}
	public String getLocation(){
		return _location;
	}
	public String toString(){
		return getLocation();
	}
}
