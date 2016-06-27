package hqio.hqrequest;

import java.io.Serializable;
import java.util.List;

import hqio.hqplayer.HQPlayer;

/**
 * 数据请求类
 * **/
public class HQRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	public int requestId;
	public HQRequestType type;
	public Long key;
	public HQPlayer player;
	public List<String> varNames;
	// 其他请求用到的对象
	public Object otherData;
}
