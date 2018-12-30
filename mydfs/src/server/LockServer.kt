package server

import java.rmi.Remote
import java.rmi.RemoteException

interface LockServer: Remote {
    @Throws(RemoteException::class)
    fun acquire(lock_id: Int)

    @Throws(RemoteException::class)
    fun release(lock_id: Int)
}