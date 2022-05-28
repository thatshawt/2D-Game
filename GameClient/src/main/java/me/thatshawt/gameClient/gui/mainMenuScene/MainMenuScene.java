package me.thatshawt.gameClient.gui.mainMenuScene;

import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameClient.gui.Scene;

import java.util.UUID;

public class MainMenuScene extends Scene {

    protected UUID mainGameUUID;

    public MainMenuScene(GameClient client, UUID uuid) {
        mainGameUUID = uuid;
        addLayer(new MainMenuUILayer(client, 10));//idk breh
    }
}
