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
		/**初始化异常处理单例**/
		isSuccess=HQManageExceptions.getInstance().init();
		System.out.println("初始化异常处理单例"+(isSuccess?" success":" fail"));
		/**初始化玩家变量模板**/
		isSuccess=HQPlayerVarModelReader.getInstance().init();
		System.out.println("初始化玩家变量模板"+(isSuccess?" success":" fail"));
		/**加载world配置文件**/
		isSuccess=HQWorldProperties.getInstance().init();
		System.out.println("加载world配置文件"+(isSuccess?" success":" fail"));
		/**加载数据库连接池**/
		isSuccess=HQDBConnectionPool.getInstance().init();
		System.out.println("加载数据库连接池"+(isSuccess?" success":" fail"));
		/**初始化线程池**/
		isSuccess=HQThreadPool.getInstance().init();
		System.out.println("初始化线程池"+(isSuccess?" success":" fail"));
		/**加载数据库操作单例**/
		isSuccess=HQDbOper.getInstance().init();
		System.out.println("加载数据库操作单例"+(isSuccess?" success":" fail"));
		/**初始化id管理器**/
		isSuccess=HQIdManager.getInstance().init();
		System.out.println("初始化id管理器"+(isSuccess?" success":" fail"));
		/**加载对象大小计算器**/
		isSuccess=HQSizeof.getInstance().init();
		System.out.println("加载对象大小计算器"+(isSuccess?" success":" fail"));
		/**加载缓存操作单例**/
		isSuccess=HQStorage.getInstance().init();
		System.out.println("加载缓存操作单例"+(isSuccess?" success":" fail"));
		/**加载game通信类单例**/
		isSuccess=HQIOGame.getInstance().init();
		System.out.println("加载game通信类单例"+(isSuccess?" success":" fail"));
	}
}
class FinalizedEscapeTestCase {
    public static FinalizedEscapeTestCase caseForEscape = null;
    @Override
    protected void finalize() throws Throwable {
       super.finalize();
       System.out.println("哈哈，我已逃逸！");
       caseForEscape = this;
    }
}