package org.thon.sleepshiftsolver.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.thon.sleepshiftsolver.Constants;
import org.thon.sleepshiftsolver.constraints.BedCannotBeUsedRange;
import org.thon.sleepshiftsolver.constraints.MaxBedConstraint;
import org.thon.sleepshiftsolver.constraints.MaxSleepingConstraint;
import org.thon.sleepshiftsolver.constraints.MaxSleepingList;
import org.thon.sleepshiftsolver.constraints.StaticShift;
import org.thon.sleepshiftsolver.domain.Bed;
import org.thon.sleepshiftsolver.domain.SleepShift;
import org.thon.sleepshiftsolver.domain.SleepShiftSchedule;
import org.thon.sleepshiftsolver.domain.User;

public class ZipCsvFileIo implements SolutionFileIO<SleepShiftSchedule> {

	@Override
	public String getInputFileExtension() {
		return "zip";
	}
	
	@Override
	public String getOutputFileExtension() {
		return "csv";
	}

	@Override
	public SleepShiftSchedule read(File inputSolutionFile) {
		List<SleepShift> sleepShifts = new ArrayList<>();
		List<Bed> bedList = new ArrayList<>();
		List<User> userList = new ArrayList<>();
		List<MaxBedConstraint> maxBedConstraints = new ArrayList<>();
		List<MaxSleepingConstraint> maxSleepingConstraints = new ArrayList<>();
		List<StaticShift> staticShifts = new ArrayList<>();

		try {
			ZipFile file = new ZipFile(inputSolutionFile);
			Enumeration<? extends ZipEntry> entries = file.entries();

			// Schedule.csv
			ZipEntry scheduleCsv = file.getEntry("schedule.csv");
			BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(scheduleCsv)));
			while (reader.ready()) {
				String[] line = reader.readLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				if (line.length == 4) {					
					maxBedConstraints.add(new MaxBedConstraint(
							Integer.parseInt(line[2]),
							Integer.parseInt(line[0]),
							Integer.parseInt(line[1]),
							line[3].replace("\"", "").split(",")));
				} else {	
					maxBedConstraints.add(new MaxBedConstraint(
							Integer.parseInt(line[2]),
							Integer.parseInt(line[0]),
							Integer.parseInt(line[1])));
				}
			}
			reader.close();

			// Constraints.csv
			ZipEntry constraintsCsv = file.getEntry("constraints.csv");
			reader = new BufferedReader(new InputStreamReader(file.getInputStream(constraintsCsv)));
			while (reader.ready()) {
				String[] line = reader.readLine().split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
				maxSleepingConstraints.add(new MaxSleepingConstraint(
						line[0].replace("\"", "").split(","),
						Integer.parseInt(line[2]),
						Integer.parseInt(line[3]),
						Integer.parseInt(line[1])));
			}
			reader.close();

			// Captains.csv
			ZipEntry captainsCsv = file.getEntry("captains.csv");
			reader = new BufferedReader(new InputStreamReader(file.getInputStream(captainsCsv)));
			while (reader.ready()) {
				String rawLine = reader.readLine();
				if (rawLine.charAt(0) != '#') {  // allow comments
					String[] line = rawLine.split(",");
					User shift1 = new User(line[0], 1);
					shift1.name = line[1];
					shift1.committee = line[2];
					User shift2 = new User(line[0], 2);
					shift2.name = line[1];
					shift2.committee = line[2];
					userList.add(shift1);
					userList.add(shift2);
				}
			}
			reader.close();

			// static_shifts.csv
			ZipEntry staticshiftsCsv = file.getEntry("static_shifts.csv");
			reader = new BufferedReader(new InputStreamReader(file.getInputStream(staticshiftsCsv)));
			while (reader.ready()) {
				String[] line = reader.readLine().split(",");
				if (line.length == 3 && line[2].length() > 0) {
					staticShifts.add(new StaticShift(
							line[0],
							Integer.parseInt(line[1]), line[2]));
				} else {					
					staticShifts.add(new StaticShift(
							line[0],
							Integer.parseInt(line[1])));
				}
			}
			reader.close();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// Process beds
		int maxBeds = 0;
		int endTime = 0;
		String[] maxBedNames = null;
		
		for (MaxBedConstraint constraint : maxBedConstraints) {
			if (constraint.maxBeds > maxBeds) {
				maxBeds = constraint.maxBeds;
				maxBedNames = constraint.names;
			}
			if (constraint.endTime > endTime) {
				endTime = constraint.endTime;
			}
		}

		for (int i = 0; i < maxBeds; i++) {
			if (maxBedNames != null && maxBedNames.length > i) {				
				bedList.add(new Bed(i, maxBedNames[i]));
			} else {	
				bedList.add(new Bed(i));
			}
		}
		
		
		// Sleep shifts
		for (int i = 0; i <= endTime; i++) {
			SleepShift newShift = new SleepShift(i);
			// Can't start sleeping at the last 7 segments
			if (endTime - i < Constants.SHIFT_LENGTH - 1) {
				System.out.println("Can't sleep at " + newShift.toDateTimeString());
				newShift.canStartSleepingAtThisShift = false;
			}
			sleepShifts.add(newShift);
		}
		
		// Static Shifts
		// Process shifts with beds first!
		staticShifts.sort(new Comparator<StaticShift>() {
			@Override
			public int compare(StaticShift o1, StaticShift o2) {
				if (o1.bed != null && o2.bed == null) {
					return -1;
				} else if (o1.bed == null && o2.bed != null) {
					return 1;
				}
				return 0; //o1.startTime - o2.startTime;
			}
		});
		for (StaticShift staticShift : staticShifts) {
			for (User u : userList) {
				if (u.getUsername().equals(staticShift.username) && u.getSleepShift() == null) {
					System.out.println("Loading static shift for " + u.name);
					u.setSleepShift(sleepShifts.get(staticShift.startTime));
					u.setIsStaticShift(true);
					
					if (staticShift.bed != null) {
						for (Bed bed : bedList) {
							if (bed.getName().equals(staticShift.bed)) {
								u.setBed(bed);
								break;
							}
						}
					} else {						
						// Find free bed for this shift
						u.setBed(Bed.findFreeBed(bedList, userList, staticShift.startTime));
						if (u.getBed() == null) {
							System.out.println("Warning: No free bed found for " + u.getUsername() + " at " + staticShift.startTime);
						}
					}
					break;
				}
			}
		}
		
		
		SleepShiftSchedule result = new SleepShiftSchedule(sleepShifts, bedList, userList);
//		result.maxBedConstraints = MaxBedConstraint.processStaticShifts(maxBedConstraints, staticShifts);
		result.maxBedConstraints = MaxBedConstraint.clean(maxBedConstraints);
		for (MaxBedConstraint constraint : result.maxBedConstraints) {
			System.out.println(">=" + constraint.maxBeds + " cannot be used [" + constraint.startTime + ", " + constraint.endTime + "]");
			for (int i = constraint.maxBeds; i < maxBeds; i++) {
				result.getBedList().get(i).cannotBeUsedDuring.add(new BedCannotBeUsedRange(constraint.startTime, constraint.endTime));
			}
		}
		for (MaxSleepingConstraint constraint : maxSleepingConstraints) {
			for (int i = Math.max(constraint.startTime - Constants.SHIFT_LENGTH + 1, 0); i <= constraint.endTime; i++) {
				SleepShift shiftAtTime = result.getSleepShiftAt(i);
				if (shiftAtTime != null) {					
					shiftAtTime.sleepingDuringThisTime.add(new MaxSleepingList(Arrays.asList(constraint.usernames),
							constraint.maxAsleep));
				}
			}
		}
//		result.prettyPrint();
		return result;
	}

	@Override
	public void write(SleepShiftSchedule solution, File outputSolutionFile) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee HH:mm");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputSolutionFile));
			for (User u : solution.getUserList()) {
				LocalDateTime shiftStart = Constants.START_TIME.plus(Duration.of(30*u.getSleepShiftStartTime(), ChronoUnit.MINUTES));
				writer.write(u.getUsername());
				writer.write(",");
				writer.write(formatter.format(shiftStart).toUpperCase());
				writer.write(",");
				writer.write(String.valueOf(u.getBed().getName()));
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
