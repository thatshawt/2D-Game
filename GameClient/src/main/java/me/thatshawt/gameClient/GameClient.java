package me.thatshawt.gameClient;

import me.thatshawt.gameClient.gui.*;
import me.thatshawt.gameClient.gui.mainScene.MainGameScene;
import me.thatshawt.gameClient.gui.mainScene.MainGameScreen;
import me.thatshawt.gameClient.gui.mainScene.MainGameUILayer;
import me.thatshawt.gameCore.game.Entity;
import me.thatshawt.gameCore.game.NetworkPlayer;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.packets.ClientPacket;
import me.thatshawt.gameCore.packets.GamePacket;
import me.thatshawt.gameCore.packets.PacketDataReader;
import me.thatshawt.gameCore.packets.ServerPacket;
import me.thatshawt.gameCore.tile.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class GameClient extends JPanel implements Runnable {

    public static final int FPS = 24;
    private static final long serialVersionUID = 549255266446745609L;

    private Thread renderThread; //tells the gui to render at a certain FPS (kinda)
    private Thread packetThread; //listens for incoming packets and processes them
    private GameControllerThread gameControllerThread;

    protected Socket serverConnection;
    protected ClientPlayer player;

//    private List<ScreenRenderer> screenRenderers = new ArrayList<>();

    public final AtomicReference<Point> lastMouseLocation = new AtomicReference<>(new Point(0,0));
    public boolean chatting = false;
    public String chatMessage = "";
    public final AtomicBoolean debug = new AtomicBoolean(false);
    public ChunkMap chunks = new ChunkMap();

//    private final DebugLayer debugLayer;

    private Scene activeScene;

    public GameClient(){
        this.addMouseWheelListener(e -> {
            if(!chatting){
                player.getCamera().addToRenderDistance(2*e.getWheelRotation());
            }
        });

        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                for(int i = screenRenderers.size()-1; i>=0; i--){
//                    ScreenRenderer renderer = screenRenderers.get(i);
//                    if(renderer.onMouseClick(e) && renderer.enabled)break;
//                }
                activeScene.handleMouseClick(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
//                for(int i=0;i< screenRenderers.size();i++){
//                for(int i = screenRenderers.size()-1; i>=0; i--){
//                    ScreenRenderer renderer = screenRenderers.get(i);
//                    if(renderer.onMouseDown(e) && renderer.enabled)break;
//                }
                activeScene.handleMouseDown(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
//                for(int i = screenRenderers.size()-1; i>=0; i--){
//                    ScreenRenderer renderer = screenRenderers.get(i);
//                    if(renderer.enabled && renderer.onMouseUp(e))break;
//                }
                activeScene.handleMouseUp(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point point = e.getPoint();
                lastMouseLocation.set(point);
            }
        });

        this.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                MainGameScene gameScene = (MainGameScene)activeScene;
                if(e.getKeyChar() == KeyEvent.VK_ENTER){
                    if(chatting){
                        try {
                            player.sendChat(chatMessage);
                            chatMessage = "";
                            chatting = false;
//                            gameScene.mainGameUILayer.chatBox.text = "";
                            gameScene.mainGameUILayer.chatBox.enabled = false;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }else{
                        chatting = true;
                        gameScene.mainGameUILayer.chatBox.enabled = true;
                    }

                }else if(chatting &&
                        (e.getKeyChar() >= 32 && e.getKeyChar() <= 126)
                        ){
                    chatMessage += e.getKeyChar();
                    chatMessage = chatMessage.substring(0, Math.min(chatMessage.length(),GamePacket.MAX_CHAT_LENGTH));
//                    gameScene.mainGameUILayer.chatBox.text = chatMessage;
                }else{
                    switch(e.getKeyChar()){
                        case 'w':
                        case 'W':
                            player.moveUp();
                            break;
                        case 's':
                        case 'S':
                            player.moveDown();
                            break;
                        case 'a':
                        case 'A':
                            player.moveLeft();
                            break;
                        case 'd':
                        case 'D':
                            player.moveRight();
                            break;
                        case '3':
                            debug.set(!debug.get());
//                            debugLayer.enabled = debug.get();
                            ((MainGameScene)activeScene).debugLayer.enabled = debug.get();
                            break;
                        case '+':
                            player.getCamera().addToRenderDistance(-1);
                            break;
                        case '-':
                            player.getCamera().addToRenderDistance(1);
                            break;
                    }
                }

            }

            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
                    if(chatting){
                        try {
                            chatMessage = chatMessage.substring(0, chatMessage.length() - 1);
                        }catch(Exception ignore){}
                    }
                }else if(e.getKeyCode() == KeyEvent.VK_ESCAPE && chatting){
                    chatting = false;
                }
            }

            public void keyReleased(KeyEvent e) {

            }
        });

//        screenRenderers.add(new MainGameScreen(this));
//        screenRenderers.add(new UILayer(this));
//
//        debugLayer = new DebugLayer(this);
//        screenRenderers.add(debugLayer);

        //render the smallest zindexes first
//        screenRenderers.sort(Comparator.comparingInt(ScreenRenderer::getZindex));

//        activeScene = new Scene();
//        activeScene.addLayer(new MainGameScreen(this));
//        activeScene.addLayer(new MainGameUILayer(this));
//
//        debugLayer = new DebugLayer(this);
//        activeScene.addLayer(debugLayer);

        activeScene = new MainGameScene(this);

        renderThread = new Thread(this);
        renderThread.start();

        packetThread = new Thread(() -> {
            while(true) {
                while (!(GameClient.this.serverConnection != null
                        && GameClient.this.serverConnection.isConnected())) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //connection is now made and we will now start parsing packets
                Socket server = GameClient.this.serverConnection;
                try {
                    DataInputStream input = new DataInputStream(new BufferedInputStream(server.getInputStream()));

                    //packet parsing and handling
                    while(true) {
                        int packetLength = input.readInt();
                        ServerPacket packet = ServerPacket.fromInt(input.readInt());

                        switch(packet){
                            case ENTITY_POSITION: {
                                UUID uuid = GamePacket.readUUID(input);
                                int x = input.readInt();
                                int y = input.readInt();

                                if(chunks.containsEntity(uuid)) chunks.getEntity(uuid).setXY(x,y);
                                break;
                            }
                            case PLAYER_CHAT: {
                                byte[] buffer = new byte[packetLength - 8 - 8];

                                UUID uuid = GamePacket.readUUID(input);
                                input.read(buffer);
                                String message = new String(buffer);

                                Player player = (Player) chunks.getEntity(uuid);
                                player.setChat(message);
                                break;
                            }
                            case PLAYER_LOGIN_RESPONSE: {
                                UUID uuid = GamePacket.readUUID(input);
                                this.player = new ClientPlayer(this, 0,0, uuid);
                                chunks.get(ChunkCoord.fromChunkXY(0,0)).addEntity(this.player);

                                System.out.println("logged in as " + uuid);
                                break;
                            }
                            case PLAYER_ENTITY_SPAWN: {
                                UUID uuid = GamePacket.readUUID(input);

                                Player player = new NetworkPlayer(chunks, 0,0, uuid);
                                if(!chunks.containsEntity(uuid)){
                                    chunks.get(ChunkCoord.fromChunkXY(0,0)).addEntity(player);
                                }
                                break;
                            }
                            case ENTITY_REMOVE:{
                                UUID uuid = GamePacket.readUUID(input);

                                try {
                                    chunks.removeEntity(uuid);
                                }catch(Exception e){
                                    System.out.println("tried to remove nonexistent entity");
                                }
                            }
                            case MAP_DATA:{
                                ChunkCoord coord = PacketDataReader.readChunkCoord(input);
                                TileChunk chunk = PacketDataReader.readChunk(input);
                                this.chunks.put(coord, chunk);
                                System.out.println("received " + chunk);
                            }
                            default:
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        packetThread.start();

        gameControllerThread = new GameControllerThread(this);
        gameControllerThread.init();
    }

    //idea to use JLabel from: https://stackoverflow.com/a/11592041/5513686
    public static final Font CHAR_FONT =
                new Font(new JLabel().getFont().getName(), Font.PLAIN, 25);
    public static final Font PLAYER_FONT =
                new Font("Times New Roman", Font.BOLD, 25);
    public static final Font UI_FONT =
            new Font(new JLabel().getFont().getName(), Font.PLAIN, 18);

    private int getBoxWidth(){
        return this.getWidth() / player.getCamera().getRenderDistance();
    }

    private int getBoxHeight(){
        return this.getHeight() / player.getCamera().getRenderDistance();
    }

    public Point tileToPixel(int tilex, int tiley){
        return new Point(
                (tilex - player.getCamera().getX() + player.getCamera().getRenderDistance()/2 - 0)*getBoxWidth(),
                (tiley - player.getCamera().getY() + player.getCamera().getRenderDistance()/2 - 0)*getBoxHeight()
        );
    }

    private Point pixelToTile(int screenx, int screeny){
        final int boxWidth = getBoxWidth();
        final int boxHeight = getBoxHeight();
        final Camera camera =  player.getCamera();
        int x = screenx/boxWidth  + (camera.getX() - camera.getRenderDistance()/2 + 0);
        int y = screeny/boxHeight + (camera.getY() - camera.getRenderDistance()/2 + 0);
        return new Point(x,y);
    }

    private boolean tileWithinRenderDistance(int tilex, int tiley){
        Point pixel = tileToPixel(tilex, tiley);
        return pixel.x > 0 && pixel.x < this.getWidth()
                && pixel.y > 0 && pixel.y < this.getHeight();
    }

    public Point pixelToTile(Point point){
        return pixelToTile(point.x, point.y);
    }

    public Tile getRenderTileAt(int x, int y){
        Entity entityAtLocation = chunks.getFirstEntityAt(x,y);
        ChunkCoord chunkCoord = ChunkCoord.fromChunkXY(x/TileChunk.CHUNK_SIZE,y/TileChunk.CHUNK_SIZE);
        if(!chunks.containsKey(chunkCoord))
            return null;
        else if(entityAtLocation != null)
            return entityAtLocation.getTile(); //entity on the map
        else
            return chunks.tileAt(x,y); //tile at location
    }

    public char getRenderCharAtTile(int x, int y){
        Tile tile = getRenderTileAt(x,y);
        char renderChar = '#';
        if(tile != null){
            renderChar = tile.getChar();
        }
        return renderChar;
    }

    //i read online that i was supposed to use a jpanel instead of a jframe sooo i ended up using that
    //it worked cus using a jpanel fixed LOTS of white flickering
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        activeScene.render(g);
    }

    public void run() {
        while(true){//simple loop for simple game
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repaint();
        }
    }

    public void sendPacket(ClientPacket packet, byte[] data) throws IOException {
        GamePacket.sendPacket(packet.ordinal(), serverConnection.getOutputStream(), data);
    }

    public static void testRun(String[] args, boolean debug){
        JFrame frame = new JFrame();
        GameClient gameClient = new GameClient();

        frame.add(gameClient);

        if(debug)frame.setLocation(2300,350);
        frame.setSize(650, 650);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String ipProperty = System.getProperty("ip");
        String portProperty = System.getProperty("port");

        String ip = ipProperty != null ? ipProperty : "127.0.0.1";
        int port = portProperty != null ? Integer.parseInt(portProperty) : 25565;
//        if(args.length > 0){
//            ip = args[0];
//            port = Integer.parseInt(args[1]);
//        }

        try {
            gameClient.serverConnection = new Socket(ip, port);

            //only let user type input after they connect to server
            //so janky but whatever im in a hurry
            frame.setVisible(true);
            gameClient.setFocusable(true);//need to do this so we can detect keyboard input
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
//        System.out.println(System.getProperties().toString().replace(',','\n'));
        testRun(args, System.getProperty("debug") != null);
    }

    public ClientPlayer getPlayer() {
        return player;
    }
}
