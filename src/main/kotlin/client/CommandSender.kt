package client

import client.udp.Frame
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import network.client.DatabaseCommand
import org.example.client.udp.CommandWithArgument
import org.example.lib.net.udp.ChannelUDPNetwork
import org.example.lib.net.udp.slice.PacketSlicer
import server.AuthorizationInfo
import java.net.InetSocketAddress

class CommandSender(
    private val authorizationInfo: AuthorizationInfo,
    private val address: InetSocketAddress,
    useAsyncReceive: Boolean = true
) {
    private val networkInterface = ChannelUDPNetwork()
    val network = PacketSlicer(networkInterface)

    init {
        if (useAsyncReceive) {
            networkInterface.enableAsync()
        } else {
            networkInterface.disableAsync()
        }
    }

    suspend fun sendCommand(command: DatabaseCommand, value: JsonElement) {
        val frame = Frame(authorizationInfo, CommandWithArgument(command, value))
        network.sendStringInPackets(Json.encodeToString(frame), address)
    }
}
