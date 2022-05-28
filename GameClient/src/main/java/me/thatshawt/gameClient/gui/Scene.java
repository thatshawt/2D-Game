package me.thatshawt.gameClient.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Scene {

    public final UUID uuid;
    private List<ScreenRenderer> layers = new ArrayList<>();

    public Scene(UUID uuid) {
        this.uuid = uuid;
    }

    public Scene(){
        this(UUID.randomUUID());
    }

    /**
     * internal method to keep the layers sorted by their z-index.
     *
     */
    public void sortLayers(){
        layers.sort(Comparator.comparingInt(ScreenRenderer::getZindex));
    }

    public boolean containsLayer(ScreenRenderer layer){
        return layers.contains(layer);
    }

    public void addLayer(ScreenRenderer renderer){
        sortLayers();
        layers.add(renderer);
    }

    /**
     *
     * @return returns true if successful, false otherwise
     */
    public boolean removeLayer(ScreenRenderer layer){
        sortLayers();
        return layers.remove(layer);
    }

    public void render(Graphics g){
        for(ScreenRenderer renderer : layers) {
            if (renderer.enabled) renderer.render(g);
        }
    }

    public void handleMouseDown(MouseEvent e){
        for(int i = layers.size()-1; i>=0; i--){
            ScreenRenderer renderer = layers.get(i);
            if(renderer.onMouseDown(e) && renderer.enabled)break;
        }
    }

    public void handleMouseUp(MouseEvent e){
        for(int i = layers.size()-1; i>=0; i--){
            ScreenRenderer renderer = layers.get(i);
            if(renderer.onMouseUp(e) && renderer.enabled)break;
        }
    }

    public void handleMouseClick(MouseEvent e){
        for(int i = layers.size()-1; i>=0; i--){
            ScreenRenderer renderer = layers.get(i);
            if(renderer.onMouseClick(e) && renderer.enabled)break;
        }
    }


}
