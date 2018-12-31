package client

class ExtentClientCacheImp(serverUrl: String): ExtentClientImp(serverUrl) {
    private enum class FileStatus { NONE, UPDATED, MODIFIED, REMOVED}

    private val fileCached = hashMapOf<String, Pair<FileStatus, ByteArray>>()

    override fun get(filename: String): ByteArray {
        if (fileCached.containsKey(filename)) {
            val (status, bytes) = fileCached[filename]!!
            when(status) {
                FileStatus.UPDATED, FileStatus.MODIFIED -> return bytes
                FileStatus.NONE -> fileCached[filename] = Pair(FileStatus.UPDATED, extentServer.get(filename))
                else -> { }
            }
        }
        else
            fileCached[filename] = Pair(FileStatus.UPDATED, extentServer.get(filename))
        return fileCached[filename]!!.second
    }

    override fun put(filename: String, buffer: ByteArray) {
        if (fileCached.containsKey(filename)) {
            val status = fileCached[filename]!!.first
            when(status) {
                FileStatus.NONE, FileStatus.UPDATED, FileStatus.MODIFIED ->
                    fileCached[filename] = Pair(FileStatus.MODIFIED, buffer)
                FileStatus.REMOVED -> { }
            }
        }
        else
            fileCached[filename] = Pair(FileStatus.MODIFIED, buffer)
    }

    override fun remove(filename: String) {
        if (fileCached.containsKey(filename)) {
            val (status, bytes) = fileCached[filename]!!
            when(status) {
                FileStatus.NONE, FileStatus.UPDATED, FileStatus.MODIFIED ->
                    fileCached[filename] = Pair(FileStatus.REMOVED, bytes)
                FileStatus.REMOVED -> { }
            }
        }
        else
            fileCached[filename] = Pair(FileStatus.REMOVED, "".toByteArray())
    }

    override fun flush(filename: String) {
        try {
            val (status, bytes) = fileCached[filename]!!
            when(status) {
                FileStatus.MODIFIED -> extentServer.put(filename, bytes)
                FileStatus.REMOVED -> extentServer.remove(filename)
                else -> { }
            }
            fileCached.remove(filename)
        }
        catch (e: KotlinNullPointerException) { }
    }
}