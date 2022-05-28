package me.thatshawt.gameClient;

import me.thatshawt.gameClient.gui.Scene;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SceneManager {
    private static final long serialVersionUID = 6887868520515782468L;

    private List<Scene> scenes = new ArrayList<>();

    private UUID activeSceneID = null;

    public SceneManager(){

    }

    /**
     *
     * @return returns false if scene was already added
     */
    public boolean addScene(Scene scene){
        if(containsScene(scene.uuid))return false;

        scenes.add(scene);
        return true;
    }


    /**
     *
     * @param uuid the uuid of the scene you want to check
     * @return returns true if scene was added
     */
    public boolean containsScene(UUID uuid){
        for(Scene scene : scenes){
            if(scene.uuid.equals(uuid)){
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param uuid the uuid of the scene you want to make active
     * @return returns false if scene was not in the list of scenes
     */
    public boolean setActiveScene(UUID uuid){
        for(Scene scene : scenes){
            if(scene.uuid.equals(uuid)){
                this.activeSceneID = uuid;
                return true;
            }
        }
        return false;
    }

    public Scene getScene(UUID uuid){
        for(Scene scene : scenes){
            if(scene.uuid.equals(uuid)){
                return scene;
            }
        }
        return null;
    }

    public Scene getActiveScene(){
        return getScene(activeSceneID);
    }

    public UUID getActiveSceneID(){
        return activeSceneID;
    }

}
