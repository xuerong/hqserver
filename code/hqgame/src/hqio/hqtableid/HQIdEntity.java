package hqio.hqtableid;

import java.io.Serializable;
/**
 * ÿ��id���������ʵidֵ�������id����
 * ����Ӧ��table����
 * **/
public class HQIdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	public String tableName;
	public Long idStart;
	public int idLength;
}
