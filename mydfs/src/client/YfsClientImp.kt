package client

import api.ExtentServer
import api.LockServer
import api.YfsClient
import java.lang.Exception
import java.rmi.Naming
import java.rmi.RemoteException

class YfsClientImp(private var _workDir: String=""): YfsClient {
    override val workDir: String
        get() = _workDir

    private var _extentServer: ExtentServer? = null
    override val extentServer: ExtentServer
        get() = _extentServer!!

    override val lockServer = Naming.lookup("rmi://127.0.0.1:12000/lockserver") as LockServer

    override fun mount(src: String, dst: String) {
        _extentServer = Naming.lookup("rmi://$src") as ExtentServer
        _workDir = dst
    }

    override fun create(filename: String) {
        try {
            extentServer.get(filename)
            throw Exception("$filename already exists.")
        }
        catch (e: RemoteException) {
            extentServer.put(filename, "".toByteArray())
        }
    }

    override fun ls(): List<String> {
        return extentServer.ls()
    }

    override fun read(filename: String): ByteArray {
        return extentServer.get(filename)
    }

    override fun write(filename: String, bytes: ByteArray) {
        extentServer.put(filename, bytes)
    }
}

fun main(args: Array<String>) {
    val client = YfsClientImp()
    while (true) {
        try {
            print(">>> ")
            val arg = readLine()!!.trim().split(' ')
            when(arg[0]) {
                "mount" -> client.mount(arg[1], arg[2])
                "create" -> client.create(arg[1])
                "ls" -> println(client.ls())
                "read" -> println(String(client.read(arg[1])))
                "write" -> client.write(arg[1], arg[2].toByteArray())
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
