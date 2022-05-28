package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class UIComponent {

    //this is the zindex within a UILayer
    protected int zindex;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean hovering = false;
    public boolean enabled = true;
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

    protected void drawStringLeft(Graphics g, Font f, String s){
        FontMetrics fontMetrics = g.getFontMetrics(f);
        final int stringHeight = fontMetrics.getHeight();
        final int stringWidth = fontMetrics.stringWidth(s);
        g.drawString(s, 0, height/2 + stringHeight/4);
    }

    protected void drawStringCentered(Graphics g, Font f, String s){
        FontMetrics fontMetrics = g.getFontMetrics(f);
        final int stringHeight = fontMetrics.getHeight();
        final int stringWidth = fontMetrics.stringWidth(s);
        g.drawString(s, (width-stringWidth)/2, height/2 + stringHeight/4);
    }

    public abstract void onMouseDown(MouseEvent e);
    public abstract void onMouseUp(MouseEvent e);
    public abstract void onMouseClick(MouseEvent e);

    public abstract void render(Graphics g);
}
