package api

interface YfsClient {
    val workDir: String
    val extentClient: ExtentClient
    val lockServer: LockServer

    fun mount(src: String, dst: String)
    fun create(filename: String)
    fun remove(filename: String)
    fun ls(): List<String>
    fun read(filename: String): ByteArray
    fun write(filename: String, bytes: ByteArray)
}

open class ScopedLock(private val lock: LockServer, private val client: ExtentClient, private val filename: String) {
    init {
        val lid = filename.hashCode()
        lock.acquire(lid)
    }

    fun destroy() {
        val lid = filename.hashCode()
        lock.release(lid)
        client.flush(filename)
    }
}