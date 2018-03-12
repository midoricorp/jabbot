#!/bin/bash
if [[ $JABBOT_COMMAND == "help" ]]; then
echo "ping ip_address";
exit;
fi
if [[ -z $1 ]]; then
echo "please specify an ip"
exit;
fi
if ping -c 1 $1 >/dev/null; then
echo "alive"
else
echo "dead"
fi

