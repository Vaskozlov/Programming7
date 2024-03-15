package org.example.client.udp

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import network.client.DatabaseCommand

@Serializable
data class CommandWithArgument(val command: DatabaseCommand, val value: JsonElement)
