package hqio.hqplayer;

import java.io.Serializable;

public class HQPlayerObjectVar  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	/**变量名**/
	private String varName;
	/**表名**/
	private String tableName;
	/**数据**/
	private HQRecord  record;
	/**
	 * 优先级
	 * 缓存根据entity的优先级决定改entity的删除策略（分多级缓存时包括存储策略）
	 * 暂且不用，因为它不被单独调出内存
	 * **/
	private int priorityLevel;
	
	/**版本，根据该版本决定是否修改该数据**/
	private long versionNum=0;
	
	public HQPlayerObjectVar(String varName,String tableName,HQRecord  record){
		this.varName=varName;
		this.tableName=tableName;
		this.record=record;
	}
	// 用HQPlayerVarModel构建HQPlayerObjectVar
	public HQPlayerObjectVar(HQPlayerVarModel playerVarModel){
		this(playerVarModel.getVarName(),playerVarModel.getTableName(),new HQRecord(playerVarModel));
//		if(playerVarModel.getVarType()!=2)
//			throw new ModelErrorException("model is error to create HQPlayerObjectVar , got type "+playerVarModel.getVarType()+
//					",but need type 2");
	}
	
	@Override
	public HQPlayerObjectVar clone() throws CloneNotSupportedException{
		HQPlayerObjectVar o=(HQPlayerObjectVar)super.clone();
		if(record!=null){
			o.record=record.clone();
		}
		return o;
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public HQRecord getRecord() {
		return record;
	}
	public void setRecord(HQRecord record) {
		this.record = record;
	}
	public int getPriorityLevel() {
		return priorityLevel;
	}
	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public long getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(long versionNum) {
		this.versionNum = versionNum;
	}
	
	
}
