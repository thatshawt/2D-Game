package me.thatshawt.gameClient.gui.mainMenuScene;

import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameClient.gui.Scene;

public class MainMenuScene extends Scene {

    public MainMenuScene(GameClient client) {
        addLayer(new MainMenuUILayer(client, 10));//idk breh
    }
}
