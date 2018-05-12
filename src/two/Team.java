package two;

/**
 *
 * @author yusuf dönmez
 */
public class Team implements Comparable<Team>{

    private double x;
    private double y;
    private double fitness;
    private double weight;

    public Team() {
    }
    
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }   
    
    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Team o) {
           return (this.getFitness()< o.getFitness() ? -1 : 
            (this.getFitness()== o.getFitness() ? 0 : 1));  
    }
    
}