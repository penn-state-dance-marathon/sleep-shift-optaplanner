
package org.thon.sleepshiftsolver.domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.thon.sleepshiftsolver.Constants;
import org.thon.sleepshiftsolver.constraints.MaxSleepingList;

public class SleepShift {

    private int startTime;
    public List<MaxSleepingList> sleepingDuringThisTime = new ArrayList<>();

    public boolean canStartSleepingAtThisShift = true;
    
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
	
	public String toDateTimeString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee HH:mm");
		LocalDateTime shiftStart = Constants.START_TIME.plus(Duration.of(30*startTime, ChronoUnit.MINUTES));
		return formatter.format(shiftStart).toUpperCase();
	}
	
	public boolean bothUsersInMaxSleepingList(User user1, User user2) {
		for (MaxSleepingList list : sleepingDuringThisTime) {
			if (list.usernames.contains(user1.getUsername()) && list.usernames.contains(user2.getUsername())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isCountLargerThanLimitForMaxSleepingList(User user, int count) {
		for (MaxSleepingList list : sleepingDuringThisTime) {
			// THIS IS BROKEN because the user could be in more than one list
			if (list.usernames.contains(user.getUsername()) && count > list.maxAsleep) {
				return true;
			}
		}
		return false;
	}

	// This is O(n^2) lol
	// Don't use this every iteration, but it's meant to only be used at the static shift assignment
	public List<Bed> getBedsAvailableInThisShift(List<Bed> allBeds, List<User> userList) {
		List<User> onlyUsersThatHaveABedAssigned = userList.stream().filter(user -> user.getBed() != null).collect(Collectors.toList());
		return allBeds.stream().filter(bed -> {
			return bed.canBeUsedAt(this.startTime) && !bed.usedByAnyUser(onlyUsersThatHaveABedAssigned, this.startTime);
		}).collect(Collectors.toList());
	}

	public static void assignBedsBasedOnTimeslots(List<Bed> allBeds, List<User> allUsers, List<SleepShift> shifts) {
		List<User> usersWithStaticShifts = allUsers.stream().filter(user -> user.getSleepShift() != null).collect(Collectors.toList());
		shifts.forEach(shift -> {
			// Iterate in order of shifts.
			List<User> usersWhoSleepInThisShift = usersWithStaticShifts.stream().filter(user -> user.getSleepShift().getStartTime() == shift.getStartTime() && user.getBed() == null).collect(Collectors.toList());
			// Ensure there is a user in this shift who doesn't have a bed, otherwise move on.
			if (usersWhoSleepInThisShift.size() > 0) {
				List<Bed> availableBeds = shift.getBedsAvailableInThisShift(allBeds, usersWithStaticShifts); // Only users with static shifts could possibly have beds assigned
				// If there are not enough beds available, crash
				if (availableBeds.size() < usersWhoSleepInThisShift.size()) {
					JOptionPane.showMessageDialog(
						null, 
						"Invalid static shifts. Not enough beds available at " + shift.startTime + " (" + Constants.convertTimeToPrettyPrint(shift.startTime) + ")",
						"This solution will probably fail!",
						JOptionPane.ERROR_MESSAGE);
				} else {
					usersWhoSleepInThisShift.forEach(user -> user.setBed(availableBeds.remove(0)));
				}
			}
		});
	}
}
