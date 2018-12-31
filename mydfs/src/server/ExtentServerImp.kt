package server

import api.ExtentServer
import java.io.File

class ExtentServerImp(port: Int, dir: String): ExtentServer, RpcServer(port) {
    private val workDir = File(dir)

    init {
        if (!workDir.isDirectory)
            workDir.mkdir()
    }

    override fun ls(): List<String> {
        return workDir.listFiles().map { it.name }
    }

    override fun put(filename: String, buffer: ByteArray) {
        val file = File(workDir.path, filename)
        file.deleteOnExit()
        file.writeBytes(buffer)
    }

    override fun get(filename: String): ByteArray {
        val file = File(workDir.path, filename)
        return file.readBytes()
    }

    override fun remove(filename: String) {
        val file = File(workDir.path, filename)
        file.delete()
    }
}

fun main(args: Array<String>) {
    val s = ExtentServerImp(12345, "test")
    s.start("fileserver1")
    readLine()
}