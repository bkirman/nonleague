package uk.ac.lincoln.games.nlfs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import uk.ac.lincoln.games.nlfs.logic.Footballer;
import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;

/**
 * Created by ben on 31/08/17.
 */

public class TeamStats extends BaseScreen {
    private Table stats_table;

    public TeamStats(final NonLeague game) {
        super(game);
        table.add(new TeamLabel(GameState.player_team,"teamname_bigger")).fillX().expandX().center().colspan(2);
        table.row();

        stats_table = new Table(Assets.skin);
        stats_table.setBackground(Assets.skin.getDrawable("darken"));
        //event_table.setDebug(true);

        table.add(new ScrollPane(stats_table)).colspan(2).width(600).height(650).expand().fill();

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
        stats_table.clear();
        stats_table.add("Manager: "+GameState.player_team.manager.getName(),"event_report").colspan(4).center();
        stats_table.row();
        stats_table.add("Ground: "+GameState.player_team.stadium,"event_report").colspan(4).center();
        stats_table.row();

        stats_table.add("POS","default_small").padLeft(5);
        stats_table.add("Name" ,"default_small").left().padLeft(12).expandX();
        stats_table.add("Age" ,"default_small").left().padRight(5);
        stats_table.add("Goals","default_small").center().padRight(5);
        stats_table.row();
        for(Footballer f:GameState.player_team.goalkeepers) {
            stats_table.add("GK","event_report").padLeft(5);
            stats_table.add(f.getName() ,"event_report").left().padLeft(12).expandX();
            stats_table.add(String.valueOf(f.getAge()),"event_report").center().padRight(5);
            stats_table.add(String.valueOf(f.getGoals()),"event_report").center().padRight(5);
            stats_table.row();
        }
        for(Footballer f:GameState.player_team.defenders) {
            stats_table.add("DF","event_report").padLeft(5);
            stats_table.add(f.getName() ,"event_report").left().padLeft(12).expandX();
            stats_table.add(String.valueOf(f.getAge()),"event_report").center().padRight(5);
            stats_table.add(String.valueOf(f.getGoals()),"event_report").center().padRight(5);
            stats_table.row();
        }
        for(Footballer f:GameState.player_team.midfielders) {
            stats_table.add("MF","event_report").padLeft(5);
            stats_table.add(f.getName() ,"event_report").left().padLeft(12).expandX();
            stats_table.add(String.valueOf(f.getAge()),"event_report").center().padRight(5);
            stats_table.add(String.valueOf(f.getGoals()),"event_report").center().padRight(5);
            stats_table.row();
        }
        for(Footballer f:GameState.player_team.strikers) {
            stats_table.add("ST","event_report").padLeft(5);
            stats_table.add(f.getName() ,"event_report").left().padLeft(12).expandX();
            stats_table.add(String.valueOf(f.getAge()),"event_report").center().padRight(5);
            stats_table.add(String.valueOf(f.getGoals()),"event_report").center().padRight(5);
            stats_table.row();
        }

    }
}
