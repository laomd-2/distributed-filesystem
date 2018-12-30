package server

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class ConditionVariable(lock: Lock) {
    enum class Status {
        FREE, LOCKED
    }
    var status = Status.FREE
    val condition = lock.newCondition()!!
}

class LockServerImp(port: Int) : LockServer, RpcServer(port) {
    private val lockMap = hashMapOf<Int, ConditionVariable>()
    private val mutex = ReentrantLock()

    override fun acquire(lock_id: Int) {
        mutex.lock()
        if (!lockMap.containsKey(lock_id))
            lockMap[lock_id] = ConditionVariable(mutex)
        val lock = lockMap[lock_id]
        while (lock?.status != ConditionVariable.Status.FREE)
            lock?.condition?.await()
        lock.status = ConditionVariable.Status.LOCKED
        mutex.unlock()
    }

    override fun release(lock_id: Int) {
        val lock = lockMap[lock_id]
        mutex.lock()
        lock?.status = ConditionVariable.Status.FREE
        lock?.condition?.signal()
        mutex.unlock()
    }
}

fun main(args: Array<String>) {
    val s = LockServerImp(12000)
    s.start("hello")
    readLine()
}