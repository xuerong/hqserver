/**create by entityBuilder**/
package hqservice;


import hqservice.entity.*;
import hqio.hqplayer.HQRecordState;
import hqio.hqrequest.HQPlayerInterface;
import java.util.List;


public class Player extends HQPlayerInterface{
	// vars
	private String name;
	private int age;
	private int level;
	private int money;
	private Body body;
	private Home home;
	private Capacity capacity;
	private List<Island> islandList;
	private List<Plant> plantList;
	

	public Player(){
		varChangeMakes.put("island",1);
		varChangeMakes.put("body",1);
		varChangeMakes.put("home",1);
		varChangeMakes.put("plant",1);
		varChangeMakes.put("capacity",1);
		versionNums.put("island",0l);
		versionNums.put("body",0l);
		versionNums.put("home",0l);
		versionNums.put("plant",0l);
		versionNums.put("capacity",0l);
	}
	

	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
		this.pairVarState=(pairVarState==HQRecordState.Identical?HQRecordState.Update:pairVarState);
	}
	public int getAge(){
		return this.age;
	}
	public void setAge(int age){
		this.age=age;
		this.pairVarState=(pairVarState==HQRecordState.Identical?HQRecordState.Update:pairVarState);
	}
	public int getLevel(){
		return this.level;
	}
	public void setLevel(int level){
		this.level=level;
		this.pairVarState=(pairVarState==HQRecordState.Identical?HQRecordState.Update:pairVarState);
	}
	public int getMoney(){
		return this.money;
	}
	public void setMoney(int money){
		this.money=money;
		this.pairVarState=(pairVarState==HQRecordState.Identical?HQRecordState.Update:pairVarState);
	}
	public Body getBody(){
		return this.body;
	}
	public void setBody(Body body){
		this.body=body;
		this.varChangeMakes.put("body",-1);
	}
	public Home getHome(){
		return this.home;
	}
	public void setHome(Home home){
		this.home=home;
		this.varChangeMakes.put("home",-1);
	}
	public Capacity getCapacity(){
		return this.capacity;
	}
	public void setCapacity(Capacity capacity){
		this.capacity=capacity;
		this.varChangeMakes.put("capacity",-1);
	}
	public List<Island> getIslandList(){
		return this.islandList;
	}
	public void setIslandList(List<Island> islandList){
		this.islandList=islandList;
		this.varChangeMakes.put("island",-1);
	}
	public List<Plant> getPlantList(){
		return this.plantList;
	}
	public void setPlantList(List<Plant> plantList){
		this.plantList=plantList;
		this.varChangeMakes.put("plant",-1);
	}
}
