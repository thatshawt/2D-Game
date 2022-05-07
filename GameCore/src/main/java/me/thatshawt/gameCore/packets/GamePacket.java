package me.thatshawt.gameCore.packets;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class GamePacket {

    public static final int MAX_CHAT_LENGTH = 128;

    public static void sendPacket(int packetID, OutputStream output, byte[] data) throws IOException {
        DataOutputStream outputStream = new DataOutputStream(output);

        // add 1 to include the packet id ( ordinal() )
        outputStream.writeInt(data.length + 4);
        outputStream.writeInt(packetID);
        outputStream.write(data);
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
