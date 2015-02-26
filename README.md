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
Assuming you have git, java 7, jsvc and maven2 installed

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
host$ bin/jabbot.sh start
```

## Jabbot Configuration
Jabbot can be configured by editing the main config file jabbot.json under the conf/ directory.
The config file consists in the following main areas

####bindings####
```json
    "bindings":[
        {   "name":"XMPP",
            "className":"org.wanna.jabbot.binding.xmpp.XmppBinding"
        }
    ]
```
Defines a list of available binding type such as xmpp, irc..

* **name:** a unique identifier for this binding
* **className:** the canonical name of the binding connection class

####serverList####
```json
    "serverList":[
        {   "type" : "XMPP",
            "url":"jabber.hostname.com",
            "serverName":"hostname.com",
            "port":5222,
            "username":"Jabbot",
            "password":"password",
            "commandPrefix":"!",
            "parameters":{
                "allow_self_signed":true,
                "ping_interval":600
            },            
            "rooms":[
                {"name":"test_room@conference.hostname.com","nickname":"Jabbot"}
            ],
            "commands":[
                {"name":"help","className":"org.wanna.jabbot.command.HelpCommand"}
            ]
        }
    ]
```

Defines a list of servers to which Jabbot will connect

* **type:** the name of a binding
* **url:** the url to which to connect
* **commandPrefix:** the command prefix used to trigger commands & action in a chatroom
* **parameters**: a map of binding specific parameters
* **rooms:**  list of rooms to join on this connection
* **commands:** list of commands available for this connection.

####commands####
```json
    "commands":[
        {"name":"jira","className":"org.wanna.jabbot.extensions.jira.IssueViewer",
            "configuration":{
                "url":"https://jira.hostname.com",
                "username":"username",
                "password":"password"
            }
        }
    ]
```

Defines a list of availble commands for the parent object

* **name:** the name of the command
* **className:** the canonical name of the Command class
* **configuration:** a configuration Map passed to the Command at initialization phase

## Testing
If you want to quickly test your new extension, there's one special type of binding which allow you to start Jabbot without any configuration required.

The **cli binding** is a binding emulator which will just read from standard input, process the command and print the response on the console.
In order to use it, simply follow the same steps show in the quickstart section but start it as following 
```bash
host$ bin/jabbotcli.sh
```
