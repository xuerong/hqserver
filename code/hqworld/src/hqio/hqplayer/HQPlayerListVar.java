package hqio.hqplayer;

import hqexceptions.HQModelErrorException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HQPlayerListVar  implements Serializable,Cloneable{
	private static final long serialVersionUID = 1L;
	
	/**默认的优先级**/
	private static final int INITPRIORITYLEVEL=1;
	/**变量名**/
	private String varName;
	/**表名**/
	private String tableName;
	/**
	 * 数据
	 * size=0，说明没有数据
	 * size>0，说明有数据
	 * null，说明被调进内存
	 * **/
	private List<HQRecord>  recordList;
	/**
	 * 优先级
	 * 缓存根据entity的优先级决定改entity的删除策略（分多级缓存时包括存储策略）
	 * **/
	private int priorityLevel;
	/**版本，根据该版本决定是否修改该数据**/
	private long versionNum=0;
	
	/**构造函数在构造的时候初始化recordList数据等，player登陆时，从数据库中取数据调用此构造函数***/
	public HQPlayerListVar(String varName,String tableName,List<HQRecord> recordList, int priorityLevel){
		this.varName=varName;
		this.tableName=tableName;
		this.recordList=recordList;
		this.priorityLevel=priorityLevel;
	}
	/**构造函数在构造的时候初始化recordList为空等,player创建时，调用此构造函数***/
	public HQPlayerListVar(String varName,String tableName) {
		this(varName,tableName,new ArrayList<HQRecord>(),INITPRIORITYLEVEL);
	}
	/**构造函数在构造的时候初始化recordList为空等,player创建时，调用此构造函数***/
	public HQPlayerListVar(String varName,String tableName,int priorityLevel) {
		this(varName,tableName,new ArrayList<HQRecord>(),priorityLevel);
	}
	// 用HQPlayerVarModel构建HQPlayerListVar
	public HQPlayerListVar(HQPlayerVarModel playerVarModel) throws HQModelErrorException{
		this(playerVarModel.getVarName(),playerVarModel.getTableName(),new ArrayList<HQRecord>(),INITPRIORITYLEVEL);
		if(playerVarModel.getVarType()!=1)
			throw new HQModelErrorException("model is error to create HQPlayerListVar , got type "+playerVarModel.getVarType()+
					",but need type 1");
	}
	
	/**克隆的时候，忽略掉已经删除的**/
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
