import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Polygraph extends JFrame {
	int modulus;
	String alphaText;
	JTextArea text = new JTextArea();
	JScrollPane textPane = new JScrollPane(text);
	JPanel PolySizePanel = new JPanel();
	JLabel PolySizeLabel = new JLabel("Polygraph Size: ");
	String[] PolySizeList = {"2","3","4","5"};
	JComboBox PolySizeBox = new JComboBox(PolySizeList);
	String[] showList = {"5","10","20","50","100"};
	JComboBox showBox = new JComboBox(showList);
	JLabel showLabel = new JLabel("  Show top: ");
	JButton CalculateButton = new JButton("Recompute");
	JPanel buttonPanel = new JPanel();

	ButtonGroup overlapGroup = new ButtonGroup();
	JRadioButton yesButton = new JRadioButton("Yes   ", true);
	int overlap = 1;
	JRadioButton noButton = new JRadioButton("No", false);
	JPanel overlapPanel = new JPanel();

	String output="";
	JTextArea str;
	int[][] freqs;
	int polyGraphSize=2;
	int maxFreq=0;
	int numDigits=1;
	int show;
	int showValue;


	// Constructor
	public Polygraph(String title, JTextArea str, int xLoc, int yLoc){
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC\
		text.setFont(font);
		setTitle(title + " Polygraph Frequencies");
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		//setSize(300,540);
		setSize(340,220);
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		PolySizePanel.add(PolySizeLabel);
		PolySizePanel.add(PolySizeBox);
		PolySizePanel.add(showLabel);
		PolySizePanel.add(showBox);
		add(PolySizePanel,BorderLayout.NORTH);
		PolySizeBox.setSelectedIndex(0);
		showBox.setSelectedIndex(0);
		add(textPane,BorderLayout.CENTER);
		PolySizeBox.addActionListener(new DisplayListener());
		showBox.addActionListener(new ShowListener());
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateButton.addActionListener(new CalculateButtonListener());
		buttonPanel.add(CalculateButton);


		overlapPanel.setLayout(new GridLayout(2,1));
		overlapGroup.add(yesButton);
		overlapGroup.add(noButton);
		overlapPanel.add(yesButton);
		overlapPanel.add(noButton);
		yesButton.addActionListener(new YesActionListener());
		noButton.addActionListener(new NoActionListener());
		overlapPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Overlap?"));
		add(BorderLayout.EAST,overlapPanel);

		setVisible(true);

		this.str= str;
		displayFrequencies();
	}

	private void displayFrequencies(){
		int freqTableSize;
		int[] freqTable;
		int[][] nzFreqTable;
		int nzFreqTableSize=0;
		int i, j, idx, n;
		String data = str.getText();

		alphaText = MainWindow.alphabet.getText();
		modulus = alphaText.length();
		output = "";
		show = showBox.getSelectedIndex();
		switch (show){
		case 0:
			showValue = 5;
			break;
		case 1:
			showValue = 10;
			break;
		case 2:
			showValue = 20;
			break;
		case 3:
			showValue = 50;
			break;
		default:
			showValue = 100;
		}
		polyGraphSize = PolySizeBox.getSelectedIndex()+2;
		if (yesButton.isSelected())
			overlap = 1;
		else
			overlap = polyGraphSize;

		freqTableSize = modulus;
		switch (polyGraphSize) {
		case 5: freqTableSize *= modulus;
		case 4: freqTableSize *= modulus;
		case 3: freqTableSize *= modulus;
		case 2: freqTableSize *= modulus;
		}
		freqTable = new int[freqTableSize];
		for (i=0; i<freqTableSize; i++) freqTable[i]=0;			// Initialize the frequency table to zero
		for (i=0; i<data.length()-polyGraphSize+1; i+=overlap)
		{
			n=0;
			for (j=i; j<i+polyGraphSize; j++)
			{
				n*=modulus;
				n += alphaText.indexOf(data.charAt(j));
			}
			if (n>=0){
				freqTable[n]++;										// Count the occurrences of a polygraph
			}
		}
		nzFreqTableSize=0;
		for (i=0; i<freqTableSize; i++)
			if (freqTable[i]>0) nzFreqTableSize++;
		nzFreqTable =  new int[nzFreqTableSize][2];
		n=0;
		for (i=0; i<freqTableSize; i++)
			if (freqTable[i]>0){
				nzFreqTable[n][0] = i;
				nzFreqTable[n][1]=freqTable[i];
				//System.out.println(""+n+": "+freqTable[i]);
				n++;
			}
		for (i=0; i<nzFreqTableSize-1; i++){
			int t;
			idx=i;
			for(n=i+1; n<nzFreqTableSize; n++)
				if (nzFreqTable[n][1]>nzFreqTable[idx][1]) idx = n;
			t = nzFreqTable[i][0];
			nzFreqTable[i][0] = nzFreqTable[idx][0];
			nzFreqTable[idx][0] = t;
			t = nzFreqTable[i][1];
			nzFreqTable[i][1] = nzFreqTable[idx][1];
			nzFreqTable[idx][1] = t;	
		}

		//		for (i=0; i<nzFreqTableSize; i++)
		int loopMax;
		if (showValue>nzFreqTableSize)
			loopMax = nzFreqTableSize;
		else
			loopMax = showValue;
		for (i=0; i<loopMax; i++)
			output += numToString(nzFreqTable[i][0],modulus,polyGraphSize)+": "+nzFreqTable[i][1]+"\n";

		text.setText(output);
		text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
	}

	public String numToString(int n, int base, int size){
		String str="",result="";
		int d,i;
		for (i=0; i<size; i++){
			d = n%base;
			n /= base;
			str += alphaText.charAt(d);
		}
		for (i=str.length()-1; i>=0; i--)	// Reverse the digit string
		{
			result += str.charAt(i);
		}
		return result;
	}

	public class DisplayListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			displayFrequencies();
		}
	}

	public class ShowListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			displayFrequencies();
		}
	}

	public class CalculateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			displayFrequencies();
		}
	}

	public class YesActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			displayFrequencies();
		}
	}

	public class NoActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){

			displayFrequencies();
		}
	}


}
