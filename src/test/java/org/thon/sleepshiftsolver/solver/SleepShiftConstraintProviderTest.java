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

package org.thon.sleepshiftsolver.solver;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;
import org.thon.sleepshiftsolver.domain.User;
import org.thon.sleepshiftsolver.constraints.MaxSleepingList;
import org.thon.sleepshiftsolver.domain.Bed;
import org.thon.sleepshiftsolver.domain.SleepShiftSchedule;
import org.thon.sleepshiftsolver.domain.SleepShift;

class SleepShiftConstraintProviderTest {

    private static final Bed BED1 = new Bed(0);
    private static final Bed BED2 = new Bed(1);

    ConstraintVerifier<SleepShiftConstraintProvider, SleepShiftSchedule> constraintVerifier = ConstraintVerifier.build(
            new SleepShiftConstraintProvider(), SleepShiftSchedule.class, User.class);

    @Test
    void bedConflict() {
        User firstUser = new User("u1", 1, new SleepShift(0), BED1);
        User conflictingUser = new User("u2", 1, new SleepShift(2), BED1);
        User nonConflictingUser = new User("u3", 1, new SleepShift(10), BED1);
        constraintVerifier.verifyThat(SleepShiftConstraintProvider::bedConflict)
                .given(firstUser, conflictingUser, nonConflictingUser)
                .penalizesBy(1);
    }

    @Test
    void maxSleepingConflictPreventsUserFromSleepingDuringInvalidTime() {
    	SleepShift shift = new SleepShift(0);
    	shift.sleepingDuringThisTime.add(new MaxSleepingList(Arrays.asList("u1", "u3"), 0));
    	User conflictingUser = new User("u1", 1, shift, BED1);
    	User nonConflictingUser = new User("u2", 1, shift, BED2);
        constraintVerifier.verifyThat(SleepShiftConstraintProvider::maxSleepingConflictInternal)
	        .given(conflictingUser, nonConflictingUser)
	        .penalizesBy(1);
    }    

    @Test
    void maxSleepingConflictDoesNotPreventUserFromSleepingGenerally() {
    	SleepShift shift = new SleepShift(0);
    	shift.sleepingDuringThisTime.add(new MaxSleepingList(Arrays.asList("u1", "u3"), 1));
    	User conflictingUser = new User("u1", 1, shift, BED1);
    	User nonConflictingUser = new User("u2", 1, shift, BED2);
        constraintVerifier.verifyThat(SleepShiftConstraintProvider::maxSleepingConflictInternal)
	        .given(conflictingUser, nonConflictingUser)
	        .penalizesBy(0);
    }
    
    @Test
    void maxSleepingConflictPreventsTwoUserFromSleeping() {
    	SleepShift shift = new SleepShift(0);
    	shift.sleepingDuringThisTime.add(new MaxSleepingList(Arrays.asList("u1", "u2"), 1));
    	User user = new User("u1", 1, shift, BED1);
    	User conflictingUser = new User("u2", 1, shift, BED1);
    	User nonConflictingUser = new User("u3", 1, shift, BED1);
        constraintVerifier.verifyThat(SleepShiftConstraintProvider::maxSleepingConflictInternal)
	        .given(user, conflictingUser, nonConflictingUser)
	        .penalizesBy(2);
    }

    @Test
    void maxSleepingConflictAllowsOneUserToSleep() {
    	SleepShift shift = new SleepShift(0);
    	shift.sleepingDuringThisTime.add(new MaxSleepingList(Arrays.asList("u1", "u2"), 1));
    	User user = new User("u1", 1, shift, BED1);
    	User conflictingUser = new User("u2", 1, new SleepShift(8), BED1);
    	User nonConflictingUser = new User("u3", 1, shift, BED1);
        constraintVerifier.verifyThat(SleepShiftConstraintProvider::maxSleepingConflictInternal)
	        .given(user, conflictingUser, nonConflictingUser)
	        .penalizesBy(0);
    }    

}
