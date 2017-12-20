package uk.ac.lincoln.games.nlfs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import uk.ac.lincoln.games.nlfs.logic.GameState;

/**
 * Created by ben on 20/12/17.
 */

public class InformedConsent extends BaseScreen {
    public InformedConsent(final NonLeague game) {
        super(game);

        Label text = new Label("Thanks for your interest in Non-League Football Supporter!\n" +
                "This game has been developed as part of a research project. By playing you will help us understand " +
                "more about how players develop strategies in games. Please click the button below to learn about the study and how data is collected."
                ,Assets.skin);
        text.setWrap(true);

        table.pad(20);
        table.setBackground(Assets.skin.getDrawable("tutorial"));

        table.add(new Label("Research Information",Assets.skin,"pagetitle")).center().expandX();
        table.row();
        table.add(text).fill().expand().top().pad(10f);
        table.row().padTop(10);

        TextButton button = new TextButton("Study Information", Assets.skin);
        TextButton button2 = new TextButton("I Agree to Participate", Assets.skin);
        table.add(button).width(410).height(85).center();
        table.row();
        table.add(button2).width(410).height(85).center();
        table.row();

        button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.net.openURI("https://sites.google.com/york.ac.uk/nlfs");
            }});

        button2.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
                GameState.league.SETTINGS.CONSENT = true;
                game.changeScreen(game.teamstatus_screen);

            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    public void update() {

    }
}
