package me.thatshawt.gameCore.tile;

import me.thatshawt.gameCore.game.Entity;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameMap implements Serializable {

    private static final long serialVersionUID = -4761830387139310254L;
    public List<Entity> entityList = new ArrayList<>();
    public Tile[][] tiles;

    public GameMap(){
        tiles = new Tile[6][6];

        for(int i=0;i<tiles.length;i++){
            for(int j=0;j<tiles[i].length;j++){
                tiles[i][j] = new AirTile();
            }
        }
    }

    public void addEntity(Entity entity){
        entityList.add(entity);
    }

    public void removeEntity(Entity entity){
        entityList.remove(entity);
    }

    public void removeEntity(UUID uuid){
        removeEntity(entityFromUUID(uuid));
    }

    public Entity entityFromUUID(UUID uuid){
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

    public void putBytes(ByteBuffer buffer) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteStream);
        out.writeObject(this);
        buffer.put(byteStream.toByteArray());
        out.close();
    }

    public static GameMap fromBytes(InputStream inz) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(inz);
        GameMap map = (GameMap)in.readObject();
        in.close();
        return map;
    }
}
