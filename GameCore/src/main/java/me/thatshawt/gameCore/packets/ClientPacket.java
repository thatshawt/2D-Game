package me.thatshawt.gameCore.packets;

import java.util.HashMap;
import java.util.Map;

public enum ClientPacket {

    INVALID,
    CLIENT_HEARTBEAT,
    PLAYER_LOGIN,
    PLAYER_POSITION,
    PLAYER_MOVE,
    PLAYER_CHAT;

    private static final Map<Integer, ClientPacket> packetMap = new HashMap<>();
    static{
        for(ClientPacket packet : ClientPacket.values()){
            packetMap.put(packet.ordinal(), packet);
        }
    }

    public static ClientPacket fromInt(int val){
        return packetMap.getOrDefault(val, INVALID);
    }

}
