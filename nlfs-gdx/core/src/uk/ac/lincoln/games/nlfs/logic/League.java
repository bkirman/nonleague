package uk.ac.lincoln.games.nlfs.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;

import uk.ac.lincoln.games.nlfs.Assets;


/**
 * A league has many teams.
 * 
 * A league has an ordered series of fixtures - matches between these teams
 * 
 * Based on the results of past fixtures, a league table can be built.
 * 
 * Fundamentally, the League (or should it be called Non-League? ;) ) object stores the entire current game state.
 * 
 * @author bkirman
 *
 */
public class League {
	public ArrayList<Match> fixtures;
	public ArrayList<Team> teams;
	private int current_week;
	public ArrayList<MatchResult> weekly_results;
	public ArrayList<LeagueTableItem> table;
	public String name;
	public SettingsContainer SETTINGS;
	public static int POINTS_WIN = 3;
	public static int POINTS_DRAW = 1;
	public static int POINTS_LOSE = 0;
	
	public static int LEAGUE_SIZE = 12; //number of teams in the league. Even numbers only, dickhead.
	public static int PROMOTION = 2; //number of teams promoted/relegated at the end of the season. (promotion + relegation) < league size, dickhead.
	public static int RELEGATION = 2;
	
	/**
	 * The constructor is used to randomly generate an entire league. This is fairly intense so might need a loading screen
	 * TODO: perhaps take a callback function that is notified of progress.
	 */
	public League(Assets assets){
		if(!assets.isGenLoaded()) assets.loadGenData();//load data files into memory
		current_week = 1;
		teams = new ArrayList<Team>();
		fixtures = new ArrayList<Match>();
		weekly_results = new ArrayList<MatchResult>();
		name=generateName();
		SETTINGS = new SettingsContainer();
		//Generate teams
		for(int i=0;i<LEAGUE_SIZE;i++) {
			teams.add(new Team(assets,this));
		}
		//Generate fixtures
		fixtures = generateFixtures(teams);
		current_week = 1;
		//final_week = 2*(LEAGUE_SIZE-1);
		resetLeagueTable();
	}
	public League(){}
	/**
	 * This will generate a new league around the given team. It will take into account promotion and relegation appropriately.
	 * relegation and promotion tell the algorithms how many teams to move from the league at both ends. OBVIOUSLY relegation + promotion must be < LEAGUE_SIZE
	 * @param t
	 */
	public void newSeason(Team team_following) {
		
		//if the given team is in relegation/promotion zone, them and any other teams move to a new league.
		//otherwise, the teams in the relegation/promotion zone are replaced by new teams
		//then, regenerate fixtures etc.
		
		ArrayList<Team> promoted_teams = new ArrayList<Team>();
		ArrayList<Team> relegated_teams = new ArrayList<Team>();
		ArrayList<Team> league_teams = new ArrayList<Team>();
		
		boolean is_promoted = isPromoted(team_following);
		boolean is_relegated = isRelegated(team_following);
		
		for(int i=0;i<table.size();i++) {
			if(i<PROMOTION) {
				promoted_teams.add(table.get(i).team);
			} else if (i>=(table.size()-RELEGATION)) {
				relegated_teams.add(table.get(i).team);
			}
			else {
				league_teams.add(table.get(i).team);
			}
		}
		
		teams.clear();
		fixtures.clear(); 
		
		if (is_promoted) {
			teams.addAll(promoted_teams);
			this.name = generateName();
		}
		else if (is_relegated) {
			teams.addAll(relegated_teams);
			this.name = generateName();
		}
		else {//not a new league
			teams.addAll(league_teams);
		}
		//clear all goals for existing teams
		for(Team t:teams) {
			for(Footballer f:t.footballers) f.resetGoals();
		}
		if(!GameState.assets.isGenLoaded()) GameState.assets.loadGenData();//load data files into memory for team generation
		//fill rest of league with teams
		for(int i=teams.size();i<LEAGUE_SIZE;i++) {
			teams.add(new Team(GameState.assets,this));//presuming the gamestate object is not currently being constructed (i.e. application starting up) (this would crash the game).
		}
		
		//Generate fixtures
		fixtures = generateFixtures(teams);
		current_week = 1;
		//final_week = 2*(LEAGUE_SIZE-1);
		resetLeagueTable(); 
		//for (Team t:teams) System.out.println(t.name);
		//for (Match m:fixtures) System.out.println(m.getDescription());
	}
	
	/**
	 * Generates and returns an ordered list of fixtures generated for the given teams using a double round robin algorithm.
	 * NB: the number of teams MUST be even and > 2. It will crash otherwise.
	 * @param tournament_teams
	 * @return
	 */
	private ArrayList<Match> generateFixtures(ArrayList<Team> tournament_teams) {
		//Each team plays each other team twice.
		ArrayList<Match> autumn = new ArrayList<Match>();
		ArrayList<Match> spring = new ArrayList<Match>();
		
		//round robin generation
		Team pivot = tournament_teams.get(0);
		Team temp;
		boolean flip = true;
		
		ArrayList<Team> robin = new ArrayList<Team>();
		for(int i=1;i<tournament_teams.size();i++) robin.add(tournament_teams.get(i));
		
		for(int i=0;i<(LEAGUE_SIZE-1);i++){//N-1 weeks
			if(flip)
				autumn.add(new Match(this,pivot,robin.get(robin.size()-1),i+1));//pivot plays last team in cycle
			else
				autumn.add(new Match(this,robin.get(robin.size()-1),pivot,i+1));
			for(int j=0;j<((int)LEAGUE_SIZE/2)-1;j++) {//for each match (N/2 matches per week), -1 we already did
				if(flip)
					autumn.add(new Match(this,robin.get(j),robin.get(robin.size()-2-j),i+1));//pair match in cyclic fashion (dark maths here, careful)
				else
					autumn.add(new Match(this,robin.get(robin.size()-2-j),robin.get(j),i+1));
			}
			//rotate cycle
			temp = robin.remove(0);
			robin.add(temp);
			flip = !flip;//ensure some mix of home vs away
		}
		
		//now duplicate but flip home/away for spring season
		for(Match m: autumn) {
			spring.add(new Match(this,m.away,m.home,m.week+(LEAGUE_SIZE-1)));
		}
		autumn.addAll(spring);//collate fixtures
		return autumn;
	}	
	
	/**
	 * Simulate the entire week's fixtures.
	 * Also fills public arraylist of weekly results
	 */
	public void playWeek() {
		Gdx.app.log("SIM","Playing week");
		MatchResult mr;
		weekly_results.clear();
		//try {
			while(nextFixture().week==current_week) {
				mr = nextFixture().run();
				weekly_results.add(mr);
				Gdx.app.log("SIM","Simmed "+mr.match.home.name+" vs "+mr.match.away.name);
				addResult(mr);//add to league calcs
			}
		//}
		//catch(NullPointerException e) {
			//end of season, no more results to add. 
		//	System.out.println("End of Season");
		//}
		current_week++;
		//update weekly positions
		int i=1;
		for(LeagueTableItem lti:table) { //could call getTeamPosition here but this is more efficient
			lti.addWeeklyPosition(i);
			i++;
		}
		GameState.getGameState(0).saveGame();//save game after each week
	}
	
	public boolean isSeasonFinished() {
		for (Match m: fixtures) 
			if(!m.has_run)return false;
		return true;
	}
	
	public boolean isPromoted(Team t) {
		return (this.getTeamPosition(t)<=League.PROMOTION);
	}
	public boolean isRelegated(Team t) {
		return (this.getTeamPosition(t)>(League.LEAGUE_SIZE - League.RELEGATION));
	}
	
	/**
	 * return next unplayed fixture
	 * @return
	 */
	private Match nextFixture(){
		for (Match m: fixtures) 
			if(!m.has_run)return m;
		//there are no unplayed matches in this league
		return null;
	}
	
	/**
	 * Reset the complete league table from the existing results (list of items)
	 * Importantly this must be run to reset weekly position tracking too
	 * @return
	 */
	private void resetLeagueTable() {
		table = new ArrayList<LeagueTableItem>();
		
		for(Team t: teams) {
			table.add(new LeagueTableItem(t));
		}
		for(Match m: fixtures) { 
			if(!m.has_run||m.result==null)//only run matches go into the table . //TODO: THIS BREAKS POSITION HISTORIES. Needs to be fixed to keep position histories correct each week too.
				continue;//presumably all the others will be not run either (since they are in the future) however just in case.
			
			addResult(m.result);
		}
	}
	
	/**
	 * Return the array of past league positions (1 per week)
	 * @param t
	 * @return
	 */
	public ArrayList<Integer> getTeamPositionHistory(Team t) {
		for(LeagueTableItem lti:table)
			if(lti.team==t) return lti.position_history;
		Gdx.app.error("League.getTeamPositionHistory", "Can't find team in table");
		return new ArrayList<Integer>();//empty if can't find team
	}
	
	/**
	 * Modify the league table according to this result.
	 * @param result
	 */
	private void addResult(MatchResult result) {
		for(LeagueTableItem lti:table) {
			if(lti.team==result.match.away ||lti.team==result.match.home) {//for both teams in the match
				lti.addResult(result);
			}
		}
		//order table
		Collections.sort(table);
	}
	/**
	 * Return 5 match form for this team (WWDDL)
	 * @param t
	 * @return
	 */
	public String getForm(Team t) {
		String form = "-----";
		Iterator<Match> i = fixtures.iterator();
		Match m;
		while(i.hasNext()) {
			m = i.next();
			if (!m.has_run)
				break;//no more past results to check
			if (!m.isTeam(t))
				continue;//not playing
			else {
				form = form.concat(m.result.resultForTeam(t));
				//Log.d("DEBUG",form);
			}
		}
		if(form.length()>5) {
			form = form.substring(form.length()-5);
		}
		return form;
	}
	
	public Match findTeamsNextFixture(Team currentTeam){
		
		for(int i=0;i<fixtures.size();i++){
			if(fixtures.get(i).has_run == false){
				if(fixtures.get(i).isTeam(currentTeam)){
					return fixtures.get(i);
				}
			}
		}
		return null;
	}
	
	public boolean teamNameInUse(String name) {
		for(Team t:teams){
			if(t.name.equals(name)) return true;
		}
		return false;
	}
	
	/**
	 * return current league position for team
	 * @param t
	 * @return
	 */
	public int getTeamPosition(Team t) {
		int i = 1;
		for(LeagueTableItem lti:table) {
			if(lti.team==t) return i;
			i++;
		}
		return i+1;
	}
	
	/**
	 * Return current position as ordinal string ("1st", "2nd")
	 * @param t
	 * @return
	 */
	public String getTeamPositionOrdinal(Team t) {
		int value = getTeamPosition(t);
		int hunRem = value % 100;
		int tenRem = value % 10;
		if (hunRem - tenRem == 10) {
			return String.valueOf(value)+"th";
		}
		switch (tenRem) {
		case 1:
			return String.valueOf(value)+"st";
		case 2:
			return String.valueOf(value)+"nd";
		case 3:
			return String.valueOf(value)+"rd";
		default:
			return String.valueOf(value)+"th";
		}
	}
	
	/**
	 * Generate a random name for this league ("Northern Champions Conference")
	 * @return
	 */
	private String generateName() {
		String prefix = GameState.assets.league_prefices.get(GameState.rand.nextInt(GameState.assets.league_prefices.size()));
		String n = ""; 
		if(GameState.rand.nextFloat()<0.8) {
			do {
				n = GameState.assets.league_prefices.get(GameState.rand.nextInt(GameState.assets.league_prefices.size()));
			} while(n==prefix);
			prefix = prefix + " " + n;
		}
		String suffix = GameState.assets.league_suffices.get(GameState.rand.nextInt(GameState.assets.league_suffices.size()));
		if(GameState.rand.nextFloat()<0.2&&n.length()<20) {
			do {
				n = GameState.assets.league_suffices.get(GameState.rand.nextInt(GameState.assets.league_suffices.size()));
			} while(n==suffix);
			suffix = suffix + " " + n;
		}
		return prefix + " " + suffix;
		
	}
	
	/**
	 * Reinitialise circular/upstream pointers after reserialisation
	 */
	public void reinit(){
		for(Team t:teams) {t.reinit(this);}
		
		for(Match m:fixtures)
			m.reinit(this);
		for(LeagueTableItem lti: table)
			lti.reinit(this);
		
		//regenerate league table
		//resetLeagueTable();
	}
	
}
