package hqio.hqplayer;

import java.io.Serializable;

/**
 * ����
 * entity�д洢�Ÿö��������ݿ��еı�ʾkey����������valueType��ֵ���ͣ���value��ֵ��
 * ����ֵ���ͣ���ֵת���ɶ�Ӧ�����ͣ�Ȼ��������ݿ�
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