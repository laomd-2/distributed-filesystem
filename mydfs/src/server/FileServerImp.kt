package server

import api.FileServer
import java.io.File
import java.io.FileNotFoundException

class FileServerImp(port: Int, dir: String): FileServer, RpcServer(port) {
    private val workDir = File(dir)

    init {
        if (!workDir.isDirectory)
            workDir.mkdir()
    }

    override fun ls(): List<String> {
        return workDir.listFiles().map { it.name }
    }

    override fun read(filename: String): ByteArray {
        val file = File(workDir.path, filename)
        if (file.isFile)
            return file.readBytes()
        else
            throw FileNotFoundException("$filename is not a file!")
    }

    override fun remove(filename: String) {
        val file = File(workDir.path, filename)
        if (file.isFile)
            file.delete()
        else
            throw FileNotFoundException("$filename is not a file!")
    }

    override fun touch(filename: String) {
        val file = File(workDir.path, filename)
        if (!file.exists())
            file.createNewFile()
    }

    override fun write(filename: String, bytes: ByteArray) {
        val file = File(workDir.path, filename)
        file.deleteOnExit()
        file.writeBytes(bytes)
    }
}

fun main(args: Array<String>) {
    val s = FileServerImp(12345, "test")
    s.start("fileserver1")
    readLine()
}