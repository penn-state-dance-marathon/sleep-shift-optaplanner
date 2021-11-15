
package org.thon.sleepshiftsolver.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class SleepShift {

    private int startTime;

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
