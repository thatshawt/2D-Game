# 2D-Game
Made this for a java class in college.

This is basically a chatting "game". You move around and chat with other users.

## Controls
You use WASD keys to move on a keyboard.

Also, if you have a ps4 controller you can use the arrow keys to move.

## Client Usage:
First argument is ip you want to connect to, second is the port.
If you don't specify arguments it will connect to my server.

```java -jar GameClient.jar 123.456.789.123 25565```

## Server Usage:
First argument is the port to listen on.

```java -jar GameServer.jar 25565```

For convince I made a docker container for the server at https://github.com/thatshawt/2D-Game/pkgs/container/2d-game.
The server runs on port 25565, so bind that port however you want lmao.

![image](https://user-images.githubusercontent.com/5741581/163100622-548dc582-d7d9-4c2a-ae3d-e048b0e6e10f.png)
