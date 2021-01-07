# DynamicBungee
simple plugin that allows you to dynamically add servers and forced hosts to your bungeecord/waterfall server

Here's a [video demonstration](https://youtu.be/9kEYm0qiOoE)

# Commands
| command | description | usage | aliases | permission |
| - | - | - | - | - |
| `/dynamicbungee` | core command, lists sub commands | `/dynamicbungee` | `/dynbun`, `/db` | `dynamicbungee` |
| `/dynamicbungee add` | adds a server to the proxy | `/dynamicbungee add <name> <host:port> [restricted [motd]]` | `` | `dynamicbungee.add` |
| `/dynamicbungee remove` | removes a server from the proxy | `/dynamicbungee remove <name>` | `` | `dynamicbungee.remove` |
| `/dynamicbungee list` | lists all your servers | `/dynamicbungee list` | `` | `dynamicbungee.list` |

# Features
- Adding servers (with motd and restricted settings)
- Removing servers
- A more detailed server list than glist
- Permission checks for each of the commands

# Coming Soon
- Configuration file that'll be read and added in onEnable() so you don't have to add all your servers every restart
- fix to the serverlist so it actually displays playercount
- colored serverlist table :3

# Considering
- configurable serverlist table so you can hide all the different columns (for privacy concerning ip-addresses / hosts)
