package client

import server.FileServer
import server.LockServer
import java.io.File
import java.lang.Exception
import java.rmi.Naming

fun main(args: Array<String>) {
    val lock = Naming.lookup("rmi://127.0.0.1:12000/hello") as LockServer
    var fileServer : FileServer? = null
    var workDir = ""

    input@ while (true) {
        try {
            print(">>>")
            val cmd = readLine()?.trim()?.split(' ')!!
            when (cmd[0]) {
                "mount" -> {
                    fileServer = Naming.lookup("rmi://${cmd[1]}") as FileServer
                    workDir = cmd[2]
                }
                "read" -> {
                    val file = File(workDir, if (cmd.size > 2) cmd[2] else cmd[1])
                    val id = cmd[1].hashCode()
                    lock.acquire(id)
                    file.writeBytes(fileServer?.read(cmd[1])!!)
                    lock.release(id)
                }
                "write" -> {
                    val filename = cmd[1]
                    val id = filename.hashCode()
                    lock.acquire(id)
                    fileServer!!.write(filename, cmd[2].toByteArray())
                    lock.release(id)
                }
                "ls" -> {
                    val id = workDir.hashCode()
                    lock.acquire(id)
                    println(fileServer!!.ls())
                    lock.release(id)
                }
                "touch" -> {
                    val filename = cmd[1]
                    val id = filename.hashCode()
                    lock.acquire(id)
                    fileServer!!.touch(filename)
                    lock.release(id)
                }
                "remove" -> {
                    val filename = cmd[1]
                    val id = filename.hashCode()
                    lock.acquire(id)
                    fileServer!!.remove(filename)
                    lock.release(id)
                }
                "exit" -> {
                    break@input
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}