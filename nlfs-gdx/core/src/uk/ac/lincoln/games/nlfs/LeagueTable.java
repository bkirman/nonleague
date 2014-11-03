package uk.ac.lincoln.games.nlfs;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

/**
 * table view that shows current league
 * @author bkirman
 *
 */
public class LeagueTable extends BaseScreen {
	public LeagueTable(final NonLeague game) {
		super(game);
		table.add("hello").expandX().height(100f);
		table.row();
		
		
		TextButton button = new TextButton("Go to Game", game.skin);	
		table.add(button).width(200).height(40);
		table.row();
		
		//button.setText(match.result.getDescription(match.home));
		
		button.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				
		}
		});
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
	}

}
