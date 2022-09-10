#!/usr/bin/env bash

export DISPLAY=:1
Xvfb :1 -screen 0 320x289x16 &
sleep 1

java -jar pokemon.jar