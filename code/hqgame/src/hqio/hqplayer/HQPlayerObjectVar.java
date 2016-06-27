package hqio.hqplayer;

import java.io.Serializable;

public class HQPlayerObjectVar  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	/**������**/
	private String varName;
	/**����**/
	private String tableName;
	/**����**/
	private HQRecord  record;
	/**
	 * ���ȼ�
	 * �������entity�����ȼ�������entity��ɾ�����ԣ��ֶ༶����ʱ�����洢���ԣ�
	 * ���Ҳ��ã���Ϊ���������������ڴ�
	 * **/
	private int priorityLevel;
	
	/**�汾�����ݸð汾�����Ƿ��޸ĸ�����**/
	private long versionNum=0;
	
	public HQPlayerObjectVar(String varName,String tableName,HQRecord  record){
		this.varName=varName;
		this.tableName=tableName;
		this.record=record;
	}
	// ��HQPlayerVarModel����HQPlayerObjectVar
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
