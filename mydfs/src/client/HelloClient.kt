package client

import server.FileServer
import server.LockServer
import java.rmi.Naming
import kotlin.concurrent.thread
import kotlin.random.Random

fun main(args: Array<String>) {
    val lock = Naming.lookup("rmi://127.0.0.1:12000/hello") as LockServer
    val fileServer = Naming.lookup("rmi://127.0.0.1:12345/fileserver1") as FileServer

    val threads = arrayListOf<Thread>()

    for (i in 0..10) {
        threads.add(thread(start = false) {
            val rand = Random(System.currentTimeMillis())
            val write = rand.nextInt(11)
            lock.acquire(write)
            fileServer.write("file$write.txt", "this is a file written by $i".toByteArray())
            lock.release(write)
            println("$i write $write")

            val read = rand.nextInt(11)
            lock.acquire(read)
            val res = String(fileServer.read("file$read.txt"))
            lock.release(read)
            println("$i read $read, $res")
        })
    }

    for (thread in threads) {
        thread.start()
    }
    for (thread in threads)
        thread.join()
}