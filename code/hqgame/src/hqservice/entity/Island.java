/**create by entityBuilder**/
package hqservice.entity;


import hqio.hqrequest.HQEntityInterface;
import hqio.hqplayer.HQRecordState;


public class Island extends HQEntityInterface{
	private String name;
	private int plantcount;
	private java.util.Date time;

	public Island(){
		super("island");
	}

	public String getName(){
		return this.name;
	}
	public void setName(String name){
		this.name=name;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public int getPlantcount(){
		return this.plantcount;
	}
	public void setPlantcount(int plantcount){
		this.plantcount=plantcount;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public java.util.Date getTime(){
		return this.time;
	}
	public void setTime(java.util.Date time){
		this.time=time;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
}
