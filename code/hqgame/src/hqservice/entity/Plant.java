/**create by entityBuilder**/
package hqservice.entity;


import hqio.hqrequest.HQEntityInterface;
import hqio.hqplayer.HQRecordState;


public class Plant extends HQEntityInterface{
	private String name;
	private int level;

	public Plant(){
		super("plant");
	}

	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public int getLevel(){
		return this.level;
	}
	public void setLevel(int level){
		this.level=level;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
}
