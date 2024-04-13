package org.example.client.commands

import database.DatabaseInterface

fun interface Command {
    fun executeImplementation(database: DatabaseInterface, argument: Any?): Result<Any?>

    fun execute(database: DatabaseInterface, argument: Any? = null): Result<Any?> {
        return try {
            executeImplementation(database, argument)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }
}
