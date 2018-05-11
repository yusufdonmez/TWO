/* 
A NOVEL META-HEURISTIC ALGORITHM: TUG OF WAR OPTIMIZATION  
 
A. Kaveh*, † and A. Zolghadr 
Centre of Excellence for Fundamental Studies in Structural Engineering,
Iran University of Science and Technology, Narmak, Tehran 16, Iran 

Yukarıdaki makelede geçen Tug of war (halat çekme oyunu) optimizasyonu için yazılmıştır.
Bu algoritmanın JAVA ile kodlanmış halidir.

3 farklı Problem için çalışır.
PROBLEM = 1 değiştirmelisiniz.
ayrıca kod içerisinde [x,y] aralık  değerlerini.

fitness.txt ye iterasyon sonucu en iyi degeri basar.
 */
package two;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author yusuf donmez
 */
public class TWO {

    private final double P = Math.PI;           //pi sayısı
    private final long SEED = 111111;           //rastgelelik için seed sayısı
    private final int MAX_ITERATION = 1000;      //algoritmanın calısma sayısı
    private final int PROBLEM = 1;              //işlem yapılacak problem no
    private final int league_size = 20;         //# of team(sayısı)
    private final double ALFA = 0.99;           //alfa aralık [0.9,0.99]
    private final double BETA = 0.01;           //beta aralık (0,1]
    private final double MUs = 1.0;             //coefficient of static friiction, statik sürtünme katsayısı
    private double MUk = 1.0;                   //coeffficien of kinematic fricton, kinetic sürtünme
    
    private ArrayList<Team> league;             //league of teams
    private Random rand;                        //rastgele üretilecek
        
    public static void main(String[] args) {
    TWO two = new TWO();
    two.initialize();
    two.run();
    
//   System.out.println("six hump camelback : 1 "+ ga.fitnessValue(1, -0.0898, 0.7126)); //=-1.316(-0.0898, 0.7126)
//   System.out.println("Branin : 2 "+ ga.fitnessValue(2, -P, 12.275));  // =0.397887(-π , 12.275), (π , 2.275), (9.42478, 2.475)
//   System.out.println("Griewank: 3 "+ ga.fitnessValue(3, 0.0 ,0.0)); // (0.0, 0.0)=0.0
    }   
    
    private void initialize(){       
        league = new ArrayList<>();       
        
        for(int i=0;i<league_size;i++){
            Team team = new Team();
            if(PROBLEM == 1){
                team.setX(randomInterval(-3, 3));
                team.setY(randomInterval(-2, 2));
                team.setFitness(calculateFitness(PROBLEM,team.getX(), team.getY()));
            } else if(PROBLEM == 2){
                team.setX(randomInterval(-5, 10));
                team.setY(randomInterval(0, 15));
                team.setFitness(calculateFitness(PROBLEM,team.getX(),team.getY()));
            } else if(PROBLEM == 3){
                team.setX(randomInterval(-100, 100));
                team.setY(randomInterval(-100, 100));
                team.setFitness(calculateFitness(PROBLEM,team.getX(),team.getY()));
            }            
            league.add(team);
        }
    }
    
    private void run(){
        rand = new Random();
        rand.setSeed(SEED);

        int t=0;
        while(t<MAX_ITERATION){            
            // Herbir aday çözüm için fitness değeri hesaplanır
            for (int idx = 0; idx < league.size(); idx++)
                league.get(idx).setFitness(calculateFitness(PROBLEM,league.get(idx).getX(),league.get(idx).getY()));
            
            // Yeni çözümleri sıralayıp listeyi güncellenir
            TeamSorter teamSorter = new TeamSorter(league);
            ArrayList<Team> sortedLeague = teamSorter.getSortedTeamByFitness();
            league = (ArrayList<Team>)sortedLeague.clone();
            for (int i = league.size() ; i > league_size; i--)          //remove worst teams to #of original teams
                league.remove(i-1);
                        
            // fit(Xi) göre yeni Weight değerleri hesaplanır
            for (int idx = 0; idx < league_size; idx++) {
                league.get(idx).setWeight(calculateWeight(            //Eq.(5)
                        league.get(idx).getFitness(),                 //fit(i)
                        league.get(0).getFitness(),                  //fit_best
                        league.get(league_size-1).getFitness()));   //fit_worst
            }
            
            // Takımlar kıyaslanır. hangisi yaklacak, bulunur.
            for (int i = 0; i < league_size; i++) {                 //for-1 
                Team newTeam = new Team();                          //be added to league
                double sumMovingX = 0.0;
                double sumMovingY = 0.0;
                for (int j = 0; j < league_size; j++) {             //for-2
                    double Wi = league.get(i).getWeight();
                    double Wj = league.get(j).getWeight();
                    if (Wi < Wj) {
                        // moving team towards other, takım hareket ettirilir
                        double pullingForce = Double.max(Wi*MUs, Wj*MUs);
                        double resultantForce = pullingForce - Wi*MUk;//Eq. (6)
                        double gravitationX = league.get(j).getX() - league.get(i).getX();//Eq. (8)
                        double gravitationY = league.get(j).getY() - league.get(i).getY();//Eq. (8)
                        double accelerationX = (resultantForce/(Wi*MUk))*gravitationX;//Eq. (7)
                        double accelerationY = (resultantForce/(Wi*MUk))*gravitationY;//Eq. (7)
                        double deltaT = 1.0;
                        rand = new Random();                        
                        double movingX = 0.5*accelerationX*deltaT*deltaT + ALFA*BETA*((3)-(-3))*rand.nextGaussian();//Eq. (9)
                        double movingY = 0.5*accelerationY*deltaT*deltaT + ALFA*BETA*((2)-(-2))*rand.nextGaussian();//Eq. (9)
                        sumMovingX += movingX; //Eq. (10)
                        sumMovingY += movingY; //Eq. (10)
                    }//end if
                }//end of for
                //Determine the total displacement of team i using Eq. (10) sumMovingX,sumMovingY
                //Determine the final position of team i using Eq.(11) 
                double newX =  league.get(i).getX() + sumMovingX; //Eq. (11)
                double newY =  league.get(i).getY() + sumMovingY; //Eq. (11)

                //Use the side constraint handling technique to regenerate violating variables
                //Aralığın dışına taşan değerler yeniden aralığa alınır.
                if(newX<-3 || newX>3){
                    newX = randomInterval(-3, 3);
                }
                if (newY<-2 || newY>2)
                    newY = randomInterval(-2, 2);
                newTeam.setX(newX);
                newTeam.setY(newY);
                
                if(sumMovingX!=0 || sumMovingY!=0)      //if it moves then add to league
                    league.add(newTeam);

            }//end of For

            //Printing BEST to file...
            printFitness(t,league.get(0).getFitness());
            System.out.println(t+" now best is : "+league.get(0).getFitness());
            
            MUk = MUk - t/MAX_ITERATION;
            t++;                            // end of 1 itaration
        }    
        
        for (int i = 0; i < league_size; i++) {
            System.out.println(i+" fit "+league.get(i).getFitness()+" weight:"+league.get(i).getWeight()
            +" X:"+league.get(i).getX()+" Y:"+league.get(i).getY());
        }
    }
            
    private double randomInterval(double low, double high){
        rand = new Random();
        return (high-low)*rand.nextGaussian()+low;
    }
    
    private double calculateFitness(int _problem, double x, double y ){
        switch (_problem){
            case 1 : return (4-(2.1*x*x)+(x*x*x*x)/3)*x*x + (x*y) + (-4+4*y*y)*y*y;
            case 2 : return Math.pow((y - (5.1/(4*P*P))*x*x + (5*x)/P - 6), 2)
                    + 10*(1 - 1/(8*P))*Math.cos(x) + 10;
            case 3: return 1 + (1/200)*(x*x + y*y) - Math.cos(x)*(Math.cos(y/Math.sqrt(2)));
            default: return 0.0;
        }       
    }
    
    private double calculateWeight(double fitX, double fitBest, double fitWorst){
        return (fitX-fitWorst)/(fitBest-fitWorst)+1;    //Eq. (5)
    }
    
    private void printFitness(int iteration,double fitness){
        PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter("./fitness.txt", true)));
                out.println(iteration+"\t"+Math.round(fitness*10000.0)/10000.0);
            }catch (IOException e) { System.err.println(e); }
             finally{ if(out != null){ out.close(); } }
    }
}


