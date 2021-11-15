
package org.thon.sleepshiftsolver.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.thon.sleepshiftsolver.constraints.MaxBedConstraint;
import org.thon.sleepshiftsolver.constraints.MaxSleepingConstraint;

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
		System.out.println("Beds:");
		for (Bed b : bedList) {
			System.out.println("\t" + b.getId());
		}

		System.out.println("Sleep shifts:");
		for (SleepShift s : sleepshiftList) {
			System.out.println("\t" + s.getStartTime());
		}
	}
	
	public static SleepShiftSchedule getSingleton() {
		return singleton;
	}
	
}
