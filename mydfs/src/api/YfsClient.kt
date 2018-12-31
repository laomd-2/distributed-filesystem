package api

import java.io.File
import java.lang.Exception
import java.rmi.RemoteException

interface YfsClient {
    val workDir: String
    val extentServer: ExtentServer
    val lockServer: LockServer

    fun mount(src: String, dst: String)
    fun create(filename: String)
    fun ls(): List<String>
    fun read(filename: String): ByteArray
    fun write(filename: String, bytes: ByteArray)
}