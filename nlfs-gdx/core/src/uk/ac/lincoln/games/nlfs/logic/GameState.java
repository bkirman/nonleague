package uk.ac.lincoln.games.nlfs.logic;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.net.DataPacket;

/**
 * GameState is the singleton that holds all current game information.
 * It manages loading/saving/restoring of game data.
 * @author bkirman
 *
 */
public class GameState {
	public static GameState state;
	public static Assets assets;
	public static League league;
	public static Team player_team;
	public static Random rand; //this is seeded to be always the same - used for generation
	public static Random rand2; //this is NOT seeded, used for match engine.
	public static String SAVEFILE = "nlfs.dat";
	private static boolean enable_saving = true; //all should be true for normal operation
	private static boolean enable_b64_savefile = true;//NB existing saves will be made invalid when changing this setting
	private static boolean enable_seed = true; //enable or disable fixed seeds based on hardware
	public static String VERSION = "2.1.3";
	public static String DEVICE_ID;

	public static GameState getGameState(String device) {
		
		if(state==null) {
			state = new GameState(device);
		}
		return state;
	}
	public static GameState getGameState(String device, boolean force) {
		
		if(state==null || force) {
			state = new GameState(device);
		}
		return state;
	}
	public static float getVol() {return league.SETTINGS.VOLUME;}

	public GameState(String device) {
		DEVICE_ID = device;
		long seed = Long.decode("0x"+device);
		assets = new Assets();

		if(enable_seed && seed != 0)	rand = new Random(seed);
		else rand = new Random();
		rand2 = new Random();
		//Try to load game from storage. If none exists create a new league
		if(!this.loadGame()) {
			Gdx.app.log("Start","no savefile found, creating new league");
			league = new League(assets);
			player_team = league.teams.get(rand.nextInt(league.teams.size()));//randomly select a player team
			player_team.setPlayerControlled(true);
		} else {
			Gdx.app.log("Start","Savefile found, loaded league");
			GameState.league.SETTINGS.FIRST_RUN = false;
			//find player team
			for(Team t:league.teams) {
				if(t.isPlayerControlled()) {
					player_team = t;
					break;
				}
			}
		}
	}
	
	/**
	 * Methods to load/save game from local storage
	 */
	public void saveGame() {
		if(!enable_saving) return;
		Json json = new Json();
		String o = json.toJson(league);
		FileHandle file = Gdx.files.local(SAVEFILE);
	    if(enable_b64_savefile)
	    	file.writeString(com.badlogic.gdx.utils.Base64Coder.encodeString(o), false);
	    else
	    	file.writeString(o, false);
	    Gdx.app.log("SAVE","Game Saved to "+SAVEFILE);
	}
	public boolean loadGame() {
		Gdx.app.log("LOAD", "Checking for save file in "+Gdx.files.getLocalStoragePath());
		if(!enable_saving) return false;
		Json json = new Json();
		String s = "";
		FileHandle file = Gdx.files.local(SAVEFILE);
	    if (file != null && file.exists()) {
	    	s = file.readString();
	    }
	    if(s.isEmpty()) return false;
	    if(enable_b64_savefile)
	    	league = json.fromJson(League.class, com.badlogic.gdx.utils.Base64Coder.decodeString(s));
	    else
	    	league = json.fromJson(League.class, s);
		league.reinit();
		return true;
	}
}
