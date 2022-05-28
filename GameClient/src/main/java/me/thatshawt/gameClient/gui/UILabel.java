package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;
import java.awt.event.MouseEvent;

public class UILabel extends UIComponent{

    public String text;

    public UILabel(int zindex, int x, int y, int width, int height) {
        super(zindex, x, y, width, height);
    }

    @Override
    public void onMouseDown(MouseEvent e) {

    }

    @Override
    public void onMouseUp(MouseEvent e) {

    }

    @Override
    public void onMouseClick(MouseEvent e) {

    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.BLACK);
        drawStringCentered(g, GameClient.UI_FONT, text);
    }
}
