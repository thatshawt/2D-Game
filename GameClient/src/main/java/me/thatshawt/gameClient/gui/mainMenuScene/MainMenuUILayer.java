package me.thatshawt.gameClient.gui.mainMenuScene;

import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameClient.gui.UIButton;
import me.thatshawt.gameClient.gui.UILabel;
import me.thatshawt.gameClient.gui.UILayer;

import java.awt.*;
import java.awt.event.MouseEvent;

public class MainMenuUILayer extends UILayer {

    public MainMenuUILayer(GameClient client, int zindex) {
        super(client, zindex);

        int playButtonWidth = 70;
        int playButtonHeight = 40;
        UIButton playButton = new UIButton(0,0,0,0) {
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
                //center it i guess
                this.x = client.getWidth()/2 - playButtonWidth/2;
                this.y = 140;
                this.width = playButtonWidth;
                this.height = playButtonHeight;
                super.render(g);
            }
        };
        playButton.text = "Play";
        components.add(playButton);


        //TODO: fix this janky stuff right here
        int titleWidth = 150;
        int titleHeight = 50;
        UILabel titleLabel = new UILabel(10,0,0,0,0){
            @Override
            public void render(Graphics g) {
                this.x = client.getWidth()/2 - titleWidth/2;
                this.y = 10;
                this.width = titleWidth;
                this.height = titleHeight;

                super.render(g);
            }
        };
        titleLabel.text = "McDonald's Sim.";
        components.add(titleLabel);
    }

}
