package api

import java.util.concurrent.locks.Lock

typealias xid_t = Long
typealias lockid_t = Int
enum class StatusCode { OK, RETRY, RPCERR, NOENT, IOERR}

class ConditionVariable(lock: Lock) {
    enum class Status {FREE, LOCKED}
    var status = Status.FREE
    val condition = lock.newCondition()!!
}