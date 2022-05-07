package me.thatshawt.gameClient;

import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.packets.ClientPacket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ClientPlayer extends Player{

    private static final long serialVersionUID = -8727067318966852110L;
    private transient final GameClient gameClient;

    public ClientPlayer(GameClient gameClient, int x, int y, UUID uuid) {
        super(x, y, uuid);
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

    public void sendChat(String msg) throws IOException {
        gameClient.sendPacket(ClientPacket.PLAYER_CHAT, msg.getBytes());
    }

    public boolean moveDown(){
        if(this.getY() == gameClient.gameMap.tiles[0].length-1)return false;
        sendMovement(0, 1);
//        System.out.println("down");
        return true;
    }

    public boolean moveUp(){
        if(this.getY() == 0)return false;
//        this.y.decrementAndGet();
        sendMovement(0, -1);
        return true;
    }

    public boolean moveRight(){
        if(this.getX() == gameClient.gameMap.tiles.length-1)return false;
//        this.x.incrementAndGet();
        sendMovement(1,0);
        return true;
    }

    public boolean moveLeft(){
        if(this.getX() == 0)return false;
//        this.x.decrementAndGet();
        sendMovement(-1,0);
        return true;
    }
}
