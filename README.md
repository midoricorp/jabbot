## Introduction

Jabbot is an easily extensiible jabber robot written in Java.

It mainly consist of 2 projects:
- jabbot-daemon: which is the current main application.
- jaboot-extension-api: which contains the api to provide a basic command interface for the robot.

Extensions can be written using this api and are currently stored under the [extensions](https://github.com/vmorsiani/jabbot/tree/master/extensions) directory.

## Building Jabbot
In order to get jabbot running you will need the following installed:
- [maven2](http://maven.apache.org/)
- java 1.7

In order ton configure jabbot, simply edit the 2 following files in jabbot-daemon:
- jabbot.properties : used to set jabber connection information as well as some additional settings for jabbot.
- chatrooms.xml: used to set a list of chatrooms to join on startup.

Once the above is complete, go into the root folder of the project and run
```
mvn clean install
```
This will produce a tar.gz file under jabbot-daemon/target
untar the file, go in the bin directory and run
```
sh jabbot.sh start
```
