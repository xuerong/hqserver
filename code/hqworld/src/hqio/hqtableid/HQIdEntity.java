package hqio.hqtableid;

import java.io.Serializable;
/**
 * 每个id请求包括其实id值，给与的id长度
 * 及对应的table名称
 * **/
public class HQIdEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	public String tableName;
	public Long idStart;
	public int idLength;
}
