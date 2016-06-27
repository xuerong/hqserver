/**create by entityBuilder**/
package hqservice.entity;


import hqio.hqrequest.HQEntityInterface;
import hqio.hqplayer.HQRecordState;


public class Home extends HQEntityInterface{
	private int peoplenum;

	public Home(){
		super("home");
	}

	public int getPeoplenum(){
		return this.peoplenum;
	}
	public void setPeoplenum(int peoplenum){
		this.peoplenum=peoplenum;
		this.state=(state==HQRecordState.Identical?HQRecordState.Update:state);
		this.isChanged=true;
	}
}
