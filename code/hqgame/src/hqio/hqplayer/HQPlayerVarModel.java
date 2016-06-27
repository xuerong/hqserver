package hqio.hqplayer;

/**
 * player�ı����Ľṹ��ÿ��������Ӧһ��HQPlayerVarModel���������ļ���ȡ����������
 * ���������������ͣ�1�б�2object��3(player table name)�������������������б������������б������ȼ�
 * **/

public class HQPlayerVarModel {
	private String varName;
	private short varType;
	private String tableName;
	private String[] columnNames;
	private HQValueType[] columnTypes;
	private int priorityLevel;
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	
	
	public short getVarType() {
		return varType;
	}
	public void setVarType(short varType) {
		this.varType = varType;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	
	public HQValueType[] getColumnTypes() {
		return columnTypes;
	}
	public void setColumnTypes(HQValueType[] columnTypes) {
		this.columnTypes = columnTypes;
	}
	public int getPriorityLevel() {
		return priorityLevel;
	}
	public void setPriorityLevel(int priorityLevel) {
		this.priorityLevel = priorityLevel;
	}
	
	@Override
	public boolean equals(Object object){
		return ((HQPlayerVarModel)object).varName.equals(this.varName);
	}
}
