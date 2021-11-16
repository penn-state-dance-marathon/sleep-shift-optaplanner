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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.entity.PlanningPin;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.thon.sleepshiftsolver.Constants;

@PlanningEntity
public class User {

    @PlanningId
    private String id;

	@PlanningPin
    private boolean isStaticShift;
    
    private String username;
    public String name;
    public String committee;
    private int shiftNumber;


	@PlanningVariable(valueRangeProviderRefs = "sleepshiftRange")
    private SleepShift sleepShift;
    @PlanningVariable(valueRangeProviderRefs = "bedRange")
    private Bed bed;

    // No-arg constructor required for OptaPlanner
    public User() {
    	this.isStaticShift = false;
    }

    public User(String username, int shiftNumber) {
    	this();
        this.id = username + "-" + shiftNumber;
        this.shiftNumber = shiftNumber;
        this.username = username;
        this.sleepShift = null;
        this.bed = null;
    }

    public User(String username, int shiftNumber, SleepShift shift, Bed bed) {
        this(username, shiftNumber);
        this.sleepShift = shift;
        this.bed = bed;
    }

    @Override
    public String toString() {
        return "User(id=" + id + ", shift=" + this.sleepShift + ", bed=" + this.bed + ")";
    }

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getId() {
        return id;
    }

    public String getUsername() {
		return username;
	}

	public SleepShift getSleepShift() {
		return sleepShift;
	}

	public void setSleepShift(SleepShift sleepShift) {
		this.sleepShift = sleepShift;
	}

	public Bed getBed() {
		return bed;
	}

	public void setBed(Bed bed) {
		this.bed = bed;
	}

	public int getShiftNumber() {
		return shiftNumber;
	}
	
	public int getSleepShiftStartTime() {
		return this.sleepShift.getStartTime();
	}
	
	public int getSleepShiftEndTime() {
		return this.sleepShift.getStartTime() + Constants.SHIFT_LENGTH;
	}
	
	public void setIsStaticShift(boolean isStaticShift) {
		this.isStaticShift = isStaticShift;
	}

}
