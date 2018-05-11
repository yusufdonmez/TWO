package two;

/**
 *
 * @author yusuf
 */

import java.util.ArrayList; 
import java.util.Collections;   

public class TeamSorter {     
  ArrayList<Team> team = new ArrayList<>();       
  public TeamSorter(ArrayList<Team> team) {         
    this.team = team;     
  }       
  public ArrayList<Team> getSortedTeamByFitness() {         
    Collections.sort(team);         
    return team;     
  } 
}
