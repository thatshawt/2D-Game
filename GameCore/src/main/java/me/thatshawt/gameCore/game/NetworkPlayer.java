package me.thatshawt.gameCore.game;

import java.util.UUID;

public class NetworkPlayer extends Player{


    public NetworkPlayer(int x, int y, UUID uuid) {
        super(x, y, uuid);
    }

    @Override
    public boolean moveDown() {
        return false;
    }

    @Override
    public boolean moveUp() {
        return false;
    }

    @Override
    public boolean moveLeft() {
        return false;
    }

    @Override
    public boolean moveRight() {
        return false;
    }
}
