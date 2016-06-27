package hqstore;
/**
 * 每个与数据库的同步都是一个HQStoreTaskInterface，然后交给线程池处理
 * **/
public interface HQStoreTaskInterface {
	public void handle();
}
