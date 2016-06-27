package hqio.hqplayer;

import java.io.Serializable;

/**
 * 数据
 * entity中存储着该对象在数据库中的表示key（列名），valueType（值类型），value（值）
 * 根据值类型，将值转化成对应的类型，然后存入数据库
 * **/
public class HQPair  implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	public String key;
	public HQValueType valueType;
	public Object value;
	
	@Override
	public HQPair clone() throws CloneNotSupportedException{
		HQPair o = (HQPair) super.clone();  
        return o;
	}
	
}