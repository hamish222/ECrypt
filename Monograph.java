import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;



public class Monograph extends JFrame{
	int modulus;
	String alphaText;
	JTextArea text = new JTextArea();
	JScrollPane textPane = new JScrollPane(text);
	JPanel decimationPanel = new JPanel();
	JLabel decimationLabel = new JLabel("Decimate by: ");
	String[] decimationList = {"1","2","3","4","5","6","7","8","9","10","11","12"};
	JComboBox decimationBox = new JComboBox(decimationList);
	String[] showList = {"5","10","20","All"};
	JComboBox showBox = new JComboBox(showList);
	JLabel showLabel = new JLabel("  Show top: ");
	JButton CalculateButton = new JButton("Recompute");
	JPanel buttonPanel = new JPanel();

	String output="";
	JTextArea str;
	int[][] freqs;
	int d;
	int maxFreq=0;
	int numDigits=1;
	int show;
	int showValue;

	// Constructor
	public Monograph(String title, JTextArea str, int xLoc, int yLoc){
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC\
		text.setFont(font);
		setTitle(title + " Monograph Frequencies");
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		//setSize(300,540);
		setSize(340,220);  // (vertical dimension, horizontal dimension)
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		decimationPanel.add(decimationLabel);
		decimationPanel.add(decimationBox);
		decimationPanel.add(showLabel);
		decimationPanel.add(showBox);
		add(decimationPanel,BorderLayout.NORTH);
		decimationBox.setSelectedIndex(0);
		showBox.setSelectedIndex(0);
		add(textPane,BorderLayout.CENTER);
		decimationBox.addActionListener(new DisplayListener());
		showBox.addActionListener(new ShowListener());
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateButton.addActionListener(new CalculateButtonListener());
		buttonPanel.add(CalculateButton);
		setVisible(true);

		this.str = str;
		displayFrequencies();
	}

	private String integerPad(int j, int numDigits){
		String out;
		out = "" ;
		int pad;
		if (j!=0)
			pad = numDigits - (1 + (int)Math.floor(Math.log(j)/Math.log(10.0)));
		else 
			pad = numDigits -1;
		for (int i=0; i<pad; i++){
			out += " ";
		}
		out += j;
		return out;
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

	private String sort(){
		int [][][] data = new int[modulus][d][2];
		int r,c,s,t,x;
		String output="";

		for (r=0; r<modulus; r++)
			for (c=0; c<d; c++)
			{
				data[r][c][0]=freqs[r][c];
				data[r][c][1]=r;
			}

		for (x=0; x<d; x++)
		{	
			for (r=0; r<modulus-1; r++)
			{
				s = r;
				for (c=r+1; c<modulus; c++)
					if (data[c][x][0] > data[s][x][0]) s=c;

				t = data[r][x][0];
				data[r][x][0] = data[s][x][0];
				data[s][x][0] = t;

				t = data[r][x][1];
				data[r][x][1] = data[s][x][1];
				data[s][x][1] = t;
			}

		}

		for (r=0; r<showValue; r++)
		{
			for (c=0; c<d; c++){
				output += alphaText.charAt(data[r][c][1]);
				output += " " + integerPad(data[r][c][0], numDigits) +"     ";
			}
			output += "\n";
		}


		return output;
	}

	private void displayFrequencies(){
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
		default:
			showValue = modulus;
		}
		freqs = monoDecFreqs(data,d=decimationBox.getSelectedIndex()+1);
		output = sort();
		text.setText(output);
		text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
		setVisible(true);
	}

	// Compute decimated monograph frequencies.
	private int[][] monoDecFreqs(String str, int d){
		int i, j, k;
		int[][] freqs = new int[modulus][d];

		for (i=0; i<modulus; i++){
			for (j=0; j<d; j++){
				freqs[i][j] = 0;  // Initialize freqs.
			}
		}
		maxFreq = 0;
		if (d==1)
		{
			for (i=0; i<str.length(); i++){
				k = alphaText.indexOf(str.charAt(i));
				if (k>=0){
					freqs[k][0]++;
					if (freqs[k][0]>maxFreq)
						maxFreq = freqs[k][0];
				}
			}
		}
		else
		{
			for (i=0; i<str.length()-d; i=i+d){
				for (j=0; j<d; j++){
					k = alphaText.indexOf(str.charAt(i+j)); // Compute frequencies for body of the text.
					if (k>=0){
						freqs[k][j]++;
						if (freqs[k][j]>maxFreq)
							maxFreq = freqs[k][j];
					}
				}
			}
			for (j=0; j<(str.length()%d); j++){
				//System.out.println("Pick up stragglers.");
				k = alphaText.indexOf(str.charAt(i+j)); // Compute frequencies for the tail end of the text.
				if (k>=0){
					freqs[k][j]++;
					if (freqs[k][j]>maxFreq)
						maxFreq = freqs[k][j];
				}
			}
		}
		numDigits = 1 + (int)Math.floor(Math.log(maxFreq)/Math.log(10.0));
		return freqs;
	}

	public class CalculateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			displayFrequencies();
		}
	}

}
