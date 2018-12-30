package client

import server.LockServer
import java.rmi.Naming
import kotlin.concurrent.thread

fun main(args: Array<String>) {
    val s = Naming.lookup("rmi://127.0.0.1:12000/hello") as LockServer
    val threads = arrayListOf<Thread>()
    var int = 0
    for (i in 0..9) {
        threads.add(thread(start = false) {
            for (j in 0..10000) {
                s.acquire(0)
                int++
                s.release(0)
            }
        })
    }
    for (thread in threads) {
        thread.start()
    }
    for (thread in threads)
        thread.join()
    println(int)
}