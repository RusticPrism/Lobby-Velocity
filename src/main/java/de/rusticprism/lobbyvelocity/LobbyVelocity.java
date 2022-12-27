package de.rusticprism.lobbyvelocity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

@Plugin(
        id = "lobby-velocity",
        name = "Lobby-Velocity",
        version = "1.0.0-Beta",
        description = "A Simple Velocity Lobby Plugin",
        authors = {"RusticPrism"}
)
public final class LobbyVelocity {

    public String prefix;

    public static LobbyVelocity plugin;
    public final ProxyServer proxy;
    public Logger logger;
    private MinecraftChannelIdentifier MODERNCHANNEL;

    @Inject
    public LobbyVelocity(ProxyServer proxyServer, Logger logger) {
        plugin = this;
        this.proxy = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getCommandManager().register("lobby", new HubCommand(),"l", "hub");
        MODERNCHANNEL = MinecraftChannelIdentifier.from("lobby:main");
        proxy.getChannelRegistrar().register(MODERNCHANNEL);
    }
    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if(event.getIdentifier().equals(MODERNCHANNEL)) {
            if (!(event.getSource() instanceof ServerConnection connection)) {
                event.setResult(PluginMessageEvent.ForwardResult.forward());
                return;
            }
            event.setResult(PluginMessageEvent.ForwardResult.handled());

            ByteArrayDataInput in = event.dataAsDataStream();
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("GetServer")) {
                String name = in.readUTF();
                Optional<RegisteredServer> optional = proxy.getServer(name);
                if (optional.isPresent()) {
                    RegisteredServer server = optional.get();
                    out.writeUTF("ServerInfo");
                    out.writeUTF(server.getServerInfo().getName());
                    try {
                        server.ping().get();
                        out.writeUTF("true");
                        out.writeUTF(String.valueOf(server.ping().get().asBuilder().getOnlinePlayers()));
                        out.writeUTF(String.valueOf(server.ping().get().asBuilder().getMaximumPlayers()));
                    } catch (InterruptedException | ExecutionException e) {
                        out.writeUTF("false");
                        out.writeUTF("0");
                        out.writeUTF("0");
                    }
                } else {
                    out.writeUTF("ServerInfo");
                    out.writeUTF(name);
                    out.writeUTF("false");
                    out.writeUTF("0");
                    out.writeUTF("0");
                }
                connection.getServer().sendPluginMessage(MODERNCHANNEL, out.toByteArray());
            }else if(subChannel.equalsIgnoreCase("connect")) {
                String server = in.readUTF();
                if(proxy.getServer(server).isPresent()) {
                    connection.getPlayer().createConnectionRequest(proxy.getServer(server).get()).fireAndForget();
                }
            }
        }
    }
}
