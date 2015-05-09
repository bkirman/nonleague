package uk.ac.lincoln.games.nlfs;


import uk.ac.lincoln.games.nlfs.logic.GameState;
import uk.ac.lincoln.games.nlfs.logic.Match;
import uk.ac.lincoln.games.nlfs.ui.RitualSelector;
import uk.ac.lincoln.games.nlfs.ui.TeamLabel;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Screen before match where players select the rituals they will use
 * @author bkirman
 *
 */
public class PreMatch extends BaseScreen{
	private Match match;
	
	private TeamLabel home_label,away_label; 
	private Label stadium_label;//,weather_label;
	
	private TextButton button;
	
	private static String[] drink_icons = {
		"none",
		"bovril",
		"lager",
		"bitter",
		"brew",
		"pop",
		
	};
	private static String[] drink_names = {
		"Nothing Special",
		"Meaty Drink",
		"Warm Lager",
		"Flat Bitter",
		"Milky Cuppa",
		"Supermarket Pop",
	};
	
	private static String[] food_icons = {
		"none",
		"bovril",
		"lager",
		"bitter",
		"brew",
		"pop",
	};
	private static String[] food_names = {
		"Nothing Special",
		"'Meat' Pie",
		"Cheese Pie",
		"Chip Pie",
		"Curry Pie",
		"Pie Barm",
	};
	
	private static String[] clothing_icons = {
		"none",
		"bovril",
		"lager",
		"bitter",
		"brew",
		"pop",
	};
	private static String[] clothing_names = {
		"Nothing Special",
		"Team Shirt",
		"Anorak",
		"Scarf",
		"Lucky Pants",
		"Bare Chest",
	};
	private static String[] bringing_icons = {
		"none",
		"bovril",
		"lager",
		"bitter",
		"brew",
		"pop",
	};
	private static String[] bringing_names = {
		"Nothing Special",
		"Dog on String",
		"Mardy Nephew",
		"Vuvuzela",
		"Stolen Brolly",
		"Tuba",
		
	};
	
	public PreMatch (final NonLeague game) {
		super(game);
		//NB remember none of this stuff is in memory yet
		home_label = new TeamLabel(null,"teamname_bigger");
		away_label = new TeamLabel(null,"teamname_bigger");
		stadium_label = new Label("at [STADIUM]",Assets.skin);
		//weather_label = new Label("weather",Assets.skin);
		
		table.add(home_label).expandX().fillX().left().colspan(2);
		table.row();
		table.add("vs.").center().colspan(2);
		table.row();
		table.add(away_label).expandX().fillX().left().colspan(2);
		table.row();
		//table.add(weather_label).left();
		table.add(stadium_label).expandX().colspan(2).center();
		table.row().padBottom(15);
		
		RitualSelector clothes_ritual = new RitualSelector("Wearing",clothing_icons,clothing_names);
		RitualSelector food_ritual = new RitualSelector("Eating",food_icons,food_names);
		RitualSelector drink_ritual = new RitualSelector("Drinking",drink_icons,drink_names);
		RitualSelector bring_ritual = new RitualSelector("Bringing",bringing_icons,bringing_names);
		//clothes_ritual.validate();
		table.add(clothes_ritual.getActor()).expandX().center().colspan(2);
		table.row().padBottom(10);
		table.add(food_ritual.getActor()).expandX().center().colspan(2);
		table.row().padBottom(10);
		table.add(drink_ritual.getActor()).expandX().center().colspan(2);
		table.row().padBottom(10);;
		table.add(bring_ritual.getActor()).expandX().center().colspan(2);
		table.row().padBottom(10);;
		
		 
		button = new TextButton("Go to Match", Assets.skin);	

		table.add(button).width(480).height(85).colspan(2);
		table.row();
		button.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	return true;
            }
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
            	game.changeScreen(game.matchview_screen);
			}});
	}
	
	public void update() {
		this.match = GameState.league.findTeamsNextFixture(GameState.player_team);
		button.setChecked(false);
		//Set team names/colours
		home_label.update(match.home);
		away_label.update(match.away);
		
		//if same kits, invert away
		if(match.away.colour_base==match.home.colour_base&&match.away.colour_primary==match.home.colour_primary) {
			away_label.getStyle().background = Assets.skin.newDrawable("base",Assets.skin.getColor(match.away.colour_primary));
			away_label.getStyle().fontColor = Assets.skin.getColor(match.away.colour_base);
		} 
		
		//weather_label.setText(match.getWeather());
		stadium_label.setText(match.getWeather()+" at "+match.home.stadium);
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
		
	}
}
