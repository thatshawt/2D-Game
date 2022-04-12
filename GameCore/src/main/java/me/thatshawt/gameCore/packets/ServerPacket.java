package me.thatshawt.gameCore.packets;

import java.util.HashMap;
import java.util.Map;

public enum ServerPacket {

    INVALID,
    CLIENT_KICK,//kick a client
    ENTITY_POSITION,//tells a client where a player is on the map
//    MAP_DATA,//give client map tile array
    PLAYER_LOGIN_RESPONSE,//sent to a player to tell them their player id
    PLAYER_ENTITY_SPAWN,
    ENTITY_REMOVE,
    PLAYER_CHAT;//every time someone chats, broadcast the message with this

    private static final Map<Integer, ServerPacket> packetMap = new HashMap<>();
    static{
        for(ServerPacket packet : ServerPacket.values()){
            packetMap.put(packet.ordinal(), packet);
        }
    }

    public static ServerPacket fromInt(int val){
        return packetMap.getOrDefault(val, INVALID);
    }



}
