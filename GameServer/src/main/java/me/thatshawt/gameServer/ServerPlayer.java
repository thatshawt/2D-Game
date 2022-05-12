package me.thatshawt.gameServer;

import me.thatshawt.gameCore.game.Direction;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.packets.ServerPacket;
import me.thatshawt.gameCore.tile.ChunkCoord;
import me.thatshawt.gameCore.tile.TileChunk;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ServerPlayer extends Player {

    private int renderDistance = 10;
    private GameServer server;
    protected Socket socket;

    public ServerPlayer(GameServer server, Socket socket, int x, int y) {
        super(server.chunks, x, y);
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

    public void sendChunk(ChunkCoord coord, TileChunk map){
        try {
            server.sendPacket(this, ServerPacket.MAP_DATA, coord.toBytes(), map.toBytes());
        } catch (IOException e) {
            e.printStackTrace();//lmao
        }
    }

    protected void sendPosition() throws IOException {
        sendPosition(this);
    }

    public boolean moveDown(){
//        if(this.getY() == server.chunks.get(ChunkCoord.fromTileXY(x,y)))return false;
        if(!checkCollision(Direction.DOWN))return false;
        this.y.incrementAndGet();
//        System.out.println("down");
        return true;
    }

    public boolean moveUp(){
//        if(this.getY() == 0)return false;
        if(!checkCollision(Direction.UP))return false;
        this.y.decrementAndGet();
        return true;
    }

    public boolean moveLeft(){
//        if(this.getX() == 0)return false;
        if(!checkCollision(Direction.LEFT))return false;
        this.x.decrementAndGet();
        return true;
    }

    public boolean moveRight(){
//        if(this.getX() == server.chunks.get(ChunkCoord.fromChunkXY(0,0)).tiles.length-1)return false;
        if(!checkCollision(Direction.RIGHT))return false;
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
