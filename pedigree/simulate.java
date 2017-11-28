package pedigree;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Random;

/**COMMENTAIRE (28 nov, 2:20 AM, Stéphanie)
 * Virtuellement, ca fonctionne. Mais pour X raison, je ne peux pas aller jusqu'à E.time > 80 ans.
 * Je vais continuer à chercher pour la solution. Si vous remarquez de quoi pouvant résoudre cette issue, SVP m'en faire avis!
 * Je considère AgeModel, Event, et Sim comme étant terminés. Reste simulate.
 * 
 */







import pedigree.Sim.Sex;

public class simulate extends AgeModel{

	public static void simulateGameOfLifeIRL(int n, double Tmax, double fidelity) {
		
		AgeModel a = new AgeModel();
		PriorityQueue<Events> eventQueue = new PriorityQueue<Events>();
		ArrayList<Sim> population  = new ArrayList<Sim>();
		for (int i = 0; i<n; i++) {
			//NAISSANCE DU FONDATEUR
			Sim fondateur  = new Sim(Sex.values()[new Random().nextInt(Sex.values().length)]);
			Events birth = new Events(fondateur, 0, (float)0.0);
			eventQueue.add(birth);
			
			//TEMPS MORT FONDATEUR
			Random rand = new Random();
			fondateur.setDeathTime((float)a.randomAge(rand));
			Events death = new Events(fondateur, 2, (float)fondateur.getDeathTime());
			eventQueue.add(death);
			
			
			//REPRODUCTION
			if(Sex.F.equals(fondateur.getSex())) { //female
				
				Events reprod = new Events(fondateur, 1,  (float)Sim.MIN_MATING_AGE_F+(float)randomWaitingTime(rand, 8));
				eventQueue.add(reprod);
			}
			population.add(fondateur);
		}
		
		int flag =0;
		while(flag == 0) {
			{
			      Events E = eventQueue.poll(); // prochain événement
			      System.out.println(E.subject.toString());
			      if(E.time>Tmax) break; // arrêter à Tmax
			      if (E.type ==2 )
			      {
			    	  population.remove(E.subject);//mort: retiré de la pop.
			    	  System.out.println("mort");

			      } 
			      if(E.type==0  && E.subject.getBirthTime()>=E.time&& !(population.contains(E.subject))) {
			    	  population.add(E.subject);
			    	  System.out.println("naissance");

			      }
			      if(E.type==1) {
			    	if(!(E.subject.isInARelationship(E.time)) ||  (E.subject.isInARelationship(E.time)  &&  Math.random()>fidelity)) {
			    		//Si la femme est vierge, ou elle veut tromper son partnenaire
			    		
			    		
			    		while(true) { //pas la meilleure solution. Une solution plus directe (directement au nombre de mâles, au lieu du nombre de la population)
			    			Random random = new Random();
			    			Sim randomSim = population.get(random.nextInt(population.size())); //Personne choisie au hasard
			    			
			    			if(((randomSim.isInARelationship(E.time)&&Math.random()<fidelity)  ||  !(randomSim.isInARelationship(E.time)))
			    					&&(randomSim.isMatingAge(E.time)&& !(Sex.F.equals(randomSim.getSex()))))
			    			
			    			//homme couplé et voulant tromper ou vierge, doit être un mâle et pouvoir s'accoupler
			    			{
			    				  Sim simBaby = new Sim(E.subject, randomSim, E.time+0.75, Sex.values()[new Random().nextInt(Sex.values().length)]);
		    					  Events birth = new Events(simBaby, 0, (E.time + (float)0.75) ); //grossesse de 9 mois
		    					  System.out.println("changing mates or search");
		    					  eventQueue.add(birth);
		    					  
		    					  //mort de l'enfant
		    					  Random rand = new Random();
		    					  simBaby.setDeathTime((float)a.randomAge(rand));
		    					  Events death = new Events(simBaby, 2, (float)simBaby.getBirthTime() +(float)simBaby.getDeathTime());
		    					  eventQueue.add(death);
		    					  
		    					  if(Sex.F.equals(simBaby.getSex())) { //female
		    							
		    							Events reprod = new Events(simBaby, 1, E.time+(float)Sim.MIN_MATING_AGE_F+(float)randomWaitingTime(rand, 1));
		    							eventQueue.add(reprod);
		    					  }
		    					  
		    					  //Reproduction of mother
		    					  Events reprod = new Events(E.subject, 1, (E.time + (float)0.75+ (float)randomWaitingTime(rand, 1)));
		    					  eventQueue.add(reprod);
		    					  System.out.println("" + reprod.time + " et temps actuel " + E.time);
		    					  
		    					  //Setting the male as mate, and vice versa
		    					  E.subject.setMate(randomSim);
		    					  randomSim.setMate(E.subject);

		    					  break;
			    				
			    			}
			    		}
			    	} else if(E.subject.getMate().isMatingAge(E.time)) {
			    		Sim simBaby = new Sim(E.subject, E.subject.getMate(), E.time+0.75, Sex.values()[new Random().nextInt(Sex.values().length)]);
  					  Events birth = new Events(simBaby, 0, (E.time + (float)0.75) ); //grossesse de 9 mois
  					  eventQueue.add(birth);
  					  
  					System.out.println("fidelité");
  					  //mort de l'enfant
  					  Random rand = new Random();
  					  simBaby.setDeathTime((float)a.randomAge(rand));
  					  Events death = new Events(simBaby, 2, (float)simBaby.getBirthTime() +(float)simBaby.getDeathTime());
  					  eventQueue.add(death);
  					  
  					  if(Sex.F.equals(simBaby.getSex())) { //female
  							
  							Events reprodu = new Events(simBaby, 1, E.time+(float)Sim.MIN_MATING_AGE_F+(float)randomWaitingTime(rand, 1));
  							eventQueue.add(reprodu);
  					  }
  					  
  					  //Reproduction of mother
  					  Events reprod = new Events(E.subject, 1, (E.time + (float)0.75+ (float)randomWaitingTime(rand, 8)));
  					  eventQueue.add(reprod);
			    	}
			      }

			   }
			if (eventQueue.isEmpty()) {flag =1;}
		}
		System.out.println("End of EventsQueue");
	}
	

	
	
	public static void main(String[] args) {
		
		simulate.simulateGameOfLifeIRL(20, 2000, (double)0.);
	}

}
