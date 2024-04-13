package org.example

import application.Application
import client.RemoteDatabase

object Client {
    @JvmStatic
    fun main(args: Array<String>) {
        val serverIp = System.getenv("SERVER_IP") ?: "localhost"
        val serverPort = System.getenv("SERVER_PORT")?.toIntOrNull() ?: 8080
        val authFile = System.getenv("AUTH_FILE") ?: null

        val application = Application(
            authFile,
            RemoteDatabase(
                serverIp,
                serverPort
            )
        )

        application.start()
    }
}