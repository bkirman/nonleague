package uk.ac.lincoln.games.nlfs.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.logic.Footballer.Position;

/**
 * Leagues have many teams. Teams have many players.
 * Teams may take part in matches with other teams.
 * 
 * @author bkirman
 *
 */
public class Team {
	private boolean player_control;
	
	public static double PLAYER_BIAS = 0.08; // Slightly higher than average bias to win. Happy to tweak this
	
	public double win_bias;
	public String name;
	public String stadium;
	public ArrayList<Footballer> footballers;
	public transient League league;
	
	public String colour_primary,colour_base;
			
	public transient ArrayList<Footballer> defenders,midfielders,goalkeepers,strikers,substitutes;
	public transient Footballer manager;
	
	/**
	 * Generate a new team from the supplied assets
	 */
	public Team(Assets assets, League league){
		this.player_control = false;
		this.league = league;
		
		win_bias = 0.5 - GameState.rand.nextDouble(); //gain a random bias between -0.5 and +0.5. The bias is an abstract representation of general team quality. theoretically teams with higher bias tend to win more, but only slightly.
		
		//generate name, stadium
		do {
		name = assets.town_names.get(GameState.rand.nextInt(assets.town_names.size()));//random town name 
		if (GameState.rand.nextDouble()<0.45&&name.length()<9) //not all teams have suffices
			name = name +" "+ assets.team_names.get(GameState.rand.nextInt(assets.team_names.size()));
		}while(league.teamNameInUse(name));
		
		
		stadium = assets.stadium_names.get(GameState.rand.nextInt(assets.stadium_names.size())) +" "+ assets.road_names.get(GameState.rand.nextInt(assets.road_names.size()));
		ArrayList<String> colour = assets.team_colours.get(GameState.rand.nextInt(assets.team_colours.size()));
		
		colour_base = colour.get(1);
		colour_primary = colour.get(0);
		
		
		//generate players (4-4-2 is the only formation used in real non-league football. Hard coded for realism.)
		footballers = new ArrayList<Footballer>();
		manager = new Footballer(this,Position.MGR);
		footballers.add(manager);
		goalkeepers = new ArrayList<Footballer>();
		Footballer player = new Footballer(this,Position.GK);
		footballers.add(player);
		goalkeepers.add(player);
		
		defenders = new ArrayList<Footballer>();
		for(int i=0;i<4;i++) {
			player = new Footballer(this,Position.DF);
			footballers.add(player);
			defenders.add(player);
		}
		midfielders = new ArrayList<Footballer>();
		for(int i=0;i<4;i++) {
			player = new Footballer(this,Position.MF);
			footballers.add(player);
			midfielders.add(player);
		}
		strikers = new ArrayList<Footballer>();
		for(int i=0;i<2;i++) {
			player = new Footballer(this,Position.ST);
			footballers.add(player);
			strikers.add(player);
		}
		substitutes = new ArrayList<Footballer>();
		for(int i=0;i<6;i++) {
			player = new Footballer(this,Position.SUB);
			footballers.add(player);
			substitutes.add(player);
		}
	}
	
	public Team() {}
	
	public boolean isPlayerControlled() {return this.player_control;}
	
	public void setPlayerControlled(boolean value){
		this.player_control = value;
		this.win_bias = PLAYER_BIAS;
		}

	/**
	 * Return random player at given position
	 * @param position
	 * @return
	 */
	public Footballer getFootballerAtPosition(Position position){
		if(position==Position.GK) return goalkeepers.get(GameState.rand.nextInt(goalkeepers.size()));
		if(position==Position.ST) return strikers.get(GameState.rand.nextInt(strikers.size()));
		if(position==Position.DF) return defenders.get(GameState.rand.nextInt(defenders.size()));
		if(position==Position.SUB) return substitutes.get(GameState.rand.nextInt(substitutes.size()));
		if(position==Position.MGR) return manager;
		return midfielders.get(GameState.rand.nextInt(midfielders.size()));
	}

    /**
     * Random PLAYER (i.e. not manager)
     * @return
     */
	private Footballer getRandomPlayer() {
        Footballer f;
        do {
            f = footballers.get(GameState.rand2.nextInt(footballers.size()));
        }while(f.getPosition()==Position.MGR);
        return f;
	}
	
	public int countUnplayedMatches() {
		int i = 0;
		for(Match m:league.fixtures) {
			if(!m.has_run&&m.isTeam(this)) i++;
		}
		return i;
	}
	
	public boolean footballerNameInUse(String full_name) {
		for(Footballer f:footballers) {
			if(f.getName().equals(full_name)) return true;
		}
		return false;
	}

	public List<Footballer> getTopScorers(int n) {
		ArrayList<Footballer> out = (ArrayList<Footballer>)footballers.clone();
		Collections.sort(out);
		return out.subList(0,n);
	}

	public void agePlayers(){
        for(Footballer f: footballers) f.setAge(f.getAge()+1);
    }

    public void removeFromTeam(Footballer f) {
        substitutes.remove(f);
        midfielders.remove(f);
        defenders.remove(f);
        strikers.remove(f);
        goalkeepers.remove(f);
        if(f==manager) manager = null;
        footballers.remove(f);
    }

    /**
     * Work out randomly if players retire/transfer
     * @return a list of string descriptions of what happened
     */
    public ArrayList<String> transferOut() {
        ArrayList<String> out = new ArrayList<String>();

        //retiring of old age
        ArrayList<Footballer> fs = new ArrayList<Footballer>();
        fs.addAll(footballers);
        for (Footballer f:fs) {
            if(f.getAge()>=35 && f.getPosition()!=Position.MGR){
                if(GameState.rand2.nextInt(50-f.getAge())==0) {
                    out.add(f.getName()+" ("+f.getPosition()+") retires at "+String.valueOf(f.getAge()));
                    removeFromTeam(f);
                }
            }
        }
        //manager retirement
        if(GameState.rand2.nextInt(70-manager.getAge())==0) {
            out.add("Team manager "+manager.getName() + " retires");
            removeFromTeam(manager);
        }
        //transfers
        int tfrs = GameState.rand2.nextInt(3);
        Footballer f;
        for(int i=0;i<=tfrs;i++) {
            f = getRandomPlayer();
            out.add(f.getName()+" ("+f.getPosition()+") transfers away");
            removeFromTeam(f);
        }
        return out;
    }

    /**
     * Fill the team with all positions missing
     * @return list of string descriptions of what happened
     */
    public ArrayList<String> transferIn() {
        ArrayList<String> out = new ArrayList<String>();
        if(manager==null) {
            manager = new Footballer(this, Position.MGR);
            footballers.add(manager);
            out.add(manager.getName()+" joins as manager");
        }
        Footballer p;
        while(substitutes.size()<6) {
            p = new Footballer(this,Position.SUB);
            footballers.add(p);
            substitutes.add(p);
            out.add(p.getName()+ " joins on the bench");
        }
        while(defenders.size()<4) {
            p = new Footballer(this,Position.DF);
            footballers.add(p);
            defenders.add(p);
            out.add(p.getName()+ " joins as defender");
        }
        while(midfielders.size()<4) {
            p = new Footballer(this,Position.MF);
            footballers.add(p);
            midfielders.add(p);
            out.add(p.getName()+ " joins the midfielders");
        }
        while(strikers.size()<2) {
            p = new Footballer(this,Position.ST);
            footballers.add(p);
            strikers.add(p);
            out.add(p.getName()+ " joins as striker");
        }
        if(goalkeepers.size()<1){
            p = new Footballer(this,Position.GK);
            footballers.add(p);
            goalkeepers.add(p);
            out.add(p.getName()+ " joins as goalie");
        }
        return out;

    }
	
	/**
	 * Reset transient pointers
	 */
	public void reinit(League league) {
		this.league = league;
		goalkeepers = new ArrayList<Footballer>();
		defenders = new ArrayList<Footballer>();
		midfielders = new ArrayList<Footballer>();
		strikers = new ArrayList<Footballer>();
		substitutes = new ArrayList<Footballer>();
		for (Footballer f: footballers){
			f.team = this;
			if(f.getPosition()==Position.GK) goalkeepers.add(f);
			if(f.getPosition()==Position.DF) defenders.add(f);
			if(f.getPosition()==Position.MF) midfielders.add(f);
			if(f.getPosition()==Position.ST) strikers.add(f);
			if(f.getPosition()==Position.SUB) substitutes.add(f);
			if(f.getPosition()==Position.MGR) manager = f;
		}
	}
}
