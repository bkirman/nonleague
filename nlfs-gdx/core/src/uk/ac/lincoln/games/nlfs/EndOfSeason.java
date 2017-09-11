package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.logic.Footballer;
import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.ui.LeaguePositionGraph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.ArrayList;

/**
 * show season results. final league positions. Calculate new league in case of promotion/relegation
 * @author bkirman
 *
 */
public class EndOfSeason extends BaseScreen {
	private Label title, description_label;
	private Table position_table,stats_table;
	
	public EndOfSeason(final NonLeague game) {
		super(game);
		title = new Label("END OF SEASON",Assets.skin,"pagetitle");
		position_table = new Table();
		stats_table = new Table(Assets.skin);
		table.add(title).expandX().fillX().center();
		table.row();
		
		description_label = new Label("[DESCRIPTION]",Assets.skin,"newspaper");
		description_label.setWrap(true);
		
		Table newspaper_table = new Table();
		newspaper_table.background(Assets.skin.getDrawable("paper"));
		newspaper_table.add(description_label).expandX().fill().pad(20);
			
		table.add(newspaper_table).expand().fillX().colspan(2).padBottom(10).minWidth(600);
		table.row();
		table.add("League Position History:").left().colspan(2);table.row();
        table.add(position_table).colspan(2).expand();
		table.row();
		stats_table.setBackground(Assets.skin.getDrawable("darken"));
		stats_table.setWidth(600);
		//event_table.setDebug(true);
		ScrollPane action_pane = new ScrollPane(stats_table);
		table.add(action_pane).colspan(2).width(610);
		table.row();

		TextButton button = new TextButton("Start New Season", Assets.skin);	
		table.add(button).width(480).height(85);
		table.row();
		
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	game.changeScreen(game.teamstatus_screen);
			}});
	}
	public void update(){
		stats_table.clear();
		//add some stats from season here
		stats_table.add("Top Scorers:").colspan(2).center();stats_table.row();
		for(Footballer f: GameState.player_team.getTopScorers(3)) {
			stats_table.add(f.getName()+" ("+String.valueOf(f.getPosition())+") ").left();
			stats_table.add(String.valueOf(f.getGoals())).right();
			stats_table.row();
		}
        stats_table.add("Team Changes:").colspan(2).center();stats_table.row();
		GameState.player_team.agePlayers();
        ArrayList<String> news = GameState.player_team.transferOut();
        for(String s:news) {
            stats_table.add(s).left().colspan(2);
            stats_table.row();
        }
        news = GameState.player_team.transferIn();
        for(String s:news) {
            stats_table.add(s).left().colspan(2);
            stats_table.row();
        }

		position_table.clear();
		Image graph = new Image(LeaguePositionGraph.generateLeaguePositionGraph(GameState.league.getTeamPositionHistory(GameState.player_team)));
		//graph.scaleBy(4.0f);
		//graph.setSize(480, 80);
		position_table.add(graph).size(480, 80);
		
		String old_league = GameState.league.name;
		String ordinal_pos = GameState.league.getTeamPositionOrdinal(GameState.player_team);
		int promotion;//hacky
		if (GameState.league.isPromoted(GameState.player_team)) promotion = 1;
		else if (GameState.league.isRelegated(GameState.player_team)) promotion = 2;
		else promotion = 3;
		
		GameState.league.newSeason(GameState.player_team);
		
		String description;
		if(promotion==1) description = "Jubilant fans fill "+GameState.player_team.stadium+" to celebrate "+GameState.player_team.name+"'s "+ordinal_pos+" place finish in the "+old_league+". The team now faces tougher challenges as they are promoted to the "+GameState.league.name+".";
		else if(promotion==2) description = "Tears before bedtime at "+GameState.player_team.stadium+" as "+GameState.player_team.name+"'s "+ordinal_pos+" place finish means relegation from the "+old_league+". The team continues their downward slide into the "+GameState.league.name+".";
		else description = "A disappointing season for "+GameState.player_team.name+" sees a "+ordinal_pos+" place finish, and another year battling on in the "+old_league;
		description_label.setText(description);
		
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
	}
}
