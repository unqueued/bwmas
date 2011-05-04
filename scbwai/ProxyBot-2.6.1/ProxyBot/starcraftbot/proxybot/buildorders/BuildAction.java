package starcraftbot.proxybot.buildorders;

public class BuildAction implements BuildOrder{
	private String _act;
	private String _location;
	private int _uid;
	private boolean _expendable;
	public BuildAction(String act, int uid, String location, boolean expendable){
		_act = act;
		_uid = uid;
		_location = location;
		_expendable = expendable;
	}
	public int getID(){
		return _uid;
	}
	public String getAction(){
		return _act;
	}
	public String getLocation(){
		return _location;
	}
	public boolean getLives(){
		return _expendable;
	}
	public String toString(){
		return "Action Type: "+_act;
	}
}
