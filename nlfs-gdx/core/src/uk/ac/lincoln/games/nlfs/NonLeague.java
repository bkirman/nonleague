package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.logic.GameState;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class NonLeague extends Game {
	Texture img;
		
	//public Skin skin;
	public FitViewport viewport;
	public GameState state;
	private String hardware_id;
	
	public TeamStatus teamstatus_screen;//messy architecture but saves on garbage collection
	public PreMatch prematch_screen;
	public MatchView matchview_screen;
	public PostMatch postmatch_screen;
	public LeagueTable leaguetable_screen;
	public EndOfSeason endofseason_screen;
	public SplashScreen splash_screen;
	/**
	 * Actor for background texture. Needs to be here so abstract basescreen can find it (I know)
	 * @author bkirman
	 *
	 */
	public class Background extends Actor {
		Texture bg = new Texture("bg_fill.png");
		
		public Background() {
			super();
			bg.setWrap(TextureWrap.Repeat,TextureWrap.Repeat);
		}
		@Override
		public void draw(Batch batch, float alpha) {
			batch.draw(bg, 0, 0,0,0,720,1280);//fill
		}
	}
	
	public void changeScreen(BaseScreen s) {
		s.update();//refresh data on screen
		this.setScreen(s);
		s.reset();
		Gdx.input.setInputProcessor(s.stage);
	}
	
	public void create () {
		viewport = new FitViewport(720,1280);
		
		
		state = GameState.getGameState(Long.valueOf(hardware_id));
		
		teamstatus_screen = new TeamStatus(this);
		prematch_screen = new PreMatch(this);
		matchview_screen = new MatchView(this);
		postmatch_screen = new PostMatch(this);
		leaguetable_screen = new LeagueTable(this);
		endofseason_screen = new EndOfSeason(this);
		splash_screen = new SplashScreen(this);
		//start app flow
		teamstatus_screen.update();
		//this.changeScreen(teamstatus_screen);
		this.changeScreen(splash_screen);
	}

	public void pause () {
		
	}

	public void resume () {
	}
	
	public void dispose () {
		GameState.getGameState(Long.valueOf(hardware_id)).saveGame();
		Assets.skin.dispose();
	}
	
	public NonLeague(String hardware_id) {
		super();
		this.hardware_id = hardware_id;
	}
}
