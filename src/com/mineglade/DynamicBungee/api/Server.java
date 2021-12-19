package com.mineglade.DynamicBungee.api;

import com.mineglade.DynamicBungee.DynamicBungee;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ListenerInfo;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import static java.lang.Integer.parseInt;

/**
 * <p></p>a "wrapper" class for {@link ServerInfo}</p><br>
 * Unfortunately, the BungeeCord API does not allow for renaming, changing the MOTD, etc.
 * while the server is in a listener. This class aims to simplify dynamic server changes
 * & additions through "reconnecting". I do not recommend using this "live renaming" in a
 * production setting, but it's VERY useful for testing & debugging.
 */
public class Server {

    static Map<String, Server> servers = new HashMap<>();

    String name;
    InetSocketAddress host;
    String motd;
    boolean restricted;

    ServerInfo serverInfo;

    public Server(String name, InetSocketAddress host, String motd, boolean restricted) {
        this.name = name;
        this.host = host;
        this.motd = motd;
        this.restricted = restricted;

        createServerInfo();
        addServer();
    }

    /**
     * gets the name of this instance of Server
     *
     * @return the name of this Server
     */
    public String getName() {
        return this.name;
    }

    /**
     * sets the name of this instance of {@link Server}
     * <p>to rename in a live environment, use {@link Server#renameServer(String, boolean)}</p>
     *
     * @param name the name of the server
     * @return the current instance of {@link Server}, for chaining
     */
    @Deprecated
    private Server setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * <p>
     * NOTE: renaming servers is not currently supported by the BungeeCord API.
     * this method is just a hacky quality-of-life method that should NOT be used in production.
     * </p> <br> <p>
     * removes entry with the old name, then creates a new entry with the new name.
     * this WILL send all players that are connected to the server you're trying to rename, to the "fallback server".
     * </p>
     *
     * @param name
     * @return a {@link Collection} of {@link ProxiedPlayer}s who were disconnected
     */
    @Deprecated
    public Collection<ProxiedPlayer> renameServer(String name) {
        return renameServer(name, false);
    }

    /**
     * <p>
     * NOTE: renaming servers is not currently supported by the BungeeCord API.
     * this method is just a hacky quality-of-life method that should NOT be used in production.
     * </p> <br> <p>
     * removes entry with the old name, then creates a new entry with the new name.
     * this WILL send all players that are connected to the server you're trying to rename, to the "fallback server".
     * it will reconnect them to the "new" server after renaming if <code>reconnect</code> is set to true
     * </p>
     *
     * @param name
     * @param reconnect
     * @return a {@link Collection} of {@link ProxiedPlayer}s who were dis- and/or reconnected
     */
    @Deprecated
    public Collection<ProxiedPlayer> renameServer(String name, boolean reconnect) {

        Collection<ProxiedPlayer> activePlayers = getPlayers();
        activePlayers.forEach(proxiedPlayer -> proxiedPlayer.connect(getFallbackServer(), ServerConnectEvent.Reason.valueOf("The server you were playing on, is being renamed.")));

        removeServer();

        setName(name);
        Server server = createServerInfo();
        addServer(server);

        if (reconnect) {
            activePlayers.forEach(proxiedPlayer -> proxiedPlayer.connect(getServerInfo(), ServerConnectEvent.Reason.valueOf("You were reconnected to the server you were previously playing on.")));
        }

        return activePlayers;
    }

    /**
     * gets the host of this instance of {@link Server}
     *
     * @return the {@link SocketAddress} host of this {@link Server}
     */
    public InetSocketAddress getHost() {
        return this.host;
    }

    // TODO
    private Server setHost(InetSocketAddress host) {
        this.host = host;
        return this;
    }

    private Server setHost(String host) {
        return setHost(InetSocketAddress.createUnresolved(host.split(":")[0], parseInt(host.split(":")[1])));
    }

    /**
     * <p>
     * NOTE: redirecting servers is not currently supported by the BungeeCord API.
     * this method is just a hacky quality-of-life method that should NOT be used in production.
     * </p> <br> <p>
     * removes entry with the old host, then creates a new entry with the new host.
     * this WILL send all players that are connected to the server you're trying to rename, to the "fallback server".
     * </p>
     *
     * @param host the new host to set the server to
     * @return {@link Collection} of {@link ProxiedPlayer}s that were dis- and/or reconnected
     */
    @Deprecated
    public Collection<ProxiedPlayer> redirectServer(InetSocketAddress host) {
        return redirectServer(host, false);
    }

    /**
     * <p>
     * NOTE: redirecting servers is not currently supported by the BungeeCord API.
     * this method is just a hacky quality-of-life method that should NOT be used in production.
     * </p> <br> <p>
     * removes entry with the old host, then creates a new entry with the new host.
     * this WILL send all players that are connected to the server you're trying to rename, to the "fallback server".
     * </p>
     *
     * @param host the new host to set the server to
     * @return {@link Collection} of {@link ProxiedPlayer}s that were dis- and/or reconnected
     */
    @Deprecated
    public Collection<ProxiedPlayer> redirectServer(String host) {
        return redirectServer(host, false);
    }

    /**
     * <p>
     * NOTE: redirecting servers is not currently supported by the BungeeCord API.
     * this method is just a hacky quality-of-life method that should NOT be used in production.
     * </p> <br> <p>
     * removes entry with the old host, then creates a new entry with the new host.
     * this WILL send all players that are connected to the server you're trying to rename, to the "fallback server".
     * it will reconnect them to the "new" server after renaming if <code>reconnect</code> is set to true
     * </p>
     *
     * @param host     the new host to set the server to
     * @param redirect whether to redirect the players to the "new" server
     * @return {@link Collection} of {@link ProxiedPlayer}s that were dis- and/or reconnected
     */
    @Deprecated
    public Collection<ProxiedPlayer> redirectServer(String host, boolean redirect) {
        return redirectServer(InetSocketAddress.createUnresolved(host.split(":")[0], parseInt(host.split(":")[1])), redirect);
    }

    /**
     * <p>
     * NOTE: redirecting servers is not currently supported by the BungeeCord API.
     * this method is just a hacky quality-of-life method that should NOT be used in production.
     * </p> <br> <p>
     * removes entry with the old host, then creates a new entry with the new host.
     * this WILL send all players that are connected to the server you're trying to rename, to the "fallback server".
     * it will reconnect them to the "new" server after renaming if <code>reconnect</code> is set to true
     * </p>
     *
     * @param host     the new host to set the server to
     * @param redirect whether to redirect the players to the "new" server
     * @return {@link Collection} of {@link ProxiedPlayer}s that were dis- and/or reconnected
     */
    @Deprecated
    public Collection<ProxiedPlayer> redirectServer(InetSocketAddress host, boolean redirect) {
        Collection<ProxiedPlayer> activePlayers = getPlayers();
        activePlayers.forEach(proxiedPlayer -> proxiedPlayer.connect(getFallbackServer(), ServerConnectEvent.Reason.valueOf("The server you were playing on, is being renamed.")));

        removeServer();

        setHost(host);
        Server server = createServerInfo();
        addServer(server);

        if (redirect) {
            activePlayers.forEach(proxiedPlayer -> proxiedPlayer.connect(getServerInfo(), ServerConnectEvent.Reason.valueOf("You were reconnected to the server you were previously playing on.")));
        }

        return activePlayers;
    }

    public String getMotd() {
        return this.motd;
    }

    public boolean getRestricted() {
        return this.restricted;
    }
    /**
     * @return
     */
    public ServerInfo getServerInfo() {
        return this.serverInfo;
    }

    /**
     * Creates & sets {@link ServerInfo} from instance variables.
     *
     * @return the current instance of {@link Server}, for chaining
     */
    private Server createServerInfo() {
        this.serverInfo = ProxyServer.getInstance().constructServerInfo(getName(), getHost(), getMotd(), getRestricted());
        return this;
    }

    /**
     * gets players on the current instance of {@link Server}
     *
     * @return {@link Collection} of {@link ProxiedPlayer}s on the current {@link Server}
     */
    public Collection<ProxiedPlayer> getPlayers() {
        return getServerInfo().getPlayers();
    }

    /**
     * gets the fallback {@link ServerInfo}
     *
     * @return {@link ServerInfo} for fallback server
     */
    public static ServerInfo getFallbackServer() {
        AtomicReference<ServerInfo> fallbackServer = new AtomicReference<>();
        ProxyServer.getInstance().getConfigurationAdapter().getListeners().forEach(l -> fallbackServer.set(ProxyServer.getInstance().getServerInfo((l.getFallbackServer()))));
        return fallbackServer.get();
    }

    /**
     * <p>static wrapper for {@link Server#addServer()}</p><br>
     * use {@link Server#getServer(String)}#{@link Server#addServer()}
     *
     * @param server {@link Server} to add
     * @return the instance of {Server}, for chaining
     */
    public static Server addServer(Server server) {
        return server.addServer();
    }

    /**
     * adds server to internal map & bungeecord
     *
     * @return the current instance of {@link Server}, for chaining
     */
    public Server addServer() {
        ProxyServer.getInstance().getServers().put(getName(), getServerInfo());
        servers.put(getName(), this);

        DynamicBungee.getInstance().getConfig().addServer(this).saveConfig();
        return this;
    }

    /**
     * <p>static wrapper for {@link Server#removeServer()}</p><br>
     * use {@link Server#getServer(String)}#{@link Server#removeServer()}
     *
     * @param server {@link Server} to remove
     */
    public Server removeServer(Server server) {
        return server.removeServer();
    }

    /**
     * removes server from internal map & bungeecord
     */
    public Server removeServer() {
        ProxyServer.getInstance().getServers().remove(getName());
        servers.remove(getName());


        DynamicBungee.getInstance().getConfig().removeServer(this).saveConfig();
        return this;
    }

    /**
     * gets a server from a provided name
     *
     * @param name the name of the server to get
     * @return the {@link Server} with provided <code>name</code>
     */
    public static Server getServer(String name) {
        return getServers().get(name);
    }

    /**
     * gets all mapped servers
     *
     * @return
     */
    public static Map<String, Server> getServers() {
        return servers;
    }
}