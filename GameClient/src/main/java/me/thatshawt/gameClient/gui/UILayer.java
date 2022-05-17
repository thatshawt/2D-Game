package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.ClientPlayer;
import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameCore.game.Entity;
import me.thatshawt.gameCore.game.Player;
import me.thatshawt.gameCore.tile.TileChunk;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UILayer extends ScreenRenderer{

    private List<UIComponent> components = new ArrayList<>();

    private final GameClient client;
    public UILayer(GameClient client) {
        super(20);
        this.client = client;

        components.add(new UIButton(10, 10, 200, 100) {
            @Override
            void onMouseDown(MouseEvent e) {
                text += "clicked";
            }

            @Override
            void onMouseUp(MouseEvent e) {
                text = "unclicked";
            }

            @Override
            void onMouseClick(MouseEvent e) {

            }
        });
    }

    public boolean onMouseClick(MouseEvent e){
        for(UIComponent component : components){
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y)){
                component.onMouseClick(e);
                return true;
            }
        }
        return false;
    }

    public boolean onMouseDown(MouseEvent e){
        for(UIComponent component : components){
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y)){
                component.onMouseDown(e);
                return true;
            }
        }
        return false;
    }

    public boolean onMouseUp(MouseEvent e){
        for(UIComponent component : components){
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y)){
                component.onMouseUp(e);
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(Graphics g) {
        Point mouseLoc = client.lastMouseLocation.get();
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

            if (client.chatting) {
                g.drawString(client.chatMessage, 40, 40);
            }
        }

        for(UIComponent component : components){
            if(component.isInRegion(mouseLoc.x, mouseLoc.y))
                component.hovering = true;
            Graphics g1 = g.create(component.x,component.y,component.width,component.height);
            component.render(g1);
            g1.dispose();
            component.hovering = false;
        }
    }
}
