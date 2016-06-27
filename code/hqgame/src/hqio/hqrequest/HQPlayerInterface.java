package hqio.hqrequest;

import hqio.hqplayer.HQRecordState;

import java.util.HashMap;

public abstract class HQPlayerInterface {
	protected long id;
	protected long pairVarVersionNum;
	protected HQRecordState pairVarState;
	// ��ʾ�����Ƿ�ı�:ͨ��set�仯��Ϊ-1��δ�仯�ģ�
	// ��ͨ���ͣ�Ϊ1���仯ͨ��set��׽��Ϊ-1
	// object���ͣ�Ϊ1���仯ͨ��set��׽��Ϊ-1������ͨ����Ӧ�����ڵ�isChanged��׽
	// list���ͣ�Ϊ�仯ǰ��size���仯ͨ��set��׽��Ϊ-1������ͨ����ǰsize�ͱ仯ǰ��size�Ƚϣ���ͨ����Ӧ�����ڵ�isChanged��׽
	protected HashMap<String, Integer> varChangeMakes=new HashMap<String, Integer>();
	// �汾��¼���������¼��ǰ���������İ汾
	// �⽫����������world�ı����Ƿ���³ɹ�
	//    ֻ����varChangeMakes��ǵı����Żᷢ�͵�world
	protected HashMap<String, Long> versionNums=new HashMap<String,Long>();
	
	public HQPlayerInterface(){
		pairVarVersionNum=0;
		pairVarState=HQRecordState.Add;
	}
	
	public HashMap<String, Integer> getVarChangeMakes(){
		return varChangeMakes;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public HQRecordState getPairVarState() {
		return pairVarState;
	}
	public void setPairVarState(HQRecordState pairVarState) {
		this.pairVarState = pairVarState;
	}
	public long getPairVarVersionNum() {
		return pairVarVersionNum;
	}
	public void setPairVarVersionNum(long pairVarVersionNum) {
		this.pairVarVersionNum = pairVarVersionNum;
	}
	public HashMap<String, Long> getVersionNums() {
		return versionNums;
	}
	public void setVersionNums(HashMap<String, Long> versionNums) {
		this.versionNums = versionNums;
	}
	
	
}
