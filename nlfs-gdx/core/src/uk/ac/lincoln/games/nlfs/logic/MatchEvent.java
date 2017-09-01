package uk.ac.lincoln.games.nlfs.logic;

import com.badlogic.gdx.Game;

import java.util.ArrayList;

/**
 * Class that covers non-goal events that might happen in a match
 * @author bkirman
 *
 */
public class MatchEvent implements Comparable<MatchEvent>{
	public transient Footballer player;
	public transient Match match;
	public MatchEventType type;
	public int minute;
	private String player_id;
	public static enum MatchEventType {REDCARD,YELLOWCARD,INJURY,GENERIC};
	private String description;
	
	public MatchEvent() {}

	/**
	 * Generate random non-goal event for this match.
	 * @param match
	 */
	public MatchEvent(Match match, int minute) {
		this.match = match;
		this.minute = minute;

		if(GameState.rand2.nextInt(15)==1) {
			yellowCard();
		}
		if(GameState.rand2.nextInt(10)==1) {
            injury();
        }

		if(type==null) {
            generic();
        }
	}

	private void yellowCard() {
		this.type = MatchEventType.YELLOWCARD;
		ArrayList<Footballer> all_players = new ArrayList<Footballer>();
        all_players.addAll(match.getActivePlayers());

        this.player = all_players.get(GameState.rand2.nextInt(all_players.size())); //random player in match
        this.description = "Yellow card for "+player.getSurname()+" ("+player.team.name+")";

	}

	private void generic(){
        this.type = MatchEventType.GENERIC;
        int r = GameState.rand2.nextInt(28);
        if(r<10) {//manager/fan events
            if(GameState.rand2.nextInt(2)==0) this.player = match.home.manager; else this.player= match.away.manager;
            if(r==0) this.description = player.team.name + "'s manager " + player.getName() + " is furious with the referee";
            if(r==1||r==7) this.description = player.getName() + " is gesticulating wildy at his " + player.team.name + " players";
            if(r==2) this.description = player.team.name + "'s manager " + player.getName() + " is holding his head in frustration";
            if(r==3) this.description = "The "+player.team.name + " fans are chanting "+player.getName()+"'s name";
            if(r==4) this.description = player.team.name + "'s fans are singing a rude song about "+player.getName();
            if(r==5) this.description = "A burst of noise from the stands to encourage the "+player.team.name+" players";
            if(r==6 && player.team == match.away) this.description = "The travelling "+player.team.name+" supporters are shouting abuse at the home fans";
            if(r==6 && player.team == match.home) this.description = "The home fans are shouting abuse at the visiting supporters";
            if(r==8) this.description = "The "+player.team.name+ " manager points angrily at keeper "+player.team.getFootballerAtPosition(Footballer.Position.GK).getSurname();
            if(r==9) this.description = "A cheer from the "+player.team.name+" fans as a rude inflatable is thrown on the pitch";
        }
        if(r>=10&&r<15) {//subs
            ArrayList<Footballer> f= new ArrayList<Footballer>();
            f.addAll(match.home.substitutes);
            f.addAll(match.away.substitutes);
            this.player = f.get(GameState.rand2.nextInt(f.size()));
            if(r==10||r==11||r==12) this.description = "Some activity on the "+player.team.name+" bench as "+this.player.getSurname()+" starts warming up";
            if(r==13) {
                if(GameState.rand2.nextBoolean()) //The substitutes eat too many pies!
                    this.description = player.team.name+" sub "+player.getSurname()+" seems to be tucking into a pie!";
                else this.description = player.team.name+" are on the counter-attack";
            }
            if(r==14) this.description = player.team.name+" manager "+player.team.manager.getName()+" seems to be giving sub "+this.player.getSurname()+" some instructions";
        }
        if(r>=15&&r<20) {
            ArrayList<Footballer> f= new ArrayList<Footballer>();
            f.addAll(match.home.strikers);
            f.addAll(match.away.strikers);
            this.player = f.get(GameState.rand2.nextInt(f.size()));
            if(r==15||r==16) this.description = player.team.name+" Striker "+player.getSurname()+" is caught offside";
            if(r==17||r==18) this.description = "A long ball from "+player.team.name+" but "+player.getSurname()+" can't quite get to it";
            if(r==19) this.description = "Striker "+player.getSurname()+" is brought down by the big centre half, but the ref waves it away";
        }
        if(r>=20&&r<25) {//defenders
            ArrayList<Footballer> f= new ArrayList<Footballer>();
            f.addAll(match.home.defenders);
            f.addAll(match.away.defenders);
            this.player = f.get(GameState.rand2.nextInt(f.size()));
            if(r==20) this.description = "Burly "+player.team.name+" defender "+player.getSurname()+" cuts out the attack with a mean sliding tackle";
            if(r==21) this.description = "Defender "+player.getSurname()+" ("+player.team.name+") hoofs the ball upfield";
            if(r==22) this.description = "Defender "+player.getSurname()+" ("+player.team.name+") clears the ball for a throw-in";
            if(r==23) this.description = "Meaty "+player.team.name+" defender "+player.getSurname()+" heads the ball clear";
            if(r==24) this.description = player.team.name+"'s "+player.getSurname()+" jogs up the channel";
        }
        //warnings
        if(r>=25) {
            ArrayList<Footballer> f= match.getActivePlayers();
            this.player = f.get(GameState.rand2.nextInt(f.size()));
            if(r==25) this.description = player.getSurname()+" ("+player.team.name+") given a lecture by the ref";
            if(r==26) this.description = player.getSurname()+" ("+player.team.name+") seems to be getting a stern warning from the ref";
            if(r==27) this.description = "The ref is talking to "+player.getSurname()+" ("+player.team.name+") after a vicious tackle";
        }
    }

    void injury() {
        this.type = MatchEventType.INJURY;
        ArrayList<Footballer> all = match.getActivePlayers();
        this.player = all.get(GameState.rand2.nextInt(all.size()));
        int r = GameState.rand2.nextInt(6);
        if(r==0) description= player.getSurname() +" ("+player.team.name+") seems to be limping slightly";
        if(r==1) description= "The physio comes on to see to "+player.getSurname() +" ("+player.team.name+")";
        if(r==2) description= player.getSurname() +" ("+player.team.name+") goes down after colliding with the opposing defender";
        if(r==3) description= player.getSurname() +" ("+player.team.name+") writhes on the floor after tripping over the ball";
        if(r==4) description= player.getSurname() +" ("+player.team.name+") is calling for the physio";
        if(r==5) description= player.getSurname() +" ("+player.team.name+") has broken his nose but is playing on";
    }
	
	public String getDescription() {
		return description;
	}
	
	/**
	 * Reinit pointers after deserialisation
	 * @param m
	 */
	public void reinit(Match m) {
		this.match = m;
		for(Footballer f : m.home.footballers) {
			if(f.getName().equals(player_id)) {
				player = f;
				return;
			}
		}
		for(Footballer f : m.away.footballers) {
			if(f.getName().equals(player_id)) {
				player = f;
				return;
			}
		}
	}
	//allows them to be sorted by time of event
	public int compareTo(MatchEvent another) {
		return  minute - another.minute;
	}

}
