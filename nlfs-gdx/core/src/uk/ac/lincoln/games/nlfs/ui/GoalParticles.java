package uk.ac.lincoln.games.nlfs.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.particles.values.LineSpawnShapeValue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.StringBuilder;

import com.badlogic.gdx.graphics.Color;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.logic.Goal;
import uk.ac.lincoln.games.nlfs.logic.Team;

/**
 * Created by ben on 05/09/17.
 */

public class GoalParticles extends Actor {
    private Team team;
    private ParticleEffect effect_1,effect_2;
    //private Image stopwatch;//test
    public GoalParticles() {
        setSize(1f,1f);
        effect_1 = new ParticleEffect();
        effect_2 = new ParticleEffect();
        effect_1.load(Gdx.files.internal("goal.p"), Gdx.files.internal("")); //you have to rename Scale X to just Scale , remove Scale Y. some mismatch in file format in gdx particle system.
        effect_2.load(Gdx.files.internal("goal.p"), Gdx.files.internal(""));

        effect_1.start();
        effect_2.start();
    }

    public void setParent(Team p) {
        team = p;
        float temp[] = effect_1.getEmitters().first().getTint().getColors();
        Color fg = Assets.skin.getColor(team.colour_primary);
        Color bg = Assets.skin.getColor(team.colour_base);

        temp[0] = fg.r;
        temp[1] = fg.g;
        temp[2] = fg.b;

        float temp2[] = effect_2.getEmitters().first().getTint().getColors();

        temp2[0] = bg.r;
        temp2[1] = bg.g;
        temp2[2] = bg.b;
        effect_1.start();
        effect_2.start();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //Gdx.app.log("DEBUG", String.valueOf(effect.findEmitter("Goal").getActiveCount()));
        effect_1.draw(batch); //define behavior when stage calls Actor.draw()
        effect_2.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        effect_1.setPosition(getX(),getY());
        effect_2.setPosition(getX(),getY());
        effect_1.update(delta); //update it
        effect_2.update(delta);


        if (effect_1.isComplete()) {
            remove();
            effect_1.reset();
            effect_2.reset();
        }

    }



    public void dispose() {
        effect_1.dispose();
        effect_2.dispose();
    }
}
