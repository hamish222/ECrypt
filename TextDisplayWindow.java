import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;



public class TextDisplayWindow extends JFrame{
	TextDisplayWindow me = this;
	//public ViewButtonListener buttonListener = new ViewButtonListener();
	public JTextArea text = new JTextArea(10,50);
	private JScrollPane pane = new JScrollPane(text);
	// create a menu bar and use it in this JFrame
	JMenuBar menuBar = new JMenuBar(  );
	static String filePath=null;

	// Constructor
	public TextDisplayWindow(String title, int yLoc){
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC

		setIconImage(MainWindow.ECryptIcon.getImage());

		setSize(580,300);
		setTitle(title);
		// create the File menu
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		// file reading and writing
		JMenuItem clearText = new JMenuItem("Clear");
		JMenuItem cleanText = new JMenuItem("Clean");
		JMenuItem openFile = new JMenuItem("Open");
		JMenuItem saveFile = new JMenuItem("Save");
		fileMenu.add(clearText);
		fileMenu.add(cleanText);
		fileMenu.addSeparator(  );	
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		clearText.addActionListener(new ClearListener());
		cleanText.addActionListener(new CleanListener());
		saveFile.addActionListener(new ActionListener(  ) 
		{
			public void actionPerformed(ActionEvent e) 
			{ 
				savefile(text.getText());
			}
		});
		openFile.addActionListener(new OpenFileListener());
		menuBar.add(fileMenu);
		menuBar.add(makeAnalysisMenu());
		setJMenuBar(menuBar);

		add(pane);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocation(200,yLoc);
		text.setFont(font);
		text.setForeground(Color.BLACK); // Colors: BLACK, BLUE, CYAN, DARK_GRAY, GRAY, GREEN, LIGHT_GRAY, MAGENTA, ORANGE, PINK, RED, WHITE, YELLOW
		text.setBackground(Color.LIGHT_GRAY);
		text.setLineWrap(true);
		setVisible(false);

	}

	public JMenu makeAnalysisMenu(){
		JMenu analyzeMenu = new JMenu("Analysis");
		JMenuItem highlightText = new JMenuItem("Highlight Text");
		JMenuItem monographFreq = new JMenuItem("Monograph Frequencies");
		JMenuItem polygraphFreq = new JMenuItem("Polygraph Frequencies");
		JMenuItem kasiskiTest = new JMenuItem("Kasiski Test");
		JMenuItem IoC = new JMenuItem("Index of Coincidence");
		JMenuItem Friedman = new JMenuItem("Friedman's Formula");

		analyzeMenu.add(highlightText); highlightText.addActionListener(new HighlightTextActionListener());
		analyzeMenu.addSeparator();
		analyzeMenu.add(monographFreq); monographFreq.addActionListener(new MonographActionListener());
		analyzeMenu.add(polygraphFreq); polygraphFreq.addActionListener(new PolygraphActionListener());
		analyzeMenu.add(kasiskiTest); kasiskiTest.addActionListener(new KasiskiActionListener());
		analyzeMenu.add(IoC); IoC.addActionListener(new IoCActionListener());
		analyzeMenu.add(Friedman); Friedman.addActionListener(new FriedmanActionListener());
		//analyzeText.addActionListener(new AnalyzeListener());
		return analyzeMenu;
	}

	public void changeTitle(String newTitle){
		setTitle(newTitle);
	}

	private class OpenFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{
			String in = readFile();
			if (in!="CANCEL") text.setText(in);
		}
	}

	/*
	public class ViewButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}
	 */

	private class ClearListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			text.setText("");
		}
	}

	private class CleanListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			text.setText(MainWindow.cleanText(text.getText()));
		}
	}

	public class HighlightTextActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + 572;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new HighlightText(getTitle(), text, xLoc, yLoc);
		}
	}

	public class MonographActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + 572;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new Monograph(getTitle(),text,xLoc,yLoc);
		}
	}

	public class PolygraphActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + 572;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new Polygraph(getTitle(),text,xLoc,yLoc);
		}
	}

	public class KasiskiActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + 572;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new Kasiski(getTitle(),text,xLoc,yLoc);
		}
	}

	public class IoCActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + 572;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new IoC(getTitle(),text,xLoc,yLoc);
		}
	}

	public class FriedmanActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int xLoc, yLoc;
			Point temp = getContentPane().getLocationOnScreen();
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + 572;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new Friedman(getTitle(),text,xLoc,yLoc);
		}
	}


	protected String readFile() 
	{
		final JFileChooser fc = new JFileChooser(filePath);
		final FileFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		String text="";

		fc.addChoosableFileFilter(filter);
		//		int returnVal = fc.showDialog(getParent(),"Save");
		int returnVal = fc.showOpenDialog(me);
		File file = fc.getSelectedFile();
		try {
			filePath = file.getPath();
		} catch (NullPointerException e){
			return "CANCEL";
		}

		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			try {
				BufferedReader reader = new BufferedReader(new FileReader(filePath));
				String line;
				while((line = reader.readLine())!= null)
				{
					text+=line;
					text+='\n';
				}
				reader.close();

			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error reading the selected file, " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
				//e1.printStackTrace();
			}
		}
		else
			if (returnVal != JFileChooser.CANCEL_OPTION)
				JOptionPane.showMessageDialog(null, "Error locating the selected file " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
		return text;
	}

	protected void savefile(String text) {
		final JFileChooser fc = new JFileChooser(filePath);
		final FileFilter filter = new FileNameExtensionFilter("Text Files", "txt");

		fc.addChoosableFileFilter(filter);
		//		int returnVal = fc.showSaveDialog(getParent());
		int returnVal = fc.showSaveDialog(me);
		File file = fc.getSelectedFile();
		try {
			filePath = file.getPath();
		} catch (NullPointerException e){
			return;
		}

		String ext;
		if (filePath.length()>3)
			ext = filePath.substring(filePath.length()-4,filePath.length());
		else
			ext="";
		if (!ext.equals(".txt")) filePath += ".txt";
		//System.out.println("Saving: "+filePath);
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
				writer.write(text);
				writer.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error writing to the selected file, " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
				//e1.printStackTrace();
			}
		}
		else
			if (returnVal != JFileChooser.CANCEL_OPTION)
				JOptionPane.showMessageDialog(null, "Error locating the selected file " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
	}

}
