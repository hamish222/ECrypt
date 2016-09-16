import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Issues: 
//	1. Need to check that the plaintext characters are all in the alphabet.
//	2. Need to alter the alphabet in the alphabet window and make it unchangeable until a different algorithm is chosen.


public class PolybiusCheckerboard{

	static JTextField[] board = new JTextField[25];
	JPanel boardPanel = new JPanel();
	JButton clear = new JButton("Clear");
	JButton random = new JButton("Randomize");
	JButton identity = new JButton("Standard");
	JPanel buttonPanel = new JPanel();
	PolybiusMatrix polybiusMatrix = new PolybiusMatrix();
	char[] alphabetChars = new char[25];
	int[] invboard = new int[25];
	static String alphaText;

	Font font = new Font("Courier", Font.PLAIN, 12);
	
	// Constructor
	PolybiusCheckerboard(){
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
		polybiusMatrix.setVisible(visible);
	}

	// Checkerboard window
	public class PolybiusMatrix extends JFrame{
		public PolybiusMatrix(){
			setSize(280,200);
			setTitle("Polybius Checkerboard");
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

	public static String Encrypt(String plain){
		String crypt = "";
		int length;
		int i, j;
		char[] plainChars;
		char[] table = new char[25];
		String temp;
		boolean warnQ = false;

		// Convert input string to a list of characters.
		plainChars = plain.toCharArray();
		length = plain.length();

/*		// Read current table in checkerboard window.
		temp = "";
		for (i=0; i<=24; i++)
		{
			table[i] = board[i].getText().charAt(0);
			temp = temp + table[i];
		}
*/
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
				return "";
			}
		}
		
		
		// Encrypt.  This is done in a very inefficient way because I search for the position of each character as I come across it in the plaintext.
		for (i=0; i<length; i++){
			j = temp.indexOf(plainChars[i]);
			crypt = crypt + (1+(j/5));
			crypt = crypt + (1+(j%5));
		}
		return crypt;
	}

	public static String Decrypt(String crypt){
		String plain = "";
		int i;
		int j;
		int length;
		int index;
		char[] table = new char[25];
		boolean warnQ = false;
		String temp;
		
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
				System.out.println(i + "   " + charCount[i]);
				JOptionPane.showMessageDialog(null, out,"Error",JOptionPane.ERROR_MESSAGE);
				return "";
			}
		}
		
		// Decrypt
		length = crypt.length();
		//System.out.println("cipher length = "+length);
		index = 0;
		while (index<length){
			i = Integer.parseInt(crypt.substring(index,index+1));
			j = Integer.parseInt(crypt.substring(index+1,index+2));
			i = i - 1;
			j = j - 1;
			plain = plain + table[5*i+j];
			index = index + 2;
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
