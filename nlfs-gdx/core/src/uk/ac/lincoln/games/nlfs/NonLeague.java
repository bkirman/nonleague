package uk.ac.lincoln.games.nlfs;

import java.util.ArrayList;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.logic.League;
import uk.ac.lincoln.games.nlfs.logic.LeagueTableItem;
import uk.ac.lincoln.games.nlfs.logic.MatchResult;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class NonLeague extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Skin skin;
	Stage stage;
	
	
	/**
	 * Actor for background texture.
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
			batch.draw(bg, 0, 0,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());//fill
		}
	}
	
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		stage = new Stage(new FitViewport(360,640));
		
		Gdx.input.setInputProcessor(stage);
		GameState state = GameState.getGameState();
		skin = new Skin();
		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
				
		
		skin.add("white", new Texture(pixmap));
		// Store the default libgdx font under the name "default".
		skin.add("default", new BitmapFont());
		skin.add("button_up",new Texture("button_up.png"));
		skin.add("button_down",new Texture("button_down.png"));
		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle button_style = new TextButtonStyle();
		button_style.font = skin.getFont("default");
		button_style.up = skin.newDrawable("button_up");
		button_style.down = skin.newDrawable("button_down");
		
				
		skin.add("default", button_style);
		skin.add("default", new LabelStyle(new BitmapFont(),Color.WHITE));
		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table();
		table.setFillParent(true);
		table.setSkin(skin);
		
		
		//build stage
		stage.addActor(new Background());
		stage.addActor(table);
		// Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
		final TextButton button = new TextButton("game", skin);
		table.add(button).width(200).height(40);
		table.row();
		final TextButton button2 = new TextButton("save", skin);
		table.add(button2).width(200).height(40);
		// Add a listener to the bu tton. ChangeListener is fired when the button's checked state changes, eg when clicked,
		// Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
		// ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
		// revert the checked state.
		
		
		button.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
			MatchResult result = GameState.league.nextFixture().run();
			Gdx.app.log(String.valueOf(result.home_goals)+"-"+String.valueOf(result.away_goals), result.getDescription(result.match.home));
		}
		});
		button2.addListener(new ChangeListener() {
			public void changed (ChangeEvent event, Actor actor) {
				GameState.getGameState().saveGame();
				
		}
		});
		
		table.row();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		//stage.getBatch().begin();
		//stage.getBatch().draw(bg, 0, 0,0,0,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		
		stage.draw();
		//stage.getBatch().end();
	}
	@Override
	public void resize (int width, int height) {
	stage.getViewport().update(width, height, true);
	}
	@Override
	public void dispose () {
	stage.dispose();
	skin.dispose();
	}
}
