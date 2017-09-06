package uk.ac.lincoln.games.nlfs;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import uk.ac.lincoln.games.nlfs.ui.GoalParticles;

public class Assets {
	public static Skin skin;
	public ArrayList<String> team_names;
	public ArrayList<String> town_names;
	public ArrayList<String> first_names;
	public ArrayList<String> surnames;
	public ArrayList<String> stadium_names;
	public ArrayList<String> road_names;
	public ArrayList<String> league_prefices,league_suffices;
	public ArrayList<ArrayList<String>> team_colours;
	public static HashMap<String,ArrayList<String>> news_summaries;
	
	public static AssetManager manager;
	
	private boolean gen_loaded;

//	public static Sound click_sfx;//,swish_sfx,goal_sfx,whistle_sfx,gameend_sfx,bg_sfx,ht_sfx;
	public static GoalParticles goal_particles;
	
	public Assets() {
		gen_loaded = false;
		manager = new AssetManager();
		loadSounds();
		loadSkin();

		loadRunData();
        Gdx.app.log("ASSETS","Loading audio");
		goal_particles = new GoalParticles();
	}

	/**
	 * Clean up
	 */
	public static void dispose() {
		skin.dispose();
		manager.dispose();
		goal_particles.dispose();
	}

	/**
	 * sounds fairly time consuming to load. Can be done async during splash/loading screen.
	 */
	private void loadSounds() {
		manager.load("click.wav",Sound.class);
		manager.load("swish.wav",Sound.class);
		manager.load("whistle.wav",Sound.class);
		manager.load("goal.mp3",Sound.class);
		manager.load("finalwhistle.mp3",Sound.class);
		manager.load("bg.mp3",Music.class);
		manager.load("htwhistle.mp3",Sound.class);
	}
	
	public boolean isGenLoaded() { return gen_loaded;}
	
	/**
	 * Fill data for gen (new leagues, teams, players)
	 */
	public void loadGenData() {
		//get the potential team names and suffixes, first names, last names and stadium names
		try{
			team_names = loadFile("teamnames.txt");
			town_names = loadFile("townnames.txt");
			first_names = loadFile("firstnames.txt");
			surnames = loadFile("surnames.txt");
			road_names = loadFile("roadnames.txt");
			stadium_names = loadFile("stadiumnames.txt");
			league_prefices = loadFile("league_prefix.txt");
			league_suffices = loadFile("league_suffix.txt");
			
			//Team colours
			ArrayList<String> colour_data = loadFile("team_colours.txt");
			
			team_colours = new ArrayList<ArrayList<String>>();
			for(String line: colour_data) {
				ArrayList<String> colour_line = new ArrayList<String>();
				
				colour_line.add(line.split(" on ")[0]);
				colour_line.add(line.split(" on ")[1]);
				
				team_colours.add(colour_line);
			}
		} catch (IOException e) {
            //CRASH TODO: fail gracefully
		}
		
	}
	/**
	 * Fill data based on day-to-day activities
	 */
	public void loadRunData() {
		ArrayList<String> news_data;
		news_summaries = new HashMap<String, ArrayList<String>>();
		try{
			news_data = loadFile("newssummaries.txt");
		} catch (IOException e) {
            //CRASH TODO: fail gracefully
			return;
		}
		String score = "0-0";
		for (String line : news_data) {//fill hashmap with news items associated with particular scores
			if(line.charAt(1)=='-') {//new score
				score = line;
				news_summaries.put(line, new ArrayList<String>());
			}
			else{
				news_summaries.get(score).add(line);//add comment to that line
			}
		}
	}
	
	/**
	 * Load the skin data into memory (must be done before anything is displayed)
	 */
	private void loadSkin() {
		
		manager.load("skin.json",Skin.class);
		//manager.load("darken.png",Texture.class);
		//manager.load("base.png",Texture.class);
		//manager.load("transparent.png",Texture.class);
		
		manager.finishLoading();
		//TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pack.atlas"));
		
		skin = manager.get("skin.json");//new Skin(Gdx.files.internal("skin.json"),atlas);
		

		Pixmap pm = new Pixmap(1,1,Format.RGBA8888);
		pm.setColor(0f, 0.3f, 0f, 0.95f);
		pm.fill();
		skin.add("tutorial",new Texture(pm));
		
	}
	
	/**
	 * Load filename into array of strings
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	private ArrayList<String> loadFile(String filename) throws IOException {
		ArrayList<String> out = new ArrayList<String>();
		BufferedReader buffreader = new BufferedReader(Gdx.files.internal(filename).reader());
		String line = buffreader.readLine();
		while(line!=null) {
			out.add(line);
			line = buffreader.readLine();
		}
		buffreader.close();
		return out;
	}

}
