package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.logic.MatchResult;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;
import uk.ac.lincoln.games.nlfs.ui.Tutorial;
import uk.ac.lincoln.games.nlfs.ui.TutorialWindow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

/**
 * Show summary of match results. Score, newspaper article, etc.
 * @author bkirman
 *
 */
public class PostMatch extends BaseScreen {
	private MatchResult result;
	private Label stadium_label,description_label,home_score_label,away_score_label;
	private TeamLabel home_label,away_label;
	private Table results_table;
	
	public PostMatch (final NonLeague game) {
		super(game);
		home_label = new TeamLabel(null,"teamname_bigger");
		away_label = new TeamLabel(null,"teamname_bigger");
		home_score_label = new Label(" 0 ",Assets.skin,"score");
		away_score_label = new Label(" 0 ",Assets.skin,"score");
						
		table.add(home_label).expandX().fillX().left();
		table.add(home_score_label).right().padLeft(2);
		table.row().padTop(2);
		table.add(away_label).expandX().fillX().left();
		table.add(away_score_label).right().padLeft(2);
		home_score_label.getStyle().background = Assets.skin.newDrawable("base",Color.WHITE); //TODO should be properly assigned in skin
		table.row().padTop(2);
		
		stadium_label = new Label("at [STADIUM]",Assets.skin);
		description_label = new Label("[DESCRIPTION]",Assets.skin,"newspaper");
		description_label.setWrap(true);
		
		Table newspaper_table = new Table();
		newspaper_table.background(Assets.skin.getDrawable("paper"));
		newspaper_table.add(description_label).expandX().fill().pad(20);
			
		//description_label.setWidth(200);
		table.add(stadium_label).expandX().right().colspan(2);
		table.row();
		table.add(newspaper_table).expand().fillX().colspan(2).padBottom(10).minWidth(600);
		table.row();
		
		TextButton button = new TextButton("Leave Match", Assets.skin);	

		table.add(button).width(480).height(85).colspan(2);
		table.row();
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Assets.manager.get("click.wav", Sound.class).play(GameState.VOLUME);
				if(GameState.league.isSeasonFinished())
            		game.changeScreen(game.endofseason_screen);
            	else
            		game.changeScreen(game.teamstatus_screen);
			}});
		
		results_table = new Table();
		results_table.setSkin(Assets.skin);
		results_table.setBackground(Assets.skin.getDrawable("darken"));
		results_table.pad(10f);
		root_table.row().padTop(25);
		root_table.add(results_table);
		Tutorial tut = new Tutorial("Post-Match", "The game is over. Here we can see a summary of the match as reported in the local papers, plus some details about other league results.\n From here, the cycle begins again - go to your next match and refine your rituals.\n Will you support your team all the way to the non-non-leagues?","Go Wide");
		tut.setPosition(stage.getWidth()-54,6);
		stage.addActor(tut);

	}
	
	public void setResult(MatchResult mr) {
		result = mr;
	}
	
	public void update() {
		//update screen since at this point we should have a result
		home_label.update(result.match.home);
		away_label.update(result.match.away);
		home_score_label.setText(" "+String.valueOf(result.home_goals.size())+" ");
		away_score_label.setText(" "+String.valueOf(result.away_goals.size())+" ");
		
		stadium_label.setText("at "+result.match.home.stadium);
		description_label.setText(result.getDescription(GameState.player_team));
		results_table.clear();
		
		if(GameState.league.weekly_results.size()==0) return;
		results_table.add(new Label("Other Results:",Assets.skin)).colspan(3);
		TeamLabel h,a;
		for(MatchResult mr2:GameState.league.weekly_results) {
			if(mr2.match==result.match) continue;//don't show current match
			results_table.row();
			h = new TeamLabel(mr2.match.home,"teamname_smaller");
			a = new TeamLabel(mr2.match.away,"teamname_smaller");
			results_table.add(h).fillX().expandX().pad(5f);
			results_table.add(new Label(" "+String.valueOf(mr2.home_goals.size())+" - "+String.valueOf(mr2.away_goals.size())+" ",Assets.skin,"score_smaller")).pad(5f);
			results_table.add(a).fillX().expandX().pad(5f).align(Align.right).right();
			
		}
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
	}
}
