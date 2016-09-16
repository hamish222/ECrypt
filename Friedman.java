import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Friedman extends JFrame{
	int modulus, numRows;
	String alphaText;
	JTextField textIoC = new JTextField(6);
	//JScrollPane textIoCPane = new JScrollPane(textIoC);
	JPanel indexPanel = new JPanel();
	JLabel indexLabel = new JLabel("Plaintext Index of Coincidence: ");
	JTextField textout = new JTextField(6);
	JLabel outLabel = new JLabel("Predicted Keyword Length: ");
	JPanel outPanel = new JPanel();
	String output;
	JTextArea str;
	int[] freqs;
	String data;
	JButton CalculateButton = new JButton("Recompute");
	JPanel buttonPanel = new JPanel();

	//Constructor
	public Friedman(String title, JTextArea str, int xLoc, int yLoc){
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC\
		setTitle("Friedman's Formula for "+title);
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		setSize(330,140);
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

		indexPanel.add(indexLabel);
		textIoC.setFont(font);
		textIoC.setText("0.065");
		indexPanel.add(textIoC);
		add(indexPanel,BorderLayout.NORTH);

		outPanel.add(outLabel);
		outPanel.add(textout);
		textout.setFont(font);
		add(outPanel,BorderLayout.CENTER);
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateButton.addActionListener(new CalculateButtonListener());
		buttonPanel.add(CalculateButton);

		setVisible(true);

		this.str = str;
		computePutativeLength();

	}


	private void computePutativeLength(){
		int n;
		String alphaText = MainWindow.alphabet.getText();
		double r, flatIoC, dataIoC, plainIoC;
		data = str.getText();

		dataIoC = computeIndex(data);
		plainIoC = Double.parseDouble(textIoC.getText());
		n = data.length();
		flatIoC = 1.0/alphaText.length();
		r = (plainIoC-flatIoC)*n;
		r = r/((n-1.0)*dataIoC - flatIoC*n + plainIoC);
		output = "" + myRound(r);  
		textout.setText(output);
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
			ioc = ioc + freqs[i]*(freqs[i]-1);
		}
		if (datalength>1)
			ioc = ioc/(datalength*(datalength-1));
		else
			ioc = 0;
		return ioc;
	}

	private String myRound(double num){
		String out;
		DecimalFormat form = new DecimalFormat("#.###");
		out = "" + Double.valueOf(form.format(num));
		while (out.length()<5)
			out += "0";
		return out;
	}

	public class CalculateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			computePutativeLength();
		}
	}



}
