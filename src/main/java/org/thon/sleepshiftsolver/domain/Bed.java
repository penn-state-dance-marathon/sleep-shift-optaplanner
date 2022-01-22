/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.thon.sleepshiftsolver.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.thon.sleepshiftsolver.Constants;
import org.thon.sleepshiftsolver.constraints.BedCannotBeUsedRange;

public class Bed {

    private int id;
    public List<BedCannotBeUsedRange> cannotBeUsedDuring = new ArrayList<>();
    private String name;
    
    public Bed(int id) {
        this.id = id;
    }
    
    public Bed(int id, String name) {
    	this(id);
    	this.name = name;
    }

    @Override
    public String toString() {
    	if (name != null) {
    		return name;
    	}
        return String.valueOf(id);
    }

	@PlanningId
    public int getId() {
        return id;
    }
	
	public boolean canBeUsedAt(int time) {
		for (BedCannotBeUsedRange range : cannotBeUsedDuring) {
			if (time <= range.endTime && time >= range.startTime - Constants.SHIFT_LENGTH + 1) {
				return false;
			}
		}
		return true;
	}
	
	public boolean usedByAnyUser(List<User> userList, int time) {
		for (User u : userList) {
			if (u.getBed() != null && u.getBed().id == id &&
					u.getSleepShift() != null && u.getSleepShiftStartTime() - Constants.SHIFT_LENGTH + 1 <= time && u.getSleepShiftEndTime() > time) {
				return true;
			}
		}
		return false;
	}
	
	public String getName() {
		if (name != null) {			
			return name;
		} else {
			return this.toString();
		}
	}
	
	public static Bed findFreeBed(List<Bed> bedList, List<User> userList, int time) {
		List<Bed> bedListSorted = new ArrayList<>(bedList);
		// Prefer beds with more constraints!
		// bedListSorted.sort(new Comparator<Bed>() {
		// 	@Override
		// 	public int compare(Bed o1, Bed o2) {
		// 		if (o1.cannotBeUsedDuring.size() != o2.cannotBeUsedDuring.size()){
		// 			System.out.println(o1.toString() + o2.toString());
		// 		}
		// 		return o2.cannotBeUsedDuring.size() - o1.cannotBeUsedDuring.size();
		// 	}
		// });
		// Hack - reverse
		Collections.reverse(bedListSorted);
		for (Bed bed : bedListSorted) {
			if (!bed.usedByAnyUser(userList, time) && bed.canBeUsedAt(time)) {
				return bed;
			}
		}
		return null;
	}

}
