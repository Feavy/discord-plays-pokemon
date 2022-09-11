# Discord Plays Pokémon

## Controls

- **Join :** Send a gif with built-in Discord browser, then send the message `s/e/pkmn`
- **A :** `s/e/wa`
- **B :** `s/e/wb`
- **Up :** `s/e/ww` | `s/e/wz`
- **Left :** `s/e/wq`
- **Right :** `s/e/wd`
- **Down :** `s/e/ws`
- **Start :** `s/e/wt` (30 seconds cooldown)
- **Select :** `s/e/we`

## How does it work?

Inspired by [s/e/x Discord hack](https://www.youtube.com/watch?v=km8CR-fdB7o).

Basically Discord has a built-in feature to replace a part of your message quickly. This is the `s/<before>/<after>` thing.

In reality when you send a gif, Discord sends its URL on tenor.com under the hood. It is something like `https://tenor.com/view/something`

So when you send `s/e/pkmn` it replaces the first

One thread is responsible of pressing the queued keys (every 3ms).

One thread takes screenshots of the game (every 100ms).

And one thread saves the game (very minute).



### Technologies

Java / Quarkus / Visual Boy Advance / Docker / Kubernetes / GitHub actions

## Deployment



## FAQ

### What game is it?

Pokémon Red

### Can I reuse your code?

No problem. MIT :)

## Known issues

### Mobile



### Image caching

Sometimes the image you see are not the current state of the game.

Indeed, although my server send pictures with no-cache property set, this option is not propagated through Discord's CDN so the images you see are effectively cached on your computer.

So if somebody already requested you are going to see screenshot of the time when he made the request.

A simple way to avoid this problem is to add some random alphanumerical characters at the end of the gif URL to make sure nobody already requested that URL.

## Credits

Feavy#9654
