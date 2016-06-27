package hqio.hqrequest;

import hqio.hqplayer.HQRecordState;

import java.util.HashMap;

public abstract class HQPlayerInterface {
	protected long id;
	protected long pairVarVersionNum;
	protected HQRecordState pairVarState;
	// 表示变量是否改变:通过set变化的为-1，未变化的：
	// 普通类型：为1，变化通过set捕捉改为-1
	// object类型：为1，变化通过set捕捉改为-1，否则通过对应对象内的isChanged捕捉
	// list类型：为变化前的size，变化通过set捕捉改为-1，否则通过当前size和变化前的size比较，或通过对应对象内的isChanged捕捉
	protected HashMap<String, Integer> varChangeMakes=new HashMap<String, Integer>();
	// 版本记录，用这个记录当前各个变量的版本
	// 这将决定，到了world的变量是否更新成功
	//    只有是varChangeMakes标记的变量才会发送到world
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
