package me.thatshawt.gameCore.tile;

import me.thatshawt.gameCore.game.Direction;
import me.thatshawt.gameCore.game.Entity;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkMap extends ConcurrentHashMap<ChunkCoord, TileChunk> {

    private static final long serialVersionUID = 4555055484744036911L;

    public ChunkMap() {
        super(10,0.75f,2);
        put(ChunkCoord.fromChunkXY(0,0), new TileChunk());//by default have the 0,0 chunk so like yea
    }

//    public void moveEntityToPosition(UUID uuid, Position position){
//        for(TileChunk chunk : this.values()){
//            Entity entity = chunk.getEntity(uuid);
//            if(entity != null){
//
//            }
//        }
//    }


    private Point getInnerTile(int tilex, int tiley){
        int tileoffsetx = tilex%TileChunk.CHUNK_SIZE;
        int tileoffsety = tiley%TileChunk.CHUNK_SIZE;
        int innertilex = tileoffsetx;
        int innertiley = tileoffsety;

        return new Point(innertilex,innertiley);
    }

    public void setTile(int tilex, int tiley, Tile tile){
        try{
            Point innertile = getInnerTile(tilex, tiley);
            get(ChunkCoord.fromTileXY(tilex, tiley)).tiles[innertile.x][innertile.y] = tile;
        }catch(Exception ignore){}
    }

    public Tile tileAt(int tilex, int tiley){
        try {
            Point innertile = getInnerTile(tilex, tiley);
            return get(ChunkCoord.fromTileXY(tilex, tiley))
                    .tiles[innertile.x][innertile.y];
        }catch(Exception ignore){
            return null;
        }
    }

    public Entity getFirstEntityAt(int tilex, int tiley){
        try {
            TileChunk chunk = get(ChunkCoord.fromTileXY(tilex, tiley));
            return chunk.firstEntityOnLocation(tilex,tiley);
        }catch(NullPointerException e){
            return null;
        }
    }

    public Entity getEntity(UUID uuid) {
        for(TileChunk chunk : this.values()){
            Entity entity = chunk.getEntity(uuid);
            if(entity != null)return entity;
        }
        return null;
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

    /**
     * gets the chunks that should be in view of an entity
     * @param tilex entity x position in tiles
     * @param tiley entity y position in tiles
     * @param renderDistance the number of tiles the entity is rendering
     * @return list of chunks in view of entity, aka chunks it should render
     */
    public Set<TileChunk> chunksWithinRenderDistance(int tilex, int tiley, int renderDistance){
        ChunkCoord topLeft = ChunkCoord.fromTileXY(tilex - renderDistance, tiley - renderDistance);
        ChunkCoord topRight = ChunkCoord.fromTileXY(tilex + renderDistance, tiley - renderDistance);
        ChunkCoord bottomLeft = ChunkCoord.fromTileXY(tilex - renderDistance, tiley + renderDistance);
        ChunkCoord bottomRight = ChunkCoord.fromTileXY(tilex + renderDistance, tiley + renderDistance);

        Set<TileChunk> renderChunks = new HashSet<>();

        //add four corners
        renderChunks.add(get(topLeft));
        renderChunks.add(get(topRight));
        renderChunks.add(get(bottomLeft));
        renderChunks.add(get(bottomRight));

        //add chunks between the corners via "scanning" the chunks
        for(int x = topLeft.x; x < topRight.x;x++){
            for(int y = topLeft.y; y < bottomLeft.y; y++ ){
                renderChunks.add(get(ChunkCoord.fromChunkXY(x,y)));
            }
        }

        try {
            renderChunks.remove(null);//remove all null's cus we dont need them
        }catch(Exception ignore){}

        return renderChunks;
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
