package com.mineglade.DynamicBungee;

import dnl.utils.text.table.TextTable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.InetSocketAddress;

import static java.lang.Integer.parseInt;

public class Command extends net.md_5.bungee.api.plugin.Command {

    public Command(Plugin plugin) {
        super("DynamicBungee", "", "db", "dynbun");
    }

    public void execute(CommandSender sender, String[] strings) {

        if (!sender.hasPermission("dynamicbungee")) {
            sender.sendMessage(new ComponentBuilder("Sorry, you do not have permission to use " + strings[0]).color(ChatColor.RED).create());
            return;
        }

        if (strings.length <= 1) {
            sender.sendMessage(new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&',
                            "&cPlease provide a sub-command:&f add&c,&f remove&c,&f list"))
                    .create());
            return;
        }

        if (strings[1].equals("add")) {
            if (!sender.hasPermission("dynamicbungee.add")) return;
            if (strings.length < 4) {
                sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                        "&cA&f name&c and&f host&c:&fport&c must be provided."))
                        .create());
                return;
            }

            if (strings.length == 4) {
                addServer(sender,
                        strings[2],
                        InetSocketAddress.createUnresolved(strings[3].split(":")[0], parseInt(strings[3].split(":")[1])));
            }
            else if (strings.length == 5) {
                addServer(sender,
                        strings[2],
                        InetSocketAddress.createUnresolved(strings[3].split(":")[0], parseInt(strings[3].split(":")[1])),
                        Boolean.parseBoolean(strings[4]));
            }
            else {
                addServer(sender,
                        strings[2],
                        InetSocketAddress.createUnresolved(strings[3].split(":")[0], parseInt(strings[3].split(":")[1])),
                        Boolean.parseBoolean(strings[4]),
                        strings[5]);
            }
        }
        else if (strings[1].equals("remove")) {
            if (!sender.hasPermission("dynamicbungee.remove")) return;
            if (strings.length < 3) {
                sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                        "&cThe &fname&c of the server you wish to remove must be provided."))
                        .create());
                return;
            }
            removeServer(sender, strings[2]);
        }
        else if (strings[1].equals("list")) {
            listServers(sender);
        }
        else {
            sender.sendMessage(new ComponentBuilder(
                    ChatColor.translateAlternateColorCodes('&',
                            "&cPlease provide a sub-command:&f add&c,&f remove&c,&f list"))
                    .create());
        }
    }

    private void addServer(CommandSender sender, String name, InetSocketAddress address) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, "", false));
        sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                "&aThe server &f" + name + "&a, with host &f" + address.getHostString() +"&a:&f" + address.getPort() + "&a has been added to the proxy."))
                .create());
    }

    private void addServer(CommandSender sender, String name, InetSocketAddress address, boolean restricted) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, "", restricted));
        sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                "&aThe server &f" + name + "&a, with host &f" + address.getHostString() +"&a:&f" + address.getPort() + "&a has been added to the proxy with restricted set to &f" + restricted + "&a."))
                .create());
    }

    public static void addServer(CommandSender sender, String name, InetSocketAddress address, boolean restricted, String motd) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted));
        sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&',
                "&aThe server &f" + name + "&a, with host &f" + address.getHostString() +"&a:&f" + address.getPort() + "&a has been added to the proxy with restricted set to &f" + restricted + "&a and an motd of &f "+ motd +"&a."))
                .create());
    }

    public static void removeServer(CommandSender sender, String name) {
        for (ProxiedPlayer p : ProxyServer.getInstance().getServerInfo(name).getPlayers()) {
            p.disconnect(new TextComponent("This server was closed by an admin."));
        }
        ProxyServer.getInstance().getServers().remove(name);
        sender.sendMessage(new ComponentBuilder("The server " + name + " has been removed from the proxy.").color(ChatColor.GREEN).create());
    }

    public static void listServers(CommandSender sender) {
        StringBuilder serverList = new StringBuilder();
        final String[] columns = {"players", "name", "address", "motd"};
        final Object[][] data = new Object[ProxyServer.getInstance().getServers().size()][columns.length];

        for (int i = 0; i < ProxyServer.getInstance().getServers().size(); i++) {
            ServerInfo serverInfo = ProxyServer.getInstance().getServers().get(i);
            data[i][1] = serverInfo.getPlayers().size();
            data[i][1] = serverInfo.getName();
            data[i][2] = serverInfo.getSocketAddress();
            data[i][3] = serverInfo.getMotd();
        }

        sender.sendMessage(new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', new TextTable(columns, data).toString())).create());
    }
}
