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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;
import org.thon.sleepshiftsolver.constraints.MaxBedConstraint;
import org.thon.sleepshiftsolver.constraints.MaxSleepingConstraint;
import org.thon.sleepshiftsolver.domain.SleepShiftSchedule;
import org.thon.sleepshiftsolver.domain.User;

public class SleepShiftConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
    	ArrayList<Constraint> constraints = new ArrayList<>();
    	// Hard Constraints
    	constraints.add(bedConflict(constraintFactory));
    	constraints.add(maxBedConflictInternal(constraintFactory));
    	constraints.add(maxSleepingConflictInternal(constraintFactory));
    	constraints.add(shiftSeparationHardConstraint(constraintFactory));
    	
    	// Soft constraints
    	constraints.add(shiftSeparationSoftConstraint(constraintFactory));
    	
    	Constraint[] array = new Constraint[constraints.size()];
    	array = constraints.toArray(array);
        return array;
    }

    Constraint bedConflict(ConstraintFactory constraintFactory) {
    	// A bed can accommodate at most 1 user at a time
    	return constraintFactory.from(User.class)
    			.join(User.class,
    					// In the range of [shift, shift + 8]
					Joiners.overlapping(User::getSleepShiftStartTime, User::getSleepShiftEndTime),
					// In the same bed
					Joiners.equal(User::getBed),
					// form unique pairs
					Joiners.lessThan(User::getId)
				).penalize("Bed conflict", HardSoftScore.ONE_HARD);
    }
    

    Constraint maxSleepingConflict(ConstraintFactory constraintFactory, List<String> usernames, int startTime, int endTime, int maxAsleep) {
    	return constraintFactory.from(User.class)
    			.filter((user) -> usernames.contains(user.getUsername()) && user.getSleepShiftStartTime() >= startTime && user.getSleepShiftEndTime() - 1 <= endTime)
    			.join(User.class,
    					Joiners.overlapping(User::getSleepShiftStartTime, User::getSleepShiftEndTime)
					)
    			.filter((user1, user2) -> usernames.contains(user1.getUsername()) &&
    					usernames.contains(user2.getUsername()))
    			.groupBy((user1, user2) -> user1, ConstraintCollectors.countBi())
    			.filter((user, count) -> count > maxAsleep)
    			.penalize("max sleeping " + String.join(",", usernames) + " " + startTime + " " + endTime, HardSoftScore.ONE_HARD);
    }

    Constraint maxSleepingConflictInternal(ConstraintFactory constraintFactory) {
    	return constraintFactory.from(User.class)
    			.join(User.class,
    					Joiners.overlapping(User::getSleepShiftStartTime, User::getSleepShiftEndTime),
    					Joiners.filtering((user1, user2) -> user1.getSleepShift().bothUsersInMaxSleepingList(user1, user2))
					)
    			.groupBy((user1, user2) -> user1, ConstraintCollectors.countBi())
    			.filter((user, count) -> user.getSleepShift().isCountLargerThanLimitForMaxSleepingList(user, count))
    			.penalize("Too many specific captains sleeping", HardSoftScore.ONE_HARD);
    }


    Constraint maxBedConflictInternal(ConstraintFactory constraintFactory) {
    	return constraintFactory.from(User.class)
    			.filter((user) -> !user.getBed().canBeUsedAt(user.getSleepShiftStartTime()))
    			.penalize("Max beds used", HardSoftScore.ONE_HARD);
    }
    
    /**
     * The 2 shifts for a single captain must be at least 12 hours apart.
     * @param constraintFactory
     * @return
     */
    Constraint shiftSeparationHardConstraint(ConstraintFactory constraintFactory) {
    	int minDistance = 2 * 12; // 12 hours
    	return constraintFactory.from(User.class)
    			.join(User.class,
					Joiners.equal(User::getUsername)
				).filter((user1, user2) -> user1.getShiftNumber() != user2.getShiftNumber() &&
						!(
							user1.getSleepShiftStartTime() <= user2.getSleepShiftStartTime() - minDistance ||
							user1.getSleepShiftStartTime() >= user2.getSleepShiftStartTime() + minDistance
						))
    			.penalize("12 hour between shifts min", HardSoftScore.ONE_HARD);
    }

    /**
     * The 2 shifts for a single captain should be at least 20 hours apart.
     * @param constraintFactory
     * @return
     */
    Constraint shiftSeparationSoftConstraint(ConstraintFactory constraintFactory) {
    	int minDistance = 2 * 20; // 20 hours
    	return constraintFactory.from(User.class)
    			.join(User.class,
					Joiners.equal(User::getUsername)
				).filter((user1, user2) -> user1.getShiftNumber() != user2.getShiftNumber() &&
						!(
							user1.getSleepShiftStartTime() <= user2.getSleepShiftStartTime() - minDistance ||
							user1.getSleepShiftStartTime() >= user2.getSleepShiftStartTime() + minDistance
						))
    			.penalize("20 hour between shifts preferred", HardSoftScore.ONE_SOFT);
    }
    
//    Constraint preferOvernight(ConstraintFactory constraintFactory) {
//    	return constraintFactory.from(User.class)
//    			.filter((user) -> user.getSleepShiftStartTime() > 8 and user.getSl)
//    }

}
