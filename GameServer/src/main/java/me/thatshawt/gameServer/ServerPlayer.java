package me.thatshawt.gameServer;

import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.packets.ServerPacket;
import me.thatshawt.gameCore.tile.GameMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ServerPlayer extends Player {

    private GameServer server;
    protected Socket socket;

    public ServerPlayer(GameServer server, Socket socket, int x, int y) {
        super(x, y);
        this.socket = socket;
        this.server = server;
    }

    /**
     * tell entire server this player's position
     */
    protected void broadcastPosition(){
        ByteBuffer buffer = ByteBuffer.allocate(8*2 + 4*2);
        buffer.putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .putInt(this.getX())
                .putInt(this.getY());
//        System.out.println("broadcast " + uuid);
        server.broadcastPacket(ServerPacket.ENTITY_POSITION, buffer.array());
    }

    /**
    Sends current player's position to another player
     @param player the player to send the position to
     */
    protected void sendPosition(ServerPlayer player) throws IOException {
        System.out.printf("sent probabky x: %d, y: %d\n", this.getX(), this.getY());
        ByteBuffer buffer = ByteBuffer.allocate(8*2 + 4*2);
        buffer.putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits())
                .putInt(this.getX())
                .putInt(this.getY());
//        System.out.println("sent " + uuid);
        server.sendPacket(player, ServerPacket.ENTITY_POSITION, buffer.array());
    }

    public void sendMap(GameMap map){
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();//crude estimate lmao
        try {
            map.putBytes(buffer);
            server.sendPacket(this, ServerPacket.MAP_DATA, buffer.toByteArray());
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();//lmao
        }
    }

    protected void sendPosition() throws IOException {
        sendPosition(this);
    }

    public boolean moveDown(){
        if(this.getY() == server.map.tiles[0].length-1)return false;
        this.y.incrementAndGet();
//        System.out.println("down");
        return true;
    }

    public boolean moveUp(){
        if(this.getY() == 0)return false;
        this.y.decrementAndGet();
        return true;
    }

    public boolean moveLeft(){
        if(this.getX() == 0)return false;
        this.x.decrementAndGet();
        return true;
    }

    public boolean moveRight(){
        if(this.getX() == server.map.tiles.length-1)return false;
        this.x.incrementAndGet();
        return true;
    }

    @Override
    public String toString() {
        return "ServerPlayer{" +
                "uuid=" + uuid +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
