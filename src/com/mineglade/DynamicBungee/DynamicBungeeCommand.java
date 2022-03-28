package com.mineglade.DynamicBungee;

import com.mineglade.DynamicBungee.DynamicBungee;
import com.mineglade.DynamicBungee.api.Server;
import com.mineglade.DynamicBungee.api.ServerBuilder;
import dnl.utils.text.table.TextTable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import xyz.derkades.derkutils.bungee.CommandSenderOutputStream;

import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Integer.parseInt;

@Deprecated
public class DynamicBungeeCommand extends net.md_5.bungee.api.plugin.Command {

    DynamicBungee plugin;

    public DynamicBungeeCommand(Plugin plugin) {
        super("DynamicBungee", "", "db", "dynbun");
        this.plugin = (DynamicBungee) plugin;
    }

    public void execute(CommandSender sender, String[] strings) {

        if (strings.length == 0) {
            sender.sendMessage(new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&',
                            "&cPlease provide a sub-command:&f add&c,&f remove&c,&f list"))
                    .create());
            return;
        }

        if (!sender.hasPermission("dynamicbungee")) {
            sender.sendMessage(new ComponentBuilder("Sorry, you do not have permission to use " + strings[0]).color(ChatColor.RED).create());
            return;
        }

        if (strings[0].equals("add")) {
            if (!sender.hasPermission("dynamicbungee.add")) return;
            if (strings.length < 3) {
                sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                        "&cA&f name&c and&f host&c:&fport&c must be provided."))
                        .create());
                return;
            }

            if (strings.length == 3) {
                addServer(sender,
                        strings[1],
                        strings[2]);
            }
            else if (strings.length == 4) {
                addServer(sender,
                        strings[1],
                        strings[2],
                        Boolean.parseBoolean(strings[3]));
            }
            else {
                addServer(sender,
                        strings[1],
                        strings[2],
                        Boolean.parseBoolean(strings[3]),
                        String.join(" ", Arrays.copyOfRange(strings, 4, strings.length)));
            }
        }
        else if (strings[0].equals("remove")) {
            if (!sender.hasPermission("dynamicbungee.remove")) return;
            if (strings.length < 2) {
                sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                        "&cThe &fname&c of the server you wish to remove must be provided."))
                        .create());
                return;
            }
            removeServer(sender, strings[1]);
        }
        else if (strings[0].equals("list")) {
            if (!sender.hasPermission("dynamicbungee.list")) return;
//            listServers(sender);
        }
        else {
            sender.sendMessage(new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&',
                            "&cPlease provide a sub-command:&f add&c,&f remove&c,&f list"))
                    .create());
        }
    }

    private void addServer(CommandSender sender, String name, String host) {
        addServer(sender, name, host, false);
    }

    private void addServer(CommandSender sender, String name, String host, boolean restricted) {
        addServer(sender, name, host, restricted, "");
    }

    public static void addServer(CommandSender sender, String name, String host, boolean restricted, String motd) {
        try {
            new ServerBuilder(name)
                    .withHost(host)
                    .withMotd(motd)
                    .withRestricted(restricted)
                    .create();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static void removeServer(CommandSender sender, String name) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(name).getPlayers()) {
            p.disconnect(new TextComponent("This server was closed by an admin."));
        }
        ProxyServer.getInstance().getServers().remove(name);
        Server server = Server.getServer(name);
        server.removeServer();
        sender.sendMessage(new ComponentBuilder("The server " + name + " has been removed from the proxy.").color(ChatColor.GREEN).create());
    }

//    public void listServers(CommandSender sender) {
//        StringBuilder serverList = new StringBuilder();
//        final String[] columns = {"players", "name", "address", "motd"};
//        final Object[][] data = new Object[ProxyServer.getInstance().getServers().size()][columns.length];
//
//        ArrayList<ServerInfo> serverInfos = DynamicBungee.listServers(this.plugin);
//
//        for (int i = 0; i < ProxyServer.getInstance().getServers().size(); i++) {
//            ServerInfo serverInfo = serverInfos.get(i);
//            data[i][1] = String.valueOf(serverInfo.getPlayers().size());
//            data[i][1] = serverInfo.getName();
//            data[i][2] = serverInfo.getSocketAddress();
//            data[i][3] = serverInfo.getMotd();
//        }
//
//        try (PrintStream stream = new PrintStream(new CommandSenderOutputStream(sender))) {
//            new TextTable(columns, data).printTable(stream, 0);
//        }
//    }
}
