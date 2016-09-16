import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class HillCipher extends JFrame{
	String[] dimList = {"1","2","3","4","5","6","7","8"};
	JComboBox dimBox = new JComboBox(dimList);
	JPanel dimPanel = new JPanel();
	JLabel dimLabel= new JLabel("Matrix dimension = ");
	JPanel matPanel;
	static int dim = 2;
	static DenseMatrix64F matrix;
	static JTextField[][] mat;
	JPanel checkPanel = new JPanel();
	JButton checkButton = new JButton("Check matrix for invertibility");
	static int modulus;
	static boolean computeQ = true;

	Font font = new Font("Courier", Font.PLAIN, 12);

	public HillActionListener actionListener = new HillActionListener();


	// Constructor
	public HillCipher(){
		setSize(280,200);
		setTitle("Hill Cipher");
		setLocation(780,95);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));

		setLayout(new BorderLayout());
		dimPanel.add(dimLabel);
		dimPanel.add(dimBox);
		dimBox.setSelectedIndex(1);
		dimBox.addActionListener(new DimActionListener()); 
		add(BorderLayout.NORTH,dimPanel);
		checkPanel.add(checkButton);
		add(BorderLayout.SOUTH,checkPanel);
		checkButton.addActionListener(new CheckActionListener());	

		resetMatrix();
	}

	public void resetMatrix(){
		// resetMatrix resets the matrix when the dimensions are changed.
		if (matPanel!=null) remove(matPanel);
		matPanel = new JPanel();
		matPanel.setLayout(new GridLayout(dim,dim));
		mat = new JTextField[dim][dim];
		matrix = new DenseMatrix64F(dim,dim);
		int entry;

		computeQ = true;
		for (int r=0; r<dim; r++)
			for (int c=0; c<dim; c++)
			{
				mat[r][c] = new JTextField(2);
				if (r==c)
					entry=1;
				else
					entry=0;
				mat[r][c].setText(""+entry);
				mat[r][c].setFont(font);
				matPanel.add(mat[r][c]);
			}
		add(BorderLayout.CENTER,matPanel);
	}

	public static void readMatrix(){
		int r,c;
		double value=0.0;

		updateModulus();
		computeQ = true;
		for (r=0; r<dim; r++)
			for (c=0; c<dim; c++)
			{
				try {
					value = Integer.parseInt(mat[r][c].getText());
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null, "The entries in the matrix must be integers.","Error",JOptionPane.ERROR_MESSAGE);
					//e1.printStackTrace();
					computeQ = false;
					//break;
					return;
				}
				matrix.set(r,c,value);
			}
		// Check the determinant, but don't bother if the entries in the matrix aren't integers.
		if (computeQ){
			double myDet = CommonOps.det(matrix);
			int myDet2 = (int) Math.round(myDet);
			myDet2 = MainWindow.RealMod(myDet2,modulus);
			int mygcd=9999;
			if (myDet2 !=0)
				mygcd = MainWindow.gcd(myDet2,modulus);
			if (myDet2!=0 && mygcd==1)
				computeQ = true;
			else{
				computeQ = false;
				JOptionPane.showMessageDialog(null, "The matrix is invalid.  The determinant is congruent to " + myDet2 +".","Error",JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public static void updateModulus(){
		String alphaText = MainWindow.alphabet.getText();
		switch (MainWindow.mode) {
		case Text:
			modulus = alphaText.length(); // modulus is the length of the alphabet
			if (modulus==0){
				JOptionPane.showMessageDialog(null, "Please enter an alphabet.","Error",JOptionPane.ERROR_MESSAGE);
			}
			break;
		case Image:
		case Sound:
		default:
			modulus = 256;
			break;
		}
	}

	public class DimActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			dim = dimBox.getSelectedIndex()+1;
			resetMatrix();
			setVisible(false);
			setVisible(true);
		}
	}

	public class CheckActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			double myDet;
			int myDet2;
			readMatrix();
			//updateModulus();
			myDet = CommonOps.det(matrix);
			myDet2 = (int) Math.round(myDet);
			myDet2 = MainWindow.RealMod(myDet2,modulus);
			/*			if (myDet2 !=0)
				mygcd = MainWindow.gcd(myDet2,modulus);
			if (myDet2!=0 && mygcd==1)
				JOptionPane.showMessageDialog(null, "The matrix is fine.  The determinant is congruent to " + myDet2 +".");
			else 
				JOptionPane.showMessageDialog(null, "The matrix is invalid.  The determinant is congruent to " + myDet2 +".","Error",JOptionPane.ERROR_MESSAGE);
			 */
			if (computeQ)
				JOptionPane.showMessageDialog(null, "The matrix is fine.  The determinant is congruent to " + myDet2 +".");
		}
	}

	// Encryption
	public static byte[] Encrypt(byte[] plain)
	{
		int i, j, k;
		int dataLength;
		byte[] cipher = new byte[plain.length];
		DenseMatrix64F vecPlain = new DenseMatrix64F(dim,1);
		DenseMatrix64F vecCipher = new DenseMatrix64F(dim,1);

		// Update data.
		readMatrix();
		updateModulus();

		if (computeQ){
			// If the dimension of the matrix doesn't divide the length of the data, then the straggler bytes at the end don't get encrypted.
			dataLength = dim*(plain.length/dim);

			j = 0;
			for (i=0; i<dataLength; i++){
				vecPlain.set(j,0,plain[i]);
				j++;
				if (j==dim){
					CommonOps.mult(matrix, vecPlain, vecCipher);  	// Perform matrix multiplication.
					for (k=0; k<dim; k++)
					{
						cipher[i-dim+1+k] = (byte)MainWindow.RealMod((int) Math.round(vecCipher.get(k,0)), 256);
					}	
					j=0;
				} 
			}
			// Fill in stragglers with plaintext.
			for (i=dataLength; i<plain.length; i++){
				cipher[i] = plain[i];
			}
		}
		else{
			for (i=0; i<plain.length; i++){
				cipher[i] = (byte) 0xff;
			}
		}
		return cipher;
	}

	public static byte[] Decrypt(byte[] cipher)
	{
		int i, j, k;
		int dataLength;
		double det;
		int detInt;
		byte[] plain = new byte[cipher.length];
		DenseMatrix64F vecPlain = new DenseMatrix64F(dim,1);
		DenseMatrix64F vecCipher = new DenseMatrix64F(dim,1);
		// Update data.
		readMatrix();
		updateModulus();

		if (computeQ){
			// Invert encryption matrix.
			det = CommonOps.det(matrix);
			CommonOps.invert(matrix); // This overwrites matrix with its inverse.
			CommonOps.scale(det, matrix); // Multiply by the determinant to get the adjoint.
			detInt = (int) det;
			detInt = MainWindow.RealMod(detInt,modulus); // Make sure the determinant is positive before calling inversemod.
			detInt = MainWindow.inversemod(detInt, 256);
			CommonOps.scale(detInt, matrix); // Multiply by the multiplicative inverse of the determinant to get the modular inverse.


			// If the dimension of the matrix doesn't divide the length of the data, then the straggler bytes at the end don't get encrypted.
			dataLength = dim*(cipher.length/dim);

			j = 0;
			for (i=0; i<dataLength; i++){
				vecCipher.set(j,0,cipher[i]);
				j++;
				if (j==dim){
					CommonOps.mult(matrix, vecCipher, vecPlain);  	// Perform matrix multiplication.
					for (k=0; k<dim; k++)
					{
						plain[i-dim+1+k] = (byte)MainWindow.RealMod((int) Math.round(vecPlain.get(k,0)), 256);
					}	
					j=0;
				} 
			}
			// Fill in stragglers with ciphertext (which is actually plaintext).
			for (i=dataLength; i<cipher.length; i++){
				plain[i] = cipher[i];
			}
		}
		else{
			for (i=0; i<cipher.length; i++){
				plain[i] = (byte) 0xff;
			}
		}
		return plain;
	}

	public static String Encrypt(String plain){
		// Update data.
		readMatrix();
		updateModulus();

		String cipher = "";
		String pplain = plain;
		int index=0;
		int i, len, newCh;
		String alphaText = MainWindow.alphabet.getText();
		DenseMatrix64F vecPlain = new DenseMatrix64F(dim,1);
		DenseMatrix64F vecCipher = new DenseMatrix64F(dim,1);
		char ch;

		if (computeQ){
			// Check length of plaintext and pad if necessary.
			len = pplain.length() % dim;
			if (len!=0){
				for (i=0; i<dim-len; i++){
					pplain = pplain + "x";
				}
			}

			// Perform encryption.
			while(index < plain.length()){
				for (i=0; i<dim; i++){
					ch = pplain.charAt(index+i);
					vecPlain.set(i,0,alphaText.indexOf(ch));
				}
				CommonOps.mult(matrix, vecPlain, vecCipher);
				for (i=0; i<dim; i++){
					newCh = (int) Math.round(vecCipher.get(i,0));  // Rounding is crucial!
					newCh = MainWindow.RealMod(newCh,modulus);
					cipher = cipher + alphaText.charAt(newCh);
				}
				index = index + dim;
			}
		}
		return cipher;
	}

	// Decryption
	public static String Decrypt(String cipher){
		// Update data.
		readMatrix();
		updateModulus();

		String plain = "";
		int index=0;
		int i, len, newCh;
		String alphaText = MainWindow.alphabet.getText();
		DenseMatrix64F vecPlain = new DenseMatrix64F(dim,1);
		DenseMatrix64F vecCipher = new DenseMatrix64F(dim,1);
		char ch;
		double det;
		int detInt;

		if (computeQ){
			// Check length of ciphertext and throw away an leftover characters.  (This should never happen.)
			len = (cipher.length()/dim)*dim;

			// Invert encryption matrix.
			det = CommonOps.det(matrix);
			CommonOps.invert(matrix); // This overwrites matrix with its inverse.
			CommonOps.scale(det, matrix); // Multiply by the determinant to get the adjoint.
			detInt = (int) det;
			detInt = MainWindow.RealMod(detInt,modulus); // Make sure the determinant is positive before calling inversemod.
			detInt = MainWindow.inversemod(detInt, modulus);
			CommonOps.scale(detInt, matrix); // Multiply by the multiplicative inverse of the determinant to get the modular inverse.
						
			
			// Perform decryption.
			while(index < len){
				for (i=0; i<dim; i++){
					ch = cipher.charAt(index+i);
					vecCipher.set(i,0,alphaText.indexOf(ch));
				}
				CommonOps.mult(matrix, vecCipher, vecPlain);
				for (i=0; i<dim; i++){
					newCh = (int) Math.round(vecPlain.get(i,0));  // Rounding is crucial!
					newCh = MainWindow.RealMod(newCh,modulus);
					plain = plain + alphaText.charAt(newCh);
				}
				index = index + dim;
			}
		}
		return plain;
	}

	/*
	private static DenseMatrix64F hillgray(DenseMatrix64F matrix, DenseMatrix64F vecPlain){
		int dim = vecPlain.getNumElements();
		DenseMatrix64F vecCipher = new DenseMatrix64F(dim,1);
		DenseMatrix64F onebytePlain = new DenseMatrix64F(dim,1);
		DenseMatrix64F onebyteCipher = new DenseMatrix64F(dim,1);
		int redLevel, greenLevel, blueLevel, tLevel;
		int i;
		int rgb, out;

		for (i=0; i<dim; i++){
			rgb = (int) vecPlain.get(i,0);
			blueLevel = rgb & 0x0FF;        	
			greenLevel = (rgb >>> 8) & 0x0FF;  	
			redLevel = (rgb >>> 16) & 0x0FF;    
			tLevel = (rgb >>> 24) & 0x0FF;    
			// Perhaps double check that all 3 levels are the same?
			onebytePlain.set(i,0,blueLevel);
		}
		CommonOps.mult(matrix, onebytePlain, onebyteCipher);  	// Perform matrix multiplication.
		for (i=0; i<dim; i++){
			blueLevel = (int) onebyteCipher.get(i,0);
			blueLevel = MainWindow.RealMod(blueLevel, 256);
			greenLevel = redLevel = blueLevel;
			rgb = (int) vecPlain.get(i,0);						// Recover rgb from plain to preserve the tLevel.
			tLevel = (rgb >>> 24) & 0x0FF;
			out = (tLevel << 24) | (redLevel <<16) | (greenLevel << 8) | (blueLevel);
			vecCipher.set(i,0,out);
		}
		return vecCipher;
	}
	 */

	public class HillActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}
}
