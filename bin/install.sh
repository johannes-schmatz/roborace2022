#!/bin/bash

args=""
args+="root@10.0.1.1"
args+=" -o KexAlgorithms=+diffie-hellman-group1-sha1"
args+=" -o HostKeyAlgorithms=+ssh-rsa"
args+=" -o Ciphers=+aes128-cbc"

cmds=""
cmds+="; chmod +x lejos/bin/jrun"
cmds+="; mkdir classes"
cmds+="; mkfifo log.txt"

cat bin/jrun | \
	ssh $args "cat > lejos/bin/jrun"
cat bin/hotswap-agent-1.4.1.jar | \
	ssh $args "cat > hotswap-agent.jar $cmds"

exit

# put this in your ssh config
Host kallisto
        Hostname 10.0.1.1
        User     root
        KexAlgorithms +diffie-hellman-group1-sha1
        HostKeyAlgorithms +ssh-rsa
        Ciphers +aes128-cbc