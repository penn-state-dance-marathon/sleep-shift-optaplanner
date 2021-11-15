package org.thon.sleepshiftsolver.constraints;

import java.util.List;

public class MaxSleepingList {

	public List<String> usernames;
	public int maxAsleep;

	public MaxSleepingList(List<String> usernames, int maxAsleep) {
		this.usernames = usernames;
		this.maxAsleep = maxAsleep;
	}
	
	public String toString() {
		return "[" + String.join(",", usernames) + "]=" + maxAsleep;
	}
}
