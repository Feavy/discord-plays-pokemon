#!/usr/bin/env bash

export DISPLAY=:1
Xvfb :1 -screen 0 320x289x16 &
sleep 1

echo nameserver 8.8.8.8 > /etc/resolv.conf

java -jar pokemon.jar