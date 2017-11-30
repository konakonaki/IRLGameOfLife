package pedigree;

public class Sim implements Comparable<Sim> {
	
	private static int NEXT_SIM_IDX=0;
    public static double MIN_MATING_AGE_F = 16.0;
    public static double MIN_MATING_AGE_M = 16.0;
    public static double MAX_MATING_AGE_F = 50.0; // Janet Jackson
    public static double MAX_MATING_AGE_M = 73.0; // Charlie Chaplin
	
    private final int sim_ident;
    private Sim mother;
    private Sim father;
	private Sim mate;
	public enum Sex {Male, Female}
	private Sex sex;
	private double birthTime, deathTime;
	
	protected Sim(Sex sex, Sim mother, Sim father, double birthTime) {
		
		this.father = father;
		this.mother = mother;
		this.sex = sex;
		this.birthTime = birthTime;
		
		this.sim_ident = NEXT_SIM_IDX++;
		
	}
	
	public Sim(Sex sex) {
		
		this(sex, null, null, 0.0);
		
	}
	
	public Sim() {
		
		sex = getRandomSex();
		setMother(null);
		setFather(null);
		this.birthTime = 0.0;
		
		this.sim_ident = NEXT_SIM_IDX++;
		
	}
	
	@Override
    public int compareTo(Sim o) {
		
        return Double.compare(this.deathTime,o.deathTime);
        
    }
	
	public static Sex getRandomSex() {
		
		double random = Math.random();
		
		if(random >= 0.5)
			return Sex.Male;
		else
			return Sex.Female;
		
	}
	
	public void setFather(Sim father) {
		
		this.father = father;
		
	}
	
	public void setMother(Sim mother) {
		
		this.mother = mother;
		
	}
	
	public Sim getFather() {
		
		return father;
		
	}
	
	public Sim getMother() {
		
		return mother;
		
	}
	
	public boolean isMale() {
		
		return sex == Sex.Male;
		
	}
	
	public boolean isFemale() {
		
		return sex == Sex.Female;
		
	}
	
	public Sex getSex() {
		
		return sex;
		
	}
	
	public void setDeath(double dateMort) {
		
		this.deathTime = dateMort;
		
	}
	
	public double getBirthTime() {
		
		return birthTime;
		
	}
	
	public double getDeathTime() {
		
		return deathTime;
		
	}
	
	public Sim getMate() {
		
		return mate;
		
	}
	
    /**
     * If this sim is of mating age at the given time
     * 
     * @param time
     * @return true if alive, sexually mature and not too old
     */
    public boolean isMatingAge(double time) {
    	
        if (time<getDeathTime())
        {
            double age = time-getBirthTime();
            return 
                    Sex.Female.equals(getSex())
                    ? age>=MIN_MATING_AGE_F && age <= MAX_MATING_AGE_F
                    : age>=MIN_MATING_AGE_M && age <= MAX_MATING_AGE_M;
        } else
            return false; // no mating with dead people
    }
    
    public boolean isInARelationship(double time) {
    	
        return mate != null && mate.getDeathTime()>time 
                && mate.getMate()==this;
        
    }
    
    public void setMate(Sim mate){this.mate = mate;}
    
    public boolean isFounder() {
    	
        return (getMother()==null && getFather()==null);
        
    }
    
    @Override
    public String toString() {
    	
        return getIdentString(this)+" ["+birthTime+".."+deathTime+", mate "+getIdentString(mate)+"\tmom "+getIdentString(getMother())+"\tdad "+getIdentString(getFather())
        +"]";
        
    }
    
    private static String getIdentString(Sim sim) {
    	
        return sim==null?"":"sim."+sim.sim_ident+"/"+sim.sex;
        
    }

}