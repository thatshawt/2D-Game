package me.thatshawt.gameCore.game;

import me.thatshawt.gameCore.tile.Tile;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity {

    public final UUID uuid;
    protected AtomicInteger x,y;
    protected Tile tile;

    protected Entity(int x, int y, Tile tile, UUID uuid){
        this.uuid = uuid;
        this.x = new AtomicInteger(x);
        this.y = new AtomicInteger(y);
        this.tile = tile;
    }

    protected Entity(int x, int y, Tile tile){
        this(x,y,tile,UUID.randomUUID());
    }

    public int getX() {
        return x.get();
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public int getY() {
        return y.get();
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public Tile getTile() {
        return this.tile;
    }

    public abstract boolean moveDown();
    public abstract boolean moveUp();
    public abstract boolean moveLeft();
    public abstract boolean moveRight();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return uuid.equals(entity.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void setXY(int x, int y) {
        setX(x);
        setY(y);
    }
}
