package hqio.hqrequest;

import hqio.hqplayer.HQPlayer;

import java.io.Serializable;

public class HQResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	public int reponseId;
	public HQRequestType type;
	public int result;
	public Long key;
	public HQPlayer player;
	public Object otherData;
}
