package me.thatshawt.gameCore.tile;

import me.thatshawt.gameCore.packets.PacketData;
import me.thatshawt.gameCore.game.Entity;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TileChunk implements Serializable, PacketData {

    private static final long serialVersionUID = -4761830387139310254L;
    public List<Entity> entityList = new ArrayList<>();
    public Tile[][] tiles;

    private TileChunk(int size){
        tiles = new Tile[size][size];

        for(int i=0;i<tiles.length;i++){
            for(int j=0;j<tiles[i].length;j++){
                tiles[i][j] = new AirTile();
            }
        }
    }

    public static final int CHUNK_SIZE = 4;

    public TileChunk(){
        this(CHUNK_SIZE);//idk cool size i guess
    }

    public void addEntity(Entity entity){
        entityList.add(entity);
    }

    public void removeEntity(Entity entity){
        entityList.remove(entity);
    }

    public void removeEntity(UUID uuid){
        removeEntity(getEntity(uuid));
    }

    public Entity getEntity(UUID uuid){
        for(Entity entity : entityList)
            if(entity.uuid.equals(uuid))return entity;
        return null;
    }

    public Entity firstEntityOnLocation(int x, int y){
        for(Entity entity : entityList)
            if(entity.getX() == x && entity.getY() == y)return entity;
        return null;
    }

    public List<Entity> getEntitiesOnLocation(int x, int y){
        List<Entity> entities= new ArrayList<>();
        for(Entity entity : entityList)
            if(entity.getX() == x && entity.getY() == y)entities.add(entity);
        return entities;
    }

    public boolean containsUUID(UUID uuid) {
        for(Entity entity : entityList)
            if(entity.uuid.equals(uuid))return true;
        return false;
    }

    @Override
    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);
        out.writeObject(this);
        out.close();
        return byteStream.toByteArray();
    }

    @Override
    public String toString() {
        return "TileChunk{" +
                "entityList=" + entityList +
                ", tiles=" + Arrays.toString(tiles) +
                '}';
    }
}
