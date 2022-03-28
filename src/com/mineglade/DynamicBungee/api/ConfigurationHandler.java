package com.mineglade.DynamicBungee.api;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class ConfigurationHandler {

    private File path;
    private File file;

    private Configuration configuration;

    private Plugin plugin;

    public ConfigurationHandler(Plugin plugin) throws IOException {
        this.plugin = plugin;

        loadConfig();
    }

    public Configuration getConfig() {
        return configuration;
    }

    public ConfigurationHandler loadConfig() throws IOException {

        this.path = this.plugin.getDataFolder();

        if (!path.exists()) {
            if (!path.mkdir()) {
                this.plugin.getLogger().log(Level.WARNING, "There was an error creating the plugin's data folder.");
                throw new IOException("Unable to create plugin's data folder.");
            }
            this.plugin.getLogger().log(Level.INFO, "Created plugin's data folder.");
        }

        this.file = new File(path, "config.yml");

        if (!file.exists()) {
            if (!file.createNewFile()) {
                this.plugin.getLogger().log(Level.WARNING, "There was an error while creating the configuration file.");
                throw new IOException("Unable to create the configuration file.");
            }
            this.plugin.getLogger().log(Level.INFO, "Created configuration file.");
        }

        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        return this;
    }

    public ConfigurationHandler saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(getConfig(), file);
        } catch (IOException e) {
            ProxyServer.getInstance().getLogger().log(Level.SEVERE, "Could not save configuration file, some dynamic servers may not have been saved to disk, and will not appear on server restart.");
            e.printStackTrace();
        }
        return this;
    }

    // TODO
    public ConfigurationHandler addServer(Server server) {
        Configuration serverSection = getConfig().getSection("servers");
        serverSection.set(server.getName()+".host", server.getHost().toString());
        serverSection.set(server.getName()+".motd", server.getMotd());
        serverSection.set(server.getName()+".restricted", server.getRestricted());
        return this;
    }

    public ConfigurationHandler  removeServer(Server server) {
        getConfig().set("servers." + server.getName(), null);
        return this;
    }

    public Collection<String> getServers() {
        return getConfig().getSection("servers").getKeys();
    }

    public Map<String, Server> mapServers(Collection<String> names) {
        Map<String, Server> servers = new HashMap<>();
        names.forEach(name -> {
            Configuration serverSection = getConfig().getSection("servers." + name);
            try {
                new ServerBuilder(name)
                        .withHost(serverSection.getString("host"))
                        .withMotd(serverSection.getString("motd"))
                        .withRestricted(serverSection.getBoolean("restricted"))
                        .create();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        });
        return servers;
    }
}
