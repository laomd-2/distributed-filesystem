package api

import java.io.File
import java.lang.Exception
import java.rmi.RemoteException

interface FileClient {
    var workDir: String
    fun read(filename: String) : ByteArray
    fun write(filename: String, bytes: ByteArray)
    fun ls() : List<String>
    fun touch(filename: String)
    fun remove(filename: String)
    fun mount(src: String, des: String)
    fun unmount()

    fun run(cmd: String) {
        val args = cmd.trim().split(' ')
        when (args[0]) {
            "mount" -> mount(args[1], args[2])
            "unmount" -> unmount()
            "read" -> {
                val file = File(workDir, if (args.size > 2) args[2] else args[1])
                file.deleteOnExit()
                file.writeBytes(read(args[1]))
            }
            "write" -> write(args[1], args[2].toByteArray())
            "ls" -> print(ls())
            "touch" -> touch(args[1])
            "remove" -> remove(args[1])
            "exit" -> throw Exception("Goodbye")
        }
    }
}