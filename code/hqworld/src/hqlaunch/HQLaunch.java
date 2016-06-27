package hqlaunch;

import hqdb.HQDBConnectionPool;
import hqdb.HQDbOper;
import hqexceptions.HQManageExceptions;
import hqfile.HQPlayerVarModelReader;
import hqfile.HQWorldProperties;
import hqio.HQIOGame;
import hqio.hqtableid.HQIdManager;
import hqstore.HQSizeof;
import hqstore.HQStorage;
import hqstore.HQThreadPool;

public class HQLaunch {
	
	public static void main(String[] args){
		boolean isSuccess;
		/**��ʼ���쳣������**/
		isSuccess=HQManageExceptions.getInstance().init();
		System.out.println("��ʼ���쳣������"+(isSuccess?" success":" fail"));
		/**��ʼ����ұ���ģ��**/
		isSuccess=HQPlayerVarModelReader.getInstance().init();
		System.out.println("��ʼ����ұ���ģ��"+(isSuccess?" success":" fail"));
		/**����world�����ļ�**/
		isSuccess=HQWorldProperties.getInstance().init();
		System.out.println("����world�����ļ�"+(isSuccess?" success":" fail"));
		/**�������ݿ����ӳ�**/
		isSuccess=HQDBConnectionPool.getInstance().init();
		System.out.println("�������ݿ����ӳ�"+(isSuccess?" success":" fail"));
		/**��ʼ���̳߳�**/
		isSuccess=HQThreadPool.getInstance().init();
		System.out.println("��ʼ���̳߳�"+(isSuccess?" success":" fail"));
		/**�������ݿ��������**/
		isSuccess=HQDbOper.getInstance().init();
		System.out.println("�������ݿ��������"+(isSuccess?" success":" fail"));
		/**��ʼ��id������**/
		isSuccess=HQIdManager.getInstance().init();
		System.out.println("��ʼ��id������"+(isSuccess?" success":" fail"));
		/**���ض����С������**/
		isSuccess=HQSizeof.getInstance().init();
		System.out.println("���ض����С������"+(isSuccess?" success":" fail"));
		/**���ػ����������**/
		isSuccess=HQStorage.getInstance().init();
		System.out.println("���ػ����������"+(isSuccess?" success":" fail"));
		/**����gameͨ���൥��**/
		isSuccess=HQIOGame.getInstance().init();
		System.out.println("����gameͨ���൥��"+(isSuccess?" success":" fail"));
	}
}
class FinalizedEscapeTestCase {
    public static FinalizedEscapeTestCase caseForEscape = null;
    @Override
    protected void finalize() throws Throwable {
       super.finalize();
       System.out.println("�������������ݣ�");
       caseForEscape = this;
    }
}