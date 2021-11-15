package org.thon.sleepshiftsolver.constraints;

public class MaxSleepingConstraint {

	public String[] usernames;
	public int startTime;
	public int endTime;
	public int maxAsleep;

	public MaxSleepingConstraint(String[] usernames, int startTime, int endTime, int maxAsleep) {
		this.usernames = usernames;
		this.startTime = startTime;
		this.endTime = endTime;
		this.maxAsleep = maxAsleep;
	}
}
