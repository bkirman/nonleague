package uk.ac.lincoln.games.nlfs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.NonLeague;
import uk.ac.lincoln.games.nlfs.logic.GameState;

/**
 * Created by ben on 07/09/17.
 */

public class Settings extends Actor {

    private Sprite cog;

    public Settings(final NonLeague game) {

        cog = Assets.skin.getSprite("settings");
        this.setSize(48,48);
        setTouchable(Touchable.enabled);
        setBounds(0, 0, getWidth(), getHeight());//relative to actor!
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
                game.changeScreen(game.settings_screen);
                return true;
            }
        });
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.setColor(1,1,1,0.3f);
        batch.draw(cog,getX(),getY());
        batch.setColor(1,1,1,1);
    }
}
