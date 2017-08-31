package uk.ac.lincoln.games.nlfs;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.logic.Goal;
import uk.ac.lincoln.games.nlfs.logic.Match;
import uk.ac.lincoln.games.nlfs.logic.MatchEvent;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;
import uk.ac.lincoln.games.nlfs.ui.Tutorial;
import uk.ac.lincoln.games.nlfs.ui.TutorialWindow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * Display the ongoing match and calculate the results
 * @author bkirman
 *
 */
public class MatchView extends BaseScreen{
	private Match match;
	private TextButton button;
	private TeamLabel home_label,away_label;
	private Label home_score_label,away_score_label,clock_label;
	private ScrollPane action_pane;
	//private VerticalGroup action_group;
	private Table event_table;
	private ArrayList<Goal> goals;
	//private int mins_in_match;
	private int current_minute, current_home, current_away;
	public static float SIMULATION_S_PER_MIN = 0.3f;//lower this is, faster the simulation gets (0.3f is about right)
	private static boolean SKIP_MATCH = false;//debug setting skips slow match report
	private enum MatchState {PRE,H1,HT,H2,FT};
	private MatchState current_state;
	
	/**
	 * Schedulable task that runs a minute worth of game.
	 * @author bkirman
	 *
	 */
	private class RunMinute extends Task {
		@Override
		public void run() {
			if(current_state==MatchState.H2&&current_minute>match.result.match_length) {
				this.cancel();
				current_state = MatchState.FT;
				button.setText("Leave Match");
				button.setDisabled(false);
				
				event_table.add("Full Time","score_report").colspan(2).center();
				event_table.row();

				action_pane.fling(1f, 0f, -500f);
				return;
			}
			if(current_state==MatchState.H1&&current_minute>(match.result.match_length/2)) {
				this.cancel();
				current_state = MatchState.HT;
				button.setText("Second Half");
				button.setDisabled(false);
				event_table.add("Half Time","score_report").colspan(2).center();
				event_table.row();
				action_pane.fling(1f, 0f, -500f);
				return;
			}
			//resolve a minute's worth of match time
			
			
			clock_label.setText(" "+String.valueOf(current_minute)+" ");
			for(MatchEvent me:match.result.match_events) {
				if(current_minute==me.minute) {
					event_table.add(String.valueOf(current_minute)+": ","event_report").right().maxWidth(40);
					event_table.add(me.getDescription() ,"event_report").left().expandX();
					event_table.row();
					action_pane.fling(1f, 0f, -500f);
				}
			}
			for(Goal g : goals) {
				if(g.time==current_minute){
					//vibrate
					Gdx.input.vibrate(800);
					if(g.scorer.team==match.home) {
						current_home++;
						home_score_label.setText(" "+String.valueOf(current_home)+" ");
					}
					else {
						current_away++;
						away_score_label.setText(" "+String.valueOf(current_away)+" ");
					}
					
					//add text
					event_table.add("GOAL for "+g.scorer.team.name,"score_report").colspan(2).center();
					event_table.row();
					event_table.add(String.valueOf(current_minute)+": ","event_report").right().maxWidth(40);
					event_table.add(g.scorer.getName()+" ("+g.scorer.getPosition().toString()+")" ,"event_report").left().expandX();
					event_table.row();
					action_pane.fling(1f, 0f, -500f);
					
				}
					
			}
			current_minute++;
			
		}
	}
	
	
	
	public MatchView (final NonLeague game) {
		super(game);
		//NB remember none of this stuff is in memory yet
		home_label = new TeamLabel(null,"teamname_bigger");
		away_label = new TeamLabel(null,"teamname_bigger");
		home_score_label = new Label(" 0 ",Assets.skin,"score");
		away_score_label = new Label(" 0 ",Assets.skin,"score");
		
		table.add(home_label).expandX().fillX().left();
		table.add(home_score_label).right().padLeft(2);
		table.row().padTop(5);
		table.add(away_label).expandX().fillX().left();
		table.add(away_score_label).right().padLeft(2);
		home_score_label.getStyle().background = Assets.skin.newDrawable("base",Color.WHITE); //TODO should be properly assigned in skin
		table.row().padTop(5);
		
		
		event_table = new Table(Assets.skin);
		event_table.setBackground(Assets.skin.getDrawable("darken"));
		//event_table.setDebug(true);		
		action_pane = new ScrollPane(event_table);
		table.add(action_pane).colspan(2).width(600).height(650).expand().fill();
		
		table.row().padTop(5);
		
		Table table2 = new Table();
		Image stopwatch = new Image(Assets.skin,"stopwatch");
		table2.add(stopwatch).maxSize(68,75).padRight(4).right();
		clock_label = new Label(" 0 ",Assets.skin,"timer");
		clock_label.getStyle().background = Assets.skin.newDrawable("base",Color.WHITE); //TODO should be properly assigned in skin
		table2.add(clock_label).width(67).right();
		button = new TextButton("Leave Match", Assets.skin);	

		table2.add(button).width(480).height(85).padLeft(5).left();
		
		table.add(table2).colspan(2);
		
		table.row().padTop(5);
		
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int b2) {
            	if(current_state == MatchState.PRE || current_state == MatchState.HT) {
					button.setDisabled(true);
					button.setText("Please Wait");
					Timer.schedule(new RunMinute(), SIMULATION_S_PER_MIN, SIMULATION_S_PER_MIN);
					if(current_state==MatchState.PRE)
						current_state = MatchState.H1;
					else
						current_state = MatchState.H2;
				}
				
				if(current_state == MatchState.FT) {
					game.postmatch_screen.setResult(match.result);
					game.changeScreen(game.postmatch_screen);
				}
			}});
		Tutorial tut = new Tutorial("Match View", "Welcome to the match! Here is where you see how your team performs.\n You cannot have any further impact at this point, so hit 'kick off', sit back and enjoy the mud-stained thrills of non-league football","Come on Lads!");
		tut.setPosition(stage.getWidth()-54,6);
		stage.addActor(tut);
	}
	
	public void update() {
		current_minute = 0;
		current_home = 0;
		current_away = 0;
		//mins_in_match = 94;
		match = GameState.league.findTeamsNextFixture(GameState.player_team);
		GameState.league.playWeek();
		
		goals = new ArrayList<Goal>();
		goals.addAll(match.result.home_goals);
		goals.addAll(match.result.away_goals);
		Collections.sort(goals);
		clock_label.setText(" 0 ");
		home_label.update(match.home);
		away_label.update(match.away);
		
		//if same kits, invert away
		if(match.away.colour_base==match.home.colour_base&&match.away.colour_primary==match.home.colour_primary) {
			away_label.getStyle().background = Assets.skin.newDrawable("base",Assets.skin.getColor(match.away.colour_primary));
			away_label.getStyle().fontColor = Assets.skin.getColor(match.away.colour_base);
		} 
		
		button.setDisabled(false);
		button.setChecked(false);
		button.setText("Kick Off");
		away_score_label.setText(" "+String.valueOf(current_away)+" ");
		home_score_label.setText(" "+String.valueOf(current_home)+" ");
		event_table.clear();
		
		//do gate
		event_table.add(" We join "+String.valueOf(match.result.gate)+" fans at "+match.home.stadium,"event_report").colspan(2).left().fillX().expandX();
		event_table.row();
		current_state = MatchState.PRE;
		if(SKIP_MATCH){
			current_state = MatchState.FT;
		}
	}
	
	
	
	@Override
	public void render(float delta){
		super.render(delta);
	}

	
}
