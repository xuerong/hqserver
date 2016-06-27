package hqio.hqplayer;

import hqexceptions.HQModelErrorException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HQPlayerListVar  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	
	/**Ĭ�ϵ����ȼ�**/
	private static final int INITPRIORITYLEVEL=1;
	/**������**/
	private String varName;
	/**����**/
	private String tableName;
	/**
	 * ����
	 * size=0��˵��û������
	 * size>0��˵��������
	 * null��˵���������ڴ�
	 * **/
	private List<HQRecord>  recordList;
	/**
	 * ���ȼ�
	 * �������entity�����ȼ�������entity��ɾ�����ԣ��ֶ༶����ʱ�����洢���ԣ�
	 * **/
	private int priorityLevel;
	/**�汾�����ݸð汾�����Ƿ��޸ĸ�����**/
	private long versionNum=0;
	
	/**���캯���ڹ����ʱ���ʼ��recordList���ݵȣ�player��½ʱ�������ݿ���ȡ���ݵ��ô˹��캯��***/
	public HQPlayerListVar(String varName,String tableName,List<HQRecord> recordList, int priorityLevel){
		this.varName=varName;
		this.tableName=tableName;
		this.recordList=recordList;
		this.priorityLevel=priorityLevel;
	}
	/**���캯���ڹ����ʱ���ʼ��recordListΪ�յ�,player����ʱ�����ô˹��캯��***/
	public HQPlayerListVar(String varName,String tableName) {
		this(varName,tableName,new ArrayList<HQRecord>(),INITPRIORITYLEVEL);
	}
	/**���캯���ڹ����ʱ���ʼ��recordListΪ�յ�,player����ʱ�����ô˹��캯��***/
	public HQPlayerListVar(String varName,String tableName,int priorityLevel) {
		this(varName,tableName,new ArrayList<HQRecord>(),priorityLevel);
	}
	// ��HQPlayerVarModel����HQPlayerListVar
	public HQPlayerListVar(HQPlayerVarModel playerVarModel) throws HQModelErrorException{
		this(playerVarModel.getVarName(),playerVarModel.getTableName(),new ArrayList<HQRecord>(),INITPRIORITYLEVEL);
		if(playerVarModel.getVarType()!=1)
			throw new HQModelErrorException("model is error to create HQPlayerListVar , got type "+playerVarModel.getVarType()+
					",but need type 1");
	}
	
	/**��¡��ʱ�򣬺��Ե��Ѿ�ɾ����**/
	@Override
	public HQPlayerListVar clone() throws CloneNotSupportedException{
		HQPlayerListVar o=(HQPlayerListVar)super.clone();
		if(recordList!=null){
			o.recordList=new ArrayList<HQRecord>();
			for (HQRecord record : recordList) {
				if(record.getRecordState()!=HQRecordState.Delete)
					o.recordList.add(record.clone());
			}
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
	
	public List<HQRecord> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<HQRecord> recordList) {
		this.recordList = recordList;
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
