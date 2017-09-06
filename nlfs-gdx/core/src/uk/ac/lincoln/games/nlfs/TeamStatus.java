package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.ui.LeaguePositionGraph;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;
import uk.ac.lincoln.games.nlfs.ui.Tutorial;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

/**
 * general view showing league position, next match details, etc.
 * @author bkirman
 *
 */
public class TeamStatus extends BaseScreen {
	//private Image position_graph;
	private Table position_table;
	private TeamLabel team_label,next_opponent_label;
	private Label league_pos_label,unplayed_label,stadium_label,home_away_label,forecast_label, opp_pos_label;
	private Tutorial tut;
	
	public TeamStatus(final NonLeague game) {
		super(game);
		team_label = new TeamLabel(GameState.player_team,"teamname_bigger");
		
		//team_label.setFontScale(0.5f);
		next_opponent_label = new TeamLabel(null,"teamname_smaller");
		league_pos_label = new Label("X",Assets.skin);
		unplayed_label = new Label("X",Assets.skin);
		home_away_label = new Label("X",Assets.skin,"default");
		stadium_label = new Label(GameState.player_team.stadium,Assets.skin,"default_small");
		forecast_label = new Label("X",Assets.skin);
		opp_pos_label = new Label("X",Assets.skin);
		position_table = new Table();
		
		table.add(team_label).fillX().expandX().center().colspan(2);
		table.row();
		table.add(stadium_label).right().colspan(2);
		table.row();
		table.add(league_pos_label).left().colspan(2);
		table.row();
		table.add("Matches left in Season: ").right();
		table.add(unplayed_label).left();
		table.row();
		table.add("Next match: ").right();
		table.add(next_opponent_label).fillX().expandX();
		table.row();
		table.add();
		table.add(home_away_label).left();
		table.row();
		table.add("Their Position: ").right();
		table.add(opp_pos_label).left();
		table.row();
		table.add("Forecast: ").right();
		table.add(forecast_label).left();
		table.row();


		table.add(position_table).colspan(2).expand();
		table.row();

		TextButton lgbutton = new TextButton("League Table", Assets.skin);
		TextButton button = new TextButton("Prepare for Match", Assets.skin);
		TextButton statsbutton = new TextButton("Team Stats",Assets.skin);

		table.add(lgbutton).width(480).height(85).colspan(2);
		table.row();
		table.add(statsbutton).width(480).height(85).colspan(2);
		table.row();

		table.add(button).width(480).height(90).colspan(2);
		table.row();
		Gdx.input.setInputProcessor(stage);
		tut = new Tutorial("Welcome", "Welcome to the most authentic football supporter simulation in the world!\n You support "+GameState.player_team.name+". You always have and always will. This cannot be changed.\n This screen shows your team's status, league position and next opponent. Once you get familiar with your team, prepare for your next match","Get Started");
		tut.setPosition(stage.getWidth()-54,6);
		stage.addActor(tut);


		
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Assets.manager.get("click.wav", Sound.class).play(GameState.VOLUME);
				game.changeScreen(game.prematch_screen);
			}});
		lgbutton.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Assets.manager.get("click.wav", Sound.class).play(GameState.VOLUME);
				game.changeScreen(game.leaguetable_screen);
			}});
		statsbutton.addListener(new InputListener() {
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Assets.manager.get("click.wav", Sound.class).play(GameState.VOLUME);
				game.changeScreen(game.stats_screen);
			}});
	}
	
	/**
	 * Reset visual elements based on current team status
	 */
	public void update() {
		team_label.setAlignment(Align.center);
		next_opponent_label.update(GameState.league.findTeamsNextFixture(GameState.player_team).opponentFor(GameState.player_team));
		league_pos_label.setText(GameState.league.getTeamPositionOrdinal(GameState.player_team) + " in " + GameState.league.name);
		unplayed_label.setText(String.valueOf(GameState.player_team.countUnplayedMatches()));
		if(GameState.player_team == GameState.league.findTeamsNextFixture(GameState.player_team).home)
			home_away_label.setText("Home at "+GameState.player_team.stadium);
		else
			home_away_label.setText("Away at "+GameState.league.findTeamsNextFixture(GameState.player_team).home.stadium);
		
		opp_pos_label.setText(GameState.league.getTeamPositionOrdinal(GameState.league.findTeamsNextFixture(GameState.player_team).opponentFor(GameState.player_team)));
		
		forecast_label.setText(GameState.league.findTeamsNextFixture(GameState.player_team).getWeather());
		position_table.clear();
		Image graph = new Image(LeaguePositionGraph.generateLeaguePositionGraph(GameState.league.getTeamPositionHistory(GameState.player_team)));
		//graph.scaleBy(4.0f);
		//graph.setSize(480, 80);
		position_table.add(graph).size(480, 80);
		
	}
}
