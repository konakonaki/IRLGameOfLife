package pedigree;

public class Events extends simulate implements Comparable<Events>{

	float time;
	int type;
	Sim subject;

	public Events(Sim subject, int typeEvent, float time) { //0=NAISSANCE, 1=REPRODUCTION, 2=DEATH
		this.subject = subject;
		this.type = typeEvent;
		this.time = time;
	}

	@Override
	public int compareTo(Events o) {
		return (Float.compare(this.time, o.time));
	}
}
