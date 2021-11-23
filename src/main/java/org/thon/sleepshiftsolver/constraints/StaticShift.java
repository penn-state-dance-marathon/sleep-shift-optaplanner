package org.thon.sleepshiftsolver.constraints;

public class StaticShift {

	public String username;
	public int startTime;
	public String bed;

	public StaticShift(String username, int startTime) {
		this.username = username;
		this.startTime = startTime;
	}
	
	public StaticShift(String username, int startTime, String bed) {
		this(username, startTime);
		this.bed = bed;
	}
}
