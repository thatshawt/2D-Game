package me.thatshawt.gameCore.packets;

import me.thatshawt.gameCore.tile.ChunkCoord;
import me.thatshawt.gameCore.tile.TileChunk;

import java.io.*;
import java.util.UUID;

public final class GamePacket {

    //I dont really know why lmao
    public static final int MAX_CHAT_LENGTH = 128;

    public static void sendPacket(int packetID, OutputStream output, byte[] data) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(output);

        // add 4 bytes cus thats the packet id
        outputStream.writeInt(data.length + 4); //packet length
        outputStream.writeInt(packetID);          //packet id
        outputStream.write(data);                 //packet data
    }

    public static void putUUID(UUID uuid, ByteArrayOutputStream buffer) throws IOException {
        DataOutputStream out = new DataOutputStream(buffer);
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(DataInputStream input) throws IOException {
        return new UUID(input.readLong(), input.readLong());
    }

}
