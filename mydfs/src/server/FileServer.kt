package server

import java.rmi.Remote
import java.rmi.RemoteException

interface FileServer : Remote {
    @Throws(RemoteException::class)
    fun read(filename: String) : ByteArray

    @Throws(RemoteException::class)
    fun write(filename: String, bytes: ByteArray)

    @Throws(RemoteException::class)
    fun ls() : List<String>

    @Throws(RemoteException::class)
    fun touch(filename: String)

    @Throws(RemoteException::class)
    fun remove(filename: String)
}