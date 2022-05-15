package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.Camera;
import me.thatshawt.gameClient.ClientPlayer;
import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameCore.tile.PlayerTile;
import me.thatshawt.gameCore.tile.Tile;

import java.awt.*;

public class MainGameScreen extends ScreenRenderer{

    public MainGameScreen(GameClient client) {
        super(10);
        this.client = client;
    }

    private final GameClient client;

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
            final int HALF_RENDER_DISTANCE = (int)((float)camera.getRenderDistance()/2.0f);
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
