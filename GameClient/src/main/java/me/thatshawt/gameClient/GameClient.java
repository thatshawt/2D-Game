package me.thatshawt.gameClient;

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

//TODO: make the combat with arrows and you hit one tile range i guess
//TODO: after combat add an AI that you can punch to test the combat lmao

public class GameClient extends JPanel implements Runnable {

    public static final int FPS = 24;
    private static final long serialVersionUID = 549255266446745609L;

    private Thread renderThread; //tells the gui to render at a certain FPS
    private Thread packetThread; //listens for incoming packets and processes them
    private GameControllerThread gameControllerThread;

    protected Socket serverConnection;
//    protected TileChunk tileChunk;
    protected ClientPlayer player;
//    protected Camera camera;

    private AtomicReference<Point> lastMouseLocation = new AtomicReference<>(new Point(0,0));
    private boolean chatting = false;
    private String chatMessage = "";
    private AtomicBoolean debug = new AtomicBoolean(true);
    protected ChunkMap chunks = new ChunkMap();

    public GameClient(){
//        tileChunk = new TileChunk();

        this.addMouseWheelListener(e -> {
            if(!chatting){
                player.getCamera().addToRenderDistance(e.getWheelRotation());
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

                if(e.getKeyChar() == KeyEvent.VK_ENTER){//pressed enter
                    if(chatting){
                        try {
                            player.sendChat(chatMessage);
                            chatMessage = "";
                            chatting = false;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }else{//enter typing mode
                        chatting = true;
                    }

                }else if(chatting &&
                        (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == ' ')) {//otherwise we just put all the poop into the pee
//                    System.out.println("typed: " + e.getKeyChar());
                    chatMessage += e.getKeyChar();
                    chatMessage = chatMessage.substring(0, Math.min(chatMessage.length(),GamePacket.MAX_CHAT_LENGTH));
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
//                    System.out.println("hit backspaec");
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

        renderThread = new Thread(this);
        renderThread.start();

        packetThread = new Thread(() -> {
            //always try to do this until the thread is killed
            while(true) {
                //wait for the connection to be not null and connected
                while (!(GameClient.this.serverConnection != null
                        && GameClient.this.serverConnection.isConnected())) {
                    try {
                        Thread.sleep(500);//200 cant be that long lmao idk bruh
//                        System.out.println("sleeping");
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
//                        System.out.println("reading packet...");
                        int packetLength = input.readInt();
                        ServerPacket packet = ServerPacket.fromInt(input.readInt());

//                        System.out.println("got packet idk man");
                        System.out.println("received packet " + packet.name());
                        switch(packet){
                            case ENTITY_POSITION: {
                                UUID uuid = GamePacket.readUUID(input);
                                int x = input.readInt();
                                int y = input.readInt();

//                                System.out.println(uuid);

                                if(chunks.containsEntity(uuid)) chunks.getEntity(uuid).setXY(x,y);

//                                System.out.printf("uuid: %s, x: %d, y: %d\n", uuid.toString(), x, y);
                                break;
                            }
                            case PLAYER_CHAT: {
                                byte[] buffer = new byte[packetLength - 8 - 8];

                                UUID uuid = GamePacket.readUUID(input);
                                input.read(buffer);
                                String message = new String(buffer);
//                                System.out.println("received " + message);

                                Player player = (Player) chunks.getEntity(uuid);
                                player.setChat(message);
                                break;
                            }
                            case PLAYER_LOGIN_RESPONSE: {
                                UUID uuid = GamePacket.readUUID(input);
//                                System.out.println("login responce: " + uuid);
                                this.player = new ClientPlayer(this, 0,0, uuid);
                                chunks.get(ChunkCoord.fromChunkXY(0,0)).addEntity(this.player);

                                System.out.println("logged in as " + uuid);
                                break;
                            }
                            case PLAYER_ENTITY_SPAWN: {
                                UUID uuid = GamePacket.readUUID(input);
//                                System.out.println("added " + uuid + "????!??!?!?!?11!?/1?");

                                Player player = new NetworkPlayer(0,0, uuid);
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
//                                System.out.println("received map data");
                                this.chunks.put(coord, chunk);
                                System.out.println(chunks);
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
//                catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }

            }
        });

        packetThread.start();

        gameControllerThread = new GameControllerThread(this);

        gameControllerThread.init();
    }

    //idea to use JLabel from: https://stackoverflow.com/a/11592041/5513686
    private static final Font CHAR_FONT =
                new Font(new JLabel().getFont().getName(), Font.PLAIN, 25);
    private static final Font PLAYER_FONT =
                new Font("Times New Roman", Font.BOLD, 25);
    private static final Font UI_FONT =
            new Font(new JLabel().getFont().getName(), Font.PLAIN, 18);

    private int getBoxWidth(){
        return this.getWidth() / player.getCamera().getRenderDistance();
    }

    private int getBoxHeight(){
        return this.getHeight() / player.getCamera().getRenderDistance();
    }

    private Point tileToPixel(int tilex, int tiley){
        return new Point(
                (tilex - player.getCamera().getX() + TileChunk.CHUNK_SIZE/2 + 1)*getBoxWidth(),
                (tiley - player.getCamera().getY() + TileChunk.CHUNK_SIZE/2 + 1)*getBoxHeight()
        );
    }

    private boolean tileWithinRenderDistance(int tilex, int tiley){
        Point pixel = tileToPixel(tilex, tiley);
        return pixel.x > 0 && pixel.x < this.getWidth()
                && pixel.y > 0 && pixel.y < this.getHeight();
    }

    private Point pixelToTile(int screenx, int screeny){
        final int boxWidth = getBoxWidth();
        final int boxHeight = getBoxHeight();
        final Camera camera =  player.getCamera();
//        player.getX() + i - gameMap.tiles.length/2 - 1
        int x = (screenx /*- (boxWidth/2)*/)/boxWidth  + (camera.getX() - camera.getRenderDistance()/2 + 1);
        int y = (screeny /*- (boxHeight/2)*/)/boxHeight  + (camera.getY() - camera.getRenderDistance()/2 + 1);
        return new Point(x,y);
    }

    private Point pixelToTile(Point point){
        return pixelToTile(point.x, point.y);
    }

    private Tile getTileAtPixel(int x, int y){
//        System.out.printf("(x,y): %d,%d\n", x, y);
        //out of bounds char
        Entity entityAtLocation = chunks.getFirstEntityAt(x,y);
        ChunkCoord chunkCoord = ChunkCoord.fromChunkXY(x/TileChunk.CHUNK_SIZE,y/TileChunk.CHUNK_SIZE);
        if(!chunks.containsKey(chunkCoord))
            return null;
        else if(entityAtLocation != null)
            return entityAtLocation.getTile(); //entity on the map
        else
            return chunks.tileAt(x,y); //tile at location
    }

    private char getRenderCharAtTile(int x, int y){
        Tile tile = getTileAtPixel(x,y);
        char renderChar = '#';
        if(tile != null){
            renderChar = tile.getChar();
        }
        return renderChar;
    }

//    private Point getTileCoordinateAtXY(int x, int y){
//        player.getX() + i - gameMap.tiles.length/2 - 1,
//                player.getY() + j - gameMap.tiles[0].length/2 - 1
//    }

    private void drawMainGame(Graphics g){
        final int screenWidth = this.getWidth();
        final int screenHeight = this.getHeight();

        if(player == null){
            g.setColor(Color.black);
            g.fillRect(0,0, screenWidth, screenHeight);
            g.setColor(Color.white);
            g.drawString("loading i guess...", screenWidth/2, screenHeight/2);
        }else{
            final Camera camera = player.getCamera();
            final int boxWidth = screenWidth / camera.getRenderDistance();
            final int boxHeight = screenHeight / camera.getRenderDistance();

            //clear screen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, screenWidth, screenHeight);

            //draw tiles
            g.setColor(Color.WHITE);
            g.setFont(CHAR_FONT);
//            final Camera camera = player.getCamera();
            final int HALF_RENDER_DISTANCE = (int)((float)camera.getRenderDistance()/2.0f);
            for(int i = -HALF_RENDER_DISTANCE; i < camera.getRenderDistance(); i++){
                for(int j = -HALF_RENDER_DISTANCE; j < camera.getRenderDistance(); j++){
                    Point tileCoord = new Point(
                            camera.getX() + i - HALF_RENDER_DISTANCE + 1,
                            camera.getY() + j - HALF_RENDER_DISTANCE + 1
                            );
                    Tile tile = getTileAtPixel(tileCoord.x, tileCoord.y);
                    char renderChar = getRenderCharAtTile(tileCoord.x, tileCoord.y);
                    if(tile instanceof PlayerTile)
                        g.setFont(PLAYER_FONT);

                    g.drawString(
                            String.valueOf(renderChar),
                            boxWidth*i + boxWidth/2,
                            boxHeight*j + boxHeight/2);
                    g.setFont(CHAR_FONT);
                }
            }
        }
    }

    private void drawUILayer(Graphics g){
        if(player != null){
            final int screenWidth = this.getWidth();
            final int screenHeight = this.getHeight();
            final Camera camera = player.getCamera();
            final int boxWidth = screenWidth / camera.getRenderDistance();
            final int boxHeight = screenHeight / camera.getRenderDistance();

            g.setColor(Color.LIGHT_GRAY);
            g.setFont(UI_FONT);

            for(Entity entity : chunks.get(ChunkCoord.fromChunkXY(0,0)).entityList){
                if(entity instanceof Player) {
                    Player playerEntity = (Player)entity;
                    Point pixelPosition = tileToPixel(playerEntity.getX(), playerEntity.getY());
//                    System.out.println(pixelPosition);
                    g.drawString(playerEntity.getChat(), pixelPosition.x, pixelPosition.y);
                }
            }

            Point point = lastMouseLocation.get();
            Point tilePoint = pixelToTile(point);

//            g.drawString(String.valueOf(getRenderTileAt(tilePoint.x, tilePoint.y)), point.x, point.y);
//        g.drawString(String.format("(%d, %d)", tilePoint.x, tilePoint.y), point.x, point.y);

            if(chatting){
                //lmao
                g.drawString(chatMessage, 40, 40);
//            System.out.println(chatMessage);
            }

            if(debug.get()){
                Tile tile = getTileAtPixel(tilePoint.x, tilePoint.y);
                String[] debugMsg = {
                        String.format("mouseTileXY:(%d,%d)\n",tilePoint.x,tilePoint.y),
                        String.format("mouseTile:%s", tile == null ? "null" : tile.getClass().getSimpleName()),
                        String.format("camera:%s",camera)
                };
                FontMetrics fontMetrics = g.getFontMetrics(UI_FONT);
                final int height = fontMetrics.getHeight();
                for(int i=0;i<debugMsg.length;i++){
                    String s = debugMsg[i];
                    int x = 5;
                    int y = 30;
                    g.setColor(Color.RED);
                    g.fillRect(x,y+height*(i-1),fontMetrics.stringWidth(s)+5, height+10);
                    g.setColor(Color.white);
                    g.drawString(s, x,y + height*i);
                }

            }
        }
    }

    //TODO: maybe implement this so the user can choose the server to join or something
    private void renderMainMenu(){ }

    //i read online that i was supposed to use a jpanel instead of a jframe sooo i ended up using that
    //it worked cus using a jpanel fixed LOTS of white flickering
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawMainGame(g);
        drawUILayer(g);
    }

    //so this class has two in one basically lmao
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

    public void sendPacket(ClientPacket packet) throws IOException {
        sendPacket(packet, new byte[]{});
    }

    public static void testRun(String[] args, boolean debug){
        JFrame frame = new JFrame();
        GameClient gameClient = new GameClient();

        frame.add(gameClient);

        frame.setLocation(2300,350); //position i used to make debugging quicker
        frame.setSize(650, 650);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        String ip;
        if(debug) ip = "127.0.0.1";
        else ip = "45.77.121.201";

        int port = 25565;

        if(args.length > 0){
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

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
        testRun(args, true);
    }

}
