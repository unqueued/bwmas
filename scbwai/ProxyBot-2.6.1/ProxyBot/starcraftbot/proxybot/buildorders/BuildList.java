package starcraftbot.proxybot.buildorders;

import java.io.*;
import java.util.*;



public class BuildList implements Serializable{
	private int supply;
	private LinkedList<BuildOrder> orders = null;
	private String versus = null;
	public BuildList(){
		supply = 0;
		orders = new LinkedList<BuildOrder>();
		versus = "default";
	}
	public BuildList(int n){
		supply = n;
		orders = new LinkedList<BuildOrder>();
		versus = "default";
	}
	public BuildList(int n, String v){
		supply = n;
		orders = new LinkedList<BuildOrder>();
		versus = v;
	}
	public void addOrder(BuildOrder order){
		orders.add(order);
	}
	public String getVersus(){
		return versus;
	}
	public int getSupply(){
		return supply;
	}
	public int getLength(){
		return orders.size();
	}
	public LinkedList<BuildOrder> getList(){
		return orders;
	}
	public String toString(){
		return "Order:"+orders.size();
	}
}
