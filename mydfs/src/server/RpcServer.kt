package server

import java.rmi.Naming
import java.rmi.registry.LocateRegistry
import java.rmi.server.UnicastRemoteObject

abstract class RpcServer(private val port: Int) : UnicastRemoteObject(port) {
    fun start(root: String) {
        LocateRegistry.createRegistry(port)
        Naming.bind("rmi://127.0.0.1:$port/$root", this)
    }
}