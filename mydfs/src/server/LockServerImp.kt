package server

import api.ConditionVariable
import api.LockServer
import api.StatusCode
import api.lockid_t

import java.util.concurrent.locks.ReentrantLock

class LockServerImp(port: Int) : LockServer, RpcServer(port) {
    private val lockMap = hashMapOf<lockid_t, ConditionVariable>()
    private val mutex = ReentrantLock()

    override fun acquire(lock_id: lockid_t): StatusCode {
        mutex.lock()
        if (!lockMap.containsKey(lock_id))
            lockMap[lock_id] = ConditionVariable(mutex)
        val lock = lockMap[lock_id]
        while (lock?.status != ConditionVariable.Status.FREE)
            lock?.condition?.await()
        lock.status = ConditionVariable.Status.LOCKED
        mutex.unlock()
        return StatusCode.OK
    }

    override fun release(lock_id: lockid_t): StatusCode {
        mutex.lock()
        val lock = lockMap.getOrDefault(lock_id, null)
        return if (lock == null) {
            mutex.unlock()
            StatusCode.IOERR
        } else {
            lock.status = ConditionVariable.Status.FREE
            lock.condition.signal()
            mutex.unlock()
            StatusCode.OK
        }
    }
}

fun main(args: Array<String>) {
    val s = LockServerImp(12000)
    s.start("lockserver")
    readLine()
}