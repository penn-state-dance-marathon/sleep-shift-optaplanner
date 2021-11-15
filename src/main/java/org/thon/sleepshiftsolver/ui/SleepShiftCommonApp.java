package org.thon.sleepshiftsolver.ui;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.thon.sleepshiftsolver.domain.SleepShiftSchedule;
import org.thon.sleepshiftsolver.io.ZipCsvFileIo;

public class SleepShiftCommonApp extends CommonApp<SleepShiftSchedule> {


    public static final String SOLVER_CONFIG = "org/thon/sleepshiftsolver/solverConfig.xml";
    public static final String DATA_DIR_NAME = "sleepshiftsolver";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new SleepShiftCommonApp().init();
    }

	protected SleepShiftCommonApp() {
		super("Sleep Shift Scheduler", "Schedule Sleep Shifts", SOLVER_CONFIG, DATA_DIR_NAME, SleepShiftPanel.LOGO_PATH);
	}
	
	@Override
	protected SolutionPanel<SleepShiftSchedule> createSolutionPanel() {
		return new SleepShiftPanel();
	}

	@Override
	public SolutionFileIO<SleepShiftSchedule> createSolutionFileIO() {
		return new ZipCsvFileIo();
	}

}
