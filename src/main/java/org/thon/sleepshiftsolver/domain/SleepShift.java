
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
}
