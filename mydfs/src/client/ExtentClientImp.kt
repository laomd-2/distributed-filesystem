package client

import api.ExtentClient
import api.ExtentServer
import java.rmi.Naming

open class ExtentClientImp(serverUrl: String): ExtentClient {
    private val _extentServer = Naming.lookup(serverUrl) as ExtentServer
    override val extentServer: ExtentServer
        get() = _extentServer

    override fun ls(): List<String> {
        return extentServer.ls()
    }

    override fun put(filename: String, buffer: ByteArray) {
        extentServer.put(filename, buffer)
    }

    override fun get(filename: String): ByteArray {
        return extentServer.get(filename)
    }

    override fun remove(filename: String) {
        extentServer.remove(filename)
    }
}