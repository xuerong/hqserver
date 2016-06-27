/**create by entityBuilder**/
package hqservice.entity;


import hqio.hqrequest.HQEntityInterface;
import hqio.hqplayer.HQRecordState;


public class Capacity extends HQEntityInterface{
	private int eat;
	private int run;
	private String hobby;

	public Capacity(){
		super("capacity");
	}

	public int getEat(){
		return this.eat;
	}
	public void setEat(int eat){
		this.eat=eat;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public int getRun(){
		return this.run;
	}
	public void setRun(int run){
		this.run=run;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public String getHobby(){
		return this.hobby;
	}
	public void setHobby(String hobby){
		this.hobby=hobby;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
}
