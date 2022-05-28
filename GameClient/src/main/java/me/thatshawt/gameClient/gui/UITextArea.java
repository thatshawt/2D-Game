package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;
import java.awt.event.MouseEvent;

public class UITextArea extends UIComponent{

    public String text = "";

    public UITextArea(int zindex, int x, int y, int width, int height) {
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
        g.setColor(Color.gray);
        g.fillRect(0,0,width,height);

        g.setColor(Color.BLACK);
        drawStringLeft(g, GameClient.UI_FONT, text);
    }
}
