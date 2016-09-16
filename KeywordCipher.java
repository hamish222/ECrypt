import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class KeywordCipher extends JFrame{
	JPanel inputPanel = new JPanel();
	JLabel inputWordLabel = new JLabel("Keyword = ");
	JLabel inputLetterLabel = new JLabel("Keyletter = ");
	static JTextField keyWordText = new JTextField(12);
	static JTextField keyLetterText = new JTextField(3);

	Font font = new Font("Courier", Font.PLAIN, 12);

	// Constructor
	public KeywordCipher(){
		setResizable(false);
		setSize(320,80);
		setTitle("Keyword Cipher");
		setLocation(785,100);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		inputPanel.add(inputWordLabel);
		inputPanel.add(keyWordText);
		inputPanel.add(inputLetterLabel);
		inputPanel.add(keyLetterText);
		keyWordText.setText("");
		keyWordText.setFont(font);
		keyLetterText.setText("");
		keyLetterText.setFont(font);
		add(inputPanel);

		setVisible(false);
	}

	public static String Encrypt(char[] plain)
	{
		String cipher = "";
		//	int length = plain.length;
		int response;

		// Make alphabet table.		
		String alphaText = MainWindow.alphabet.getText();
		int n = alphaText.length(); // n is the length of the alphabet
		if (n==0){
			JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}

		// Check keyword for characters that are not in the alphabet.
		String keyWordString = keyWordText.getText(); 
		String cleankeyWordString = MainWindow.cleanText(keyWordString);
		if (keyWordString.equals(cleankeyWordString)){
		}
		else{
			response = JOptionPane.showConfirmDialog(null, "There are characters in the keyword that do not appear in the alphabet.\nDo you want ECrypt to make the keyword conform to the alphabet?", "Clean Keyword?" , JOptionPane.YES_NO_OPTION );
			if (response == JOptionPane.YES_OPTION){
				keyWordString = cleankeyWordString;
				keyWordText.setText(keyWordString);
			}
			else
				return "";
		}

		// Check keyletter for characters that are not in the alphabet.
		String keyLetterString = keyLetterText.getText(); 
		String cleankeyLetterString = MainWindow.cleanText(keyLetterString);
		if (keyLetterString.equals(cleankeyLetterString)){
		}
		else{
			response = JOptionPane.showConfirmDialog(null, "There are characters in the keyletter that do not appear in the alphabet.\nDo you want ECrypt to make the keyletter conform to the alphabet?", "Clean Keyletter?" , JOptionPane.YES_NO_OPTION );
			if (response == JOptionPane.YES_OPTION){
				keyLetterString = cleankeyLetterString;
				keyLetterText.setText(keyLetterString);
			}
			else
				return "";
		}

		// Check that the keyletter is a single letter in the alphabet.
		if (keyLetterString.length()>1){
			JOptionPane.showMessageDialog(null, "The key letter can only consist of one character.", "Error" , JOptionPane.ERROR_MESSAGE);
			return "";
		}

		// Remove repeated characters from the keyword.
		String temp = "";
		char ch;
		int i, k;
		boolean warnQ = false;
		boolean dropQ;
		for (i=0; i<keyWordString.length(); i++){
			dropQ = false;
			ch = keyWordString.charAt(i);
			for (k=0; k<i; k++){
				if (ch == keyWordString.charAt(k)){
					dropQ = true;
					warnQ = true;
					continue;
				}
			}
			if (!dropQ){
				temp = temp + ch;
			}
		}
		if (warnQ){
			JOptionPane.showMessageDialog(null, "Removing repeated characters from the keyword.","Warning",JOptionPane.WARNING_MESSAGE);
		}
		keyWordString = temp;
		keyWordText.setText(keyWordString);
		//System.out.println("New keyword:" + keyWordString);

		// Verify that the keyword isn't empty.
		if (keyWordString.length()==0){
			JOptionPane.showMessageDialog(null, "Please enter a keyword.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Verify that the keyletter isn't empty.
		if (keyLetterString.length()==0){
			JOptionPane.showMessageDialog(null, "Please enter a key letter.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		//MainWindow.keyLetter.setText(MainWindow.keyLetter.getText().substring(0,1));

		char[] key = keyWordString.toCharArray();
		char keyLet = keyLetterString.charAt(0);
		int index = alphaText.indexOf(keyLet); //find index of the key letter

		//make array for substitution
		char[] newAlpha = new char[n];
		for(i=0; i<keyWordString.length(); i++)
		{
			if(String.valueOf(newAlpha).indexOf(key[i])==-1) //if newAlpha doesn't contain key[i] add it
				newAlpha[(index++)%n]=key[i];
		}
		for(int j=0; j<alphaText.length(); j++)
		{ 
			if(String.valueOf(key).indexOf(alphaText.charAt(j))!=-1)
				continue;
			newAlpha[index++%n]=alphaText.charAt(j);
		}

		// Encrypt
		for(int x=0; x<plain.length; x++)
		{
			if(String.valueOf(alphaText).indexOf(plain[x])==-1)
				continue;
			int alphaIndex = alphaText.indexOf(plain[x]);
			cipher += newAlpha[alphaIndex];
		}	
		return cipher;
	}

	public static String Decrypt(char[] cipher)
	{
		String plain = "";
		int response;

		//make alphabet table		
		String alphaText = MainWindow.alphabet.getText();
		int n = alphaText.length(); // n is the length of the alphabet
		if (n==0){
			JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}

		// Check keyword for characters that are not in the alphabet.
		String keyWordString = keyWordText.getText(); 
		String cleankeyWordString = MainWindow.cleanText(keyWordString);
		if (keyWordString.equals(cleankeyWordString)){
		}
		else{
			response = JOptionPane.showConfirmDialog(null, "There are characters in the keyword that do not appear in the alphabet.\nDo you want ECrypt to make the keyword conform to the alphabet?", "Clean Keyword?" , JOptionPane.YES_NO_OPTION );
			if (response == JOptionPane.YES_OPTION){
				keyWordString = cleankeyWordString;
				keyWordText.setText(keyWordString);
			}
			else
				return "";
		}


		// Check keyletter for characters that are not in the alphabet.
		String keyLetterString = keyLetterText.getText(); 
		String cleankeyLetterString = MainWindow.cleanText(keyLetterString);
		if (keyLetterString.equals(cleankeyLetterString)){
		}
		else{
			response = JOptionPane.showConfirmDialog(null, "There are characters in the keyletter that do not appear in the alphabet.\nDo you want ECrypt to make the keyletter conform to the alphabet?", "Clean Keyletter?" , JOptionPane.YES_NO_OPTION );
			if (response == JOptionPane.YES_OPTION){
				keyLetterString = cleankeyLetterString;
				keyLetterText.setText(keyLetterString);
			}
			else
				return "";
		}

		// Check that the keyletter is a single letter in the alphabet.
		if (keyLetterString.length()>1){
			JOptionPane.showMessageDialog(null, "The key letter can only consist of one character.", "Error" , JOptionPane.ERROR_MESSAGE);
			return "";
		}

		// Remove repeated characters from the keyword.
		String temp = "";
		char ch;
		int i, k;
		boolean warnQ = false;
		boolean dropQ;
		for (i=0; i<keyWordString.length(); i++){
			dropQ = false;
			ch = keyWordString.charAt(i);
			for (k=0; k<i; k++){
				if (ch == keyWordString.charAt(k)){
					dropQ = true;
					warnQ = true;
					continue;
				}
			}
			if (!dropQ){
				temp = temp + ch;
			}
		}
		if (warnQ){
			JOptionPane.showMessageDialog(null, "Removing repeated characters from the keyword.","Warning",JOptionPane.WARNING_MESSAGE);
		}
		keyWordString = temp;
		keyWordText.setText(keyWordString);

		// Verify that the keyword isn't empty.
		if (keyWordString.length()==0){
			JOptionPane.showMessageDialog(null, "Please enter a keyword.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Verify that the keyletter isn't empty.
		if (keyLetterString.length()==0){
			JOptionPane.showMessageDialog(null, "Please enter a key letter.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		
		char[] key = keyWordString.toCharArray();
		char keyLet = keyLetterString.charAt(0);
		int index = alphaText.indexOf(keyLet); //find index of the key letter

		//make array for substitution
		char[] newAlpha = new char[n];
		for(i=0; i<key.length; i++)
		{
			if(String.valueOf(newAlpha).indexOf(key[i])==-1) //if newAlpha doesn't contain key[i] add it
				newAlpha[(index++)%n]=key[i];
		}
		for(int j=0; j<alphaText.length(); j++)
		{ 
			if(String.valueOf(key).indexOf(alphaText.charAt(j))!=-1)
				continue;
			newAlpha[index++%n]=alphaText.charAt(j);
		}

		for(int x=0; x<cipher.length; x++)
		{
			int alphaIndex = String.valueOf(newAlpha).indexOf(cipher[x]);
			plain += alphaText.toCharArray()[alphaIndex];
		}	
		return plain;
	}

	public class KeywordActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}

}
