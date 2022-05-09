package me.thatshawt.gameCore.tile;

import me.thatshawt.gameCore.game.Entity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkMap extends ConcurrentHashMap<ChunkCoord, TileChunk> {

    private static final long serialVersionUID = 4555055484744036911L;

    public ChunkMap() {
        super(10,0.75f,2);
        put(ChunkCoord.fromChunkXY(0,0), new TileChunk());//by default have the 0,0 chunk so like yea
    }

    public Entity getEntity(UUID uuid) {
        for(TileChunk chunk : this.values()){
            Entity entity = chunk.getEntity(uuid);
            if(entity != null)return entity;
        }
        return null;
    }

    public Tile tileAt(int tilex, int tiley){
//        System.out.println("" + tilex + " " + tiley);
        try {
            int chunkx = tilex/TileChunk.CHUNK_SIZE;
            int chunky = tiley/TileChunk.CHUNK_SIZE;

            int idkx = tilex%TileChunk.CHUNK_SIZE;
            int idky = tiley%TileChunk.CHUNK_SIZE;
            int innertilex = idkx;
            int innertiley = idky;
            if(idkx < 1)
                innertilex = TileChunk.CHUNK_SIZE+idkx-1;
            if(idky < 1)
                innertiley = TileChunk.CHUNK_SIZE+idky-1;
            return get(ChunkCoord.fromChunkXY(chunkx, chunky))
                    .tiles[innertilex][innertiley];
        }catch(NullPointerException e){
            return null;
        }
    }

    public Entity getFirstEntityAt(int tilex, int tiley){
        try {
            TileChunk chunk =
                    get(ChunkCoord.fromChunkXY(tilex / TileChunk.CHUNK_SIZE, tiley / TileChunk.CHUNK_SIZE));
            return chunk.firstEntityOnLocation(tilex%TileChunk.CHUNK_SIZE,tiley%TileChunk.CHUNK_SIZE);
        }catch(NullPointerException e){
            return null;
        }
    }

    public boolean containsEntity(UUID uuid){
        return getEntity(uuid) != null;
    }

    public void removeEntity(UUID uuid){
        for(TileChunk chunk : this.values()){
            Entity entity = chunk.getEntity(uuid);
            if(entity != null)chunk.removeEntity(uuid);
        }
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        for(ChunkCoord coord : keySet()){
            builder.append(coord);
            builder.append(get(coord));
            builder.append("\n");
        }
        builder.append("}");
        return builder.toString();
    }

}
