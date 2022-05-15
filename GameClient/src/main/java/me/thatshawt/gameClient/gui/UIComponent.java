package me.thatshawt.gameClient.gui;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class UIComponent {

    //this is the zindex within a UILayer
    protected int zindex;
    protected final int x, y, width, height;
    protected boolean hovering = false;
    public UIComponent(int zindex, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.zindex = zindex;
    }

    /**
     *  returns whether a point lies inside a component.
     *  used to check if a user clicked a component
     * @return true if the point lies within the component, false otherwise
     */
    public boolean isInRegion(int x, int y){
        return x <= this.x+width  && x >= this.x
            && y <= this.y+height && y >= this.y;
    }

    abstract void onMouseDown(MouseEvent e);
    abstract void onMouseUp(MouseEvent e);
    abstract void onMouseClick(MouseEvent e);

    abstract void render(Graphics g);
}