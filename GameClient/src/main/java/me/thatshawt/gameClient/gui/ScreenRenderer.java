package me.thatshawt.gameClient.gui;

import java.awt.*;

public abstract class ScreenRenderer {

    public int zindex;
    public boolean enabled = true;
    public ScreenRenderer(int zindex) {
        this.zindex = zindex;
    }

    public abstract void render(Graphics g);

}
