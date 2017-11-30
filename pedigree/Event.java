package pedigree;

public class Event implements Comparable<Event>{
	
	public static final int BIRTH = 0;
	public static final int DEATH = 1;
	public static final int REPRODUCTION = 2;
	
	private Sim subject;
	private int type;
	private double time;
	
	public Event(Sim subject, int type, double time) {
		
		this.subject = subject;
		this.type = type;
		this.time = time;
		
	}
	
	public Sim getSubject() {
		
		return subject;
		
	}
	
	public int getType() {
		
		return type;
		
	}
	
	public double getTime() {
		
		return time;
		
	}
	
	@Override
	public int compareTo(Event o) {
		return (Double.compare(this.time, o.time));
	}
	
}