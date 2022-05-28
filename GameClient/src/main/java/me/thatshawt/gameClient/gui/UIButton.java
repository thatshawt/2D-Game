package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;

public abstract class UIButton extends UIComponent{

    public String text = "Button";

    public UIButton(int x, int y, int width, int height) {
        super(1, x, y, width, height);
    }

    @Override
    public void render(Graphics g) {
        Color bg = hovering ? Color.DARK_GRAY : Color.GRAY;
        g.setColor(bg);
        g.fillRect(0,0,width,height);

        g.setFont(GameClient.UI_FONT);
        g.setColor(Color.BLACK);
        drawStringCentered(g, GameClient.UI_FONT, text);
    }
}
