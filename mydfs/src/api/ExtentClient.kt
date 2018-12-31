package api

interface ExtentClient {
    val extentServer: ExtentServer
    
    fun ls(): List<String>

    fun put(filename: String, buffer: ByteArray)

    fun get(filename: String): ByteArray

    fun remove(filename: String)

    fun flush(filename: String) { }
}