package me.thatshawt.gameClient.gui.mainScene;

import me.thatshawt.gameClient.ClientPlayer;
import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameClient.gui.ScreenRenderer;
import me.thatshawt.gameClient.gui.UIComponent;
import me.thatshawt.gameClient.gui.UILayer;
import me.thatshawt.gameCore.game.Entity;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.tile.TileChunk;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class MainGameUILayer extends UILayer {

    public MainGameUILayer(GameClient client) {
        super(client, 20);
    }

    @Override
    public void render(Graphics g) {
        ClientPlayer player = client.getPlayer();
        if(player != null) {
            g.setColor(Color.LIGHT_GRAY);
            g.setFont(GameClient.UI_FONT);

            for (TileChunk chunk :
                    client.chunks.chunksWithinRenderDistance(
                            player.getX(), player.getY(), player.getCamera().getRenderDistance())) {
                for (Entity entity : chunk.entityList) {
                    if (entity instanceof Player) {
                        Player playerEntity = (Player) entity;
                        Point pixelPosition = client.tileToPixel(playerEntity.getX(), playerEntity.getY());
                        g.drawString(playerEntity.getChat(), pixelPosition.x, pixelPosition.y);
                    }
                }
            }

//            if (client.chatting) {
//                g.drawString(client.chatMessage, 40, 40);
//            }
        }

        super.render(g);//render all the components
    }
}
