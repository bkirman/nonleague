package uk.ac.lincoln.games.nlfs.logic;

/**
 * Created by ben on 11/09/17.

 We need this struct to store settings between plays, through the save system.

 */

public class SettingsContainer {
    public float VOLUME = 0.7f;
    public boolean CONSENT = true;
    public boolean FIRST_RUN = true;

    public void toggleMute(){ if(VOLUME == 0.0f) VOLUME=0.7f;else VOLUME=0.0f; }
}
