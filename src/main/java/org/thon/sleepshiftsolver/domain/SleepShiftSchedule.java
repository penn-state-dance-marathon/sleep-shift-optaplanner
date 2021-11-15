
package org.thon.sleepshiftsolver.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.thon.sleepshiftsolver.Constants;
import org.thon.sleepshiftsolver.constraints.MaxBedConstraint;
import org.thon.sleepshiftsolver.constraints.MaxSleepingConstraint;
import org.thon.sleepshiftsolver.constraints.MaxSleepingList;

@PlanningSolution
public class SleepShiftSchedule {

	private static SleepShiftSchedule singleton;
	
    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "sleepshiftRange")
    private List<SleepShift> sleepshiftList;

	@ProblemFactCollectionProperty
    @ValueRangeProvider(id = "bedRange")
    private List<Bed> bedList;

    @PlanningEntityCollectionProperty
    private List<User> userList;

    @PlanningScore
    private HardSoftScore score;

    public List<MaxBedConstraint> maxBedConstraints;
    public List<MaxSleepingConstraint> maxSleepingConstraints;
    
    // No-arg constructor required for OptaPlanner
    public SleepShiftSchedule() {
    	singleton = this;
    }

    public SleepShiftSchedule(List<SleepShift> sleepshiftList, List<Bed> bedList, List<User> userList) {
    	this();
        this.sleepshiftList = sleepshiftList;
        this.bedList = bedList;
        this.userList = userList;
    }

    public List<SleepShift> getSleepshiftList() {
		return sleepshiftList;
	}
    
    public SleepShift getSleepShiftAt(int startTime) {
    	for (SleepShift shift : sleepshiftList) {
    		if (shift.getStartTime() == startTime) {
    			return shift;
    		}
    	}
    	return null;
    }

	public List<Bed> getBedList() {
		return bedList;
	}

	public List<User> getUserList() {
		return userList;
	}

	public HardSoftScore getScore() {
		return score;
	}
	
	public void prettyPrint() {
		System.out.println(" = = Solution = = ");
//		System.out.println("Beds:");
//		for (Bed b : bedList) {
//			System.out.println("\t" + b.getId());
//		}
//
//		System.out.println("Sleep shifts:");
//		for (SleepShift s : sleepshiftList) {
//			System.out.println("\t" + s.getStartTime());
//			if (s.sleepingDuringThisTime.size() > 0) {
//				for (MaxSleepingList maxS : s.sleepingDuringThisTime) {
//					System.out.println(maxS);
//				}
//			}
//		}
		for (User u : userList) {
			System.out.println("\t" + u.toString());
		}
	}
	
	public SleepShift getLastSleepShiftFrom(SleepShift shift) {
		if (shift == null) {
			return null;
		}
		SleepShift lastShift = sleepshiftList.get(0);
		for (SleepShift shift2 : sleepshiftList) {
			if (shift.getStartTime() + Constants.SHIFT_LENGTH - 1 == shift2.getStartTime()) {
				return shift2;
			}
			if (shift2.getStartTime() > lastShift.getStartTime()) {
				lastShift = shift2;
			}
		}
		return lastShift;
	}
	
	public static SleepShiftSchedule getSingleton() {
		return singleton;
	}
	
}
