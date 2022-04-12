package me.thatshawt.gameServer;

import me.thatshawt.gameCore.packets.ClientPacket;
import me.thatshawt.gameCore.packets.GamePacket;
import me.thatshawt.gameCore.packets.ServerPacket;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ServerPlayerHandler extends Thread {

    private final ServerPlayer player;
    private final GameServer server;

    public ServerPlayerHandler(GameServer server, ServerPlayer player) {
        this.player = player;
        this.server = server;
        this.start();


    }

    public ServerPlayer getPlayer(){
        return this.player;
    }

    public void run() {
        try {
            {
                System.out.println();
                UUID uuid = player.uuid;
                ByteBuffer uuidBuffer = ByteBuffer.allocate(8 * 2);
                GamePacket.putUUID(uuid, uuidBuffer);
                server.sendPacket(player, ServerPacket.PLAYER_LOGIN_RESPONSE, uuidBuffer.array());

                server.broadcastPacket(ServerPacket.PLAYER_ENTITY_SPAWN, uuidBuffer.array());
                System.out.println(server.players);
                for (ServerPlayerHandler otherServerPlayerHandler : server.players) {
                    uuid = otherServerPlayerHandler.getPlayer().uuid;
                    uuidBuffer.clear();
                    GamePacket.putUUID(uuid, uuidBuffer);
                    server.sendPacket(player, ServerPacket.PLAYER_ENTITY_SPAWN, uuidBuffer.array());
                    otherServerPlayerHandler.getPlayer().sendPosition(this.player);
                    System.out.println("sent position for " + server);
                    player.broadcastPosition();
                }
            }

            DataInputStream input = new DataInputStream(new BufferedInputStream(player.socket.getInputStream()));
            while(true){

                int packetLength = input.readInt();
                ClientPacket packet = ClientPacket.fromInt(input.readInt());

//                System.out.println("receioved packet " + packet.name());
                switch(packet){
                    case PLAYER_MOVE:
//                        System.out.println("received PLAYER_MOVE");
                        int xOffset = input.readInt();
                        int yOffset = input.readInt();

//                        System.out.printf("xOffset: %d, yOffset: %d\n", xOffset, yOffset);
//                        System.out.println("before: " + this.player);

                        if(xOffset > 0)player.moveRight();
                        if(xOffset < 0)player.moveLeft();
                        if(yOffset > 0)player.moveDown();
                        if(yOffset < 0)player.moveUp();

//                        System.out.println("after: " + this.player);

                        if(xOffset != 0 || yOffset != 0) {//i know this is redundant but i dont caaaarreeeeee
                            player.broadcastPosition();
                        }

                        break;
                    case PLAYER_CHAT: {
                        UUID uuid = this.player.uuid;
                        byte[] buffer = new byte[packetLength - 4];
                        input.read(buffer);

                        //TODO BUG: when they send an empty string it breaks the client lmao
                        if (buffer.length == 0) break;
                        byte[] message = new String(buffer).getBytes();
//                        System.out.println("received '" + new String(message) + "', length: " + message.length);

                        ByteBuffer uuidBuffer = ByteBuffer.allocate(8 + 8 + message.length);
                        GamePacket
                                .putUUID(uuid, uuidBuffer)
                                .put(message);

                        server.broadcastPacket(ServerPacket.PLAYER_CHAT, uuidBuffer.array());
                        System.out.println("player " + uuid);
                        break;
                    }
                }

            }
        } catch (IOException ignored) {
            UUID uuid = player.uuid;
            ByteBuffer buffer = ByteBuffer.allocate(8*2);
            GamePacket.putUUID(uuid, buffer);
            server.broadcastPacket(ServerPacket.ENTITY_REMOVE, buffer.array());

            server.players.remove(this);
        }

    }

    @Override
    public String toString() {
        return "ServerPlayerHandler{" +
                "player=" + player +
                ", server=" + server +
                '}';
    }
}
