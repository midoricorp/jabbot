# Jabbot

Jabbot is an easily extensible chat robot written in java.
It supports connection to multiple servers & chat systems, and a pluggable chatroom command system and focus on extensibility. 

Jabbot consists of 3 projects:

1. **jabbot-binding-api** whihch provide a set of interfaces to create new chat system bindings such like XMPP, IRC...
2. **jabbot-extension-api** which aims at providing an interface for plugging new commands & features for chat rooms
3. **jabbot-daemon** which is the actual Bot daemon

**Bindings** can be written using jabbot-binding api and are currently stored under the
[bindings](https://github.com/midoricorp/jabbot/tree/master/bindings) directory.

**Extensions** can be written using jabbot-extension-api and are currently stored under the [extensions](https://github.com/midoricopr/jabbot/tree/master/extensions) directory.

**CGI Extensions** can be written using in any language you want. Scripts and their json configuration are currently stored under the [scripts](https://github.com/midoricorp/jabbot/tree/master/scripts) directory.

**Script Extensions** can also be written the in chat language. Declaring a sub will result in a new bot function being created. Any existing bot command (regardless of implementation) can be called as a function. See the project [Searle Script](https://github.com/midoricorp/script/) for information on the language syntax.

There is also a collection of script commands you can install, check them out in the [Snippet Archive](https://github.com/midoricorp/snippets)

## Quickstart

**Installing it via APT **

Tested on Ubuntu Bionic
```bash
sudo apt-get install apt-transport-https ca-certificates
echo "deb [trusted=yes] https://dl.bintray.com/midoricorp/deb unstable main" > /etc/apt/sources.list.d/midoricorp-jabbot.list
sudo apt-get update
sudo apt-get install jabbot-full
```
**Configuring**
Installing via apt will trigger genconf.pl being run.  This will ask you which protocols and bindings you want to use.
If you make a mistake during this phase, simply delete or fix /etc/jabbot/saved_values.ini and invoke ```dpkg-reconfigure jabbbot```.  See "[Per-Protocol Configuration Details](#per-protocol-configuration-details)" for advice on how to configure each protocool.

**Logs**
logs for jabbot can be found in /var/log/jabbot/

**Build debs from master**

Tested on Ubuntu Bionic
```bash
sudo apt-get install git ca-certificates
git clone https://github.com/midoricorp/jabbot.git
cd jabbot
sudo apt-get install build-essential fakeroot devscripts equivs
sudo mk-build-deps --install --tool 'apt-get --yes --no-install-recommends' --remove
sudo rm ../jabbot*_all.deb
sudo dpkg-buildpackage -uc -us
cd ..
apt install ./jabbot*_all.deb
```


**Build it by hand**
Assuming you have git, java 8, jsvc and maven2 installed

```bash
host$ git clone https://github.com/midoricorp/jabbot.git
host$ cd jabbot/
host$ mvn clean install -Pstandalone,all
host$ cd jabbot-daemon/target/
host$ tar -xzf jabbot-daemon-<version>.tar.gz
host$ cd ../../

```
**Configure it**
```bash
apt-get install libjson-perl libconfig-simple-perl
./genconf.pl
mv jabbot.json jabbot-daemon/target/jabbot-daemon-<version>/conf/
```
**Start it**
```bash
host$ cd jabbot-daemon/target/jabbot-daemon-<version>/
host$ bin/jabbot.sh start
```

## Jabbot Configuration
Jabbot can be configured by editing the main config file jabbot.json under the conf/ directory.
The config file consists in the following main areas

## Bindings
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

### serverList
```json
    "serverList":[
        {   "type" : "XMPP",
            "identifier" : "XMPP",
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
* **identifier:** used to identify the connection, unless you are connecting to multiple servers of the same protocol, you can set it to the same value as "type"
* **commandPrefix:** the command prefix used to trigger commands & action in a chatroom
* **parameters**: a map of binding specific parameters
* **rooms:**  list of rooms to join on this connection
* **commands:** list of commands available for this connection.


#### Per-Protocol Configuration Details
##### IRC
* **type:** IRC
* **url:** hostname of the irc server
* **port:** port of the irc server
* **username:** the nick when connecting to irc
* **password:** the nick to identify to NickServ with
* **rooms:** This protocol supports joining rooms, so you can specify the number of rooms it should try to join and then their names. Example room name "#midori-dev"
##### XMPP
* **type:** XMPP
* **url:** hostname of the XMPP server
* **port:** port of the XMPP server
* **username:** username of the XMPP account to use
* **password:** password of the XMPP account to use
* **rooms:** This protocol supports joining rooms, so you can specify the number of rooms it should try to join and then their names. Example room name "midori@chat.yax.im"
* **parameters.allow_self_signed:** true/false allow connecting to servers with self-signed certificates
* **parameters.ping_interval:** intervial keepalive pings are sent to the xmpp server
##### Slack
* **type:** SLACK
* **url:** not used, just leave blank
* **port:** not used, just leave blank
* **username:** not used, just leave blank
* **password:** the bot token this bot should use
* **rooms:** not used, send an invite to get the bot to join the room
##### Discord
* **type:** DISCORD
* **url:** not used, just leave blank
* **port:** not used, just leave blank
* **username:** not used, just leave blank
* **password:** the bot token this bot should use
* **rooms:** not used, send an invite to get the bot to join the room
##### Matrix
* **type:** MATRIX
* **url:** http or https url of the matrix server to connect to
* **port:** not used, just leave blank
* **username:** not used, just leave blank
* **password:** the bot token this bot should use
* **rooms:** not used, send an invite to get the bot to join the room
##### Webex Teams (previously Cisco Spark)
* **type:** SPARK
* **url:** leave as default (https://webexapis.com/v1)
* **port:** leave as default 443
* **username:** not used
* **password:** The BOT Token you want to use
* **rooms:** Joining rooms isn't used. Simply invite the bot to the desired room
* **parameters.use_webook:** true/false allow use webhook to receive events or poll if false
* **parameters.webhook_url:** if webhook is used, the the url the bot can be reached at (jabbot will listen on port 8080)
> You can use [ngrok](https://ngrok.com/) that can generate public URLs for testing your chatbots. After downloading and installation type ```./ngrok http 8080``` then copy URL and paste it in variable ```parameters.webhook_url```

> Markdown in the Web Client (and possibly other clients) can break in chat scrpting.  Be sure to either disable markdown in the client, or place the code you are scripting into a code block e.g.
>
>!script
>
>\`\`\`
>
>print "hello world!"
>
>\`\`\`

### commands
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

#### Command list
* **chuck** Generates a Chuck Norris Compliment
* **help** Lists available commands
* **uptime** Displays how long the bot has been up for
* **dice** Dice Roll Simulator
* **attack** Swears at a person using 'Fuck Off As A Service'
* **jira** Command to display info about JIRA IDs
  - password => password
  - username => username
  - url => https://jira.hostname.com
* **propaganda** Prints random North Korean Quote, can be customized to your environment thru substitutions
  - :{action} => revolution
  - :{guy1} => Kim Il Sung
  - :{guy2} => Kim Jong Il
  - :{company_name} => Korea
  - :{group} => People's Army
  - :{philosophy} => socialism
  - :{enemy} => Japan
  - :{corporate} => national
  - :{exec} => Party
  - :{hq} => Paektu
  - :{guy3} => Kim Jong Un
  - :{company} => country
  - :{job} => Worker
  - :{capital} => Pyongyang
* **say** Allows a message to be sent to a different room/binding
* **script** Command to execute script statements or create script commands.
Scripts are stored in script_dir.
loop_limit and buffer_limit protect against abuse
  - script_dir => /var/lib/jabbot/script
  - loop_limit => 1000
  - buffer_limit => 1000
* **delete** Allows commands written in script to be removed
  - script_dir => /var/lib/jabbot/script
* **translate** Translates a message from any language to any other language
* **ping** Pings specified host
* **urban** Looks up term in the Urban Dictionary

## Testing
If you want to quickly test your new extension, there's one special type of binding which allow you to start Jabbot without any configuration required.

The **cli binding** is a binding emulator which will just read from standard input, process the command and print the response on the console.
In order to use it, simply follow the same steps show in the quickstart section but start it as following 
```bash
host$ bin/jabbotcli.sh
```
