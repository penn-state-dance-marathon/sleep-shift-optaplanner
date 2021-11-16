package org.thon.sleepshiftsolver;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import org.optaplanner.examples.common.app.CommonApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thon.sleepshiftsolver.ui.SleepShiftCommonApp;

import javax.swing.JButton;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
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
//        CommonApp.prepareSwingEnvironment();
        SleepShiftSolverApp app = new SleepShiftSolverApp();
//        app.pack();
        app.setSize(300, 110);
        app.setUndecorated(true);
        app.setLocationRelativeTo(null);
        app.setVisible(true);
        
        SleepShiftCommonApp.main(args);
        app.setVisible(false);
    }
}
