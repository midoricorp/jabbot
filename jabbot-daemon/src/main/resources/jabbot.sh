#!/bin/bash 

daemon_start(){
  echo "starting Jabbot service.."
  echo "JABBOT_HOME: "$JABBOT_HOME
  echo "JAVA_HOME: "$JAVA_HOME
  exec $(which jsvc)  -cp $J_CLASSPATH -Djabbot.logs_dir=$JABBOT_LOGS -pidfile $JABBOT_HOME"/jabbot.pid" -debug -home $JAVA_HOME $JABBOT_MAIN
}

daemon_stop(){
  echo "stopping Jabbot service.."
  exec $(which jsvc) -stop -cp $J_CLASSPATH -Djabbot.logs_dir=$JABBOT_LOGS -pidfile $JABBOT_HOME"/jabbot.pid" -debug -home $JAVA_HOME $JABBOT_MAIN
}


###
# Script main
###

# Absolute path to this script
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in,
SCRIPTPATH=$(dirname "$SCRIPT")
JABBOT_HOME=$(dirname "$SCRIPTPATH")
JABBOT_MAIN="org.wanna.jabbot.Launcher"
JABBOT_LOGS=$JABBOT_HOME"/logs"
J_CLASSPATH=$JABBOT_HOME"/lib/*:"$JABBOT_HOME"/conf/"

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
