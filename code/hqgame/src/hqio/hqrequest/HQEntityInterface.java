package hqio.hqrequest;

import hqio.hqplayer.HQRecordState;
import hqio.hqtableid.HQIdManager;

public abstract class HQEntityInterface {
	protected long id;
	protected long playerId;
	protected HQRecordState state;
	protected boolean isChanged;
	
	public HQEntityInterface(String tableName){
		this.id=HQIdManager.getInstance().getId(tableName);
		this.state=HQRecordState.Add;
		isChanged=false;
	}
	
	public long getId(){
		return id;
	}
	public void setId(long id){
		this.id=id;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public HQRecordState getState() {
		return state;
	}

	public void setState(HQRecordState state) {
		this.state = state;
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
	
}
