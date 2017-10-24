package uk.ac.lincoln.games.nlfs;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.logic.Goal;
import uk.ac.lincoln.games.nlfs.logic.Match;
import uk.ac.lincoln.games.nlfs.logic.MatchEvent;
import uk.ac.lincoln.games.nlfs.net.DataLogger;
import uk.ac.lincoln.games.nlfs.ui.Settings;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;
import uk.ac.lincoln.games.nlfs.ui.Tutorial;
import uk.ac.lincoln.games.nlfs.ui.TutorialWindow;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
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
	private Table event_table;
	private ArrayList<Goal> goals;
	private int current_minute, current_home, current_away,total_minutes; //current_minute IN this half total_minutes PLAYED this match
	public static float SIMULATION_S_PER_MIN = 0.3f;//lower this is, faster the simulation gets (0.3f is about right)
	private static boolean SKIP_MATCH = false;//debug setting skips slow match report
	private enum MatchState {PRE,H1,HT,H2,FT};
	private MatchState current_state;
	private Music bg_music;

	/**
	 * Schedulable task that runs a minute worth of game.
	 * @author bkirman
	 *
	 */
	private class RunMinute extends Task {
		@Override
		public void run() {
            /* this is all fairly horrific but working out the timings at different statuses is a bit mind-bending*/
			if(current_state==MatchState.H2&&current_minute>match.result.second_half_length) {
				this.cancel();
				current_state = MatchState.FT;
				Assets.manager.get("finalwhistle.mp3", Sound.class).play(GameState.getVol());
				button.setText("Leave Match");
				button.setDisabled(false);
				bg_music.stop();
				event_table.add("Full Time","score_report").colspan(2).center();
				event_table.row();

				action_pane.fling(1f, 0f, -500f);
				return;
			}
			if(current_state==MatchState.H1&&current_minute>(match.result.first_half_length)) {
				this.cancel();
				current_state = MatchState.HT;
				button.setText("Second Half");
				Assets.manager.get("htwhistle.mp3", Sound.class).play(GameState.getVol());
				bg_music.stop();
				button.setDisabled(false);
				event_table.add("Half Time","score_report").colspan(2).center();
				event_table.row();
				action_pane.fling(1f, 0f, -500f);
				current_minute = 0;
				return;
			}

			if(current_minute==45&&current_state==MatchState.H1) {
				Label l;
				event_table.add("45: ","event_report").right().top();
				l = new Label(String.valueOf(match.result.first_half_length-45)+" minutes of injury time",Assets.skin);
				l.setWrap(true);
				event_table.add(l).left().expandX().width(550);

				event_table.row();
				action_pane.fling(1f, 0f, -500f);

			}
			else if(current_minute==45&&current_state==MatchState.H2) {
				Label l;
				event_table.add("90: ","event_report").right().top();
				l = new Label(String.valueOf(match.result.second_half_length-45)+" minutes of injury time",Assets.skin);
				l.setWrap(true);
				event_table.add(l).left().expandX().width(550);

				event_table.row();
				action_pane.fling(1f, 0f, -500f);

			}

			if(current_state==MatchState.H1) {//set clock display
                if(current_minute<=45)
                    clock_label.setText(" " + String.valueOf(total_minutes) + " ");
                else
                    clock_label.setText(" 45+"+ String.valueOf(total_minutes-45)+" ");
			}
			else if(current_state==MatchState.H2) {
                if (current_minute <= 45)
                    clock_label.setText(" " + String.valueOf(45+current_minute) + " ");
                else
                    clock_label.setText(" 90+" + String.valueOf(total_minutes - match.result.first_half_length-46) + " ");
            }

			for(MatchEvent me:match.result.match_events) {
				if(total_minutes==me.minute) {
					String min = String.valueOf(total_minutes);
					if(current_state==MatchState.H1) {
						min = String.valueOf(total_minutes);
						if(current_minute>=45) min = "45+" + String.valueOf(total_minutes-45);
					}
					else if(current_state==MatchState.H2) {
						min = String.valueOf(total_minutes-(match.result.first_half_length-45));
						if(current_minute>=45) min = "90+" + String.valueOf(total_minutes-match.result.first_half_length-45);
					}
					event_table.add(min+": ","event_report").right().top();
					Label l = new Label(me.getDescription(),Assets.skin);
					l.setWrap(true);
					event_table.add(l).left().expandX().width(550);
					if(me.type== MatchEvent.MatchEventType.YELLOWCARD) {
						Assets.manager.get("whistle.wav", Sound.class).play(GameState.getVol());
					}

					event_table.row();
					action_pane.fling(1f, 0f, -500f);
				}
			}
			for(Goal g : goals) {
				if(g.time==total_minutes){
					//vibrate
					Gdx.input.vibrate(800);
                    table.validate();
					if(g.scorer.team==match.home) {
						current_home++;
						home_score_label.setText(" "+String.valueOf(current_home)+" ");
                        Assets.goal_particles.setPosition(
                                home_score_label.localToStageCoordinates(new Vector2(0,0)).x+home_score_label.getWidth()/2,
                                home_score_label.localToStageCoordinates(new Vector2(0,0)).y+home_score_label.getHeight()/2);
					}
					else {
						current_away++;
						away_score_label.setText(" "+String.valueOf(current_away)+" ");
                        Assets.goal_particles.setPosition(
                                away_score_label.localToStageCoordinates(new Vector2(0,0)).x+away_score_label.getWidth()/2,
                                away_score_label.localToStageCoordinates(new Vector2(0,0)).y+away_score_label.getHeight()/2);
					}
					TeamLabel l = new TeamLabel(g.scorer.team,"teamname");
					l.setText(" GOAL for "+g.scorer.team.name.toUpperCase()+" ");
					Assets.manager.get("goal.mp3", Sound.class).play(GameState.getVol());


					Assets.goal_particles.setParent(g.scorer.team);
                    stage.addActor(Assets.goal_particles);

					//add text
					event_table.add(l).colspan(2).center();
					event_table.row();
                    event_table.validate();//


                    String min = String.valueOf(total_minutes);
					if(current_state==MatchState.H1 && current_minute>=45){
						min = "45+" + String.valueOf(total_minutes-45);
					}
					else if(current_state==MatchState.H2) {
						min = String.valueOf(total_minutes-(match.result.first_half_length-45));
						if(current_minute>=45) min = "90+" + String.valueOf(total_minutes-match.result.first_half_length-45);
					}
					event_table.add(min+": ","event_report").right();
					event_table.add(g.scorer.getName()+" ("+g.scorer.getPosition().toString()+")" ,"event_report").left().expandX();
					event_table.row();
					action_pane.fling(1f, 0f, -500f);
					
				}
					
			}
			current_minute++;
			total_minutes++;
			
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
		event_table.setWidth(600);
		//event_table.setDebug(true);
		action_pane = new ScrollPane(event_table);
		table.add(action_pane).colspan(2).width(610).height(650).expand().fill();
		
		table.row().padTop(5);
		
		Table table2 = new Table();
		Image stopwatch = new Image(Assets.skin,"stopwatch");
		table2.add(stopwatch).maxSize(68,75).padRight(4).right();
		clock_label = new Label(" 0 ",Assets.skin,"timer");
		clock_label.getStyle().background = Assets.skin.newDrawable("base",Color.WHITE); //TODO should be properly assigned in skin
        clock_label.setAlignment(Align.center);
		table2.add(clock_label).width(110).center();
		button = new TextButton("Leave Match", Assets.skin);	

		table2.add(button).width(440).height(85).padLeft(5).left();
		
		table.add(table2).colspan(2);
		
		table.row().padTop(5);
		
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int b2) {
				Assets.manager.get("click.wav", Sound.class).play(GameState.getVol());
				if(current_state == MatchState.PRE || current_state == MatchState.HT) {

					button.setDisabled(true);
					button.setText("Please Wait");
					Timer.schedule(new RunMinute(), SIMULATION_S_PER_MIN, SIMULATION_S_PER_MIN);
					bg_music.play();
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
		current_minute = 0; //current minute in this half
		total_minutes = 0; //total minutes elapsed in match so far.
		current_home = 0;
		current_away = 0;
		match = GameState.league.findTeamsNextFixture(GameState.player_team);
		GameState.league.playWeek();//run simulation
		//add data to logger
		GameState.current_packet.addResult(match.opponentFor(GameState.player_team).name,(match.home==GameState.player_team),
				match.result.goalsFor(GameState.player_team),match.result.goalsAgainst(GameState.player_team), GameState.league.getTeamPosition(GameState.player_team));
		GameState.data_packets.add(GameState.current_packet);
		DataLogger.sendData(GameState.data_packets);
		bg_music = Assets.manager.get("bg.mp3",Music.class);
		bg_music.setVolume(0.2f*GameState.getVol());
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
		event_table.add(" We join "+String.valueOf(match.result.gate)+" fans at "+match.home.stadium,"event_report").left().fillX().expandX().colspan(2);
		event_table.row();
		current_state = MatchState.PRE;
		if(SKIP_MATCH){
			current_state = MatchState.FT;
		}
	}
}
