
package org.thon.sleepshiftsolver.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.thon.sleepshiftsolver.constraints.MaxSleepingList;

public class SleepShift {

    private int startTime;
    public List<MaxSleepingList> sleepingDuringThisTime = new ArrayList<>();

    public SleepShift(int startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return String.valueOf(startTime);
    }

	@PlanningId
    public int getStartTime() {
        return startTime;
	}
	
	public boolean bothUsersInMaxSleepingList(User user1, User user2) {
		for (MaxSleepingList list : sleepingDuringThisTime) {
			if (list.usernames.contains(user1.getUsername()) && list.usernames.contains(user2.getUsername())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCountLargerThanLimitForMaxSleepingList(User user, int count) {
		for (MaxSleepingList list : sleepingDuringThisTime) {
			// THIS IS BROKEN because the user could be in more than one list
			if (list.usernames.contains(user.getUsername()) && count > list.maxAsleep) {
				return true;
			}
		}
		return false;
	}
}
