package server

import collection.Address
import collection.NetworkCode
import collection.Organization
import exceptions.*
import lib.ExecutionStatus
import client.DatabaseCommand
import org.example.server.commands.ServerSideCommand

var commandMap: Map<DatabaseCommand, ServerSideCommand> = mapOf(
    DatabaseCommand.ADD to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        database.add(argument as Organization)
        Result.success(null)
    },

    DatabaseCommand.ADD_IF_MAX to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        database.addIfMax(argument as Organization)
            .takeIf { it == ExecutionStatus.SUCCESS }
            ?.let { Result.success(null) }
            ?: Result.failure(NotMaximumOrganizationException())
    },

    DatabaseCommand.SHOW to ServerSideCommand
    {
            _,
            database,
            _,
        ->
        Result.success(database.toJson())
    },

    DatabaseCommand.CLEAR to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        database.clear(argument as Int)
    },

    DatabaseCommand.INFO to ServerSideCommand
    {
            _,
            database,
            _,
        ->
        Result.success(database.getInfo())
    },

    DatabaseCommand.MAX_BY_FULL_NAME to ServerSideCommand
    {
            _,
            database,
            _,
        ->
        database.maxByFullName()
            .takeIf { it != null }
            ?.let { Result.success(it) }
            ?: Result.failure(OrganizationNotFoundException())
    },

    DatabaseCommand.REMOVE_HEAD to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        Result.success(database.removeHead(argument as Int))
    },

    DatabaseCommand.REMOVE_BY_ID to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        val (id, creatorId) = argument as Pair<Int, Int>
        database.removeById(id, creatorId)
            .takeIf { it == ExecutionStatus.SUCCESS }
            ?.let { Result.success(null) }
            ?: Result.failure(OrganizationNotFoundException())
    },

    DatabaseCommand.REMOVE_ALL_BY_POSTAL_ADDRESS to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        val (address, creatorId) = argument as Pair<Address, Int>
        database.removeAllByPostalAddress(address, creatorId)
        Result.success(null)
    },

    DatabaseCommand.UPDATE to ServerSideCommand
    {
            _,
            database,
            argument,
        ->
        database.modifyOrganization(argument as Organization)
        Result.success(null)
    },

    DatabaseCommand.EXIT to ServerSideCommand
    {
            _,
            _,
            _,
        ->
        Result.success(null)
    },

    DatabaseCommand.SUM_OF_ANNUAL_TURNOVER to ServerSideCommand
    {
            _,
            database,
            _,
        ->
        Result.success(database.getSumOfAnnualTurnover())
    },

    DatabaseCommand.HISTORY to ServerSideCommand
    {
            _,
            database,
            _,
        ->
        Result.success(database.getHistory())
    },
)

fun errorToNetworkCode(error: Throwable?): NetworkCode {
    return when (error) {
        is OrganizationAlreadyPresentedException -> NetworkCode.ORGANIZATION_ALREADY_EXISTS

        is OrganizationNotFoundException -> NetworkCode.NOT_FOUND

        is NotMaximumOrganizationException -> NetworkCode.NOT_A_MAXIMUM_ORGANIZATION

        is FileReadException -> NetworkCode.UNABLE_TO_READ_FROM_FILE

        is FileWriteException -> NetworkCode.UNABLE_TO_SAVE_TO_FILE

        is OrganizationKeyException -> NetworkCode.ORGANIZATION_KEY_ERROR

        is InvalidOutputFormatException -> NetworkCode.INVALID_OUTPUT_FORMAT

        is IllegalAccessException -> NetworkCode.ACCESS_LIMITED

        else -> NetworkCode.FAILURE
    }
}