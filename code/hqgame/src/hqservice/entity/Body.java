/**create by entityBuilder**/
package hqservice.entity;


import hqio.hqrequest.HQEntityInterface;
import hqio.hqplayer.HQRecordState;


public class Body extends HQEntityInterface{
	private String head;
	private String leg;
	private String hand;

	public Body(){
		super("body");
	}

	public String getHead(){
		return this.head;
	}
	public void setHead(String head){
		this.head=head;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public String getLeg(){
		return this.leg;
	}
	public void setLeg(String leg){
		this.leg=leg;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
	public String getHand(){
		return this.hand;
	}
	public void setHand(String hand){
		this.hand=hand;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
}
