package client

import api.FileClient
import api.FileServer
import api.LockServer
import java.lang.Exception
import java.rmi.Naming
import java.util.*

open class FileClientNoCache : FileClient {
    protected val lockServer = Naming.lookup("rmi://127.0.0.1:12000/lockserver") as LockServer
    protected var fileServer: FileServer? = null
    override var workDir = ""

    override fun ls() : List<String> {
        val id = workDir.hashCode()
        lockServer.acquire(id)
        val files = fileServer!!.ls()
        lockServer.release(id)
        return files
    }

    override fun mount(src: String, des: String) {
        fileServer = Naming.lookup("rmi://$src") as FileServer
        workDir = des
    }

    override fun unmount() {
        fileServer = null
    }

    override fun read(filename: String): ByteArray {
        val id = filename.hashCode()
        lockServer.acquire(id)
        val res = fileServer!!.read(filename)
        println("llala")
        lockServer.release(id)
        return res
    }

    override fun remove(filename: String) {
        val id = filename.hashCode()
        lockServer.acquire(id)
        fileServer!!.remove(filename)
        lockServer.release(id)
    }

    override fun touch(filename: String) {
        val id = filename.hashCode()
        lockServer.acquire(id)
        fileServer!!.touch(filename)
        lockServer.release(id)
    }

    override fun write(filename: String, bytes: ByteArray) {
        val id = filename.hashCode()
        lockServer.acquire(id)
        fileServer!!.write(filename, bytes)
        lockServer.release(id)
    }
}

fun main(args: Array<String>) {
    val client = FileClientNoCache()
    while (true) {
        try {
            print(">>> ")
            client.run(readLine()!!)
        }
        catch (e: Exception) {
            println(e.message)
            break
        }
    }
}