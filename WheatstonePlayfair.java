import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class WheatstonePlayfair{

	static JTextField[] board = new JTextField[25];
	JPanel boardPanel = new JPanel();
	JButton clear = new JButton("Clear");
	JButton random = new JButton("Randomize");
	JButton identity = new JButton("Standard");
	JPanel buttonPanel = new JPanel();
	WPMatrix wpMatrix = new WPMatrix();
	char[] alphabetChars = new char[25];
	int[] invboard = new int[25];
	static boolean computeQ;
	static String alphaText;

	Font font = new Font("Courier", Font.PLAIN, 12);
	
	// Constructor
	WheatstonePlayfair(){
		int i;
		alphaText = "abcdefghijklmnopqrstuvwxy";
		alphabetChars = alphaText.toCharArray();
		boardPanel.setLayout(new GridLayout(5,5));
		for (i=0; i<=24; i++)
		{
			board[i] = new JTextField(2);
			board[i].setText(""+alphabetChars[i]);
			board[i].setFont(font);
			boardPanel.add(board[i]);
		}
	}

	public void setMatrixVisible(boolean visible){
		wpMatrix.setVisible(visible);
	}

	// Checkerboard window
	public class WPMatrix extends JFrame{
		public WPMatrix(){
			setSize(280,200);
			setTitle("Wheatstone-Playfair");
			setLocation(780,95);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			setIconImage(MainWindow.ECryptIcon.getImage());
//			setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
			
			setLayout(new BorderLayout());
			clear.addActionListener(new ClearActionListener()); 
			random.addActionListener(new RandomActionListener()); 
			identity.addActionListener(new IdentityActionListener());
			buttonPanel.add(clear);
			buttonPanel.add(random);
			buttonPanel.add(identity);
			add(buttonPanel,BorderLayout.NORTH);
			//setLayout(new GridLayout(5,5));
			add(boardPanel,BorderLayout.CENTER);
			setVisible(false);
		}
	}		

	// Encryption
	public static String Encrypt(String plain){
		String crypt = "";
		int length;
		int i, j;
		int pad, pad2;
		int index;
		int firstR, firstC, secondR, secondC;
		char[] plainChars;
		char[] table = new char[25];
		int[] invtable = new int[25];
		String temp;
		boolean warnQ = false;

		// Convert input string to a list of characters.
		plainChars = plain.toCharArray();
		length = plain.length();

		// Read current table in checkerboard window.
		temp = "";
		for (i=0; i<=24; i++)
		{
			if (board[i].getText().length()>1){
				warnQ = true;
			}
			try {
				table[i] = board[i].getText().charAt(0);
				temp = temp + table[i];
			} catch (StringIndexOutOfBoundsException e1) {
					JOptionPane.showMessageDialog(null, "The entries in the matrix must be characters from the alphabet.","Error",JOptionPane.ERROR_MESSAGE);
					//e1.printStackTrace();
					return "";
			}
		}
		if (warnQ){
			JOptionPane.showMessageDialog(null, "Only the first character in each cell is used.","Warning",JOptionPane.WARNING_MESSAGE);
		}
		
		// Compare board characters with alphabet.
		int[] charCount = new int[25];
		String missing = "";
		String extra = "";
		String out = "The letters in the table do not conform to the alphabet.\n";
		int temp2;
		for (i=0; i<25; i++){
			charCount[i] = 0;  // Initialize counter array
		}
		for (i=0; i<25; i++){
			temp2 = alphaText.indexOf(table[i]);
			if (temp2>=0){
				charCount[temp2]++;  // Count.
			}
		}
		for (i=0; i<25; i++){
			if (charCount[i]>1){
				extra += "   " + alphaText.charAt(i) + " (" + charCount[i] + ")";	// Determine which letters have extras.
			}
			if (charCount[i]==0){	
				missing += "   " + alphaText.charAt(i);	// Determine which letters are missing.
			}
			
		}
		if (missing.length()>0){
			out += "Missing: " + missing;
		}
		if (missing.length()>0){
			out += "\n";
		}
		if (extra.length()>0){
			out += "Extra: " + extra;
		}
		for (i=0; i<24; i++){
			if (charCount[i] != 1){
				JOptionPane.showMessageDialog(null, out,"Error",JOptionPane.ERROR_MESSAGE);
				return "";
			}
		}
			
		// Find position of padding character x and backup pad j.
		pad = temp.indexOf("x");
		pad2 = temp.indexOf("j");
		// Encrypt.  
		index = 0;
		while (index<length-1){
			i = temp.indexOf(plainChars[index]);
			j = temp.indexOf(plainChars[index+1]);

			index = index + 2;  // characters are encrypted in pairs
			if (i==j && i!=pad){ // check to see if the letters repeated 
				j = pad;
				index = index - 1;
			}
			if (i==j && i==pad){ // check to see if the letters are xx
				j = pad2;
				index = index - 1;
			}
			firstR = i/5;
			firstC = i%5;
			secondR = j/5;
			secondC = j%5;
			//System.out.println("("+firstR+","+firstC+"),("+secondR+","+secondC+")");
			if (firstR==secondR && firstC!=secondC){
				firstC = (firstC + 1) % 5;
				secondC = (secondC + 1) % 5;
			}
			else if (firstR!=secondR && firstC==secondC){				
				firstR = (firstR + 1) % 5;
				secondR = (secondR + 1) % 5;
			}
			else if (firstR!=secondR && firstC!=secondC){
				j = firstC;
				firstC = secondC;
				secondC = j;
			}
			else{
				JOptionPane.showMessageDialog(null, "Something has gone horribly wrong.","Error",JOptionPane.ERROR_MESSAGE);
				return "";
			}
			
			crypt = crypt + table[5*firstR+firstC] + table[5*secondR+secondC];
		}
		return crypt;
	}

	// Decryption
	public static String Decrypt(String cipher){
		String plain = "";
		int length;
		int i, j;
		int pad, pad2;
		int index;
		int firstR, firstC, secondR, secondC;
		char[] cipherChars;
		char[] table = new char[25];
		int[] invtable = new int[25];
		String temp;
		boolean warnQ = false;

		// Convert input string to a list of characters.
		cipherChars = cipher.toCharArray();
		length = cipher.length();

		
		computeQ = true;
		// Read current table in checkerboard window.
		temp = "";
		for (i=0; i<=24; i++)
		{
			if (board[i].getText().length()>1){
				warnQ = true;
			}
			try {
				table[i] = board[i].getText().charAt(0);
				temp = temp + table[i];
			} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "The entries in the matrix must be characters from the alphabet.","Error",JOptionPane.ERROR_MESSAGE);
					//e1.printStackTrace();
					computeQ = false;
					break;
			}
		}
		if (warnQ){
			JOptionPane.showMessageDialog(null, "Only the first character in each cell is used.","Warning",JOptionPane.WARNING_MESSAGE);
		}
		
		// Compare board characters with alphabet.
		int[] charCount = new int[25];
		String missing = "";
		String extra = "";
		String out = "The letters in the table do not conform to the alphabet.\n";
		int temp2;
		for (i=0; i<25; i++){
			charCount[i] = 0;  // Initialize counter array
		}
		for (i=0; i<25; i++){
			temp2 = alphaText.indexOf(table[i]);
			if (temp2>=0){
				charCount[temp2]++;  // Count.
			}
		}
		for (i=0; i<25; i++){
			if (charCount[i]>1){
				extra += "   " + alphaText.charAt(i) + " (" + charCount[i] + ")";	// Determine which letters have extras.
			}
			if (charCount[i]==0){	
				missing += "   " + alphaText.charAt(i);	// Determine which letters are missing.
			}
			//System.out.println(i + "   " + charCount[i]);
			
		}
		if (missing.length()>0){
			out += "Missing: " + missing;
		}
		if (missing.length()>0){
			out += "\n";
		}
		if (extra.length()>0){
			out += "Extra: " + extra;
		}
		for (i=0; i<25; i++){
			if (charCount[i] != 1){
				JOptionPane.showMessageDialog(null, out,"Error",JOptionPane.ERROR_MESSAGE);
				computeQ = false;
				break;
			}
		}
		
		if (!computeQ)
			return "";
	
		// Find position of padding character x and backup pad j.
		pad = temp.indexOf("x");
		pad2 = temp.indexOf("j");
		// Decrypt.  There should never be repeated characters in a pair of ciphertext characters, but I'll code for that contingency anyway.
		index = 0;
		while (index<length-1){
			i = temp.indexOf(cipherChars[index]);
			j = temp.indexOf(cipherChars[index+1]);

			index = index + 2;  // characters are encrypted in pairs
			if (i==j && i!=pad){ // check to see if the letters repeated 
				j = pad;
				index = index - 1;
			}
			if (i==j && i==pad){ // check to see if the letters are xx
				j = pad2;
				index = index - 1;
			}
			firstR = i/5;
			firstC = i%5;
			secondR = j/5;
			secondC = j%5;
			if (firstR==secondR && firstC!=secondC){
				firstC = (firstC - 1 + 5) % 5;
				secondC = (secondC - 1 + 5) % 5;
			}
			else if (firstR!=secondR && firstC==secondC){				
				firstR = (firstR - 1 + 5) % 5;
				secondR = (secondR - 1 + 5) % 5;
			}
			else if (firstR!=secondR && firstC!=secondC){
				j = firstC;
				firstC = secondC;
				secondC = j;
			}
			else JOptionPane.showMessageDialog(null, "Something has gone horribly wrong.");
			
			plain = plain + table[5*firstR+firstC] + table[5*secondR+secondC];
		}
		return plain;
	}
	
	public class ClearActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			for (int i=0; i<=24; i++)
			{
				board[i].setText("");
				boardPanel.add(board[i]);
			}
		}
	}

	public boolean memberQ(int num, int[] arr){
		int i;
		boolean out = false;
		for (i=0; i<=24; i++){
			if (arr[i] == num)
				out = true;
		}
		return out;  // Tests to see if the integer num is in array arr.
	}

	public class RandomActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int[] rand = new int[25];
			int newNumber;
			int index;
			int i;
			int j;
			boolean contQ;

			for (j=0; j<=24; j++){
				rand[j] = 9999;
			}
			newNumber = (int) (Math.random()*25);
			rand[0] = newNumber;
			for (index=1; index<25; index++){
				newNumber = (int) (Math.random()*25);
				//System.out.println("I: "+index + "  R: "+newNumber);
				contQ = memberQ(newNumber, rand);   // Check to see if newNumber already appears in the list.
				while (contQ){
					newNumber = (newNumber + 1) % 25;
					contQ = memberQ(newNumber, rand);   // Check to see if newNumber already appears in the list.
				}	
				rand[index] = newNumber;
			}

			for (i=0; i<=24; i++)
			{
				board[i].setText(""+alphabetChars[rand[i]]);
				boardPanel.add(board[i]);
			}

		}
	}

	public class IdentityActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			for (int i=0; i<=24; i++)
			{
				board[i].setText(""+alphabetChars[i]);
				boardPanel.add(board[i]);
			}
		}
	}

}
