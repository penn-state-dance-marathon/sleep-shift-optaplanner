package org.thon.sleepshiftsolver;

import java.util.HashMap;
import java.awt.Color;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Constants {
	
	// This should only be used to convert an int index into "FRI 12:00" etc
	// So it *shouldn't* need to change from year to year
	public static final LocalDateTime START_TIME = LocalDateTime.of(2022, 2, 18, 19, 0);
	
	public static final int SHIFT_LENGTH = 8;
	
	public static HashMap<String, Color> COLOR_DICT = new HashMap<>();
	public static HashMap<String, Color> FONT_DICT = new HashMap<>();
	
	public static void fillColorMap() {
		COLOR_DICT.put("*Executive Committee", Color.decode("#31869b"));
		FONT_DICT.put("*Executive Committee", Color.YELLOW);
		COLOR_DICT.put("THON Communications", Color.decode("#4cbb17"));
		FONT_DICT.put("THON Communications", Color.WHITE);
		COLOR_DICT.put("THON Dancer Relations", Color.decode("#ffff00"));
		FONT_DICT.put("THON Dancer Relations", Color.BLACK);
		COLOR_DICT.put("THON Donor & Alumni Relations Alumni Engagement", Color.decode("#0046d2"));
		FONT_DICT.put("THON Donor & Alumni Relations Alumni Engagement", Color.WHITE);
		COLOR_DICT.put("THON Donor & Alumni Relations Development", Color.decode("#0046d2"));
		FONT_DICT.put("THON Donor & Alumni Relations Development", Color.WHITE);
		COLOR_DICT.put("THON Entertainment", Color.decode("#000000"));
		FONT_DICT.put("THON Entertainment", Color.WHITE);
		COLOR_DICT.put("THON Family Relations", Color.decode("#7cb9e8"));
		FONT_DICT.put("THON Family Relations", Color.WHITE);
		COLOR_DICT.put("THON Finance", Color.decode("#228b22"));
		FONT_DICT.put("THON Finance", Color.WHITE);
		COLOR_DICT.put("THON Hospitality", Color.decode("#e0285c"));
		FONT_DICT.put("THON Hospitality", Color.WHITE);
		COLOR_DICT.put("THON Merchandise", Color.decode("#e483b6"));
		FONT_DICT.put("THON Merchandise", Color.BLACK);
		COLOR_DICT.put("THON OPPerations", Color.decode("#000066"));
		FONT_DICT.put("THON OPPerations", Color.WHITE);
		COLOR_DICT.put("THON Public Relations", Color.decode("#6a0dad"));
		FONT_DICT.put("THON Public Relations", Color.WHITE);
		COLOR_DICT.put("THON R&R Event Safety", Color.decode("#ff0000"));
		FONT_DICT.put("THON R&R Event Safety", Color.WHITE);
		COLOR_DICT.put("THON R&R Fundraising Safety", Color.decode("#ff0000"));
		FONT_DICT.put("THON R&R Fundraising Safety", Color.WHITE);
		COLOR_DICT.put("THON Special Events", Color.decode("#920000"));
		FONT_DICT.put("THON Special Events", Color.WHITE);
		COLOR_DICT.put("THON Supply Logistics", Color.decode("#00e600"));
		FONT_DICT.put("THON Supply Logistics", Color.BLACK);
		COLOR_DICT.put("THON Technology", Color.decode("#ccff00"));
		FONT_DICT.put("THON Technology", Color.BLACK);
	}

	public static String convertTimeToPrettyPrint(int time) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee HH:mm");
		LocalDateTime shiftStart = Constants.START_TIME.plus(Duration.of(30*time, ChronoUnit.MINUTES));
		return formatter.format(shiftStart).toUpperCase();
	}
}
