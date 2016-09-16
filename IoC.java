import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class IoC extends JFrame{
	int modulus, numRows;
	String alphaText;
	JTextArea text = new JTextArea();
	JScrollPane textPane = new JScrollPane(text);
	JPanel decimationPanel = new JPanel();
	JLabel decimationLabel = new JLabel("Number of Rows: ");
	String[] decimationList = {"5","10","15","20","30","40","50"};
	JComboBox decimationBox = new JComboBox(decimationList);
	JButton CalculateButton = new JButton("Recompute");
	JPanel buttonPanel = new JPanel();

	String output;
	JTextArea str;
	int[] freqs;
	int[] data;
	
	//Constructor for image/sound data
	public IoC(String title, int[] data2, int xLoc, int yLoc){
		data = data2;
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC\
		text.setFont(font);
		setTitle(title+" Index of Coincidence Table");
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		setSize(620,340);
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		decimationPanel.add(decimationLabel);
		decimationPanel.add(decimationBox);
		add(decimationPanel,BorderLayout.NORTH);
		decimationBox.setSelectedIndex(1);
		numRows = 10;
		decimationBox.addActionListener(new NumRowsListener());
		add(textPane,BorderLayout.CENTER);
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateButton.addActionListener(new CalculateButtonListener());
		buttonPanel.add(CalculateButton);

		setVisible(true);
		makeTable2();
	}

	//Constructor for text data
	public IoC(String title, JTextArea str, int xLoc, int yLoc){
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC\
		text.setFont(font);
		setTitle(title+" Index of Coincidence Table");
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		setSize(620,340);
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		decimationPanel.add(decimationLabel);
		decimationPanel.add(decimationBox);
		add(decimationPanel,BorderLayout.NORTH);
		decimationBox.setSelectedIndex(1);
		numRows = 10;
		decimationBox.addActionListener(new NumRowsListener());
		add(textPane,BorderLayout.CENTER);
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateButton.addActionListener(new CalculateButtonListener());
		buttonPanel.add(CalculateButton);

		setVisible(true);

		this.str = str;
		makeTable();
		/*		output = "The index of coincidence is " + myRound(computeIndex(str)) +".\n\n";
		for (d=1; d<=numRows; d++)
		{
			if (d<10)
				output = output + " " + d + "  ";
			else
				output = output + d + "  ";
			for (i=0; i<d; i++)
			{
				output = output + myRound(computeIndex(decimateString(str, d, i))) + "  ";
			}
			output = output + "\n";
		}
		text.setText(output);
		 */
	}
	
	private void makeTable2(){
		int i, d;
		
		output = "The index of coincidence is " + myRound(computeIndex(data)) +".\n\n";		
		for (d=1; d<=numRows; d++)
		{
			if (d<10)
				output = output + " " + d + "  ";
			else
				output = output + d + "  ";
			for (i=0; i<d; i++)
			{
				output = output + myRound(computeIndex(decimateData(data, d, i))) + "  ";
			}
			output = output + "\n";
		}
		text.setText(output);
		text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
	}
	
/*	private void makeTable(int[] data){
		int i, d;
		
		output = "The index of coincidence is " + myRound(computeIndex(data)) +".\n\n";		
		for (d=1; d<=numRows; d++)
		{
			if (d<10)
				output = output + " " + d + "  ";
			else
				output = output + d + "  ";
			for (i=0; i<d; i++)
			{
				output = output + myRound(computeIndex(decimateData(data, d, i))) + "  ";
			}
			output = output + "\n";
		}
		text.setText(output);
		text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
	} */

	private void makeTable(){
		int i, d;
		String data = str.getText();

		output = "The index of coincidence is " + myRound(computeIndex(data)) +".\n\n";
		for (d=1; d<=numRows; d++)
		{
			if (d<10)
				output = output + " " + d + "  ";
			else
				output = output + d + "  ";
			for (i=0; i<d; i++)
			{
				output = output + myRound(computeIndex(decimateString(data, d, i))) + "  ";
			}
			output = output + "\n";
		}
		text.setText(output);
		text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
	}

	private int[] decimateData(int[] data, int d, int offset){
		// We have to get the length of the array exactly right for computeIndex().
		int datalength=data.length/d;
		if (offset < data.length%d) datalength++;
		int[] out = new int[datalength];
		int j=0;
		for (int i=offset; i<data.length; i=i+d)
		{
			out[j]= data[i];
			j++;
		}
		return out;
	}
	
	private String decimateString(String data, int d, int offset){
		String out="";
		for (int i=offset; i<data.length(); i=i+d)
		{
			out += data.charAt(i);
		}
		return out;
	}
	
	private double computeIndex(int[] data){
		modulus = 256; 
		double ioc;
		int datalength = data.length;
		int[] freqs = new int[modulus];
		int i;

		for (i=0; i<modulus; i++){
			freqs[i] = 0;
		}
		for (i=0; i<datalength; i++){
			freqs[data[i]]++;
		}
		ioc = 0;
		for (i=0; i<modulus; i++){
			ioc = ioc + freqs[i]*(freqs[i]-1.0);
		}
		if (datalength>1)
			ioc = ioc/(datalength*(datalength-1.0));
		else
			ioc = 0;
		return ioc;
	}

	private double computeIndex(String data){
		alphaText = MainWindow.alphabet.getText();
		modulus = alphaText.length(); 
		double ioc;
		int datalength;
		int[] freqs = new int[modulus];
		int i, j;

		datalength = data.length();
		for (i=0; i<modulus; i++){
			freqs[i] = 0;
		}
		for (i=0; i<datalength; i++){
			j = alphaText.indexOf(data.charAt(i));
			if (j>=0){
				freqs[j]++;
			}
		}
		ioc = 0;
		for (i=0; i<modulus; i++){
			ioc = ioc + freqs[i]*(freqs[i]-1.0);
		}
		if (datalength>1)
			ioc = ioc/(datalength*(datalength-1.0));
		else
			ioc = 0;
		return ioc;
	}

	private String myRound(double num){
		String out;
		DecimalFormat form = new DecimalFormat("#.####");
		out = "" + Double.valueOf(form.format(num));
		while (out.length()<6)
			out += "0";
		return out;
	}

	public class NumRowsListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int i;
			i = decimationBox.getSelectedIndex();
			switch (i){
			case 0:
				numRows = 5;
				break;
			case 1:
				numRows = 10;
				break;
			case 2:
				numRows = 15;
				break;
			case 3:
				numRows = 20;
				break;
			case 4:
				numRows = 30;
				break;
			case 5:
				numRows = 40;
				break;
			case 6:
				numRows = 50;
				break;
			}
			if (MainWindow.mode==MainWindow.Modes.Text) 
				makeTable();
			else 
				makeTable2();
		}
	}

	public class CalculateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if (MainWindow.mode==MainWindow.Modes.Text) 
				makeTable();
			else 
				makeTable2();
		}
	}
}
