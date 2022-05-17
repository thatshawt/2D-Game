package me.thatshawt.gameClient.gui;

import me.thatshawt.gameClient.GameClient;

import java.awt.*;

public abstract class UIButton extends UIComponent{

    public String text = "Button";

    public UIButton(int x, int y, int width, int height) {
        super(1, x, y, width, height);
    }

    @Override
    void render(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
//        g2d.
        Color bg = hovering ? Color.DARK_GRAY : Color.GRAY;
        g.setColor(bg);
        g.fillRect(x,y,width,height);

        g.setFont(GameClient.UI_FONT);
        g.setColor(Color.BLACK);
//        FontMetrics fontMetrics = g.getFontMetrics(GameClient.UI_FONT);
//        final int stringHeight = fontMetrics.getHeight();
//        final int stringWidth = fontMetrics.stringWidth(text);
//        g.drawString(text, x+(width-stringWidth)/2, y+(height-stringHeight)/2 + stringHeight/2);
        drawStringCentered(g, GameClient.UI_FONT, text);
    }
}
