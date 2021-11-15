package org.thon.sleepshiftsolver.constraints;

import java.util.ArrayList;
import java.util.List;

import org.thon.sleepshiftsolver.Constants;

public class BedCannotBeUsedRange {

	public int startTime;
	public int endTime;

	public BedCannotBeUsedRange(int startTime, int endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
}
