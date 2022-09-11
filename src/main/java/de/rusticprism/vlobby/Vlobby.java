package de.rusticprism.vlobby;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "vlobby",
        name = "Vlobby",
        version = "1.0.0-Beta",
        description = "A Simple Velocity Lobby Plugin",
        authors = {"RusticPrism"}
)
public final class Vlobby {

    public String prefix;

    public static Vlobby plugin;
    public final ProxyServer proxy;
    public Logger logger;

    @Inject
    public Vlobby(ProxyServer proxyServer,Logger logger) {
        plugin = this;
        this.proxy = proxyServer;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getCommandManager().register("lobby", new HubCommand(),"l", "hub");
    }
}
