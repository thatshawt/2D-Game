package me.thatshawt.gameCore.game;

import java.util.UUID;

/**
 * this represents another player on the screen that is not you.
 * all the move methods return false because your not supposed to be able to control them.
 */
public class NetworkPlayer extends Player{

    private static final long serialVersionUID = -3360067197945155592L;

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
