package me.thatshawt.gameClient.gui.mainScene;

import me.thatshawt.gameClient.Camera;
import me.thatshawt.gameClient.ClientPlayer;
import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameClient.gui.ScreenRenderer;
import me.thatshawt.gameCore.tile.AirTile;
import me.thatshawt.gameCore.tile.PlayerTile;
import me.thatshawt.gameCore.tile.Tile;
import me.thatshawt.gameCore.tile.WallTile;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MainGameScreen extends ScreenRenderer {

    public MainGameScreen(GameClient client) {
        super(10);
        this.client = client;
    }

    private final GameClient client;

    @Override
    public boolean onMouseDown(MouseEvent e) {
        if(e.getButton() == 1){
            try {
                Point pixel = e.getPoint();
                Point tileCoord = client.pixelToTile(pixel);
                client.chunks.setTile(tileCoord.x, tileCoord.y, new WallTile());
            }catch (Exception ignore){}
        }else{
            try {
                Point pixel = e.getPoint();
                Point tileCoord = client.pixelToTile(pixel);
                client.chunks.setTile(tileCoord.x, tileCoord.y, new AirTile());
            }catch (Exception ignore){}
        }
        return true;
    }

    @Override
    public boolean onMouseUp(MouseEvent e) {
        return true;
    }

    @Override
    public boolean onMouseClick(MouseEvent e) {
        return true;
    }

    public void render(Graphics g) {
        final int screenWidth = client.getWidth();
        final int screenHeight = client.getHeight();
        ClientPlayer player = client.getPlayer();

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
            g.setFont(GameClient.CHAR_FONT);
            final int HALF_RENDER_DISTANCE = (int)((float)camera.getRenderDistance()/2.0f)+1;
            for(int i = -HALF_RENDER_DISTANCE; i < camera.getRenderDistance(); i++){
                for(int j = -HALF_RENDER_DISTANCE; j < camera.getRenderDistance(); j++){
                    Point tileCoord = new Point(
                            camera.getX() + i - HALF_RENDER_DISTANCE + 1,
                            camera.getY() + j - HALF_RENDER_DISTANCE + 1
                    );
                    Tile tile = client.getRenderTileAt(tileCoord.x, tileCoord.y);
                    char renderChar = client.getRenderCharAtTile(tileCoord.x, tileCoord.y);
                    if(tile instanceof PlayerTile)
                        g.setFont(GameClient.PLAYER_FONT);

                    g.drawString(
                            String.valueOf(renderChar),
                            boxWidth*i + boxWidth/2,
                            boxHeight*j + boxHeight/2);
                    g.setFont(GameClient.CHAR_FONT);
                }
            }
        }

    }
}
