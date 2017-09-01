package uk.ac.lincoln.games.nlfs.logic;

import java.util.ArrayList;
import java.util.Collections;

import uk.ac.lincoln.games.nlfs.Assets;
import uk.ac.lincoln.games.nlfs.logic.Footballer.Position;
import uk.ac.lincoln.games.nlfs.logic.MatchEvent.MatchEventType;

public class MatchResult {
	public transient Match match;
	public ArrayList<Goal> home_goals, away_goals;
	public ArrayList<MatchEvent> match_events;
	public int first_half_length;
    public int second_half_length;
	public int gate;
	
	public MatchResult(Match match,int h_goals,int a_goals){
		this.match = match;
		this.home_goals = new ArrayList<Goal>();
		this.away_goals = new ArrayList<Goal>();
		this.match_events = new ArrayList<MatchEvent>();

        first_half_length = 46+ GameState.rand2.nextInt(7);
        second_half_length = 46+ GameState.rand2.nextInt(7);
		//calculate scorers
		for(int i=0;i<h_goals;i++){
			this.home_goals.add(new Goal(pickScorer(match.home),(GameState.rand2.nextInt(first_half_length+second_half_length))));
		}
		for(int i=0;i<a_goals;i++){
			this.away_goals.add(new Goal(pickScorer(match.away),(GameState.rand2.nextInt(first_half_length+second_half_length))));
		}
		//calculate events

		for(int i=0;i<10+GameState.rand2.nextInt(10);i++) {//10-20 events per match
			match_events.add(new MatchEvent(match,GameState.rand2.nextInt(first_half_length+second_half_length)));
        }

        /*
        TODO: Make players able to get 2nd yellow card or red card
        At the moment the next lines prevent that from happening. Obviously this would be good, but sendings-off create headaches for us:
            * Currently events are given random times. we would need to sim them minute by minute, to make sure an event doesn't happen to a player who is off
                * Note this also makes substitutions tricky.
            * What happens in future matches? tracking bans between matches will require significant rewrite. What is the benefit? Flavour?
         */
        //remove all but first yellow card for each player
        ArrayList<Footballer> yellows = new ArrayList<Footballer>();
        for(MatchEvent me: match_events){
            if(me.type==MatchEventType.YELLOWCARD)
                if(yellows.contains(me.player)) match_events.remove(me);
                else yellows.add(me.player);
        }

		Collections.sort(match_events);
		
		//set gate for this match. more fans come if the team is doing well
		gate = 70 + GameState.rand2.nextInt(190) + ((League.LEAGUE_SIZE - GameState.league.getTeamPosition(match.home))*8)+((League.LEAGUE_SIZE - GameState.league.getTeamPosition(match.away))*2);
		
	}
	public MatchResult(){}

	public Team getWinner() {
		if(this.home_goals.size()==this.away_goals.size()) return null;
		if(this.home_goals.size()>this.away_goals.size()) return match.home; else return match.away;
	}
	
	/**
	 * Returns a random scoring player
	 * @param team
	 * @return
	 */
	private Footballer pickScorer(Team team){
		//60% of goals are scored by strikers, 30% by midfielders and 9% by DF and 1% by GK!
		float s = GameState.rand2.nextFloat();
		if(s<0.6) return team.getFootballerAtPosition(Position.ST);
		if(s<0.9) return team.getFootballerAtPosition(Position.MF);
		if(s<0.99) return team.getFootballerAtPosition(Position.DF);
		else return team.getFootballerAtPosition(Position.GK);
	}
	
	/**
	 * Returns a nicely filled news story about this match, from the perspective of the supplied team
	 * @param team
	 * @return
	 */
	public String getDescription(Team team) {
		if(team!=match.home&&team!=match.away) return getDescription(match.home);//fail silently and return the description for the home team
		String scoreline;
		Team opposition;
		if(team==match.home) {
			scoreline = String.valueOf(home_goals.size())+"-"+String.valueOf(away_goals.size());
			opposition = match.away;
		}
		else {
			scoreline = String.valueOf(away_goals.size())+"-"+String.valueOf(home_goals.size());
			opposition = match.home;
		}
		
		ArrayList<String> news_items = Assets.news_summaries.get(scoreline);
        String news_item = news_items.get(GameState.rand2.nextInt(news_items.size()));//random description
        // Tokens: yourteam, opposition, goalkeeper, defender, midfielder, attacker, stadium
        news_item = news_item.replace("{yourteam}",team.name);
        news_item = news_item.replace("{opposition}",opposition.name);
        news_item = news_item.replace("{goalkeeper}",team.getFootballerAtPosition(Position.GK).getName());
        news_item = news_item.replace("{defender}",team.getFootballerAtPosition(Position.DF).getName());
        news_item = news_item.replace("{attacker}",team.getFootballerAtPosition(Position.ST).getName());
        news_item = news_item.replace("{midfielder}",team.getFootballerAtPosition(Position.MF).getName());
        news_item = news_item.replace("{stadium}",match.home.stadium);
        return news_item;
	}
	
	public String toString() {
		return match.home.name +" "+String.valueOf(home_goals.size())+":"+String.valueOf(away_goals.size())+" "+match.away.name;
	}
	/**
	 * Returns the letter result for this team in this match W/L/D
	 * @param t
	 * @return
	 */
	public String resultForTeam(Team t){
		if(!(t.equals(match.home)||t.equals(match. away))) return null;//team not in this match
		if(getWinner()==null) return("D");
		if(getWinner()==t) return ("W");
		return ("L");
	}
	
	/**
	 * Used for simplifying GF/GA calculations
	 * @param t
	 * @return
	 */
	public int goalsFor(Team t) {
		if(t==match.home) return home_goals.size();
		return away_goals.size();
	}
	public int goalsAgainst(Team t) {
		if(t==match.home) return away_goals.size();
		return home_goals.size();
	}
	
	//Reload circular pointers after deserialisation
	public void reinit(Match m) {
		match = m;
		for(Goal g: home_goals) {
			g.reinit(match.home);
		}
		for(Goal g: away_goals) {
			g.reinit(match.away);
		}
		for(MatchEvent me:match_events) {
			me.reinit(match);
		}
	}
}
