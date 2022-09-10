FROM openjdk:19-bullseye

WORKDIR /app

RUN apt-get update && apt-get install -y \
    xvfb \
    visualboyadvance \
#    mgba-sdl \
    imagemagick \
    kmod \
    gedit
#    xserver-xorg-video-dummy \
#    x11-xserver-utils

COPY build/discord-plays-pokemon-1.0-SNAPSHOT-runner.jar /app/pokemon.jar
COPY data/xorg.conf /etc/X11/xorg.conf
COPY data/pokemon_red.gb /app/pokemon.gb
COPY data/config.ini /root/.config/mgba/config.ini
COPY data/entrypoint.sh /app/entrypoint.sh
COPY data/vba.ini /app/vba.ini

CMD ["bash", "entrypoint.sh"]

#export DISPLAY=:1
#Xvfb :1 -screen 0 1024x768x16 &
#sleep 1
#/usr/games/mgba pokemon.gb &
#import -display :1 -window root image.png
