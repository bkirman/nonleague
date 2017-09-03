package uk.ac.lincoln.games.nlfs;

import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.logic.League;
import uk.ac.lincoln.games.nlfs.logic.LeagueTableItem;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

/**
 * table view that shows current league
 * @author bkirman
 *
 */
public class LeagueTable extends BaseScreen {
	private Table league_table;
	private Label title;
	
	public LeagueTable(final NonLeague game) {
		super(game);
		title = new Label(" ",Assets.skin,"pagetitle");
		table.add(title).expandX().fillX();
		table.row();
		league_table = new Table(Assets.skin);
		league_table.setBackground(Assets.skin.getDrawable("darken"));
		table.add(league_table).expand().fill();
		table.row();
		
		TextButton button = new TextButton("Back to Team", Assets.skin);	
		table.add(button).width(480).height(85);
		table.row().pad(2);
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				Assets.click_sfx.play();
				game.changeScreen(game.teamstatus_screen);
			}});
	}
	
	/**
	 * Regenerate table
	 */
	public void update() {
		title.setText(GameState.league.name.toUpperCase());
		league_table.clear();
		league_table.add().align(Align.left).fillX();
		league_table.add(new Label("W",Assets.skin,"default"));
		league_table.add(new Label("L",Assets.skin,"default"));
		league_table.add(new Label("D",Assets.skin,"default"));
		league_table.add(new Label("PTS",Assets.skin,"default"));
		league_table.row().pad(2);
		int i = 0;
		
		for(LeagueTableItem lti:GameState.league.table) {
			i++;
			league_table.add(new TeamLabel(lti.team,"teamname")).align(Align.left).fillX();
			//league_table.add(new Label(String.valueOf(lti.getGamesPlayed()),Assets.skin,"lt_points"));
			league_table.add(new Label(String.valueOf(lti.wins),Assets.skin,"lt_points"));
			league_table.add(new Label(String.valueOf(lti.losses),Assets.skin,"lt_points"));
			league_table.add(new Label(String.valueOf(lti.draws),Assets.skin,"lt_points"));
			
			if(i<=League.PROMOTION) {
				league_table.add(new Label(" "+String.valueOf(lti.points),Assets.skin,"lt_points_p"));
			}
			else if (i>(League.LEAGUE_SIZE-League.RELEGATION)) {
				league_table.add(new Label(" "+String.valueOf(lti.points),Assets.skin,"lt_points_r"));
			} else {
				league_table.add(new Label(" "+String.valueOf(lti.points),Assets.skin,"lt_points_w"));
			}
		
			
			league_table.row().pad(2);
		}
		league_table.add(new Label(" ",Assets.skin)).colspan(4);
		league_table.row().pad(2);
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
	}

}
