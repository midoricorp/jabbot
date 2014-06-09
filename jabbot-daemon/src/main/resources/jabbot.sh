#!/bin/sh

JABOT_HOME=../
JABBOT_MAIN="org.wanna.jabbot.Launcher"


daemon_start(){
  cd $JABOT_HOME/lib
  exec jsvc  -cp "./*" -pidfile /tmp/jabbot.pid -debug -home $JAVA_HOME $JABBOT_MAIN
}

daemon_stop(){
  cd $JABOT_HOME/lib
  exec jsvc -stop -cp "./*" -pidfile /tmp/jabbot.pid -debug -home $JAVA_HOME $JABBOT_MAIN
}

case $1 in
start)
daemon_start
;;
stop)
daemon_stop
;;
*)
 echo "Usage: jabbot.sh {start|stop}" >&2
 exit 1
esac