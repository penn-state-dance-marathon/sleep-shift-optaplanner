package org.thon.sleepshiftsolver.constraints;

import java.util.ArrayList;
import java.util.List;

import org.thon.sleepshiftsolver.Constants;

public class MaxBedConstraint {

	public int maxBeds;
	public int startTime;
	public int endTime;

	public MaxBedConstraint(int maxBeds, int startTime, int endTime) {
		this.maxBeds = maxBeds;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	/**
	 * Remove bed constraints that are at the max
	 * @param input
	 * @return
	 */
	public static List<MaxBedConstraint> clean(List<MaxBedConstraint> input) {
		List<MaxBedConstraint> result = new ArrayList<>();
		int maxBeds = 0;
		for (MaxBedConstraint constraint : input) {
			if (constraint.maxBeds > maxBeds) {
				maxBeds = constraint.maxBeds;
			}
		}
		
		for (MaxBedConstraint constraint : input) {
			if (constraint.maxBeds != maxBeds) {
				result.add(constraint);
			}
		}
		return result;
	}
	
	public static List<MaxBedConstraint> processStaticShifts(List<MaxBedConstraint> maxBedConstraints, List<StaticShift> staticShifts) {
		int maxBeds = 0;
		int endTime = 0;
		for (MaxBedConstraint constraint : maxBedConstraints) {
			if (constraint.maxBeds > maxBeds) {
				maxBeds = constraint.maxBeds;
			}
			if (constraint.endTime > endTime) {
				endTime = constraint.endTime;
			}
		}
		
		// This is just the easiest way unfortunately
		// Set up a block-by-block set of max bed constraints
		List<MaxBedConstraint> bedsAtTime = new ArrayList<>();
		for (int i = 0; i <= endTime; i++) {
			bedsAtTime.add(new MaxBedConstraint(maxBeds, i, i));
		}
		
		// Populate based on existing bed counts
		for (MaxBedConstraint constraint : maxBedConstraints) {
			for (int time = constraint.startTime; time <= constraint.endTime; time++) {
				bedsAtTime.get(time).maxBeds = constraint.maxBeds;
			}
		}
		
		// Subtract static shift times
		for (StaticShift shift : staticShifts) {
			for (int time = shift.startTime; time < shift.startTime + Constants.SHIFT_LENGTH; time++) {
				if (time <= bedsAtTime.size()) {
					bedsAtTime.get(time).maxBeds--;
					if (bedsAtTime.get(time).maxBeds < 0) {
						System.out.println("Too many beds used at time " + time);
						return null;
					}
				}
			}
		}

		// Construct final bed constraint list
		List<MaxBedConstraint> finalBedConstraints = new ArrayList<>();
		int previousMaxBeds = -1;
		for (int time = 0; time < endTime; time++) {
			if (previousMaxBeds == bedsAtTime.get(time).maxBeds) {
				finalBedConstraints.get(finalBedConstraints.size() - 1).endTime = bedsAtTime.get(time).endTime;
			} else {
				finalBedConstraints.add(bedsAtTime.get(time));
			}
		}
		return clean(finalBedConstraints);
	}
}
