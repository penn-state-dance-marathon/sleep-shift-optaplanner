package org.thon.sleepshiftsolver;

import javax.swing.JFrame;

import org.optaplanner.examples.common.app.CommonApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SleepShiftSolverApp extends JFrame {

	public SleepShiftSolverApp() {
		setTitle("Sleep Shift Solver");
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JButton btnLaunchSolver = new JButton("Launch Solver");
		btnLaunchSolver.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		getContentPane().add(btnLaunchSolver, BorderLayout.SOUTH);
	}

    private static final Logger LOGGER = LoggerFactory.getLogger(SleepShiftSolverApp.class);

    public static void main(String[] args) {
        CommonApp.prepareSwingEnvironment();
        SleepShiftSolverApp app = new SleepShiftSolverApp();
        app.pack();
        app.setLocationRelativeTo(null);
        app.setVisible(true);
//        SolverFactory<SleepShiftSchedule> solverFactory = SolverFactory.create(new SolverConfig()
//                .withSolutionClass(SleepShiftSchedule.class)
//                .withEntityClasses(User.class)
//                .withConstraintProviderClass(SleepShiftConstraintProvider.class)
//                // The solver runs only for 5 seconds on this small dataset.
//                // It's recommended to run for at least 5 minutes ("5m") otherwise.
//                .withTerminationSpentLimit(Duration.ofSeconds(5)));
//
//        // Load the problem
//        SleepShiftSchedule problem = generateDemoData();
//
//        // Solve the problem
//        Solver<SleepShiftSchedule> solver = solverFactory.buildSolver();
//        SleepShiftSchedule solution = solver.solve(problem);
//
//        // Visualize the solution
//        printTimetable(solution);
    }
}
