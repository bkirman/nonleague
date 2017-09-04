package uk.ac.lincoln.games.nlfs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.logic.GameState;

/**
 * Created by ben on 31/08/17.
 */

public class Tutorial extends Actor {
    private Sprite icon;
    private TutorialWindow tw;
    private boolean setup = false;

    public Tutorial(String title, String content, String button_text) {
        icon = Assets.skin.getSprite("help");
        tw = new TutorialWindow(title,content,button_text);
        this.setSize(48,48);
        setTouchable(Touchable.enabled);
        setBounds(0, 0, getWidth(), getHeight());//relative to actor!
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if(!getStage().getActors().contains(tw,true)){
                tw.reset();
                getStage().addActor(tw);}
                return true;
            }
        });

    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if(!setup) { //set up windows when we are on stage
            if (getStage()==null) return;

            if(GameState.first_run) {
                this.getStage().addActor(tw);

            }

            setup=true;
        }
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        batch.setColor(1,1,1,0.3f);
        batch.draw(icon,getX(),getY());
        batch.setColor(1,1,1,1);

    }
}
