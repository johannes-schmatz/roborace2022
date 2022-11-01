#!/bin/sudo /bin/bash

set -m

address=00:16:53:44:42:90
address_="$(sed -Ee s/:/_/g <<< "$address")"

echo "running dbus-send Connect $address"
interface=$( \
	dbus-send --system --print-reply --type=method_call --dest=org.bluez "/org/bluez/hci0/dev_$address_" org.bluez.Network1.Connect string:"nap" \
	| tee /dev/stderr \
	| sed -Ee '/^\s{3}string\s"/!d;s/\s{3}.*\s"(.*)"/\1/'
)

echo "running bneptest $interface"
bneptest -b "$interface" -n "$interface" -c "$address" &

echo "waiting for connection"
sleep 1

echo "setup ip address"
ip a add 10.0.1.2/24 dev $interface

echo "ip:"
ip a show dev $interface

echo "continue bneptest"
fg 1

echo "running dbus-send Disconnect"
dbus-send --system --print-reply --type=method_call --dest=org.bluez "/org/bluez/hci0/dev_$address_" org.bluez.Network1.Disconnect

