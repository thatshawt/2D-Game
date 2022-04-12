package me.thatshawt.gameServer;

import me.thatshawt.gameCore.game.Entity;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.packets.GamePacket;
import me.thatshawt.gameCore.packets.ServerPacket;
import me.thatshawt.gameCore.tile.GameMap;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class GameServer{

    private ServerSocket serverSocket;
    //make it synchronized cus i access it from main thread and clientThread thread
    protected List<ServerPlayerHandler> players = Collections.synchronizedList(new ArrayList<>());
    protected GameMap map;

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        map = new GameMap();

        //continue doing this i guess
        while(true) {
            ServerPlayer player = new ServerPlayer(this, serverSocket.accept(), 0, 0);

            ServerPlayerHandler serverPlayerHandler = new ServerPlayerHandler(this, player);

            players.add(serverPlayerHandler);
        }
    }

    public GameServer() throws IOException {
        this(1337);
    }

    public void sendPacket(ServerPlayer serverPlayer, ServerPacket packet, byte[] data) throws IOException {
        GamePacket.sendPacket(packet.ordinal(), serverPlayer.socket.getOutputStream(), data);
    }

    public void sendPacket(ServerPlayer serverPlayer, ServerPacket packet) throws IOException {
        sendPacket(serverPlayer, packet, new byte[]{});
    }

    /*
    ignores exceptions
     */
    public void broadcastPacket(ServerPacket packet, byte[] data) {
            for (ServerPlayerHandler serverPlayerHandler : players) {
                try {
                    sendPacket(serverPlayerHandler.getPlayer(), packet, data);
                }catch(IOException ignore){
//                    ignore.printStackTrace();
                }
            }

    }

    public static void main(String[] args) {
        try {
            int port = 1337;
            if(args.length > 0)port = Integer.parseInt(args[0]);//you can put ur own port if you want

            GameServer server = new GameServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
