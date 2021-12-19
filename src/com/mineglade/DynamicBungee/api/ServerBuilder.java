package com.mineglade.DynamicBungee.api;

import com.mineglade.DynamicBungee.DynamicBungee;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.logging.Level;

import static java.lang.Integer.parseInt;

public class ServerBuilder {

    private String name;
    private InetSocketAddress host;
    private String motd = "";
    private boolean restricted = false;

    public ServerBuilder(String name) {
        withName(name);
    }
    public ServerBuilder(InetSocketAddress host) {
        withHost(host);
    }

    public ServerBuilder() {}

    public ServerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ServerBuilder withHost(String host) {
        return withHost(InetSocketAddress.createUnresolved(host.split(":")[0], parseInt(host.split(":")[1])));
    }
    public ServerBuilder withHost(InetSocketAddress host) {
        this.host = host;
        return this;
    }

    public ServerBuilder withMotd(String motd) {
        this.motd = motd;
        return this;
    }

    public ServerBuilder withRestricted(boolean restricted) {
        this.restricted = restricted;
        return this;
    }

    public Server create() throws InstantiationException {
        if (name == null || host == null) {
            throw new InstantiationException("name or host not specified");
        }
        DynamicBungee.getInstance().getLogger().log(Level.INFO, "Adding server with name: " + name + " and host " + host);
        return new Server(this.name, this.host, this.motd, this.restricted);
    }
}