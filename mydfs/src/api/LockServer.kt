package api

import java.rmi.Remote
import java.rmi.RemoteException

interface LockServer: Remote {
    @Throws(RemoteException::class)
    fun acquire(lock_id: lockid_t): StatusCode

    @Throws(RemoteException::class)
    fun release(lock_id: lockid_t): StatusCode
}