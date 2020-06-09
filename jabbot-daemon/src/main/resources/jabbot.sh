#!/bin/bash 

daemon_start(){
  echo "starting Jabbot service.."
  echo "JABBOT_HOME: "$JABBOT_HOME
  $EXEC >& /dev/null &
  echo $! > "$JABBOT_HOME"/jabbot.pid
}

daemon_stop(){
  echo "stopping Jabbot service.. PID $(cat "$JABBOT_HOME"/jabbot.pid)"
  kill  $(cat "$JABBOT_HOME"/jabbot.pid)
}


###
# Script main
###

# Absolute path to this script
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in,
SCRIPTPATH=$(dirname "$SCRIPT")
JABBOT_HOME=$(dirname "$SCRIPTPATH")
JABBOT_MAIN="org.wanna.jabbot.Jabbot"
JABBOT_LOGS=$JABBOT_HOME"/logs"
J_CLASSPATH=$JABBOT_HOME"/lib/*:"$JABBOT_HOME"/conf/:"$JABBOT_HOME"/extension-scripts/"

EXEC="$(which java) -Xmx64m -Xms64m -cp $J_CLASSPATH -Djabbot.logs_dir=$JABBOT_LOGS $JABBOT_MAIN"

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
