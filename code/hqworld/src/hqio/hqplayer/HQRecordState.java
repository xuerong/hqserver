package hqio.hqplayer;

/**
 * ���������ݵ�״̬��ָ���������������ݿ������ݵĹ�ϵ��������
 * 1��ͬIdentical��
 * 2�����еĸ���Update��
 * 3���ݿ��в�����Add()��
 * 4���ݿ�������Ҫɾ��Delete��
 * 5δ֪���ݿ����Ƿ����UpdateOrAdd(Ӧ�ò������)
 * ***/

public enum HQRecordState {
	Identical,Add,Update,Delete,UpdateOrAdd
}
