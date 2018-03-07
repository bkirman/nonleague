package uk.ac.lincoln.games.nlfs;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;

/**
 * Created by ben on 07/09/17.
 */

public class ChangeSettings extends BaseScreen {
    private CheckBox mute,stats;

    public ChangeSettings(final NonLeague game) {
        super(game);
        mute = new CheckBox("   ",Assets.skin);
        if(GameState.league.SETTINGS.VOLUME==0.0f)mute.setChecked(true);
        mute.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
                GameState.league.SETTINGS.toggleMute();
            }
        });
        stats = new CheckBox("   ",Assets.skin);
        if(GameState.league.SETTINGS.CONSENT)stats.setChecked(true);
        stats.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
                GameState.league.SETTINGS.CONSENT=!GameState.league.SETTINGS.CONSENT;
            }
        });

        TextButton weblink = new TextButton("Research Information",Assets.skin);
        weblink.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
                Gdx.net.openURI("https://sites.google.com/york.ac.uk/nlfs");
            }
        });


        table.add(new Label("Settings",Assets.skin, "teamname")).fillX().expandX().center().colspan(2);

        table.row();
        table.add("Mute sounds").left();
        table.add(mute);
        table.row();
        table.add("Share anonymous stats for research").left();
        table.add(stats);
        table.row().padTop(10);
        table.add("Debug Info:").left();
        table.add("v:"+GameState.VERSION+", "+"id:"+GameState.DEVICE_ID).right().colspan(2);

        table.row().padTop(25);
        table.add(weblink).colspan(2);
        table.row().padTop(5);
        TextButton backbutton = new TextButton("Next Match", Assets.skin);
        table.add(backbutton).width(480).height(85).colspan(2);
        table.row();

        backbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
                game.changeScreen(game.teamstatus_screen);
            }});
    }

    @Override
    public boolean back(){
        game.changeScreen(game.teamstatus_screen);
        return true;
    }

    public void update() {

    }
}
