package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UILayer extends ScreenRenderer{

    protected List<UIComponent> components = new ArrayList<>();

    protected final GameClient client;

    public UILayer(GameClient client, int zindex) {
        super(zindex);
        this.client = client;
    }

    @Override
    public boolean onMouseClick(MouseEvent e){
        for(UIComponent component : components){
            if(!component.enabled)continue;
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y)){
                component.onMouseClick(e);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseDown(MouseEvent e){
        for(UIComponent component : components){
            if(!component.enabled)continue;
            Point mouseLoc = client.lastMouseLocation.get();
            if(component.isInRegion(mouseLoc.x, mouseLoc.y)){
                component.onMouseDown(e);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onMouseUp(MouseEvent e){
        for(UIComponent component : components){
            if(!component.enabled)continue;
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
        for(UIComponent component : components){
            if(!component.enabled)continue;

            if(component.isInRegion(mouseLoc.x, mouseLoc.y))
                component.hovering = true;
            Graphics g1 = g.create(
                    component.x, component.y,
                    component.width,component.height);

            component.render(g1);
            g1.dispose();

            //assume hovering is false for next frame
            component.hovering = false;
        }
    }

}
