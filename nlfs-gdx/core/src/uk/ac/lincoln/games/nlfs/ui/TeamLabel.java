package uk.ac.lincoln.games.nlfs.ui;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.logic.Team;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TeamLabel extends Label{
	public Color bg,fg;
	
	public TeamLabel(Team t, String font) {
		super("[PLACEHOLDER]",Assets.skin,font);

		setStyle(new LabelStyle(Assets.skin.get(font, LabelStyle.class)));
		
		if(t!=null) update(t);
		}
	
	/**
	 * Update label element with new team data.
	 * @param t
	 */
	public void update(Team t) {
		this.setText(" "+t.name+" ");
		fg = Assets.skin.getColor(t.colour_primary);
		bg = Assets.skin.getColor(t.colour_base);
		getStyle().background = Assets.skin.newDrawable("base",bg);
		getStyle().fontColor = fg;
	}
	
	

}
