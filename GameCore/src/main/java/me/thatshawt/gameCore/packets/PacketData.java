package me.thatshawt.gameCore.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface PacketData {

    byte[] toBytes() throws IOException;

//    void toBytes(ByteArrayOutputStream buffer) throws IOException;
}
