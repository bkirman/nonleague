package uk.ac.lincoln.games.nlfs.logic;

import java.util.Random;
import uk.ac.lincoln.games.nlfs.Assets;

public class Footballer implements Comparable<Footballer>{
	private String first_name, surname;
	private int age;
	private int goals_this_season;
	private Position position;
	public transient Team team;
	public static enum Position {GK,DF,MF,ST,MGR,SUB}
	
	/**
	 * Generate a random new Footballer, of given position.
	 * @param pos
	 */
	public Footballer(Team team, Position pos){
		position = pos;
		this.team = team;
		goals_this_season = 0;
		do{
		first_name = Assets.first_names.get(GameState.rand.nextInt(Assets.first_names.size()));
		surname = Assets.surnames.get(GameState.rand.nextInt(Assets.surnames.size()));
		}while(team.footballerNameInUse(getName()));
		age = GameState.rand.nextInt(25)+15;
		if(pos==Position.MGR) age = age+20;//older managers
		//Gdx.app.log("ftgen", getName()+" is a "+getPositionName(position)+" for "+team.name+" at "+team.stadium);
		
	}
	
	public Footballer(){}
	
	public void setPostion(Position newPos){ position = newPos;}
	
	public int getAge(){ return age; }
	public void setAge(int a) {age = a;}

	public int getGoals(){return goals_this_season;}
	public void addGoal(){goals_this_season++;}
	public void resetGoals(){goals_this_season=0;}
	
	public Position getPosition(){ return position; }
	
	public String getPositionName(Position p) {
		if(p==Position.GK) return "Goalkeeper";
		if(p==Position.DF) return "Defender";
		if(p==Position.MF) return "Midfielder";
		if(p==Position.SUB) return "Substitute";
		if(p==Position.MGR) return "Manager";
		return "Striker";
	}
	
	public String getFirstName(){ return first_name; }
	
	public String getSurname(){ return surname; }
	
	public String getName() { return first_name+" "+surname; }

	public int compareTo(Footballer f) { return f.goals_this_season - this.goals_this_season;}

}
