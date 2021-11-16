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
	
	public String getName() {
		if (name != null) {			
			return name;
		} else {
			return this.toString();
		}
	}

}
