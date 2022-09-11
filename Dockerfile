FROM openjdk:19-bullseye

WORKDIR /app

RUN apt-get update && apt-get install -y \
    xvfb \
    visualboyadvance \
    libgtk-3-0 # from gedit

COPY build/discord-plays-pokemon-1.0-SNAPSHOT-runner.jar /app/pokemon.jar
COPY data/pokemon_red.gb /app/pokemon.gb
COPY data/entrypoint.sh /app/entrypoint.sh
COPY data/vba.ini /app/vba.ini

CMD ["bash", "entrypoint.sh"]
