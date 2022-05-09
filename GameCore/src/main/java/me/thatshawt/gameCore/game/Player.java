package me.thatshawt.gameCore.game;

import me.thatshawt.gameCore.tile.PlayerTile;

import java.io.Serializable;
import java.time.Clock;
import java.util.UUID;

public abstract class Player extends Entity implements Serializable{

    private static final long serialVersionUID = 2945172386414521018L;
    protected transient String chatMsg = "";
    private transient long chatTime;

    public Player(int x, int y){
        super(x,y,new PlayerTile());
    }

    public Player(int x, int y, UUID uuid){
        super(x,y,new PlayerTile(), uuid);
    }

    public void setChat(String chatMsg){
        this.chatMsg = chatMsg;
        this.chatTime = Clock.systemDefaultZone().millis();
    }

    public String getChat(){
        return this.chatMsg;
    }

}
