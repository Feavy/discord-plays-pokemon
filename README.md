<br/>

![Discord Plays Pokémon](./title.png)

## Controls

- **Join :** Send a gif with built-in Discord Tenor browser, then send the message `s/e/pkmn`
- **A :** `s/e/wa`
- **B :** `s/e/wb`
- **Up :** `s/e/ww` | `s/e/wz`
- **Left :** `s/e/wq`
- **Right :** `s/e/wd`
- **Down :** `s/e/ws`
- **Start :** `s/e/wt` (30 seconds cooldown)
- **Select :** `s/e/we`

## How does it work?

The original idea comes from the [s/e/x Discord hack](https://www.youtube.com/watch?v=km8CR-fdB7o) by [@rebane2001](https://github.com/rebane2001)

To sum up Discord has a built-in shortcut to replace a part of your last message. This is the `s/<before>/<after>` thing. Sending `test` and then `s/test/ok` will replace `test` by `ok` in your previous message.

Added to the fact that when you send a gif from Discord, you send a message with the gif URL (hidden by Discord) (eg. `https://tenor.com/view/something`), it makes possible to execute code by listening to URL changes caused by using the replacement shortcut.

I reused this principle to control a GameBoy emulator through a Java web server.

### Technologies

Java / Quarkus / Visual Boy Advance / Docker / Kubernetes / GitHub actions

### Threads

- **GameInteractionLoop :** Presses a key every 3ms.
- **GameRecordingLoop :** Takes screenshots of the game every 100ms.
- **GameSavingLoop :** Makes a save state every minute.

### Deployment

Deployment is done through GitHub actions. It builds the project, creates a Docker image and pushes it to the GitHub registry. See `Dockerfile`

Then this image is deployed in my Kubernetes cluster. See `deployment.yml`

## Installation

**Requirements:** Docker, Java 18

```bash
git clone https://github.com/feavy/discord-plays-pokemon.git
cd discord-plays-pokemon
./gradlew build
docker build . -t pokemon
docker run --name pokemon -p 8080:8080 -v /path-where-you-want-to-store-game-saves:/root/.vba pokemon

# To stop the container
docker kill pokemon
docker rm pokemon
```

## FAQ

### What game is it?

Pokémon Red

### Can I reuse your code?

Of course!

### How is online counting done?

It is an estimation. The bot counts the number of keys added to the queue during the last second.

## Known issues

### Mobile

The `s/<before>/<after>` thing seems not to work on some smartphones.

### Image caching

Sometimes the image you see can not represent the current state of the game.

Indeed, although my server send pictures with no-cache property set, this option is not propagated through Discord's CDN so the images you see are effectively cached on your computer.

So if somebody already requested the same URL as you, you are going to see an old screenshot of the time when he made the request.

A simple way to avoid this problem is to add some random characters at the end of the gif URL to make it unique.

## Credits

Feavy#9654
