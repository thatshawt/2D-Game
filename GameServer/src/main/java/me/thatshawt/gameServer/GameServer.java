package me.thatshawt.gameServer;

import me.thatshawt.gameCore.packets.GamePacket;
import me.thatshawt.gameCore.packets.ServerPacket;
import me.thatshawt.gameCore.tile.GameMap;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameServer{

    private final ServerSocket serverSocket;
    //make it synchronized cus i access it from main thread and clientThread thread
    private Thread playerAcceptingThread;
    protected List<ServerPlayerHandler> players = Collections.synchronizedList(new ArrayList<>());
    protected GameMap map;

    public GameServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        map = new GameMap(10);

        playerAcceptingThread = new Thread(() -> {
            while(true) {

                try {
                    ServerPlayer player
                            = new ServerPlayer(GameServer.this, serverSocket.accept(), 0, 0);
                    ServerPlayerHandler serverPlayerHandler
                            = new ServerPlayerHandler(GameServer.this, player);

                    players.add(serverPlayerHandler);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        playerAcceptingThread.start();
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
            int port = 25565;
            if(args.length > 0)port = Integer.parseInt(args[0]);//you can put ur own port if you want

            System.out.println("listening on" + port);

            GameServer server = new GameServer(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
