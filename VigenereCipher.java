import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class VigenereCipher extends JFrame{
	JPanel inputPanel = new JPanel();
	JLabel inputLabel = new JLabel("Keyword = ");
	static JTextField keyText = new JTextField(25);

	Font font = new Font("Courier", Font.PLAIN, 12);

	// Constructor
	public VigenereCipher(){
		setResizable(false);
		setSize(320,80);
		setTitle("Vigenere Cipher");
		setLocation(785,100);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setIconImage(MainWindow.ECryptIcon.getImage());
		//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		inputPanel.add(inputLabel);
		inputPanel.add(keyText);
		keyText.setText("");
		keyText.setFont(font);
		add(inputPanel);

		setVisible(false);
	}

	public static byte[] Encrypt(byte[] plain)
	{
		int i;
		byte[] cipher = new byte[plain.length];
		int keyLength;
		int[] key;
		String keyString = keyText.getText(); 
		key = string2Bytes(keyString);  // Convert the input to an array of bytes.
		keyLength = key.length;
		try {
			for (i=0; i<plain.length; i++) {
				cipher[i] = (byte)MainWindow.RealMod(plain[i] + key[MainWindow.RealMod(i,keyLength)], 256);
			}
		}  catch(Exception e){
			JOptionPane.showMessageDialog(null, "Please enter key as a list of integers separated by spaces.","Error",JOptionPane.ERROR_MESSAGE);
			cipher = null;
		}
		return cipher;
	}

	public static byte[] Decrypt(byte[] plain)
	{
		int i;
		byte[] cipher = new byte[plain.length];
		int keyLength;
		int[] key;
		String keyString = keyText.getText(); 
		keyLength = keyString.length();
		key = string2Bytes(keyString);  // Convert the input to an array of bytes.
		keyLength = key.length;
		for (i=0; i<plain.length; i++) {
			cipher[i] = (byte)MainWindow.RealMod(plain[i] + 256 - key[MainWindow.RealMod(i,keyLength)], 256);
		}
		return cipher;
	}

	public static String Encrypt(char[] plain)
	{
		String cipher = "";
		int length = plain.length;
		boolean computeQ = true;
		int response;

		//make alphabet table		
		char[] alpha = MainWindow.alphabet.getText().toCharArray();
		int n = alpha.length; // n is the length of the alphabet
		if (n==0){
			JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}

		// Check keyword for characters that are not in the alphabet.
		String keyString = keyText.getText(); 
		String cleankeyString = MainWindow.cleanText(keyString);
		if (keyString.equals(cleankeyString)){
			computeQ = true;
		}
		else{
			response = JOptionPane.showConfirmDialog(null, "There are characters in the keyword that do not appear in the alphabet.\nDo you want ECrypt to make the keyword conform to the alphabet?", "Clean Keyword?" , JOptionPane.YES_NO_OPTION );
			if (response == JOptionPane.YES_OPTION){
				keyString = cleankeyString;
				keyText.setText(keyString);
				computeQ = true;
			}
			else
				computeQ = false;
		}

		try {
			// Encrypt
			if (computeQ){
				char[] key = keyString.toCharArray();
				int kLength = key.length;

				int[] lookup = new int[256];
				for(int i=0; i<256; i++) //initialize to -1
					lookup[i] = -1;
				for(int i=0; i<n; i++) //makes lookup table used for encoding
				{
					char ch = alpha[i];
					int x = ch; //ascii value
					lookup[x] = i;
				}

				int j=0;
				for(int i=0; i<length; i++)
				{
					if(lookup[plain[i]]<0)
						continue;
					if(lookup[key[j%kLength]]<0)
					{
						i--;
						j++;
						continue;
					}
					cipher += alpha[(lookup[plain[i]]+lookup[key[j%kLength]])%n];
					j++;
				}
			}
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Please enter key a keyword.","Error",JOptionPane.ERROR_MESSAGE);
			cipher = null;
		}
		return cipher;
	}

	public static String Decrypt(char[] cipher)
	{
		String plain = "";
		int length = cipher.length;
		boolean computeQ = true;
		int response;

		//make alphabet table		
		char[] alpha = MainWindow.alphabet.getText().toCharArray();
		int n = alpha.length; // n is the length of the alphabet
		if (n==0){
			JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}

		// Check keyword for characters that are not in the alphabet.
		String keyString = keyText.getText(); 
		String cleankeyString = MainWindow.cleanText(keyString);
		if (keyString.equals(cleankeyString)){
			computeQ = true;
		}
		else{
			response = JOptionPane.showConfirmDialog(null, "There are characters in the keyword that do not appear in the alphabet.\nDo you want ECrypt to make the keyword conform to the alphabet?", "Clean Keyword?" , JOptionPane.YES_NO_OPTION );
			if (response == JOptionPane.YES_OPTION){
				keyString = cleankeyString;
				keyText.setText(keyString);
				computeQ = true;
			}
			else
				computeQ = false;
		}

		try {
			// Decrypt
			if (computeQ){
				char[] key = keyString.toCharArray();
				int kLength = key.length;

				int[] lookup = new int[256];
				for(int i=0; i<256; i++) //initialize to -1
					lookup[i] = -1;
				for(int i=0; i<n; i++) //makes lookup table used for encoding
				{
					char ch = alpha[i];
					int x = ch; //ascii value
					lookup[x] = i;
				}

				int j=0;
				for(int i=0; i<length; i++)
				{
					if(lookup[cipher[i]]<0)
						continue;
					if(lookup[key[j%kLength]]<0)
					{
						i--;
						j++;
						continue;
					}
					int x = lookup[cipher[i]]-lookup[key[j%kLength]];
					if(x<0)
						x+=n;
					plain += alpha[x%n];
					j++;
				}
			}
		} catch (Exception e){
			JOptionPane.showMessageDialog(null, "Please enter a keyword.","Error",JOptionPane.ERROR_MESSAGE);
			plain = null;
		}
		return plain;
	}


	static public int[] string2Bytes(String str){
		String[] tempArray = str.split(" ");
		int[] intArray = new int[tempArray.length];
		int i, counter = 0;
		for (i=0; i< tempArray.length; i++){
			//System.out.println(""+i+" ["+tempArray[i]+"]");
			//if (tempArray[i]!=null && tempArray[i]!="")
			try{
				intArray[counter] = MainWindow.RealMod(Integer.parseInt(tempArray[i]),256);
				counter++;
			} catch (NumberFormatException e){
				//System.out.println("Number format exception");
			}
		}
		int[] retArray = new int[counter];
		for (i=0; i<counter; i++)
			retArray[i] = intArray[i];
		return retArray;
	}

	public class VigenereActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}
}


