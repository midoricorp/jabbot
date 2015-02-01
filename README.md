# Jabbot

Jabbot is an easily extensible chat robot written in java.
It supports connection to multiple servers & chat systems, and a pluggable chatroom command system and focus on extensibility. 

Jabbot consists of 3 projects:

1. **jabbot-binding-api** whihch provide a set of interfaces to create new chat system bindings such like XMPP, IRC...
2. **jabbot-extension-api** which aims at providing an interface for plugging new commands & features for chat rooms
3. **jabbot-daemon** which is the actual Bot daemon

**Bindings** can be written using jabbot-binding api and are currently stored under the
[bindings](https://github.com/vmorsiani/jabbot/tree/master/bindings) directory.

**Extensions** can be written using jabbot-extension-api and are currently stored under the [extensions](https://github.com/vmorsiani/jabbot/tree/master/extensions) directory.

## Quickstart
Assuming you have git, java 7 and maven2 installed

**Build it**
```bash
host$ git clone https://github.com/vmorsiani/jabbot.git
host$ cd jabbot/
host$ mvn clean install -Pstandalone
host$ cd jabbot-daemon/target/
host$ tar -xzf jabbot-daemon-<version>.tar.gz
host$ cd jabbot-daemon-<version>/
```
**Configure it**
```bash
host$ vi conf/jabbot.json
```
**Start it**
```bash
host$ sh bin/jabbot.sh start
```
