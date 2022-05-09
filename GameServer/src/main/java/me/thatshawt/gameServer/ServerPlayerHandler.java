package me.thatshawt.gameServer;

import me.thatshawt.gameCore.packets.ClientPacket;
import me.thatshawt.gameCore.packets.GamePacket;
import me.thatshawt.gameCore.packets.ServerPacket;
import me.thatshawt.gameCore.tile.ChunkCoord;
import me.thatshawt.gameCore.tile.TileChunk;

import java.io.*;
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
                try{
//                    ByteArrayOutputStream mapBuffer = new ByteArrayOutputStream();
                    ChunkCoord coord = ChunkCoord.fromChunkXY(0,0);
                    TileChunk chunk = server.chunks.get(coord);
                    player.sendChunk(coord, chunk);

                    coord = ChunkCoord.fromChunkXY(1,0);
                    chunk = server.chunks.get(coord);
                    player.sendChunk(coord, chunk);
                }catch(Exception e){
                    System.out.println("failed to send map data");
                    e.printStackTrace();
                }
                System.out.println("sent map data");

                UUID uuid = player.uuid;
                ByteArrayOutputStream uuidBuffer = new ByteArrayOutputStream();
                GamePacket.putUUID(uuid, uuidBuffer);
                server.sendPacket(player, ServerPacket.PLAYER_LOGIN_RESPONSE, uuidBuffer.toByteArray());
                System.out.println();

                server.broadcastPacket(ServerPacket.PLAYER_ENTITY_SPAWN, uuidBuffer.toByteArray());
                System.out.println(server.players);
                for (ServerPlayerHandler otherServerPlayerHandler : server.players) {
                    uuid = otherServerPlayerHandler.getPlayer().uuid;
                    uuidBuffer.reset();
                    GamePacket.putUUID(uuid, uuidBuffer);
                    server.sendPacket(player, ServerPacket.PLAYER_ENTITY_SPAWN, uuidBuffer.toByteArray());
                    otherServerPlayerHandler.getPlayer().sendPosition(this.player);
                    System.out.println("sent entity spawn to " + otherServerPlayerHandler.player);
                    player.broadcastPosition();
                    System.out.println("broadcasted position of " + player);
                }
            }

            DataInputStream input = new DataInputStream(new BufferedInputStream(player.socket.getInputStream()));
            while(true){
                if(player.socket.isClosed()){
                    System.out.println("dead on arrival");
                    return;
                }
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

                        ByteArrayOutputStream messageBuffer = new ByteArrayOutputStream();
                        GamePacket.putUUID(uuid, messageBuffer);
                        messageBuffer.write(message);

                        server.broadcastPacket(ServerPacket.PLAYER_CHAT, messageBuffer.toByteArray());
                        System.out.println("player " + uuid);
                        break;
                    }
                }

            }
        } catch (IOException ignored) {
            UUID uuid = player.uuid;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try {
                GamePacket.putUUID(uuid, buffer);
                server.broadcastPacket(ServerPacket.ENTITY_REMOVE, buffer.toByteArray());
                System.out.println("sent entity remove");
                ignored.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace(); //bruh imagine
            }
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
