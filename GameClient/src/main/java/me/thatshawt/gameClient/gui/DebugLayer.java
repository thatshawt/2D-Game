package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.Camera;
import me.thatshawt.gameClient.ClientPlayer;
import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameCore.game.Entity;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.tile.Tile;
import me.thatshawt.gameCore.tile.TileChunk;

import java.awt.*;
import java.awt.event.MouseEvent;

public class DebugLayer extends ScreenRenderer{
    private final GameClient client;

    public DebugLayer(GameClient client) {
        super(100);
        this.client = client;
        this.enabled = client.debug.get();
    }

    @Override
    public boolean onMouseDown(MouseEvent e) {
        return false;
    }

    @Override
    public boolean onMouseUp(MouseEvent e) {
        return false;
    }

    @Override
    public boolean onMouseClick(MouseEvent e) {
        return false;
    }

    @Override
    public void render(Graphics g) {
        ClientPlayer player = client.getPlayer();
        if(player != null){
            final Camera camera = player.getCamera();

            Point point = client.lastMouseLocation.get();
            Point tilePoint = client.pixelToTile(point);

            Tile tile = client.getRenderTileAt(tilePoint.x, tilePoint.y);
            String[] debugMsg = {
                    String.format("mouseTileXY:(%d,%d)\n",tilePoint.x,tilePoint.y),
                    String.format("mouseTile:%s", tile == null ? "null" : tile.getClass().getSimpleName()),
                    String.format("camera:%s",camera)
            };
            FontMetrics fontMetrics = g.getFontMetrics(GameClient.UI_FONT);
            final int height = fontMetrics.getHeight();
            for(int i=0; i<debugMsg.length; i++){
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
