package hqio.hqplayer;

/**
 * 缓存中数据的状态，指缓存中数据与数据库中数据的关系，包括：
 * 1相同Identical，
 * 2缓存中的更新Update，
 * 3数据库中不存在Add()，
 * 4数据库中有需要删除Delete，
 * 5未知数据库中是否存在UpdateOrAdd(应该不会存在)
 * ***/

public enum HQRecordState {
	Identical,Add,Update,Delete,UpdateOrAdd
}
