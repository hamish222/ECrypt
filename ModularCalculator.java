import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ModularCalculator extends JFrame{
	BigInteger mod = new BigInteger("1000");
	// Set up panel for modulus.
	JPanel modulusPanel = new JPanel();
	JLabel modulusLabel= new JLabel("Modulus = ");
	
	JTextField modulus = new JTextField(15);
	// Set up panel for display of calculations.
	JTextArea output = new JTextArea(50,80);
	private JScrollPane outpane = new JScrollPane(output);
	//JPanel centerPanel = new JPanel();
	String out;
	String history;
	// Set up panel for calculator buttons.
	JPanel buttonPanel = new JPanel();
	JPanel buttonRow1 = new JPanel();
	JPanel buttonRow2 = new JPanel();
	JPanel buttonRow3 = new JPanel();
	JPanel buttonRow4 = new JPanel();
	JPanel buttonRow5 = new JPanel();
	JButton Button1 = new JButton("1");
	JPanel panel1 = new JPanel();
	JButton Button2 = new JButton("2");
	JPanel panel2 = new JPanel();
	JButton Button3 = new JButton("3");
	JPanel panel3 = new JPanel();
	JButton Button4 = new JButton("4");
	JPanel panel4 = new JPanel();
	JButton Button5 = new JButton("5");
	JPanel panel5 = new JPanel();
	JButton Button6 = new JButton("6");
	JPanel panel6 = new JPanel();
	JButton Button7 = new JButton("7");
	JPanel panel7 = new JPanel();
	JButton Button8 = new JButton("8");
	JPanel panel8 = new JPanel();
	JButton Button9 = new JButton("9");
	JPanel panel9 = new JPanel();
	JButton Button0 = new JButton("0");
	JPanel panel0 = new JPanel();
	JButton ButtonPlus = new JButton("+");
	JPanel panelPlus = new JPanel();
	JButton ButtonMinus = new JButton("-");
	JPanel panelMinus = new JPanel();
	JButton ButtonTimes = new JButton("*");
	JPanel panelTimes = new JPanel();
	JButton ButtonInverse = new JButton(" inv ");
	JPanel panelInverse = new JPanel();
	JButton ButtonGCD = new JButton("gcd");
	JPanel panelGCD = new JPanel();
	JButton ButtonLCM = new JButton("lcm");
	JPanel panelLCM = new JPanel();
	JButton ButtonExp = new JButton("^");
	JPanel panelEXP = new JPanel();
	JButton ButtonLeftParen = new JButton("(");
	JPanel panelLeftParen = new JPanel();
	JButton ButtonRightParen = new JButton(")");
	JPanel panelRightParen = new JPanel();
	JButton EnterButton = new JButton("=");
	JPanel panelEnter = new JPanel();
	JButton ClearButton = new JButton("  C  ");
	JPanel panelClear = new JPanel();
	JButton AllClearButton = new JButton("AC ");
	JPanel panelAllClear = new JPanel();	
	JButton CommaButton = new JButton(",");
	JPanel panelComma = new JPanel();
	JButton AnswerButton = new JButton("Ans");
	JPanel panelAnswer = new JPanel();

	Interpret interpreter = new Interpret(mod); 

	String answer = "";


	// See http://wiki.answers.com/Q/How_do_you_set_button_size_in_GridLayout_java for hope in sizing buttons.

	public MCActionListener actionListener = new MCActionListener();

	// Constructor
	public ModularCalculator(){
		setSize(280,500);
		setTitle("Modular Calculator");
		setLocation(780,245);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		setLayout(new BorderLayout());
		// Modulus panel at top.
		modulusPanel.add(modulusLabel);
		modulusPanel.add(modulus);
		modulus.setText(""+mod); // Initialize the modulus.
		add(BorderLayout.NORTH,modulusPanel);
		// Output panel in center.
		output.setLineWrap(true);
		output.setWrapStyleWord(true);
		//centerPanel.add(outpane);
		add(BorderLayout.CENTER,outpane);
		// Button panel at bottom.
		buttonPanel.setLayout(new GridLayout(6,4));
		panel1.add(Button1);
		buttonPanel.add(panel1);
		panel2.add(Button2);
		buttonPanel.add(panel2);
		panel3.add(Button3);
		buttonPanel.add(panel3);
		panelClear.add(ClearButton);
		buttonPanel.add(panelClear);

		panel4.add(Button4);
		buttonPanel.add(panel4);
		panel5.add(Button5);
		buttonPanel.add(panel5);
		panel6.add(Button6);
		buttonPanel.add(panel6);
		panelAllClear.add(AllClearButton);
		buttonPanel.add(panelAllClear);

		panel7.add(Button7);
		buttonPanel.add(panel7);
		panel8.add(Button8);
		buttonPanel.add(panel8);
		panel9.add(Button9);
		buttonPanel.add(panel9);
		panelGCD.add(ButtonGCD);
		buttonPanel.add(panelGCD);

		panelLeftParen.add(ButtonLeftParen);
		buttonPanel.add(panelLeftParen);
		panel0.add(Button0);
		buttonPanel.add(panel0);
		panelRightParen.add(ButtonRightParen);
		buttonPanel.add(panelRightParen);
		panelLCM.add(ButtonLCM);
		buttonPanel.add(panelLCM);

		panelPlus.add(ButtonPlus);
		buttonPanel.add(panelPlus);
		panelMinus.add(ButtonMinus);
		buttonPanel.add(panelMinus);
		panelTimes.add(ButtonTimes);
		buttonPanel.add(panelTimes);
		panelInverse.add(ButtonInverse);
		buttonPanel.add(panelInverse);

		panelComma.add(CommaButton);
		buttonPanel.add(panelComma);
		panelEXP.add(ButtonExp);
		buttonPanel.add(panelEXP);
		panelEnter.add(EnterButton);
		buttonPanel.add(panelEnter);
		panelAnswer.add(AnswerButton);
		buttonPanel.add(panelAnswer);

		add(BorderLayout.SOUTH,buttonPanel);
		// Enable the buttons.
		ClearButton.addActionListener(new ClearActionListener()); 
		AllClearButton.addActionListener(new AllClearActionListener()); 
		EnterButton.addActionListener(new EnterActionListener()); 
		AnswerButton.addActionListener(new AnswerActionListener()); 
		ButtonPlus.addActionListener(new PlusActionListener()); 
		ButtonMinus.addActionListener(new MinusActionListener()); 
		ButtonTimes.addActionListener(new TimesActionListener()); 
		ButtonInverse.addActionListener(new InverseActionListener());
		ButtonLeftParen.addActionListener(new LeftParenActionListener()); 
		ButtonRightParen.addActionListener(new RightParenActionListener()); 
		CommaButton.addActionListener(new CommaActionListener()); 
		Button1.addActionListener(new Button1ActionListener()); 
		Button2.addActionListener(new Button2ActionListener()); 
		Button3.addActionListener(new Button3ActionListener()); 
		Button4.addActionListener(new Button4ActionListener()); 
		Button5.addActionListener(new Button5ActionListener()); 
		Button6.addActionListener(new Button6ActionListener()); 
		Button7.addActionListener(new Button7ActionListener()); 
		Button8.addActionListener(new Button8ActionListener()); 
		Button9.addActionListener(new Button9ActionListener());
		Button0.addActionListener(new Button0ActionListener()); 
		ButtonExp.addActionListener(new ExpActionListener());
		ButtonGCD.addActionListener(new GCDActionListener());
		ButtonLCM.addActionListener(new LCMActionListener());
		output.addKeyListener(new KeyEventDemo());
		// Initialize output.
		out = "";
		history = "";
		output.requestFocusInWindow(); 

	}

	public class EnterActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String result = "";
			BigInteger one = new BigInteger("1");
			try {
				mod = new BigInteger(modulus.getText());
				if (mod.compareTo(one)<0)
				{
					JOptionPane.showMessageDialog(null,"The modulus must be at least one.  Resetting the modulus to 1000.","ERROR!",JOptionPane.ERROR_MESSAGE);
					mod = new BigInteger("1000");
					modulus.setText("1000");
				}
			}
			catch(NumberFormatException nFE){
				JOptionPane.showMessageDialog(null,"The modulus must be an integer.  Resetting the modulus to 1000.","ERROR!",JOptionPane.ERROR_MESSAGE);
				mod = new BigInteger("1000");
				modulus.setText("1000");
			}
			interpreter.setModulus(mod);
			if (out.length()>0){
				try {
					result =  interpreter.eval(out);
					history = history + out + "\n answer = " + result + "\n";
					output.setText(history);
				} catch (Exception e1) {
					history = history + out + "\n" + e1.getMessage() + "\n";
					output.setText(history);
				}
				//e1.printStackTrace();
			}
			else
			{
				history = history + "\n";
				output.setText(history);
			}

			answer = "" + result;
			out = "";
			output.requestFocusInWindow(); 

		}
	}

	public class AnswerActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + answer;
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class ClearActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = "";
			//history = "";
			output.setText(history);
			output.requestFocusInWindow(); 

		}
	}

	public class AllClearActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = "";
			history = "";
			output.setText(out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button1ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "1";
			output.setText(history + out);
			output.requestFocusInWindow(); 

		}
	}

	public class Button2ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "2";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button3ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "3";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button4ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "4";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button5ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "5";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button6ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "6";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button7ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "7";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button8ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "8";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button9ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "9";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class Button0ActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "0";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class PlusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + " + ";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class MinusActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + " - ";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class TimesActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + " * ";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class InverseActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "inv(";
			output.setText(history + out);
			output.requestFocusInWindow();
		}
	}

	public class LeftParenActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "(";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class RightParenActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + ")";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}	

	public class CommaActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + ",";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class ExpActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "^";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class GCDActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "gcd(";
			output.setText(history + out);
			output.requestFocusInWindow(); 
		}
	}

	public class LCMActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			out = out + "lcm(";
			output.setText(history + out);
			output.requestFocusInWindow(); 
			// lcm(a,b) = a*b/gcd(a,b)
		}
	}

	public class MCActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}

	public class KeyEventDemo implements KeyListener {
		public void keyTyped(KeyEvent e){
			char ch;
			ch = e.getKeyChar();
			//System.out.print(ch);
			switch (ch) {
			default:
				out += ch;
				//System.out.println("Key: "+ (int)ch);
				break;
			case (char)8:
				if (out.length()>0)
					out = out.substring(0,out.length()-1);
				else
				{
					output.setText(history);
					out="";
				}
			break;
			case '\n':
				EnterButton.doClick();
				break;

			case (int)27:
				ClearButton.doClick();
			break;
			case (int)127:
				AllClearButton.doClick();
			break;
			}
		}
		@Override
		public void keyPressed(KeyEvent e) {
		}
		@Override
		public void keyReleased(KeyEvent e) {
		}
	}
}
