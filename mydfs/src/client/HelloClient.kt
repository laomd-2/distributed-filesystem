package client

import server.FileServer
import server.LockServer
import java.rmi.Naming
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val lock = Naming.lookup("rmi://127.0.0.1:12000/hello") as LockServer
    val fileServer = Naming.lookup("rmi://127.0.0.1:12345/fileserver1") as FileServer

    val threads = arrayListOf<Thread>()

    for (i in 0..4) {
        threads.add(thread(start = false) {
            lock.acquire(0)
            fileServer.write("file1.txt", "llalala".toByteArray())
            lock.release(0)

            Thread.sleep(2000)

            lock.acquire(1)
            println(String(fileServer.read("file2.txt")))
            lock.release(1)
        })

        threads.add(thread(start = false) {
            lock.acquire(1)
            fileServer.write("file2.txt", "hahaha".toByteArray())
            lock.release(1)

            Thread.sleep(2000)

            lock.acquire(0)
            println(String(fileServer.read("file1.txt")))
            lock.release(0)
        })
    }

    for (thread in threads) {
        thread.start()
    }
    for (thread in threads)
        thread.join()
}