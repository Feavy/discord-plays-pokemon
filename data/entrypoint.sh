#!/usr/bin/env bash

export DISPLAY=:1
Xvfb :1 -screen 0 320x288x16 &
sleep 1

echo nameserver 8.8.8.8 > /etc/resolv.conf  # Enable internet access from within the pod

java -jar pokemon.jar