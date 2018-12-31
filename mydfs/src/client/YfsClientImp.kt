package client

import api.ExtentClient
import api.LockServer
import api.YfsClient
import api.ScopedLock
import java.lang.Exception
import java.rmi.Naming
import java.rmi.RemoteException

open class YfsClientImp(private var _workDir: String=""): YfsClient {
    override val workDir: String
        get() = _workDir

    private var _extentClient: ExtentClient? = null
    override val extentClient: ExtentClient
        get() = _extentClient!!

    override val lockServer = Naming.lookup("rmi://127.0.0.1:12000/lockserver") as LockServer

    override fun mount(src: String, dst: String) {
        _extentClient = ExtentClientCacheImp("rmi://$src")
        _workDir = dst
    }

    override fun create(filename: String) {
        val lock = ScopedLock(lockServer, extentClient, filename)
        try {
            extentClient.get(filename)
        }
        catch (e: RemoteException) {
            try {
                extentClient.put(filename, "".toByteArray())
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            lock.destroy()
        }
    }

    override fun remove(filename: String) {
        val lock = ScopedLock(lockServer, extentClient, filename)
        try {
            extentClient.remove(filename)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            lock.destroy()
        }
    }

    override fun ls(): List<String> {
        val lock = ScopedLock(lockServer, extentClient, workDir)
        var files = listOf<String>()
        try {
            files = extentClient.ls()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            lock.destroy()
        }
        return files
    }

    override fun read(filename: String): ByteArray {
        val lock = ScopedLock(lockServer, extentClient, filename)
        var bytes = byteArrayOf()
        try {
            bytes = extentClient.get(filename)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            lock.destroy()
        }
        return bytes
    }

    override fun write(filename: String, bytes: ByteArray) {
        val lock = ScopedLock(lockServer, extentClient, filename)
        try {
            extentClient.put(filename, bytes)
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        finally {
            lock.destroy()
        }
    }
}

fun main(args: Array<String>) {
    val client = YfsClientImp()
    loop@ while (true) {
        try {
            print(">>> ")
            val arg = readLine()!!.trim().split(' ')
            when (arg[0]) {
                "mount" -> client.mount(arg[1], arg[2])
                "create" -> client.create(arg[1])
                "ls" -> println(client.ls())
                "read" -> println(String(client.read(arg[1])))
                "write" -> client.write(arg[1], arg[2].toByteArray())
                "remove" -> client.remove(arg[1])
                "exit", "quit" -> break@loop
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
