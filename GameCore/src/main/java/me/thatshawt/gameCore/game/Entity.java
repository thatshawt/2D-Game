package me.thatshawt.gameCore.game;

import me.thatshawt.gameCore.tile.*;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity implements Serializable {

    private static final long serialVersionUID = -4391882970385321938L;
    public final UUID uuid;
    protected AtomicInteger x,y;
    protected Tile tile;
    protected ChunkMap chunks;

    protected Entity(ChunkMap chunks, int x, int y, Tile tile, UUID uuid){
        this.uuid = uuid;
        this.x = new AtomicInteger(x);
        this.y = new AtomicInteger(y);
        this.tile = tile;
        this.chunks = chunks;
    }

    protected Entity(ChunkMap chunks, int x, int y, Tile tile){
        this(chunks, x,y,tile,UUID.randomUUID());
    }

    public int getX() {
        return x.get();
    }

    public void setX(int x) {
        this.updateChunkLocation(getX(), getY(), x, getY());
        this.x.set(x);
    }

    public int getY() {
        return y.get();
    }

    public void setY(int y) {
        this.updateChunkLocation(getX(), getY(), getX(), y);
        this.y.set(y);
    }

    public Tile getTile() {
        return this.tile;
    }

    public abstract boolean moveDown();
    public abstract boolean moveUp();
    public abstract boolean moveLeft();
    public abstract boolean moveRight();

    /**
     * moves entity to new chunk if needed.
     * should be used <i>after</i> an entity moves to make sure it actually gets
     * put into the entityList field in {@link TileChunk}
     */
    protected void updateChunkLocation(int oldX, int oldY, int newX, int newY){
        TileChunk previousChunk = chunks.get(ChunkCoord.fromTileXY(oldX, oldY));
        TileChunk newChunk = chunks.get(ChunkCoord.fromTileXY(newX, newY));
        if(newChunk != previousChunk){
//            System.out.println("moved entity " + this + " from " + previousChunk + " to " + newChunk);
            newChunk.addEntity(this);
            previousChunk.removeEntity(this);
        }
    }

//    /**
//     * convenience method used <i>before</i> you move using a direction
//     * @param direction
//     */
//    protected void updateChunkLocation(Direction direction){
//        updateChunkLocation(getX()getX() + direction.xOffset, getY() + direction.yOffset);
//    }

    /**
     * return true if there is no collision, false if there is a collision
     * @param direction the direction of movement
     */
    public boolean checkCollision(Direction direction){
        Tile target = chunks.tileAt(getX() + direction.xOffset, getY() + direction.yOffset);
        return target instanceof AirTile;
    }

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
