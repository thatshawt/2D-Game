package me.thatshawt.gameCore.packets;

import me.thatshawt.gameCore.tile.ChunkCoord;
import me.thatshawt.gameCore.tile.TileChunk;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public final class PacketDataReader {

    public static TileChunk readChunk(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        return (TileChunk)objectInputStream.readObject();
    }

    public static ChunkCoord readChunkCoord(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(in);
        return (ChunkCoord)objectInputStream.readObject();
    }

}
