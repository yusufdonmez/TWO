/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author yusuf
 */
public class TWO {

    private final double P = Math.PI;           //pi sayısı
    private final double E = Math.E;            // e sayısı
    private final long SEED = 111111;           //rastgelelik için seed sayısı
    private final int MAX_ITERATION = 200;      //algoritmanın calısma sayısı
    private final int PROBLEM = 1;              //işlem yapılacak problem no
    private final int league_size = 10;              // parents sayısı
    private final int c_size = 30;              // childs sayısı
    private double GAMA = 1.1;                  //gama sabiti
    private double ALFA = 0.9;                  //alfa aralık [0.9,0.99]
    private double BETA = 0.05;                 //beta aralık (0,1]
    
    private ArrayList<Team> league;              //parents olarak
    private ArrayList<Team> c;              //child olarak
    private Random rand;                        //rastgele üretilecek
        
    public static void main(String[] args) {
    TWO two = new TWO();
    two.initialize();
    two.run();
    
//   System.out.println("problem: 1 "+ ga.fitnessValue(1, -0.0898, 0.7126)); //=-1.316(-0.0898, 0.7126)
//    System.out.println("problem: 2 "+ ga.fitnessValue(2, -P, 12.275));  // =0.397887(-π , 12.275), (π , 2.275), (9.42478, 2.475)
//    System.out.println("problem: 3 "+ ga.fitnessValue(3, 0.0 ,0.0)); // (0.0, 0.0)=0.0
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
            int k=0;        //yeni kromozom indeksi
            //double SR=0.0;  //Success Rate
            
            // Herbir aday çözüm için fitness değeri hesaplanır
            for (int i = 0; i < league_size; i++) {
                league.get(i).setFitness(calculateFitness(PROBLEM,league.get(i).getX(),league.get(i).getY()));                
            }
            // Yeni çözümleri sıralayıp listeyi güncellenir
            TeamSorter teamSorter = new TeamSorter(league);
            ArrayList<Team> sortedLeague = teamSorter.getSortedTeamByFitness();
            // fit(Xi) göre yeni Weight değerleri hesaplanır
            for (int i = 0; i < league_size; i++) {
                league.get(i).setWeight(calculateWeight(league.get(i).getFitness(), sortedLeague.get(0).getFitness(), sortedLeague.get(league_size-1).getFitness()));                
            }
            
            for(int i=0;i<league_size;i++){      //parent üzerinden child üretimi
                for(int j=0; j<(c_size/league_size) ;j++){
                    double moving_x = rand.nextGaussian()*GAMA;
                    double moving_y = rand.nextGaussian()*GAMA;
                    c.get(k).setX(league.get(i).getX() + moving_x);
                    c.get(k).setY(league.get(i).getY() + moving_y);
                    c.get(k).setFitness( calculateFitness(PROBLEM, c.get(k).getX(), c.get(k).getY()));
                    if(c.get(k).getFitness() < league.get(i).getFitness()){
                        SR = SR + 1.0;
                    }
                    k++;                    //c_size'a ulaşacak
                }
            }
            if(SR/c_size > 0.2)            // mutasyon
                GAMA = GAMA*( 1/0.85);
                else
                GAMA = GAMA*( 0.85 );
            chooseKromozom(t);               //Kromozom seçimi
            t++;                            // Bir iterasyon sonu
        }                
    }
    
    private void chooseKromozom(int index){
        for(int i=0;i<league_size;i++){
            for(int j=0;j<c_size;j++){
                if(c.get(j).getFitness() < league.get(i).getFitness()){
                    league.get(i).setFitness( c.get(j).getFitness());
                    league.get(i).setX(c.get(j).getX());
                    league.get(i).setY(c.get(j).getY());
                    c.get(j).setFitness(999999.999);
                } else
                    league.get(i).setFitness( league.get(i).getFitness());            
            }
        }
        double best = 999999.0;
        for (int i = 0; i < league_size; i++) {
            if (league.get(i).getFitness() < best )
                best = league.get(i).getFitness();
        }
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter("./test/fitness.txt", true)));
            out.println(index+"\t"+Math.round(best*10000.0)/10000.0);
        }catch (IOException e) {           System.err.println(e);            }
         finally{            if(out != null){            out.close();            }        }
        System.out.println("now best is : "+best);
    }
    
        
    private double randomInterval(double low, double high){
        rand = new Random();
        return (high-low)*rand.nextDouble()+low;
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
        return (fitX-fitWorst)/(fitBest-fitWorst)+1;    
    }
}


