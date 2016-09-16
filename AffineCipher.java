import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class AffineCipher extends JFrame {
	static int a; // Multiplicative key
	static int b; // Additive key
	JPanel inputPanel = new JPanel();
	JLabel inputLabel1 = new JLabel("Multiplicative Key = ");
	JLabel inputLabel2 = new JLabel("Additive Key = ");
	static JTextField aText = new JTextField(5);
	static JTextField bText = new JTextField(5);

	public AffineActionListener actionListener = new AffineActionListener();

	Font font = new Font("Courier", Font.PLAIN, 12);

	// Constructor
	public AffineCipher(){
		setResizable(false);
		setSize(320,80);
		setTitle("Affine Cipher");
		setLocation(785,100);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		inputPanel.add(inputLabel1);
		inputPanel.add(aText);
		inputPanel.add(inputLabel2);
		inputPanel.add(bText);
		aText.setText("1");
		bText.setText("0");
		aText.setFont(font);
		bText.setFont(font);
		add(inputPanel);

		setVisible(false);
	}

	public static byte[] Encrypt(byte[] plain)
	{
		int i;
		byte[] cipher = new byte[plain.length];
		// Update a.
		try{
			a = Integer.parseInt(aText.getText()); // a is the multiplicative
			a = MainWindow.RealMod(a,256);
		}
		catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "The multiplicative key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			//return "";
		}
		// Confirm that a is relatively prime to the modulus.
		if (MainWindow.gcd(a,256)!=1){
			JOptionPane.showMessageDialog(null, "The multiplicative key must be relatively prime to 256.","Error",JOptionPane.ERROR_MESSAGE);
			//return "";
		}
		// Update b.
		try{
			b = Integer.parseInt(bText.getText()); // b is the additive
			b = MainWindow.RealMod(b,256);
		}
		catch (NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "The additive key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			//return "";
		} 
		for (i=0; i<plain.length; i++)
		{
			cipher[i] = (byte)(MainWindow.RealMod((((int)plain[i])&0x0FF)*a + b, 256));
		}
		return cipher;
	}
	
	public static byte[] Decrypt(byte[] cipher)
	{
		byte[] plain = new byte[cipher.length];
		int i;
		int ainv;

		// Update a.
		try{
			a = Integer.parseInt(aText.getText()); // a is the multiplicative
			a = MainWindow.RealMod(a,256);
		}
		catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "The multiplicative key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			//return "";
		}
		// Confirm that a is relatively prime to the modulus.
		if (MainWindow.gcd(a,256)!=1){
			JOptionPane.showMessageDialog(null, "The multiplicative key must be relatively prime to 256.","Error",JOptionPane.ERROR_MESSAGE);
			//return "";
		}
		// Update b.
		try{
			b = Integer.parseInt(bText.getText()); // b is the additive
			b = MainWindow.RealMod(b,256);
		}
		catch (NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "The additive key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			//return "";
		}
		ainv = MainWindow.inversemod(a,256);
		for (i=0; i<cipher.length; i++)
		{
			plain[i] = (byte)MainWindow.RealMod(((((int)cipher[i])&0x0FF)+256-b)*ainv, 256);
		}
		return plain;
	}
	
	public static String Encrypt(String plain)
	{
		String cipher = "";
		char ch;
		int i, j;
		// Update the alphabet and confirm that it isn't empty.
		String alphaText = MainWindow.alphabet.getText();
		int n = alphaText.length(); // n is the length of the alphabet
		if (n==0){
			JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Update a.
		try{
			a = Integer.parseInt(aText.getText()); // a is the multiplicative
			a = MainWindow.RealMod(a,n);
		}
		catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "The multiplicative key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Confirm that a is relatively prime to the modulus.
		if (MainWindow.gcd(a,n)!=1){
			JOptionPane.showMessageDialog(null, "The multiplicative key must be relatively prime to the modulus.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Update b.
		try{
			b = Integer.parseInt(bText.getText()); // b is the additive
			b = MainWindow.RealMod(b,n);
		}
		catch (NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "The additive key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		} 
		// Encrypt data.
		for(i=0 ; i<plain.length(); i++)  
		{
			ch = plain.charAt(i);
			j = alphaText.indexOf(ch);
			if(j<0) continue; // This should never happen because the text will be cleaned.
			j = (a*j+b) % n;
			ch = alphaText.charAt(j);
			cipher += ch;
		}
		return cipher;
	}

	public static String Decrypt(String cipher)
	{
		String plain = "";
		char ch;
		int i, j;
		int ainv;
		// Update the alphabet and confirm that it isn't empty.
		String alphaText = MainWindow.alphabet.getText();
		int n = alphaText.length(); // n is the length of the alphabet
		if (n==0){
			JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Update a.
		try{
			a = Integer.parseInt(aText.getText()); // a is the multiplicative
			a = MainWindow.RealMod(a,n);
		}
		catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "The multiplicative key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Confirm that a is relatively prime to the modulus.
		if (MainWindow.gcd(a,n)!=1){
			JOptionPane.showMessageDialog(null, "The multiplicative key must be relatively prime to the modulus.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Update b.
		try{
			b = Integer.parseInt(bText.getText()); // b is the additive
			b = MainWindow.RealMod(b,n);
		}
		catch (NumberFormatException e1){
			JOptionPane.showMessageDialog(null, "The additive key must be an integer.","Error",JOptionPane.ERROR_MESSAGE);
			return "";
		}
		// Compute ainv and decrypt.
		ainv = MainWindow.inversemod(a,n);
		for(i=0 ; i<cipher.length(); i++)  
		{
			ch = cipher.charAt(i);
			j = alphaText.indexOf(ch);
			if(j<0) continue; // This should never happen because the text will be cleaned.
			j = MainWindow.RealMod(ainv*(j-b), n);
			ch = alphaText.charAt(j);
			plain += ch;
		}
		return plain;
	}

	public class AffineActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}

}
