package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.ui.LeaguePositionGraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * title screen
 * @author bkirman
 *
 */
public class SplashScreen extends BaseScreen {
	private Title title;
    private Label label;
	private Sound intro;

	private class Title extends Actor {
		Texture title_screen = new Texture(Gdx.files.internal("title_screen.png"));

		public Title() {
			this.getColor().a = 0.0f;

			addListener(new InputListener(){
				public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
					intro.stop();
					intro.dispose();
					if(GameState.league.SETTINGS.FIRST_RUN)
						game.changeScreen(game.consent_screen);
					else
						game.changeScreen(game.teamstatus_screen);
					return true;
				}
			});
			intro = Gdx.audio.newSound(Gdx.files.internal("intro.mp3"));
		}

		@Override
        public void draw(Batch batch, float alpha){
			Color c = getColor();
			batch.setColor(c.r, c.g, c.b, c.a * alpha);
			batch.draw( title_screen,0,0);
			batch.setColor(c.r, c.g, c.b, 1f);
        }
	}
	
	public SplashScreen(final NonLeague game) {
		super(game);
		
		stage.clear();
		title = new Title();
		title.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		title.setTouchable(Touchable.enabled);

		title.addAction(Actions.fadeIn(0.2f));
		stage.addActor(title);

        label = new Label("Touch to Start",Assets.skin,"pagetitle");
        label.setPosition(stage.getWidth()/2-label.getWidth()/2,100); //Note may need adjusting for other devices. unclear if stage width = viewport width
        label.addAction(sequence(fadeOut(0.0f),fadeIn(0.2f),forever(sequence(fadeIn(0.4f),fadeOut(0.4f)))));
        label.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				intro.stop();
				intro.dispose();
				if(GameState.league.SETTINGS.FIRST_RUN)
					game.changeScreen(game.consent_screen);
				else
					game.changeScreen(game.teamstatus_screen);

                return true;
            }
        });
        stage.addActor(label);


		Gdx.input.setInputProcessor(stage);

	}
	public void update(){
		intro.play(GameState.getVol());
	}
	

}
