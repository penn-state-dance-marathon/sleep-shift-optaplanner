package org.thon.sleepshiftsolver;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thon.sleepshiftsolver.ui.SleepShiftCommonApp;

import java.io.File;
import java.awt.Color;
import javax.swing.SwingConstants;

public class SleepShiftSolverApp extends JFrame {

	public SleepShiftSolverApp() {
		getContentPane().setBackground(Color.WHITE);
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("Sleep Shift Solver");
		ImageIcon icon = new ImageIcon("thon.png");
		getContentPane().setLayout(null);
		JLabel label = new JLabel(new ImageIcon(SleepShiftSolverApp.class.getResource("/org/thon/sleepshiftsolver/thon.png")));
		label.setBounds(0, 0, 302, 83);
		getContentPane().add(label);
		
		JLabel lblNewLabel = new JLabel("Loading Sleep Shift Solver...");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 89, 292, 14);
		getContentPane().add(lblNewLabel);
	}

    private static final Logger LOGGER = LoggerFactory.getLogger(SleepShiftSolverApp.class);

    public static void main(String[] args) {
        // https://stackoverflow.com/questions/50237516/proper-fix-for-java-10-complaining-about-illegal-reflection-access-by-jaxb-impl
    	System.setProperty("com.sun.xml.bind.v2.bytecode.ClassTailor.noOptimize", "true");
//        CommonApp.prepareSwingEnvironment();
        SleepShiftSolverApp app = new SleepShiftSolverApp();
//        app.pack();
        app.setSize(300, 110);
        app.setUndecorated(true);
        app.setLocationRelativeTo(null);
        app.setVisible(true);
        
        // Choose data directory
        File inDirectory = null;
        if (args.length > 0) {
            inDirectory = new File(args[0]);
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Choose a directory for storing sleep shift data");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);
            if (chooser.showOpenDialog(app) == JFileChooser.APPROVE_OPTION) {
                inDirectory = chooser.getSelectedFile();
            } else {
                return;
            }
        }
        System.setProperty("org.optaplanner.examples.dataDir", inDirectory.getAbsolutePath());
        new File(inDirectory, "sleepshiftsolver").mkdir();
        new File(inDirectory, "sleepshiftsolver/unsolved").mkdir();
        new File(inDirectory, "sleepshiftsolver/solved").mkdir();
        new File(inDirectory, "sleepshiftsolver/import").mkdir();
        SleepShiftCommonApp.main(args);
        app.setVisible(false);
//        LOGGER.info(String.join(", ", dataDir.list()));

    }
}
