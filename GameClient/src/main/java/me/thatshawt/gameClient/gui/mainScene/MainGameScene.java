package me.thatshawt.gameClient.gui.mainScene;

import me.thatshawt.gameClient.GameClient;
import me.thatshawt.gameClient.gui.DebugLayer;
import me.thatshawt.gameClient.gui.Scene;

public class MainGameScene extends Scene {

    public DebugLayer debugLayer;
    public MainGameScreen mainGameScreen;
    public MainGameUILayer mainGameUILayer;

    public MainGameScene(GameClient client) {
        this.debugLayer = new DebugLayer(client);
        this.mainGameScreen = new MainGameScreen(client);
        this.mainGameUILayer = new MainGameUILayer(client);

        addLayer(debugLayer);
        addLayer(mainGameScreen);
        addLayer(mainGameUILayer);
    }
}
