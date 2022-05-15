package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
//import java.util.List;

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

    public void passMouseClickEvent(MouseEvent e){
        for(UIComponent component : components){
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y))component.onMouseClick(e);
        }
    }
    public void passMouseDownEvent(MouseEvent e){
        for(UIComponent component : components){
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y))component.onMouseDown(e);
        }
    }
    public void passMouseUpEvent(MouseEvent e){
        for(UIComponent component : components){
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y))component.onMouseUp(e);
        }
    }

    @Override
    public void render(Graphics g) {
        Point mouseLoc = client.lastMouseLocation.get();
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
