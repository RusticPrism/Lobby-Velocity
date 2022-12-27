package de.rusticprism.lobbyvelocity;

import com.velocitypowered.api.command.RawCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class HubCommand implements RawCommand {
    @Override
    public void execute(Invocation invocation) {
        if(invocation.source() instanceof Player player) {
            if(player.hasPermission("vlobby.command.lobby")) {
                Optional<RegisteredServer> server = LobbyVelocity.plugin.proxy.getServer("lobby");
                if(server.isPresent()) {
                    player.createConnectionRequest(server.get()).fireAndForget();
                }else player.sendMessage(Component.text(LobbyVelocity.plugin.prefix + "§cLobby server not present!"));
            }
        }else invocation.source().sendMessage(Component.text(LobbyVelocity.plugin.prefix + "§cYou have to be a §4Player §cto perform that Command!"));
    }
}
