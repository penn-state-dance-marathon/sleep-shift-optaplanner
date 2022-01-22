package org.thon.sleepshiftsolver.ui;

import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderColumnKey.HEADER_COLUMN_GROUP1;
import static org.optaplanner.examples.common.swingui.timetable.TimeTablePanel.HeaderRowKey.HEADER_ROW;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.solver.DefaultSolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.timetable.TimeTablePanel;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;
import org.thon.sleepshiftsolver.Constants;
import org.thon.sleepshiftsolver.constraints.BedCannotBeUsedRange;
import org.thon.sleepshiftsolver.domain.Bed;
import org.thon.sleepshiftsolver.domain.SleepShift;
import org.thon.sleepshiftsolver.domain.SleepShiftSchedule;
import org.thon.sleepshiftsolver.domain.User;

/**
 * Inspired by org.optaplanner.examples.common.swingui.SolutionPanel
 * 
 * @author ngearhart
 *
 */
public class SleepShiftPanel extends SolutionPanel<SleepShiftSchedule> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5145094752005357266L;
	public static final String LOGO_PATH = "/org/thon/sleepshiftsolver/dash.png";

    private TimeTablePanel<SleepShift, Bed> timeTablePanel;
    
    public SleepShiftPanel() {
    	Constants.fillColorMap();
        setLayout(new BorderLayout());
        timeTablePanel = new TimeTablePanel<>();
        add(timeTablePanel, BorderLayout.CENTER);
    }
    
	@Override
	public void resetPanel(SleepShiftSchedule solution) {
        timeTablePanel.reset();
        defineGrid(solution);
        fillCells(solution);
        repaint(); // Hack to force a repaint of TimeTableLayout during "refresh screen while solving"
	}


    private void defineGrid(SleepShiftSchedule solution) {
        JButton footprint = SwingUtils.makeSmallButton(new JButton("Patient9999"));
        int footprintWidth = footprint.getPreferredSize().width;
        timeTablePanel.defineColumnHeaderByKey(HEADER_COLUMN); // Shift Header
        for (SleepShift shift : solution.getSleepshiftList()) {
            timeTablePanel.defineColumnHeader(shift, footprintWidth);
        }
        timeTablePanel.defineRowHeaderByKey(HEADER_ROW); // Night header
        timeTablePanel.defineRowHeader(null); // Unassigned bed
        for (Bed bed : solution.getBedList()) {
            timeTablePanel.defineRowHeader(bed);
        }
    }

    private void fillCells(SleepShiftSchedule solution) {
        timeTablePanel.addCornerHeader(HEADER_COLUMN, HEADER_ROW, createHeaderPanel(new JLabel("Bed")));
        fillShiftCells(solution);
        fillBedCells(solution);
        fillBedDesignationCells(solution);
    }

    private JPanel createHeaderPanel(JLabel label) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(label, BorderLayout.NORTH);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TangoColorFactory.ALUMINIUM_5),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        return headerPanel;
    }

    private void fillShiftCells(SleepShiftSchedule solution) {
    	int maxTime = 0;
        for (SleepShift shift : solution.getSleepshiftList()) {
            timeTablePanel.addColumnHeader(shift, HEADER_ROW,
                    createHeaderPanel(new JLabel(shift.toDateTimeString(), SwingConstants.CENTER)));
            if (shift.getStartTime() > maxTime) {
            	maxTime = shift.getStartTime();
            }
        }
    }

    private void fillBedCells(SleepShiftSchedule solution) {
        timeTablePanel.addRowHeader(HEADER_COLUMN, null, HEADER_COLUMN, null,
                createHeaderPanel(new JLabel("Unassigned")));
        for (Bed bed : solution.getBedList()) {
            timeTablePanel.addRowHeader(HEADER_COLUMN, bed,
                    createHeaderPanel(new JLabel(bed.getName(), SwingConstants.RIGHT)));
        }
    }


    private void fillBedDesignationCells(SleepShiftSchedule solution) {
        for (User user : solution.getUserList()) {
        	if (user.getSleepShift() != null && user.getBed() != null) {
        		JButton button = SwingUtils.makeSmallButton(new JButton(new UserAction(user)));
        		button.setBackground(Constants.COLOR_DICT.get(user.committee));
        		button.setForeground(Constants.FONT_DICT.get(user.committee));
        		timeTablePanel.addCell(user.getSleepShift(), user.getBed(),
        				solution.getLastSleepShiftFrom(user.getSleepShift()), user.getBed(), button);
        	}
        }

        // Add blocks when beds cannot be used
        for (Bed bed : solution.getBedList()) {
            for (BedCannotBeUsedRange invalidRange : bed.cannotBeUsedDuring) {
        		JButton button = SwingUtils.makeSmallButton(new JButton("UNAVAILABLE"));
        		button.setBackground(Color.DARK_GRAY);
        		button.setForeground(Color.WHITE);
        		timeTablePanel.addCell(solution.getSleepShiftAt(invalidRange.startTime), bed,
                    solution.getSleepShiftAt(invalidRange.endTime), bed, button);
            }
        }
    }
    

    private class UserAction extends AbstractAction {

        private final User user;

        public UserAction(User user) {
            super(user.name + " (" + user.getUsername() + ")");
            this.user = user;
            putValue(SHORT_DESCRIPTION, "<html>User: " + user.getId() + "<br/>"
                    + "Bed: " + user.getBed() + "<br/>"
                    + "</html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
//            JPanel listFieldsPanel = new JPanel(new GridLayout(2, 1));
//            List<Bed> bedList = getSolution().getBedList();
//            // Add 1 to array size to add null, which makes the entity unassigned
//            JComboBox bedListField = new JComboBox(
//                    bedList.toArray(new Object[bedList.size() + 1]));
//            LabeledComboBoxRenderer.applyToComboBox(bedListField);
//            bedListField.setSelectedItem(bedDesignation.getBed());
//            listFieldsPanel.add(bedListField);
//            int result = JOptionPane.showConfirmDialog(PatientAdmissionSchedulePanel.this.getRootPane(),
//                    listFieldsPanel, "Select bed for " + bedDesignation.getAdmissionPart().getPatient().getName(),
//                    JOptionPane.OK_CANCEL_OPTION);
//            if (result == JOptionPane.OK_OPTION) {
//                Bed toBed = (Bed) bedListField.getSelectedItem();
//                solutionBusiness.doChangeMove(bedDesignation, "bed", toBed);
//                solverAndPersistenceFrame.resetScreen();
//            }
        }

    }


}