package api

import java.rmi.Remote
import java.rmi.RemoteException
import java.util.concurrent.locks.Lock

interface ExtentServer : Remote {
    
    @Throws(RemoteException::class)
    fun ls(): List<String>

    @Throws(RemoteException::class)
    fun put(filename: String, buffer: ByteArray)

    @Throws(RemoteException::class)
    fun get(filename: String): ByteArray

    @Throws(RemoteException::class)
    fun remove(filename: String)
}