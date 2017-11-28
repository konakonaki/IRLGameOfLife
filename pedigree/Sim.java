/*
 * Copyright (c) 2017, Mikl&oacute;s Cs&#369;r&ouml;s
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package pedigree;

/**
 *
 * @author Mikl&oacute;s Cs&#369;r&ouml;s
 */
public class Sim implements Comparable<Sim>
{
    private static int NEXT_SIM_IDX=0;
    
    public static double MIN_MATING_AGE_F = 16.0;
    public static double MIN_MATING_AGE_M = 16.0;
    public static double MAX_MATING_AGE_F = 50.0; // Janet Jackson
    public static double MAX_MATING_AGE_M = 73.0; // Charlie Chaplin
    
    /** 
     * Ordering by death date.
     * 
     * @param o
     * @return 
     */
    @Override
    public int compareTo(Sim o) 
    {
        return Double.compare(this.deathtime,o.deathtime); //permet de déterminer si date de mort est celle actuellement ou est dépassée.
    }	//Si this est plus petit que le second: pas encore mort. Sinon, est mort.
    
    public enum Sex {F, M};

    private final int sim_ident;
    private double birthtime;
    private double deathtime;
    private Sim mother;
    private Sim father;
    private Sim mate;
    private int children=0;
    
    public int numberKids() {
    	return children;
    }
    public void ExpandingFamily() {
    	children++;
    }
    
    private Sex sex;
    
    protected Sim(Sim mother, Sim father, double birth, Sex sex)		//Constructeur.
    {
        this.mother = mother;
        this.father = father;
        
        this.birthtime = birth;
        this.deathtime = Double.POSITIVE_INFINITY; //L'âge de mort n'est pas encore déterminée. C'est calculé à fur et à mesure.
        
        this.sex = sex;
        
        this.sim_ident = NEXT_SIM_IDX++;
    }
    
    
    
    /**
     * A founding Sim.
     * 
     */
    public Sim(Sex sex) //Sexe choisi aléatoirement
    {
        this(null, null, 0.0, sex);
    }
    
    /**
     * If this sim is of mating age at the given time
     * 
     * @param time
     * @return true if alive, sexually mature and not too old
     */
    public boolean isMatingAge(double time)
    {
        if (time<this.getDeathTime())
        {
            double age = time-this.getBirthTime();
            return 
                    Sex.F.equals(this.getSex())
                    ? age>=MIN_MATING_AGE_F && age <= MAX_MATING_AGE_F
                    : age>=MIN_MATING_AGE_M && age <= MAX_MATING_AGE_M;
        } else
            return false; // no mating with dead people! (no necrophiliacs)
    }
    
    /**
     * Test for having a (faithful and alive) partner. 
     * 
     * @param time
     * @return 
     */
    public boolean isInARelationship(double time)
    {
        return mate != null && mate.getDeathTime()>time //est en relation, avec un partenaire existant, et vivant.
                && mate.getMate()==this; //et que le partenaire du partenaire est le Sim désiré.
    }
    
    public void setDeath(double death)
    {
        this.deathtime = death;
    }
    
    public Sex getSex()
    {
        return sex;
    }
    
    public double getBirthTime()
    {
        return birthtime;
    }
    
    public double getDeathTime()
    {
        return deathtime;
    }
    
    public void setDeathTime(double death_time)
    {
        this.deathtime = death_time;
    }
    
    
    
    /**
     * 
     * @return null for a founder
     */
    public Sim getMother()
    {
        return mother;
    }
    
    /**
     * 
     * @return null for a founder
     */
    public Sim getFather()
    {
        return father;
    }
    
    public Sim getMate()
    {
        return mate;
    }
    
    public void setMate(Sim mate){this.mate = mate;}
    
    public boolean isFounder()
    {
        return (mother==null && father==null);
    }
    
    private static String getIdentString(Sim sim)
    {
        return sim==null?"":"sim."+sim.sim_ident+"/"+sim.sex;
    }
    
    @Override
    public String toString()
    {
        return getIdentString(this)+" ["+birthtime+".."+deathtime+", mate "+getIdentString(mate)+"\tmom "+getIdentString(getMother())+"\tdad "+getIdentString(getFather())
        +"]";
    }
}