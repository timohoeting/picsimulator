package picsimulator;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import javax.swing.JRadioButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;

public class frame_trisA extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame_trisA frame = new frame_trisA();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public frame_trisA() {
		setTitle("TRIS A");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 205, 60);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JRadioButton radioButton_1 = new JRadioButton("");
		radioButton_1.setBounds(6, 5, 24, 26);
		contentPane.add(radioButton_1);

		JRadioButton radioButton_4 = new JRadioButton("");
		radioButton_4.setBounds(30, 5, 24, 26);
		contentPane.add(radioButton_4);

		JRadioButton radioButton_5 = new JRadioButton("");
		radioButton_5.setBounds(54, 5, 24, 26);
		contentPane.add(radioButton_5);

		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("");
		rdbtnNewRadioButton_1.setBounds(78, 5, 24, 26);
		contentPane.add(rdbtnNewRadioButton_1);

		JRadioButton radioButton_3 = new JRadioButton("");
		radioButton_3.setBounds(102, 5, 24, 26);
		contentPane.add(radioButton_3);

		JRadioButton rdbtnNewRadioButton = new JRadioButton("");
		rdbtnNewRadioButton.setBounds(126, 5, 24, 26);
		contentPane.add(rdbtnNewRadioButton);

		JRadioButton radioButton_6 = new JRadioButton("");
		radioButton_6.setBounds(150, 5, 24, 26);
		contentPane.add(radioButton_6);

		JRadioButton radioButton_7 = new JRadioButton("");
		radioButton_7.setBounds(174, 5, 24, 26);
		contentPane.add(radioButton_7);
	}
}
