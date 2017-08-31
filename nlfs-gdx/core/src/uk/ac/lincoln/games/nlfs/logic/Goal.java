package uk.ac.lincoln.games.nlfs.logic;

public class Goal implements Comparable<Goal>{
	
	public transient Footballer scorer;
	public int time;
	
	private String scorer_id;//serialisation id
	
	public Goal(Footballer scorer,int time) {
		this.time = time;
		this.scorer = scorer;
		scorer.addGoal();
		this.scorer_id = scorer.getName();
	}
	public Goal() {}
	
	public void reinit(Team t) {
		for(Footballer f:t.footballers) {
			if (f.getName().equals(scorer_id)) {
				scorer = f;
				break;
			}
		}
	}
	//allows them to be sorted by time of goal
	public int compareTo(Goal another) {
		return  time - another.time;
	}

}
