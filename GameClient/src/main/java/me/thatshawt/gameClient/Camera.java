package me.thatshawt.gameClient;

import java.util.concurrent.atomic.AtomicInteger;

public class Camera {

    private AtomicInteger x,y;
    private int renderDistance;

    public Camera(int x, int y, int renderDistance) {
        this.x = new AtomicInteger(x);
        this.y = new AtomicInteger(y);
        this.renderDistance = renderDistance;
    }

    public int getX() {
        return x.get();
    }

    public void setX(int x) {
        this.x.set(x);
    }

    public int getY() {
        return y.get();
    }

    public void setY(int y) {
        this.y.set(y);
    }

    public int getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(int renderDistance) {
        this.renderDistance = renderDistance;
    }
}