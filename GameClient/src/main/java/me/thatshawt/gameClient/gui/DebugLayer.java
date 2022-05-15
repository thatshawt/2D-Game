package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.Camera;
import me.thatshawt.gameClient.ClientPlayer;
import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameCore.game.Entity;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.tile.Tile;
import me.thatshawt.gameCore.tile.TileChunk;

import java.awt.*;

public class DebugLayer extends ScreenRenderer{
    private final GameClient client;

    public DebugLayer(GameClient client) {
        super(100);
        this.client = client;
    }

    @Override
    public void render(Graphics g) {
        ClientPlayer player = client.getPlayer();
        if(player != null){
            final Camera camera = player.getCamera();

            g.setColor(Color.LIGHT_GRAY);
            g.setFont(GameClient.UI_FONT);

            for(TileChunk chunk :
                    client.chunks.chunksWithinRenderDistance(
                            player.getX(), player.getY(), player.getCamera().getRenderDistance())){
                for(Entity entity : chunk.entityList){
                    if(entity instanceof Player) {
                        Player playerEntity = (Player)entity;
                        Point pixelPosition = client.tileToPixel(playerEntity.getX(), playerEntity.getY());
                        g.drawString(playerEntity.getChat(), pixelPosition.x, pixelPosition.y);
                    }
                }
            }


            Point point = client.lastMouseLocation.get();
            Point tilePoint = client.pixelToTile(point);

            if(client.chatting){
                g.drawString(client.chatMessage, 40, 40);
            }

            if(client.debug.get()){
                Tile tile = client.getRenderTileAt(tilePoint.x, tilePoint.y);
                String[] debugMsg = {
                        String.format("mouseTileXY:(%d,%d)\n",tilePoint.x,tilePoint.y),
                        String.format("mouseTile:%s", tile == null ? "null" : tile.getClass().getSimpleName()),
                        String.format("camera:%s",camera)
                };
                FontMetrics fontMetrics = g.getFontMetrics(GameClient.UI_FONT);
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
}
