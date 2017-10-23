package uk.ac.lincoln.games.nlfs.ui;

import java.util.ArrayList;

import uk.ac.lincoln.games.nlfs.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * UI Widget that allows user to select a ritual from a given set
 * @author bkirman
 *
 */
public class RitualSelector {
	//private WidgetGroup wg;
	private Label selected_label;
	private Table table;
	private ButtonGroup bg;
	
	private ArrayList<RitualButton> buttons;
	private ScrollPane sp;
	
	public class RitualButton extends Button {
		private String description;
		public String icon;
		RitualButton(String icon, String name) {
			super(Assets.skin,"ritual");
			description = name;
			this.icon = icon;
			add(new Image(Assets.skin.getDrawable(icon)));
			addListener(new ChangeListener() {
				public void changed(ChangeEvent event, Actor actor) {
					selected_label.setText(description);

				}});
		}
		
	}
	
	
	public RitualSelector(String description,String[] icons, String[] names) {
		selected_label = new Label("Nothing Special",Assets.skin);
		buttons = new ArrayList<RitualButton>();
		bg = new ButtonGroup();
		HorizontalGroup hg = new HorizontalGroup();
		
		for(int i=0;i<icons.length;i++) {
			buttons.add(new RitualButton(icons[i],names[i]));
		}
		
		for(RitualButton b:buttons) {
			hg.addActor(b);
			bg.add(b);	
		}
		
		bg.setMaxCheckCount(1);
		bg.setUncheckLast(true);
		sp = new ScrollPane(hg);
		table = new Table();
		table.setBackground(Assets.skin.getDrawable("darken"));
		
		table.add(new Label(description+":",Assets.skin)).left().padLeft(10);
		table.add(selected_label).right().expandX().padRight(10);
		table.row();
		table.add(sp).colspan(2);
		
	}

	public void reset() { //reset selection
		bg.uncheckAll();
		for(RitualButton r:buttons){
			if(r.icon=="none") r.setChecked(true);
			else r.setChecked(false);
		}
	}

	/**
	 * Return icon name of selected button
	 * @return
	 */
	public String getSelected() {
		return ((RitualButton) bg.getChecked()).icon;
	}
	
	public Table getActor() {return table;}
}
