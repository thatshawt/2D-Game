package me.thatshawt.gameCore.tile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ChunkCoord implements Serializable {

    private static final long serialVersionUID = 2052994042185807896L;
    public final int x,y;

    public ChunkCoord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(this);
        return bytes.toByteArray();
    }

    public static ChunkCoord fromChunkXY(int x, int y){
        return new ChunkCoord(x,y);
    }

    public static ChunkCoord fromTileXY(int x, int y){
        return new ChunkCoord(x/TileChunk.CHUNK_SIZE,y/TileChunk.CHUNK_SIZE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkCoord that = (ChunkCoord) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "ChunkCoord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
