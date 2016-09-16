import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainWindow extends JFrame {	
	JLabel alphabetLabel= new JLabel("Alphabet");
	JPanel alphabetPanel = new JPanel();
	JPanel controlPanel = new JPanel();
	//	public static byte[] gimg = null;

	static JTextArea alphabet = new JTextArea(50,1);
	private JScrollPane alphaPane = new JScrollPane(alphabet);

	JButton EncryptButton = new JButton("Encrypt");
	JButton DecryptButton = new JButton("Decrypt");

	TextDisplayWindow plaintextWindow = new TextDisplayWindow("Plaintext",245);
	TextDisplayWindow ciphertextWindow = new TextDisplayWindow("Ciphertext",545);
	ImageDisplayWindow plainimageWindow = new ImageDisplayWindow("Plain Image",245);
	ImageDisplayWindow cipherimageWindow = new ImageDisplayWindow("Cipher Image",585);
	SoundDisplayWindow plainsoundWindow = new SoundDisplayWindow("Plain Sound",245);
	SoundDisplayWindow ciphersoundWindow = new SoundDisplayWindow("Cipher Sound",585);
	AffineCipher affineCipher = new AffineCipher();
	VigenereCipher vigenereCipher = new VigenereCipher();
	KeywordCipher keywordCipher = new KeywordCipher();
	HillCipher hillCipher = new HillCipher();
	PolybiusCheckerboard polybiusWindow = new PolybiusCheckerboard();
	WheatstonePlayfair wpWindow = new WheatstonePlayfair();
	RecursiveCalculator recursiveCalculator = new RecursiveCalculator();
	JMenuItem RCmenuItem; // = new JMenuItem("Recursive");
	ModularCalculator modularCalculator = new ModularCalculator();
	JMenuItem MCmenuItem;

	boolean lowercaseOn;
	boolean uppercaseOn;
	boolean digitsOn;
	boolean punctuationOn;

	public enum Ciphers {Plain, Affine, Vigenere, Keyword, Hill, Polybius, WheatstonePlayfair};
	public Ciphers cipher = Ciphers.Plain;
	public enum Modes {Text, Image, Sound};
	public static Modes mode = Modes.Text;
	public static Icon ECryptIcon = new Icon();

	// Constructor
	public MainWindow()
	{
//		In Eclipse, you can set the icon with the following (assuming ECryptIcon.png is in the right place):
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
// 		In a jar file, you have to make a URL first.  See introcs.cs.princeton.edu/java/85application/jar/jar.html
		setIconImage(ECryptIcon.getImage());
		setResizable(false);

		Font font = new Font("Courier", Font.PLAIN, 12);
		alphabet.setFont(font);
		lowercaseOn = true;
		uppercaseOn = false;
		digitsOn = false;
		punctuationOn = false;

		setSize(570,140);
		setLocation(205,100);
		setTitle("ECrypt");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		plaintextWindow.text.setText("");
		EncryptButton.addActionListener(new EncryptButtonListener());
		DecryptButton.addActionListener(new DecryptButtonListener());

		//CREATE THE MENU BAR
		final JMenuItem keywordItem = new JMenuItem("Keyword");
		final JMenuItem wheatstoneItem = new JMenuItem("Wheatstone-Playfair");
		final JMenuItem polybiusItem = new JMenuItem("Polybius Checkerboard");
		final JMenu alphabetMenu = new JMenu("Alphabet");

		// create the help menu
		JMenu helpMenu = new JMenu("Help");
		JMenuItem aboutItem = new JMenuItem("About");
		aboutItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				JOptionPane.showMessageDialog(null, "ECrypt written at Elizabethtown College by: \n Kathryn Howser (v2) 2012 \n Steve Bicker (v1) 2009\n Amanda Schock (v1) 2008\n Bob McDevitt (v0) 2006\n Tom Leap\n Tim McDevitt","About ECrypt" +
						"",JOptionPane.INFORMATION_MESSAGE,new ImageIcon(ECryptIcon.getImage()));

			}
		});
		helpMenu.add(aboutItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { System.exit(0); }
		});
		helpMenu.add(exitItem);

		// create the Mode menu
		JMenu modeMenu = new JMenu("Mode");
		modeMenu.setMnemonic(KeyEvent.VK_M);

		JMenuItem textItem = new JMenuItem("Text");
		textItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) {
				mode = Modes.Text;
				plainimageWindow.setVisible(false);
				cipherimageWindow.setVisible(false);
				plainsoundWindow.setVisible(false);
				ciphersoundWindow.setVisible(false);
				plaintextWindow.setVisible(true);
				ciphertextWindow.setVisible(true);
				alphaPane.setVisible(true);
				alphabetLabel.setVisible(true);
				keywordItem.setEnabled(true);
				wheatstoneItem.setEnabled(true);
				polybiusItem.setEnabled(true);
				alphabetMenu.setEnabled(true);
			}
		});
		modeMenu.add(textItem);

		//modeMenu.add(new JMenuItem("Binary"));   STILL TO DO

		JMenuItem imageItem = new JMenuItem("Image");
		modeMenu.add(imageItem);
		imageItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) {
				mode = Modes.Image;
				plainimageWindow.setVisible(true);
				cipherimageWindow.setVisible(true);
				plainsoundWindow.setVisible(false);
				ciphersoundWindow.setVisible(false);
				plaintextWindow.setVisible(false);
				ciphertextWindow.setVisible(false);
				alphaPane.setVisible(false);
				alphabetLabel.setVisible(false);
				keywordItem.setEnabled(false);
				wheatstoneItem.setEnabled(false);
				polybiusItem.setEnabled(false);
				alphabetMenu.setEnabled(false); 
			}
		});

		JMenuItem waveItem = new JMenuItem("Sound");
		waveItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) {
				mode = Modes.Sound;
				plainimageWindow.setVisible(false);
				cipherimageWindow.setVisible(false);
				plainsoundWindow.setVisible(true);
				ciphersoundWindow.setVisible(true);
				plaintextWindow.setVisible(false);
				ciphertextWindow.setVisible(false);
				alphaPane.setVisible(false);
				alphabetLabel.setVisible(false);
				keywordItem.setEnabled(false);
				wheatstoneItem.setEnabled(false);
				polybiusItem.setEnabled(false);
				alphabetMenu.setEnabled(false);
			}
		});
		modeMenu.add(waveItem);          

		textItem.doClick(); //Initialize Mode to Text

		// create the View menu
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic(KeyEvent.VK_V);
		JMenuItem viewPlain = new JMenuItem("Plain");
		JMenuItem viewCipher = new JMenuItem("Cipher");
		viewMenu.add(viewPlain);
		viewMenu.add(viewCipher);
		viewPlain.addActionListener(new ViewPlainButtonListener());
		viewCipher.addActionListener(new ViewCipherButtonListener());
		//		viewPlain.addActionListener(plaintextWindow.buttonListener);
		//		viewCipher.addActionListener(ciphertextWindow.buttonListener);

		// create the Encryption Algorithms menu
		JMenu eAlgorithmsMenu = new JMenu("Encryption Algorithms");
		eAlgorithmsMenu.setMnemonic(KeyEvent.VK_E);

		// Set Affine Properties
		JMenuItem affineItem = new JMenuItem("Affine");
		affineItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				cipher = Ciphers.Affine;
				affineCipher.setVisible(true);
				vigenereCipher.setVisible(false);
				keywordCipher.setVisible(false);
				hillCipher.setVisible(false); 
				wpWindow.setMatrixVisible(false);
				polybiusWindow.setMatrixVisible(false);
				alphabet.setEditable(true);
			}
		});
		eAlgorithmsMenu.add(affineItem);

		// Set Vigenere Properties
		JMenuItem vigenereItem = new JMenuItem("Vigenere");
		vigenereItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				cipher = Ciphers.Vigenere;
				affineCipher.setVisible(false);
				vigenereCipher.setVisible(true);
				keywordCipher.setVisible(false);
				hillCipher.setVisible(false); 
				wpWindow.setMatrixVisible(false);
				polybiusWindow.setMatrixVisible(false);
				alphabet.setEditable(true);
			}
		});
		eAlgorithmsMenu.add(vigenereItem);

		// Set Hill Properties
		JMenuItem hillItem = new JMenuItem("Hill");
		hillItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				cipher = Ciphers.Hill;
				affineCipher.setVisible(false);
				vigenereCipher.setVisible(false);
				keywordCipher.setVisible(false);
				hillCipher.setVisible(true); 
				wpWindow.setMatrixVisible(false);
				polybiusWindow.setMatrixVisible(false);
				alphabet.setEditable(true);
			}
		});
		eAlgorithmsMenu.add(hillItem);

		// Set Keyword Properties
		//JMenuItem keywordItem = new JMenuItem("Keyword");
		keywordItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				cipher = Ciphers.Keyword;
				affineCipher.setVisible(false);
				vigenereCipher.setVisible(false);
				keywordCipher.setVisible(true);
				hillCipher.setVisible(false); 
				wpWindow.setMatrixVisible(false);
				polybiusWindow.setMatrixVisible(false);
				alphabet.setEditable(true);
			}
		});
		eAlgorithmsMenu.add(keywordItem);

		// Set Wheatstone-Playfair Properties
		//JMenuItem wheatstoneItem = new JMenuItem("Wheatstone-Playfair");
		wheatstoneItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				cipher = Ciphers.WheatstonePlayfair;
				affineCipher.setVisible(false);
				vigenereCipher.setVisible(false);
				keywordCipher.setVisible(false);
				hillCipher.setVisible(false); 
				wpWindow.setMatrixVisible(true);
				polybiusWindow.setMatrixVisible(false); 
				alphabet.setEditable(false);	// Freeze the alphabet.
				alphabet.setText("abcdefghijklmnopqrstuvwxy");
				JOptionPane.showMessageDialog(null, "The alphabet is set to abcdefghijklmnopqrstuvwxy (no z)\nfor the Wheatstone-Playfair cipher.","Warning",JOptionPane.WARNING_MESSAGE);
			}
		});
		eAlgorithmsMenu.add(wheatstoneItem);

		// Set Polybius Checkerboard Properties
		//JMenuItem polybiusItem = new JMenuItem("Polybius Checkerboard");
		polybiusItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) { 
				cipher = Ciphers.Polybius;
				affineCipher.setVisible(false);
				vigenereCipher.setVisible(false);
				keywordCipher.setVisible(false);
				hillCipher.setVisible(false); 
				wpWindow.setMatrixVisible(false);
				polybiusWindow.setMatrixVisible(true);
				alphabet.setEditable(false);	// Freeze the alphabet.
				alphabet.setText("abcdefghijklmnopqrstuvwxy");
				JOptionPane.showMessageDialog(null, "The alphabet is set to abcdefghijklmnopqrstuvwxy (no z)\nfor the Polybius checkerboard cipher.","Warning",JOptionPane.WARNING_MESSAGE);
			}
		});
		eAlgorithmsMenu.add(polybiusItem);

		// create the Alphabet menu
		//JMenu alphabetMenu = new JMenu("Alphabet");
		alphabetMenu.setMnemonic(KeyEvent.VK_A);
		final JCheckBoxMenuItem lCaseItem = new JCheckBoxMenuItem("Lower Case Letters");
		final JCheckBoxMenuItem cCaseItem = new JCheckBoxMenuItem("Capital Letters");
		final JCheckBoxMenuItem dCaseItem = new JCheckBoxMenuItem("Digits");
		final JCheckBoxMenuItem pCaseItem = new JCheckBoxMenuItem("Puncuation");

		lCaseItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) 
			{ 
				String alpha = alphabet.getText();
				String newchars = "abcdefghijklmnopqrstuvwxyz";
				lowercaseOn = !lowercaseOn;
				//setAlpha(lCaseItem,cCaseItem,dCaseItem,pCaseItem);
				if (lowercaseOn){
					alpha += newchars;  // add lowercase letters to the alphabet
				}
				else{
					for(int i=0; i<newchars.length(); i++){
						alpha = alpha.replaceAll(""+newchars.charAt(i), "");
					}
				}
				alphabet.setText(alpha);
			}
		});
		alphabetMenu.add(lCaseItem);

		cCaseItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) 
			{ 
				String alpha = alphabet.getText();
				String newchars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
				uppercaseOn = !uppercaseOn;
				//setAlpha(lCaseItem,cCaseItem,dCaseItem,pCaseItem);
				if (uppercaseOn){
					alpha += newchars;
				}
				else{
					for(int i=0; i<newchars.length(); i++){
						alpha = alpha.replaceAll(""+newchars.charAt(i), "");
					}
				}
				alphabet.setText(alpha);
			}
		});
		alphabetMenu.add(cCaseItem);

		dCaseItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) 
			{ 
				String alpha = alphabet.getText();
				String newchars = "0123456789";
				digitsOn = !digitsOn;
				//setAlpha(lCaseItem,cCaseItem,dCaseItem,pCaseItem);
				if (digitsOn){
					alpha += newchars;
				}
				else{
					for(int i=0; i<newchars.length(); i++){
						alpha = alpha.replaceAll(""+newchars.charAt(i), "");
					}
				}
				alphabet.setText(alpha);
			}
		});
		alphabetMenu.add(dCaseItem);

		pCaseItem.addActionListener(new ActionListener(  ) {
			public void actionPerformed(ActionEvent e) 
			{ 
				String alpha = alphabet.getText();
				String newchars = " ()-,?[].;':";  
				punctuationOn = !punctuationOn;
				//setAlpha(lCaseItem,cCaseItem,dCaseItem,pCaseItem);
				if (punctuationOn){
					alpha += newchars;
				}
				else{
					for(int i=0; i<newchars.length(); i++){
						alpha = alpha.replaceAll("\\Q"+newchars.charAt(i)+"\\E", "");   
					}
				}
				alphabet.setText(alpha);
			}
		});
		alphabetMenu.add(pCaseItem);
		alphabet.setText("abcdefghijklmnopqrstuvwxyz");	// Sets lowercase letters as the default alphabet.
		//lCaseItem.doClick(); 	
		lCaseItem.setState(true);						// Toggles lowercase choice in menu.

		// create the Calculators menu
		JMenu calculatorsMenu = new JMenu("Calculators");
		calculatorsMenu.setMnemonic(KeyEvent.VK_C);
		//calculatorsMenu.add(new JMenuItem("Modular"));
		RCmenuItem = new JMenuItem("Recursive");
		RCmenuItem.addActionListener(recursiveCalculator.actionListener);
		calculatorsMenu.add(RCmenuItem);
		MCmenuItem = new JMenuItem("Modular");
		MCmenuItem.addActionListener(modularCalculator.actionListener);
		calculatorsMenu.add(MCmenuItem);


		// create a menu bar and use it in this JFrame
		JMenuBar menuBar = new JMenuBar(  );
		menuBar.add(modeMenu);
		menuBar.add(viewMenu);
		menuBar.add(eAlgorithmsMenu);
		menuBar.add(alphabetMenu);
		menuBar.add(calculatorsMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
		//Menu Bar Created

		//New Grid Layout:
		//setLayout(new GridLayout(4,1));
		setLayout(new BorderLayout());
		alphabetPanel.add(alphabetLabel);
		//alphabetPanel.add(alphabet);
		add(BorderLayout.NORTH, alphabetPanel);
		add(alphaPane);
		controlPanel.add(EncryptButton);
		controlPanel.add(DecryptButton);
		add(BorderLayout.SOUTH, controlPanel);
		setVisible(true);
		plaintextWindow.setVisible(true);
		ciphertextWindow.setVisible(true);
	}

	protected String readfile() 
	{
		final JFileChooser fc = new JFileChooser();
		final FileFilter filter = new FileNameExtensionFilter("Text Files", "txt");
		String text="";

		fc.addChoosableFileFilter(filter);
		int returnVal = fc.showOpenDialog(MainWindow.this);
		File file = fc.getSelectedFile();

		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
				String line;
				while((line = reader.readLine())!= null)
				{
					text+=line;
					text+='\n';
				}
				reader.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error reading the selected file, " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
		else
			JOptionPane.showMessageDialog(null, "Error locating the selected file, " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
		return text;
	}

	protected void savefile(String text) {
		final JFileChooser fc = new JFileChooser();
		final FileFilter filter = new FileNameExtensionFilter("Text Files", "txt");

		fc.addChoosableFileFilter(filter);
		int returnVal = fc.showSaveDialog(MainWindow.this);
		File file = fc.getSelectedFile();

		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()+".txt"));
				writer.write(text);
				writer.close();
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "Error writing to the selected file, " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			}
		}
	}

	private class EncryptButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String data=null, cleandata;
			boolean computeQ = true;
			int response;
			byte[] imgData = null;
			byte[] sndData = null;

			switch (mode) {
			case Text:
			{
				removeRepeatsFromAlphabet();
				data = plaintextWindow.text.getText();
				cleandata = cleanText(data);
				if (data.equals(cleandata)){
					computeQ = true;
				}
				else{
					response = JOptionPane.showConfirmDialog(null, "There are characters in the plaintext that do not appear in the alphabet.\nDo you want ECrypt to make the plaintext conform to the alphabet?", "Clean Plaintext?" , JOptionPane.YES_NO_OPTION );
					if (response == JOptionPane.YES_OPTION){
						data = cleandata;
						plaintextWindow.text.setText(data);
						computeQ = true;
					}
					else
						computeQ = false;
				}
			}
			break;
			case Image:
				imgData = plainimageWindow.getData();
				break;
			case Sound:
				sndData = plainsoundWindow.getData();
				break;
			}
			if (computeQ){
				switch (cipher){
				case Affine:		// ***** Affine Cipher
					switch (mode) {
					case Text:
						ciphertextWindow.text.setText(AffineCipher.Encrypt(data));
						break;
					case Image:
						cipherimageWindow.setData(plainimageWindow.img,AffineCipher.Encrypt(imgData));
						cipherimageWindow.setVisible(false);
						cipherimageWindow.setVisible(true);
						break;
					case Sound:
						ciphersoundWindow.setData(AffineCipher.Encrypt(sndData));
						ciphersoundWindow.setFormat(plainsoundWindow.getFormat());
						ciphersoundWindow.setVisible(false);
						ciphersoundWindow.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher not yet implemented in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Vigenere:		// ***** Vigenere Cipher
					switch (mode) {
					case Text:
						ciphertextWindow.text.setText(VigenereCipher.Encrypt(data.toCharArray()));
						break;
					case Image:
						cipherimageWindow.setData(plainimageWindow.img,VigenereCipher.Encrypt(imgData));
						cipherimageWindow.setVisible(false);
						cipherimageWindow.setVisible(true);
						break;
					case Sound:
						ciphersoundWindow.setData(VigenereCipher.Encrypt(sndData));
						ciphersoundWindow.setFormat(plainsoundWindow.getFormat());
						ciphersoundWindow.setVisible(false);
						ciphersoundWindow.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher not yet implemented in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Keyword:
					switch (mode) {
					case Text:
						ciphertextWindow.text.setText(KeywordCipher.Encrypt(data.toCharArray()));
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher does not apply in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Hill:
					switch (mode) {
					case Text:
						ciphertextWindow.text.setText(HillCipher.Encrypt(plaintextWindow.text.getText()));
					//	ciphertextWindow.text.setText(HillCipher.Encrypt(data));
						break;
					case Image:
						cipherimageWindow.setData(plainimageWindow.img,HillCipher.Encrypt(imgData));
						cipherimageWindow.setVisible(false); 
						cipherimageWindow.setVisible(true);
						break;
					case Sound:
						ciphersoundWindow.setData(HillCipher.Encrypt(sndData));
						ciphersoundWindow.setFormat(plainsoundWindow.getFormat());
						ciphersoundWindow.setVisible(false);
						ciphersoundWindow.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher not yet implemented in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case WheatstonePlayfair:
					switch (mode) {
					case Text:
						ciphertextWindow.text.setText(WheatstonePlayfair.Encrypt(data));
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher does not apply in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Polybius:
					switch (mode) {
					case Text:
						ciphertextWindow.text.setText(PolybiusCheckerboard.Encrypt(data));
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher does not apply in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				default:
					JOptionPane.showMessageDialog(null, "Please choose an encryption algorithm.");
				}
			}
			ciphertextWindow.text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
		}
	}

	private class DecryptButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String data=null, cleandata;
			boolean computeQ=true;
			int response;
			byte [] imgData = null;
			byte[] sndData = null;

			switch (mode) {
			case Text:
				removeRepeatsFromAlphabet();
				data = ciphertextWindow.text.getText();

				// Clean the data, but do it differently for the Polybius checkerboard.
				if (cipher==Ciphers.Polybius){
					cleandata = cleanTextPolybius(data);
				}
				else{
					cleandata = cleanText(data);
				}
				if (data.equals(cleandata)){
					computeQ = true;
				}
				else{
					if (cipher==Ciphers.Polybius){
						response = JOptionPane.showConfirmDialog(null, "There are characters in the ciphertext other than the digits 1-5.\nDo you want ECrypt to eliminate the inappropriate characters?", "Clean Ciphertext?" , JOptionPane.YES_NO_OPTION );
					}
					else{
						response = JOptionPane.showConfirmDialog(null, "There are characters in the ciphertext that do not appear in the alphabet.\nDo you want ECrypt to make the ciphertext conform to the alphabet?", "Clean Ciphertext?" , JOptionPane.YES_NO_OPTION );
					}
					if (response == JOptionPane.YES_OPTION){
						data = cleandata;
						ciphertextWindow.text.setText(data);
						computeQ = true;
					}
					else
						computeQ = false;
				}
				break;
			case Image:
				imgData = cipherimageWindow.getData();
				break;
			case Sound:
				sndData = ciphersoundWindow.getData();
				break;
			}


			if (computeQ){		
				switch (cipher){
				case Affine:		
					switch (mode) {
					case Text:
						plaintextWindow.text.setText(AffineCipher.Decrypt(data));
						break;
					case Image:
						plainimageWindow.setData(cipherimageWindow.img,AffineCipher.Decrypt(imgData));
						plainimageWindow.setVisible(false);
						plainimageWindow.setVisible(true);
						break;
					case Sound:
						plainsoundWindow.setData(AffineCipher.Decrypt(sndData));
						plainsoundWindow.setFormat(ciphersoundWindow.getFormat());
						plainsoundWindow.setVisible(false);
						plainsoundWindow.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher not yet implemented in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Vigenere:
					switch (mode) {
					case Text:				
						plaintextWindow.text.setText(VigenereCipher.Decrypt(data.toCharArray()));
						break;
					case Image:
						plainimageWindow.setData(cipherimageWindow.img,VigenereCipher.Decrypt(imgData));
						plainimageWindow.setVisible(false);
						plainimageWindow.setVisible(true);
						break;
					case Sound:
						plainsoundWindow.setData(VigenereCipher.Decrypt(sndData));
						plainsoundWindow.setFormat(ciphersoundWindow.getFormat());
						plainsoundWindow.setVisible(false);
						plainsoundWindow.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher not yet implemented in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Hill:
					switch (mode) {
					case Text:
						plaintextWindow.text.setText(HillCipher.Decrypt(data));
						break;
					case Image:
						plainimageWindow.setData(cipherimageWindow.img,HillCipher.Decrypt(imgData));
						plainimageWindow.setVisible(false);
						plainimageWindow.setVisible(true);
						break;
					case Sound:
						plainsoundWindow.setData(HillCipher.Decrypt(sndData));
						plainsoundWindow.setFormat(ciphersoundWindow.getFormat());
						plainsoundWindow.setVisible(false);
						plainsoundWindow.setVisible(true);
						break;
					default:
						JOptionPane.showMessageDialog(null, "Cipher not yet implemented in this mode.","Warning",JOptionPane.WARNING_MESSAGE);
						break;
					}
					break;
				case Keyword:
					plaintextWindow.text.setText(KeywordCipher.Decrypt(data.toCharArray()));
					break;
				case WheatstonePlayfair:
					plaintextWindow.text.setText(WheatstonePlayfair.Decrypt(data));
					break;
				case Polybius:
					plaintextWindow.text.setText(PolybiusCheckerboard.Decrypt(data));
					break;
				default:
					JOptionPane.showMessageDialog(null, "Please choose an encryption algorithm.");
				}
			}
			plaintextWindow.text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
		}
	}

	// Other Functions      

	public static void removeRepeatsFromAlphabet(){
		String alphaText;
		String out = "";
		char ch;
		boolean warnQ = false;
		boolean dropQ;
		int i, k;

		alphaText = MainWindow.alphabet.getText();
		for (i=alphaText.length()-1; i>=0; i=i-1){
			dropQ = false;
			ch = alphaText.charAt(i);
			for (k=0; k<i; k++){
				if (ch == alphaText.charAt(k)){
					dropQ = true;
					warnQ = true;
					continue;
				}
			}
			if (!dropQ){
				out = ch + out;
			}
		}
		if (warnQ){
			JOptionPane.showMessageDialog(null, "Removing repeated characters from the alphabet.","Warning",JOptionPane.WARNING_MESSAGE);
		}
		alphabet.setText(out);
	}

	public static String cleanTextPolybius(String data){
		String out = "";
		int i;
		char ch;
		String chS;

		for (i=0; i<data.length(); i++){
			ch = data.charAt(i);
			chS = "" + ch;
			if (chS.equals("1") || chS.equals("2") || chS.equals("3") || chS.equals("4") || chS.equals("5")){
				out += chS;
			}
		}
		return out;
	}

	public static String cleanText(String data){
		String out = "";
		String alphaText;
		int i, j, index;
		char ch;
		String chs;

		alphaText = MainWindow.alphabet.getText();
		j = 0;
		for (i=0; i<data.length(); i++){
			ch = data.charAt(i);
			index = alphaText.indexOf(ch);		// Check to see if ch is in the alphabet.
			if (index>=0){
				out+=ch;
				j++;
				continue;
			}
			if (index<0){						// index=-1 if ch is not in alphaText
				//			warnQ = true;
				chs = ""+ch;					// convert ch to a string so we can use toLowerCase
				chs = chs.toLowerCase();
				index = alphaText.indexOf(chs);  // check to see if the lower case version of ch is in the alphabet
				if (index>=0){
					out+=chs;
					j++;
					continue;
				}
				//				chs = ""+ch;
				chs = chs.toUpperCase();
				index = alphaText.indexOf(chs);  // check to see if the upper case version of ch is in the alphabet
				if (index>=0){
					out+=chs;
					j++;
					continue;
				}
			}	
		}
		return out;
	}

	private class ViewPlainButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e){
			switch (mode) {
			case Text:
				plaintextWindow.setVisible(true);
				break;
			case Image:
				plainimageWindow.setVisible(true);
				break;
			case Sound:
				plainsoundWindow.setVisible(true);
				break;
			}
		}
	}

	private class ViewCipherButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e){
			switch (mode) {
			case Text:
				ciphertextWindow.setVisible(true);
				break;
			case Image:
				cipherimageWindow.setVisible(true);
				break;
			case Sound:
				ciphersoundWindow.setVisible(true);
				break;
			}
		}
	}

	public static int RealMod(int num, int modval) 
	{
		return ((num % modval) + modval) % modval;
	}

	public static int inversemod(int base, int modn) 
	{
		int b, quotient;
		int a = modn;
		int veca[] = new int[2];
		int vecb[] = new int[2];
		//veca = [1,0] vecb = [0,1]
		veca[0]=vecb[1]=1; 
		veca[1]=vecb[0]=0;

//		base = base % modn; 
		base = RealMod(base,modn);
		b= base;

		if(b == 0)
			//can't use return something else
			return -999;

		while (true)
		{
			quotient = a/b;
			a = a%b;
			veca[0] -= (quotient * vecb[0]);
			veca[1] -= (quotient * vecb[1]);

			if(a == 0)
				return RealMod(vecb[1], modn);

			quotient = b/a;
			b = b%a;
			vecb[0] -= (quotient * veca[0]);
			vecb[1] -= (quotient * veca[1]);

			if(b == 0)
				return RealMod(veca[1], modn);
		}
	}

	// There is already a gcd method in the BigInteger class.
	// There is also a modInverse method and a modPow method.
	// See http://docs.oracle.com/javase/1.5.0/docs/api/java/math/BigInteger.html.
	/*	static public BigInteger powermod2(BigInteger base, BigInteger power, BigInteger modulus)
	{
		BigInteger y = new BigInteger("1");
		BigInteger p = base;
		BigInteger n = power;
		BigInteger zero = new BigInteger("0");
		BigInteger one = new BigInteger("1");
		BigInteger two = new BigInteger("2");

		while (n!=zero)
		{
			if ( (n.mod(two))==one ) y = (y.multiply(p)).mod(modulus);
			n = n.divide(two);
			p = (p.multiply(p)).mod(modulus);
		}
		System.out.println("Big one");
		System.out.println(y);
		return y;
	}

	static public int powermod(int base, int power, int modulus)
	{
		int y = 1;
		int p = base;
		int n = power;

		while (n!=0)
		{
			if ( (n%2)==1 ) y = (y*p) % modulus;
			n = n/2;
			p = (p*p) % modulus;
		}
		System.out.println(y);
		return y;
	}
	 */
	static public int gcd(int a, int n) 
	{
		if(a < n)
		{
			int t = a;
			a = n;
			n = t; 
		}		
		if(a%n==0)
			return n;
		else 
			return gcd(n, a%n);
	}

	public static boolean isInt(String numbers)
	{
		for(int i=0; i<numbers.length(); i++)
		{
			String x = String.valueOf(numbers.charAt(i));
			if(!x.contentEquals("0")&&!x.contentEquals("1")&&!x.contentEquals("2")&&!x.contentEquals("3")&&!x.contentEquals("4")&&!x.contentEquals("5")&&!x.contentEquals("6")&&!x.contentEquals("7")&&!x.contentEquals("8")&&!x.contentEquals("9"))	
				return false;
		}
		return true;
	}

	public static boolean checkKey(char[] key, char[] alpha) {
		// check to see if all key values are in the alphabet
		boolean match;
		for(int i=0; i<key.length; i++)
		{
			match = false;
			for(int j=0; j<alpha.length; j++)
			{
				if(key[i]==alpha[j])
					match = true;
			}
			if(match == false)
				return false;
		}
		return true;
	}
}
