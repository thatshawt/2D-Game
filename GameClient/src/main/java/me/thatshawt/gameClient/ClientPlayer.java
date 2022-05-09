package me.thatshawt.gameClient;

import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.packets.ClientPacket;
import me.thatshawt.gameCore.tile.ChunkCoord;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ClientPlayer extends Player{
    private static final long serialVersionUID = -8727067318966852110L;

    private transient Camera camera;
    private transient final GameClient gameClient;
    public transient boolean cameraFollow = true;

    public ClientPlayer(GameClient gameClient, int x, int y, UUID uuid) {
        super(x, y, uuid);
        this.camera = new Camera(x,y,10);
        this.gameClient = gameClient;
    }

    private void sendMovement(int xOffset, int yOffset) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4*2);

        byteBuffer.putInt(xOffset);
        byteBuffer.putInt(yOffset);

        try {
            gameClient.sendPacket(ClientPacket.PLAYER_MOVE, byteBuffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        if(cameraFollow)camera.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        if(cameraFollow)camera.setY(y);
    }

    public void sendChat(String msg) throws IOException {
        gameClient.sendPacket(ClientPacket.PLAYER_CHAT, msg.getBytes());
    }

    public boolean moveDown(){
        if(!gameClient.chunks.containsKey(ChunkCoord.fromTileXY(x.get(),y.get()+1)))return false;
        sendMovement(0, 1);
//        System.out.println("down");
        return true;
    }

    public boolean moveUp(){
        if(!gameClient.chunks.containsKey(ChunkCoord.fromTileXY(x.get(),y.get()-1)))return false;
//        this.y.decrementAndGet();
        sendMovement(0, -1);
        return true;
    }

    public boolean moveRight(){
        if(!gameClient.chunks.containsKey(ChunkCoord.fromTileXY(x.get()+1,y.get())))return false;
//        this.x.incrementAndGet();
        sendMovement(1,0);
        return true;
    }

    public boolean moveLeft(){
        if(!gameClient.chunks.containsKey(ChunkCoord.fromTileXY(x.get()-1,y.get())))return false;
//        this.x.decrementAndGet();
        sendMovement(-1,0);
        return true;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
