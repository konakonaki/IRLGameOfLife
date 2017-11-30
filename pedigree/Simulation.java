package pedigree;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;

public class Simulation {
	
	private static final double FIDELITY = 0.9;
	private static final Random RANDOM = new Random();
	private static final AgeModel AGEMODEL = new AgeModel();
	private static double r;
	
	//test pour calculer le ratio naissances/morts
	private double deathRatio = 0;
	private double birthRatio = 0;
	private double deathRatio2 = 0;
	private double birthRatio2 = 0;
	
	//test pour calculer la moyenne d'enfants par mère
	private Map<Sim, Integer> mothers = new HashMap<>();
	
	private int nbDeaths = 0;
	private double deathAge = 0;
	private int nbDeathsYoung = 0;
	private int nbDeathsOld = 0;
	
	public static void main(String[] args) {
	
		Simulation simulation = new Simulation();
		int nbFounders = 2000;
		int tMax = 10000;
		simulation.simulate(nbFounders, tMax);
		
	}
	
	public void simulate(int n, double tMax) {
		
		//Priority queue pour les événements
		PriorityQueue<Event> queue = new PriorityQueue<>();
		
		r = 2.0 / AGEMODEL.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
		List<Sim> population = new LinkedList<>();

		generateFounders(n, queue);
		
		while(!queue.isEmpty()) {
		
			Event currentEvent = queue.poll();

			System.out.println("Date: " + currentEvent.getTime());
			System.out.println("Population: " + population.size() + "\n");
			
			Sim subject = currentEvent.getSubject();
			
			//si on dépasse la limite de temps, on quitte la boucle
			if(currentEvent.getTime() > tMax) {
				
				System.out.println("Arrêt: temps dépassé.\n");
				break;
				
			}
			
			//si le sujet de l'événement est mort, on passe au prochain événement
			if(subject.getDeathTime() < currentEvent.getTime()) {
				
				continue;
				
			}
			
			switch(currentEvent.getType()) {
			
			case Event.DEATH:
				
				incrementDeathsTest(currentEvent); //test temporaire
				incrementDeathAgesTest(currentEvent);
				population.remove(subject);
				
				break;
				
			case Event.BIRTH:
					
				population.add(currentEvent.getSubject());
				
				incrementBirthsTest(currentEvent);	//test temporaire
				
				break;
				
			case Event.REPRODUCTION:
				
				if(subject.isMatingAge(currentEvent.getTime())) {
					
					incrementChildrenTest(currentEvent);	//test temporaire
					
					//décide si la femme change de partenaire ou non
					decideNewPartner(currentEvent, queue, population);
					
					createChild(currentEvent, queue);

					//prépare le prochain accouplement de la femme
					
				}
				enqueueMotherNextReproduction(currentEvent, queue);
				
				break; //FIN REPRODUCTION
			
			} 
			
		}
		
		printResults(population);
		
	}
	
	private Map<Double, Sim> coalescence(PriorityQueue<Sim> queue) {
		
		Map<Double, Sim> coalescence = new HashMap<>();
		
		while(!queue.isEmpty()) {
			
			Sim sim = queue.poll();
			
			Sim father = sim.getFather();
			
			if(queue.contains(father)) {
				
				//aucune idée si c'est bon
				coalescence.put(sim.getBirthTime(), father);
				
			}
			else {
				
				queue.offer(father);
				
			}
			
		}
		
		return coalescence;
		
	}
	
	private PriorityQueue<Sim> getMalePopulation(List<Sim> population) {
		
		PriorityQueue<Sim> queue = new PriorityQueue<>();
		
		for(Sim sim : population) {
			
			if(sim.isMale()) {
				
				queue.offer(sim);
				
			}
			
		}
		
		return queue;
		
	}
	
	private void createChild(Event event, PriorityQueue<Event> queue) {
		
		//conception du nouveau sim
		Sim child = new Sim(Sim.getRandomSex(), event.getSubject(), event.getSubject().getMate(), event.getTime());
		Event birth = new Event(child, Event.BIRTH, child.getBirthTime());
		queue.offer(birth);
		
		double age = AGEMODEL.randomAge(RANDOM);
		
		child.setDeath(event.getTime() + age);
		Event death = new Event(child, Event.DEATH, child.getDeathTime());
		queue.offer(death);
		
		//si l'enfant est une fille, on planifie son prochain accouplement
		if(child.isFemale()) {
					
			double wait = AgeModel.randomWaitingTime(RANDOM, r) + child.getBirthTime();
			
			Event childReproduction = new Event(child, Event.REPRODUCTION, wait);
			queue.offer(childReproduction);
			
		}	
		
	}
	
	private void enqueueMotherNextReproduction(Event event, PriorityQueue<Event> queue) {
		
		double wait = AgeModel.randomWaitingTime(RANDOM, r) + event.getTime();	
		
		Event nextReproduction = new Event(event.getSubject(), Event.REPRODUCTION, wait);
		queue.offer(nextReproduction);
		
	}
	
	private void decideNewPartner(Event event, PriorityQueue<Event> queue, List<Sim> population) {
		
		//si la femme n'est pas en couple ou si elle est infidèle
		if(!event.getSubject().isInARelationship(event.getTime()) || Math.random() > FIDELITY) {
			
			//on lui trouve un nouveau partenaire
			Sim mate = findMate(population, event.getTime());
			event.getSubject().setMate(mate);
			mate.setMate(event.getSubject());
			
		}
		
	}
	
	private Sim findMate(List<Sim> population, double time) {
		
		Sim mate = null;

		//tant qu'on n'a pas trouver de partenaire
		while(mate == null) {
			
			int randomIndex = RANDOM.nextInt(population.size());
			
			mate = population.get(randomIndex);
			
			//on retourne le sim s'il est un mâle pouvant se reproduire et qu'il est soit célibataire soit infidèle
			if(mate.isMale() && mate.isMatingAge(time) && (!mate.isInARelationship(time) || Math.random() < 1 - FIDELITY)) {
				
				return mate;
				
			}
			
			mate = null;
			
		}
		
		return null;
		
	}
	
	private void generateFounders(int n, PriorityQueue<Event> queue) {
		
		//création de n nouveau sims
		for(int i = 0; i < n; i++) {
			
			Sim founder = new Sim();
			Event birth = new Event(founder, Event.BIRTH, 0.0);
			queue.offer(birth);
			
			double age = AGEMODEL.randomAge(RANDOM);
			founder.setDeath(age);
			Event death = new Event(founder, Event.DEATH, founder.getDeathTime());
			queue.offer(death);
			
			//si le fondateur est une femme, on planifie son prochain accouplement
			if(founder.isFemale()) {
				
				double wait = AgeModel.randomWaitingTime(RANDOM, r);
				Event reproduction = new Event(founder, Event.REPRODUCTION, wait);
				queue.offer(reproduction);
				
			}
			
		}
		
	}
	
	private void printResults(List<Sim> population) {
		
		System.out.println("Résultats:\n");
		System.out.println("Population finale:" + population.size());
		
		//calcul de la moyenne d'enfants par mères
		Iterator it = mothers.entrySet().iterator();
		
	    double moyenne = 0;
	    
	    int size = mothers.size();
	    
	    while (it.hasNext()) {
	    	
	        Map.Entry pair = (Map.Entry)it.next();
	        it.remove();
	        moyenne += ((Integer)pair.getValue());
	        
	    }
	    
	    moyenne /= size;
	    
	    System.out.println("En moyenne " + moyenne + " enfants par mère");
	    
	    //on compare le ratio naissances/morts avant et après 50 ans
	    System.out.println("Naissances/morts avant la 50ème année: " + birthRatio / deathRatio + "(" + birthRatio + " : " + deathRatio + " )");
	    System.out.println("Naissances/morts après la 50ème année " + birthRatio2 / deathRatio2 + "(" + birthRatio2 + " : " + deathRatio2 + " )");
	    System.out.println("Espérance de vie: " + deathAge / nbDeaths + " années");
	    System.out.println("Nombre de morts vieux de moins de 50 ans: " + nbDeathsYoung);
	    System.out.println("Nombre de morts vieux de 50 ans et plus: " + nbDeathsOld);
		
	}
	
	private void incrementBirthsTest(Event event) {
		
		//test pour calculer le ratio naissances/morts
		if(event.getTime() < 50) {
			
			birthRatio++;
			
		}
		else {
			
			birthRatio2++;
			
		}
		
	}
	
	private void incrementDeathsTest(Event event) {
		
		//test pour calculer le ratio naissances/morts
		if(event.getTime() < 50) {
			
			deathRatio++;
			
		}
		else {
			
			deathRatio2++;
			
		}
		
	}
	
	private void incrementChildrenTest(Event event) {
		
		int children = 1;
		
		//test pour calculer la moyenne d'enfants par mère
		if(mothers.containsKey(event.getSubject())) {
			
			children = mothers.get(event.getSubject()) + 1;
			
		}
			
		mothers.put(event.getSubject(), children);
		
	}
	
	private void incrementDeathAgesTest(Event event) {
		
		nbDeaths++;
		
		double age = event.getTime() - event.getSubject().getBirthTime();
		
		deathAge += age;
		
		if(age < 50) {
			
			nbDeathsYoung++;
			
		}
		else {
			
			nbDeathsOld++;
			
		}
		
	}

}
