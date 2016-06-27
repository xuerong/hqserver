package hqstore;

import hqexceptions.HQManageExceptions;
import hqexceptions.HQThreadNumOverHeap;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * �̳߳�
 * ���̳߳س�ʼͨ������һ��HQStoreTaskInterfaceʵ����Ȼ�����HQStoreTaskInterface��handle����������һ������
 * �̳߳س�ʼ��ʱ������MINTHREADNUM���̣߳������MAXTHREADNUM���̣߳�����ʱ����HQThreadNumOverHeap�쳣
 * 
 * �̳߳�ԭ���������̴߳���wait״̬��������idle�����У�������ʱ���Ӷ����е����̣߳�
 * ��������ָ��HQStoreTaskInterface��notify�̣߳�ִ����ϣ��Żض���
 * �̲߳���������10���������䵽MAXTHREADNUM��
 * **/
public class HQThreadPool {
	
	private static final HQThreadPool pool=new HQThreadPool();
	public static HQThreadPool getInstance(){
		return pool;
	}
	private HQThreadPool(){
		
	}
	
	/**��С���߳�����Ҳ��Ĭ�Ͽ������߳���**/
	private static final int MINTHREADNUM=30;
	/**�����߳���**/
	private static final int MAXTHREADNUM=800;
	/**��ǰ�߳���**/
	private int nowThreadNum=0;
	
	
	private final Queue<HQStoreTaskThread> idleThreads=new ConcurrentLinkedQueue<HQStoreTaskThread>();
	//private final Queue<HQStoreTaskThread> workingThreads=new ConcurrentLinkedQueue<HQStoreTaskThread>();
	
	public boolean init(){
		for(int i=0;i<MINTHREADNUM;i++){
			createTaskThread();
		}
		return true;
	}
	
	public void workTask(HQStoreTaskInterface task){
		// ���������߳�
		if(idleThreads.isEmpty()){
			if(nowThreadNum+10<=MAXTHREADNUM){
				for(int i=0;i<10;i++){
					createTaskThread();
				}
			}else if(nowThreadNum==MAXTHREADNUM){
				try {
					throw new HQThreadNumOverHeap("store task threads is too much , max num = "+MAXTHREADNUM);
				} catch (HQThreadNumOverHeap e) {
					HQManageExceptions.getInstance().manageExceptions(e);
				}
			}else{
				for(int i=0;i<MAXTHREADNUM-nowThreadNum;i++){
					createTaskThread();
				}
			}
		}
		HQStoreTaskThread taskThread=idleThreads.poll();
		taskThread.workTask(task);
	}
	
	private void createTaskThread(){
		HQStoreTaskThread taskThread=new HQStoreTaskThread();
		taskThread.start();
		idleThreads.add(taskThread);
		nowThreadNum++;
	}
	
	
	
	public int getNowThreadNum() {
		return nowThreadNum;
	}
	public Queue<HQStoreTaskThread> getIdleThreads() {
		return idleThreads;
	}



	class HQStoreTaskThread extends Thread{
		private HQStoreTaskInterface task=null;
		public void workTask(HQStoreTaskInterface task){
			this.task=task;
			synchronized (this) {
				notify();
			}
		}
		@Override
		public void run() {
			while(true){
				synchronized (this) {
					try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				task.handle();
				// ������֮�󣬰�����������߳�
				idleThreads.add(this);
			}
		}
	}
}
