package com.mineglade.DynamicBungee;

import com.mineglade.DynamicBungee.api.ConfigurationHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import java.io.IOException;
import java.util.concurrent.Callable;

public class DynamicBungee extends Plugin {

    private static DynamicBungee instance;

    private ConfigurationHandler configuration;

    public DynamicBungee() {
        instance = this;
    }

    Metrics metrics;

    @Override
    public void onEnable() {


        int pluginId = 9945; // <-- Replace with the id of your plugin!
        metrics = new Metrics(this, pluginId);

        try {
            configuration = new ConfigurationHandler(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getConfig().mapServers(getConfig().getServers());

//        listServers(this);

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new DynamicBungeeCommand(this));
    }

    @Override
    public void onDisable() {
        ProxyServer.getInstance().getPluginManager().unregisterCommands(this);

        metrics.addCustomChart(new Metrics.SingleLineChart("connected_servers", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 0;
            }
        }));
    }

//    public static ArrayList<ServerInfo> listServers(DynamicBungee plugin) {
//
//        ArrayList<ServerInfo> serverInfos = new ArrayList<>(ProxyServer.getInstance().getServers().values());
//
//        plugin.metrics.addCustomChart(new Metrics.SingleLineChart("connected_servers", new Callable<Integer>() {
//
//            @Override
//            public Integer call() throws Exception {
//                return serverInfos.size();
//            }
//        }));
//        return serverInfos;
//    }

    public static DynamicBungee getInstance() {
        return instance;
    }

    public ConfigurationHandler getConfig() {
        return this.configuration;
    }
}
