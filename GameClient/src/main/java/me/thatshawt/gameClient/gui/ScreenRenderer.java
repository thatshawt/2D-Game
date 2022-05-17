package me.thatshawt.gameClient.gui;

import java.awt.*;
import java.awt.event.MouseEvent;

public abstract class ScreenRenderer {

    public int zindex;
    public boolean enabled = true;
    public ScreenRenderer(int zindex) {
        this.zindex = zindex;
    }

    public abstract boolean onMouseDown(MouseEvent e);
    public abstract boolean onMouseUp(MouseEvent e);
    public abstract boolean onMouseClick(MouseEvent e);

    public abstract void render(Graphics g);

}
