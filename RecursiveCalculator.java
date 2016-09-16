import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

//import TextDisplayWindow.ViewButtonListener;

public class RecursiveCalculator extends JFrame{
	/*	public ViewButtonListener buttonListener = new ViewButtonListener();
	public JTextArea text = new JTextArea(10,50);
	private JScrollPane pane = new JScrollPane(text);
	 */

	JPanel modulusPanel = new JPanel();
	JLabel modulusLabel= new JLabel("Modulus = ");
	JTextField modulus = new JTextField(4);
	JLabel lengthLabel= new JLabel("     Length = ");
	JTextField length = new JTextField(2);
	int n = 2;
	int mod = 1000;

	String[] lengthList = {"1","2","3","4","5","6","7","8"};
	JComboBox lengthBox = new JComboBox(lengthList);

	JPanel recursivePanel = new JPanel();
	JLabel anlLabel = new JLabel("a(n) = ");  // Check out JEditorPane to use HTML to make subscripts.
	//JEditorPane html = new JEditorPane( "text/html",
    //        "<HTML><BODY> CH<SUB>4</SUB> <BR> x<SUP>2</SUP> </BODY></HTML>");
	JTextField[] anCoeff = new JTextField[8];
	JLabel[] anLabel = new JLabel[8]; 
	int[] coeffs = new int[8];

	JPanel seedPanel = new JPanel();
	JTextField[] seedValue = new JTextField[8];
	JLabel[] seedLabel = new JLabel[8];

	JTextArea output = new JTextArea(50,80);
	private JScrollPane outpane = new JScrollPane(output);
	int[] register = new int[8];
	int[] seeds = new int[8];
	String out = "";
	JPanel centerPanel = new JPanel();
	JPanel buttonPanel = new JPanel();
	JButton CalculateButton = new JButton("Calculate");
	JButton ClearButton = new JButton("Clear");

	public RCActionListener actionListener = new RCActionListener();

	// Constructor
	RecursiveCalculator(){
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		int i;

		setSize(580,300);
		setTitle("Recursive Calculator");
		setLocation(780,245);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		setLayout(new BorderLayout());
		modulusPanel.add(modulusLabel);
		modulusPanel.add(modulus);
		modulus.setText(""+mod); // Initialize the modulus.
		modulusPanel.add(lengthLabel);
		modulusPanel.add(lengthBox);
		lengthBox.setSelectedIndex(n-1);
		lengthBox.addActionListener(new LengthActionListener()); // n starts as 2 by default.
		add(BorderLayout.NORTH,modulusPanel);
		//add(BorderLayout.NORTH,html);

		// Initialize ALL coefficients and seeds. 
		for (i=0; i<8; i++){
			coeffs[i] = 1;
			seeds[i] = i;
		}
		// Initialize the register.
		for (i=0; i<n; i++)
			register[i] = seeds[i] % mod;
		// Initialize the output.
		output.setText("");
		out = "";
		for (i=0; i<n; i++)
		{
			if (mod <= 10) 
				out = out + register[i];
			else
				out = out + register[i] + " ";
		}
		displayLSR();
		centerPanel.setLayout(new BorderLayout());
		centerPanel.add(BorderLayout.NORTH,recursivePanel);
		centerPanel.add(BorderLayout.SOUTH,seedPanel);

		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		centerPanel.add(BorderLayout.CENTER,outpane);
		add(BorderLayout.CENTER,centerPanel);
		buttonPanel.add(CalculateButton);
		buttonPanel.add(ClearButton);
		//add(CalculateButton);
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateActionListener x;
		x = new CalculateActionListener();
		CalculateButton.addActionListener(x);
		ClearActionListener y;
		y = new ClearActionListener();
		ClearButton.addActionListener(y);

		setVisible(false);
	}

	private void displayLSR()
	{
		// displayLSR displays the known coefficients and seeds.
		recursivePanel.removeAll();
		seedPanel.removeAll();
		recursivePanel.add(anlLabel);
		for (int i=0; i<n; i++)
		{
			anCoeff[i] = new JTextField(2);
			anCoeff[i].setText(""+coeffs[i]);
			if (i<n-1)
				anLabel[i] = new JLabel("a(n-"+(i+1)+")  + ");
			else
				anLabel[i] = new JLabel("a(n-"+(i+1)+")");
			recursivePanel.add(anCoeff[i]);
			recursivePanel.add(anLabel[i]);

			seedValue[i] = new JTextField(2);
			seedValue[i].setText(""+seeds[i]);
			seedLabel[i] = new JLabel(" a("+(i)+") =");
			seedPanel.add(seedLabel[i]);
			seedPanel.add(seedValue[i]);		
		}

		/*out = "";
		output.setText("");

		for (int i=0; i<n; i++)
		{
			seeds[i] = Integer.parseInt(seedValue[i].getText());
			if (mod <= 10) 
				out = out + seeds[i];
			else
				out = out + seeds[i] + " ";
		}*/
	}

	private void reset(){
		int i;
		mod = Integer.parseInt(modulus.getText());
		for (i=0; i<n; i++){
			coeffs[i] = Integer.parseInt(anCoeff[i].getText());
			seeds[i] = Integer.parseInt(seedValue[i].getText());
			register[i] = seeds[i] % mod;
		}
		output.setText("");
		out = "";
		for (i=0; i<n; i++)
		{
			if (mod <= 10) 
				out = out + register[i];
			else
				out = out + register[i] + " ";
		}
		//		output.setText(out);
	}

	public class LengthActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int i;
			//			for (i=0; i<n; i++){
			//			seeds[i] = Integer.parseInt(seedValue[i].getText());
			//		coeffs[i] = Integer.parseInt(anCoeff[i].getText());
			//}
			reset();
			n = lengthBox.getSelectedIndex()+1;
			displayLSR();
			reset();
			if (n>6) 
				setSize(780,300);
			else
				setSize(580,300);
			setVisible(false);
			setVisible(true);
		}
	}

	public class CalculateActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int i;
			// Update modulus, seeds, and coefficients before computing.
			// If the modulus has changed, reset.
			if (mod != Integer.parseInt(modulus.getText()))
				reset();
			// If any of the visible seed values have changed, reset.
			for (i=0; i<n; i++){
				if (seeds[i] != Integer.parseInt(seedValue[i].getText()))
					reset();
			}
			// If any of the coefficients have changed, reset.
			for (i=0; i<n; i++){
				if (coeffs[i] != Integer.parseInt(anCoeff[i].getText()))
					reset();
			}
			//mod = Integer.parseInt(modulus.getText());
			//			for (i=0; i<n; i++){
			//			seeds[i] = Integer.parseInt(seedValue[i].getText());
			//		coeffs[i] = Integer.parseInt(anCoeff[i].getText());
			//}
			// Compute 20 new terms in the sequence when the Calculate button is pressed.
			for (i=0; i<20; i++){	
				register = LFSR.shift(register,coeffs,n,mod);
				if (mod <= 10)
					out = out + register[n-1];
				else
					out = out + register[n-1] + " ";
			}

			output.setText(out);
		}
	}

	public class ClearActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			reset();
			//out = "";
			//output.setText(out);
			//for (int i=0; i<n; i++){
			//register[i] = seeds[i];
			//out = out + seeds[i];
		}
	}

	public class RCActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}
}
