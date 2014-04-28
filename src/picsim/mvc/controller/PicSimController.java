package picsim.mvc.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import picsim.mvc.model.PicSimModel;
import picsim.mvc.view.PicSimCalculatorView;
import picsim.mvc.view.PicSimView;

public class PicSimController {
	private PicSimView view;
	private PicSimModel model;
	private boolean running;

	public PicSimController(PicSimView view, PicSimModel model) {
		this.view = view;
		this.model = model;
		this.running = false;
		model.reset_model();
		addListener();
		ReloadGUI();
		view.setTrisALabels(255);
		view.setPortALabels(255);
		view.setTrisBLabels(255);
		view.setPortBLabels(0);

	}

	private void addListener() {
		view.setNextStepListener(new NextStepListener());
		view.setStartProgramListener(new StartProgramListener());
		view.setPauseListener(new PauseListener());
		view.setSpeichernRegisterListener(new SpeichernRegisterListener());
		view.setOpenFileListener(new OpenFileListener());
		view.setSliderChangeListener(new SliderChangeListener());
		view.setOpenCalculatorListener(new OpenCalculatorListener());
		view.setChangePortABit0(new ChangePortABit0());
		view.setChangePortABit1(new ChangePortABit1());
		view.setChangePortABit2(new ChangePortABit2());
		view.setChangePortABit3(new ChangePortABit3());
		view.setChangePortABit4(new ChangePortABit4());
		view.setChangePortABit5(new ChangePortABit5());
		view.setChangePortABit6(new ChangePortABit6());
		view.setChangePortABit7(new ChangePortABit7());
		view.setChangePortBBit0(new ChangePortBBit0());
		view.setChangePortBBit1(new ChangePortBBit1());
		view.setChangePortBBit2(new ChangePortBBit2());
		view.setChangePortBBit3(new ChangePortBBit3());
		view.setChangePortBBit4(new ChangePortBBit4());
		view.setChangePortBBit5(new ChangePortBBit5());
		view.setChangePortBBit6(new ChangePortBBit6());
		view.setChangePortBBit7(new ChangePortBBit7());
	}

	public boolean get_running() {
		return running;
	}

	public void set_running(boolean s) {

		running = s;
	}

	public int get_Frequency() {
		return model.get_takt();
	}

	/* ARRAY F��R DAS REGISTER */

	public void start_programm(int takt) throws InterruptedException {
		/*
		 * ��berpr��fung ob Ende des Programms erreicht wird kann am Ende
		 * gel��scht werden !!
		 */// TODO l��schen
		if (model.getProgrammCounter() == model.code_list.size()) {
			Thread.sleep(takt);
			model.setProgramCounter(0);
			start_function();
		} else {
			Thread.sleep(takt);
			start_function();
		}
	}

	/*
	 * ######## F��hrt f��r jedes in der code_list enthaltene Elemente die
	 * Funktion what_to_do() aus #######
	 */
	public void start_function() throws InterruptedException {
		model.what_to_do(model.code_list.get(model.getProgrammCounter()));
		model.setProgramCounter(model.getProgrammCounter() + 1);
		ReloadGUI();
	}

	public void ReloadGUI() {
		/* Erweiterungen aktualisieren */
		ReloadElements();
		/* aktuell ausgef��hrten Code markieren */
		view.select_code(model.getProgrammCounter());
		/* W-Register in GUI setzen */
		view.set_W_value("1");
		// frame.lbl_wreg_value.setText(String.valueOf(w_register).toString());
		/* Aktualisieren der Tabelle mit den Werten aus Register_Array */
		ReloadTable();
		/* z ��berpr��fen ob true/false */
		if (model.get_Z() == 1) {

			view.set_Z_value(String.valueOf(model.get_Z()));
		} else {

			view.set_Z_value(String.valueOf(model.get_Z()));
		}

		/* PC setzen in Frame */
		view.set_PC_value(String.valueOf(model.getProgrammCounter()));

		/* C setzen in Frame */
		view.set_C_value(String.valueOf(model.get_C()));

		/* Status setzen */
		view.set_Status_value(String.valueOf(model.get_status()));

		/* DC setzen */
		view.set_DC_value(String.valueOf(model.get_DC()));

	}

	public void ReloadElements() {
		// TODO Elemente aktualisieren LEDs etc
	}

	public void ReloadTable() {
		int m1 = 0, n1 = 0, t1 = 0;
		while (m1 < 256) {
			while (t1 < 8) {
				/* Tabelle bekommt Werte aus Array zugewiesen */
				view.set_TableEntry(model.getRegisterEntry(m1), n1, t1);
				t1++;
				m1++;
			}
			t1 = 0;
			n1++;
		}
	}

	class NextStepListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			run_one_function();
		}
	}

	public void run_one_function() {

		Thread t1 = new Thread(new PicSimControllerThread_Once(this));
		t1.start();
	}

	private void start() {
		filter_code();
		int i;
		for (i = 0; i < view.getListModelSize(); i++) {
			model.analyze_code(view.getElementListModel(i));
		}
	}

	public void filter_code() {
		/* Programmcode nach relevanten Zeilen filtern */
		int i;
		for (i = 0; i < view.getListModelSize(); i++) {
			String temp = view.getElementListModel(i);
			if (temp.startsWith("     ")) {
				view.remove_ElementListModel(i);
				i--;
			}
		}
	}

	class StartProgramListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			run_all_functions();

		}
	}

	public void run_all_functions() {

		if (view.getListModelSize() > 0) {

			start();

			if (view.getListModelSize() != 0) {
				set_running(true);
				view.setVisibilityButtons(false, true, true);
				Thread t1 = new Thread(new PicSimControllerThread(this));

				t1.start();

			} else {
				view.set_ErrorMsgs("Kein Programm ge��ffnet.");
				set_running(false);
			}

		}
	}

	class PauseListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			set_running(false);

		}

	}

	class SpeichernRegisterListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			int i, m;
			for (i = 0; i <= 31; i++) {
				for (m = 0; m <= 7; m++) {
					int adress = i * 8 + m;

					String entry = view.get_TableEntry(i, m);
					int value;
					try {
						value = Integer.parseInt(entry);
						model.setRegisterEntry(adress, value);

					} catch (NumberFormatException e1) {

						e1.printStackTrace();
					}

				}
			}

		}
	}

	class SliderChangeListener implements ChangeListener {
		// TODO Slider
		@Override
		public void stateChanged(ChangeEvent e) {
			model.set_takt(view.get_Frequency());
			view.set_Frequency(model.get_takt());
		}

	}

	class OpenFileListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			loadFile();
		}

	}

	@SuppressWarnings("resource")
	private void loadFile() {

		running = false;
		view.clear_ListModel();
		model.reset_model();
		ReloadGUI();

		/* Auswählen der Datei */
		JFileChooser chooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Lst File", "lst");
		chooser.setFileFilter(filter);
		/* Was wurde angeklickt -> rueckgabewert */
		int rueckgabeWert = chooser.showOpenDialog(null);
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			try {

				/* Datei einlesen mit Buffered Reader */

				BufferedReader in;
				String zeile = null;
				model.setPath_of_programfile(chooser.getSelectedFile()
						.getAbsolutePath());
				in = new BufferedReader(new FileReader(
						model.getPath_of_programfile()));

				while ((zeile = in.readLine()) != null) {
					try {
						/* Zeile f��r Zeile wird in TextPane eingef��gt */

						view.addElementListModel(zeile);

					} catch (Exception e1) {
						System.out.println(e1);
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		if (view.getListModelSize() == 0) {
			view.set_ErrorMsgs("Kein Programm enthalten.");
		}

	}

	class OpenCalculatorListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			PicSimCalculatorView calc = new PicSimCalculatorView();
			calc.setVisible(true);

		}

	}

	class RegisterLadenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			register_load();

		}

	}

	@SuppressWarnings("resource")
	public void register_load() {
		/* Ausw��hlen der Datei */
		JFileChooser chooser = new JFileChooser();
		/* Was wurde angeklickt -> rueckgabewert */
		int rueckgabeWert = chooser.showOpenDialog(null);
		if (rueckgabeWert == JFileChooser.APPROVE_OPTION) {
			try {
				/* Datei einlesen mit Buffered Reader */
				BufferedReader in;
				String zeile = null;
				model.setPath_of_registerfile(chooser.getSelectedFile()
						.getAbsolutePath());
				in = new BufferedReader(new FileReader(
						model.getPath_of_registerfile()));
				zeile = in.readLine();
				String[] splitResult = zeile.split(";");

				int m, s = 0;
				for (m = 0; m < 256; m++) {
					{
						/* Array wird mit Werten aus Dokument gef��llt */
						model.setRegisterEntry(m,
								Integer.parseInt(splitResult[s]));

						s++;
					}
				}

				ReloadGUI();

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	class OpenWuerfelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangeTableEntryListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

		}

	}

	public void writeToRegister(int adress, int value) {
		model.setRegisterEntry(adress, value);
		ReloadGUI();

	}

	class ChangePortABit0 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(0);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit1 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(1);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit2 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(2);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit3 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(3);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit4 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(4);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit5 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(5);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit6 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(6);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortABit7 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortA(7);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit0 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(0);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit1 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(1);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit2 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(2);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit3 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(3);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit4 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(4);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit5 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(5);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class ChangePortBBit6 implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(6);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}
		
		
	
	 class ChangePortBBit7 implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			
			// TODO Auto-generated method stub
			changeTheRegisterFromPortB(7);
			System.out.println("klick!!");
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	 }
	
	 
	 private void changeTheRegisterFromPortA(int ID){
		 switch(ID){
		 case 0:{int temp = view.getValuePortA() & 0b00000001; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 1)); break;}
		 case 1:{int temp = view.getValuePortA() & 0b00000010; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 2)); break;}
		 case 2:{int temp = view.getValuePortA() & 0b00000100; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 4)); break;}
		 case 3:{int temp = view.getValuePortA() & 0b00001000; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 8)); break;}
		 case 4:{int temp = view.getValuePortA() & 0b00010000; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 16)); break;}
		 case 5:{int temp = view.getValuePortA() & 0b00100000; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 32)); break;}
		 case 6:{int temp = view.getValuePortA() & 0b01000000; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 64)); break;}
		 case 7:{int temp = view.getValuePortA() & 0b10000000; model.setRegisterEntry(5, (model.getRegisterEntry(5) + 128)); break;}
		 default:{break;}
		 }
	 }
	 
	 private void changeTheRegisterFromPortB(int ID){
		 switch(ID){
		 case 0:{int temp = view.getValuePortB() & 0b00000001; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 1)); break;}
		 case 1:{int temp = view.getValuePortB() & 0b00000010; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 2)); break;}
		 case 2:{int temp = view.getValuePortB() & 0b00000100; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 4)); break;}
		 case 3:{int temp = view.getValuePortB() & 0b00001000; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 8)); break;}
		 case 4:{int temp = view.getValuePortB() & 0b00010000; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 16)); break;}
		 case 5:{int temp = view.getValuePortB() & 0b00100000; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 32)); break;}
		 case 6:{int temp = view.getValuePortB() & 0b01000000; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 64)); break;}
		 case 7:{int temp = view.getValuePortB() & 0b10000000; model.setRegisterEntry(6, (model.getRegisterEntry(5) + 128)); break;}
		 default:{break;}
		 }
	 }
	 

}
