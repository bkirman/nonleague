package uk.ac.lincoln.games.nlfs;

import java.util.ArrayList;
import java.util.Arrays;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.ui.LeaguePositionGraph;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * general view showing league position, next match details, etc.
 * @author bkirman
 *
 */
public class TeamStatus extends BaseScreen {
	//private Image position_graph;
	private Table position_table;
	private Label team_label,next_opponent_label,league_pos_label,unplayed_label;
	
	public TeamStatus(final NonLeague game) {
		super(game);
		team_label = new Label(GameState.player_team.name.toUpperCase(),Assets.skin,"teamname");
		//team_label.setFontScale(0.5f);
		next_opponent_label = new Label("[NEXT TEAM]",Assets.skin);
		league_pos_label = new Label("X",Assets.skin);
		unplayed_label = new Label("X",Assets.skin);
		position_table = new Table();
		
		table.add(team_label).expandX().colspan(2);
		table.row();
		table.add("Next opponent: ");
		table.add(next_opponent_label).expandX();
		table.row();
		table.add("League position: ");
		table.add(league_pos_label).expandX().right();
		table.row();
		table.add("Matches left in Season: ");
		table.add(unplayed_label);
		table.row();
		
		//position_graph = new Image(LeaguePositionGraph.generateLeaguePositionGraph(new ArrayList<Integer>(Arrays.asList(0))));
		//table.add(position_graph).expandX().colspan(2);
		table.add(position_table).colspan(2).expand();
		table.row();

		TextButton lgbutton = new TextButton("League Table", Assets.skin);
		TextButton button = new TextButton("Prepare for Match", Assets.skin);
		
		table.add(lgbutton).width(200).height(40).colspan(2);
		table.row();
		table.add(button).width(200).height(40).colspan(2);
		table.row();
		
		button.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				game.changeScreen(game.prematch_screen);
		
		}
		});
		lgbutton.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				game.changeScreen(game.leaguetable_screen);
		
		}
		});
	}
	
	/**
	 * Reset visual elements based on current team status
	 */
	public void update() {
		next_opponent_label.setText(GameState.league.findTeamsNextFixture(GameState.player_team).opponentFor(GameState.player_team).name);
		league_pos_label.setText(String.valueOf(GameState.league.getTeamPosition(GameState.player_team)));
		unplayed_label.setText(String.valueOf(GameState.player_team.countUnplayedMatches()));
		position_table.clear();
		position_table.add(new Image(LeaguePositionGraph.generateLeaguePositionGraph(GameState.league.getTeamPositionHistory(GameState.player_team))));
		
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
	}

}
