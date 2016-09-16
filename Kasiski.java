import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class Kasiski extends JFrame{
	int modulus;
	String alphaText;
	JTextArea text = new JTextArea();
	JScrollPane textPane = new JScrollPane(text);
	JButton CalculateButton = new JButton("Recompute");
	JPanel buttonPanel = new JPanel();

	String output="";
	JTextArea str;

	int startSize;
	int maxFreq = 0;  // Keep track of the maximum frequency so we know how to dimension the initpolys array.
	int numPolys;
	int[][] initpolys;  
	int[][] polyGraphFreqTable;
	Poly polyList = null;
	Poly polyListTail = null;
	int polyGraphSize;
	String data;
	String[] polys = new String[1000];  // 1000 is made up.  THINK ABOUT THIS MORE!

	// Constructor
	public Kasiski(String title, JTextArea str, int xLoc, int yLoc){
		Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC\
		text.setFont(font);
		setTitle(title + " Kasiski Test");
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		setSize(480,400);
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());

//		showPanel.add(showLabel);
//		showPanel.add(showBox);
//		add(showPanel,BorderLayout.NORTH);
//		showBox.setSelectedIndex(0);
		startSize = 4;  // Start by looking for repeated quadgraphs.
//		showBox.addActionListener(new DisplayListener());

		add(textPane,BorderLayout.CENTER);
		add(BorderLayout.SOUTH,buttonPanel);
		CalculateButton.addActionListener(new CalculateButtonListener());
		buttonPanel.add(CalculateButton);
		setVisible(true);

		this.str= str;
		initialize();	
		extend();

	}

	// Extend polygraphs.
	private void extend(){
		int i, j;
		String output="";
		Poly p;
		Poly cur = polyList;
		int diff;
		int[] startVector = new int[maxFreq];
		int thisFreqCount;
		while (cur!=null){
			int[] curStarts = new int[cur.starts.length];
			for (i=0; i<cur.starts.length; i++) curStarts[i] = cur.starts[i];
			for (i=0; i<curStarts.length-1; i++){
				if (curStarts[i]<0) continue;
				p = null; 
				thisFreqCount=0;
				startVector[thisFreqCount++] = curStarts[i];
				char nextch = data.charAt(curStarts[i]+cur.polygraph.length());
				for (j=i+1; j<curStarts.length; j++)
					if ( (curStarts[j]>=0) &&(curStarts[j]+cur.polygraph.length()<data.length()) 
							&& (nextch == data.charAt(curStarts[j]+cur.polygraph.length())))
					{
						if (p==null){
							p = new Poly();
							p.polygraph = cur.polygraph+nextch;
							polyListTail.next = p;
							polyListTail = p;
							p.next = null;
						}
						startVector[thisFreqCount++]=curStarts[j];
						curStarts[j]=-9999;
					}
				if (p!=null){
					// Found at least one DUPLICATE
					p.starts = new int[thisFreqCount];
					for (j=0; j<thisFreqCount; j++)
						p.starts[j]=startVector[j];
				}
			}

			cur = cur.next;
		}

		Poly t = polyList;
		Poly tp = t;
		polyList = null;			// Reverse the list so that we can print it from largest string to shortest string
		polyListTail = t;
		while (t!=null){
			//	t = t.next;
			//	tp.next = polyList;
			//	polyList = tp;
			tp = t.next;
			t.next = polyList;
			polyList = t;
			t = tp;
		}

		//  Sort into order by frequency of occurrence
		Poly tn = null;
		boolean ordered=false;
		while (!ordered){
			ordered=true;
			t = polyList;
			tp= null;
			tn = null;
			while (t!=null){
				tn = t.next;
				if (tn==t) break;
				if (tn==null) break;
				if (t.polygraph.length()==tn.polygraph.length() && t.starts.length < tn.starts.length)
				{
					ordered=false;
					t.next = tn.next;
					tn.next = t;
					if (tp==null)
						polyList = tn;
					else
						tp.next = tn;
					tp = tn;
				}
				else
				{
					tp = t;
					t = t.next;
				}
			}
		}


		t = polyList;
		while (t!=null){							//  Generate the output
			output += t.polygraph + "\n   Positions: ";
			for (j = 0 ; j<t.starts.length; j++)
			{
				output += (t.starts[j]+1)+"  ";
			}
			// Go through the array again and factor the difference of the pairs
			output += "\n   Difference";
			if (t.starts.length>2)
				output+="s";
			output+=": ";
			diff = t.starts[1] - t.starts[0];
			output += (t.starts[1]+1) + " - " + (t.starts[0]+1) + " = "+ diff + " = " + factor(diff) + "\n";
			for (j=1; j<t.starts.length-1; j++){
				diff = t.starts[j+1] - t.starts[j];
				output += "                " + (t.starts[j+1]+1) + " - " + (t.starts[j]+1) + " = "+ diff + " = " + factor(diff) + "\n";
			}
			output+="\n";
			t = t.next;
		}
		text.setText(output);
		if (output.equals("")){
			text.setText("There are no repeated strings of length 4 or more.");
		}
		text.setCaretPosition(0);  // Sets the cursor back to the top of the window.
	}


	// Compute all polygraphs of a given initial size.  This size is chosen by the user.
	private void initialize(){
		int freqTableSize;
		int counter;

		int[] freqTable;
		int[][] nzFreqTable;
		int overlap = 1;
		int nzFreqTableSize=0;
		int i,j,idx,n;
		String substring;

		data = str.getText();
		alphaText = MainWindow.alphabet.getText();
		modulus = alphaText.length(); 
		polyGraphSize = startSize;
		//System.out.println("polyGraphSize = " + polyGraphSize);
		freqTableSize = (int) Math.pow(modulus, polyGraphSize);
		//System.out.println("freqTableSize = "+freqTableSize);
		freqTable = new int[freqTableSize];
		for (i=0; i<freqTableSize; i++) freqTable[i]=0;			// Initialize the frequency table to zero
		for (i=0; i<data.length()-polyGraphSize+1; i+=overlap)
		{
			n=0;
			for (j=i; j<i+polyGraphSize; j++)
			{
				n*=modulus;
				n += alphaText.indexOf(data.charAt(j));			// Convert the string to an integer.  Same as stringToNum.
			}
			if (n>=0){
				freqTable[n]++;										// Count the occurrences of a polygraph of initial size.
			}
		}
		nzFreqTableSize=0;
		for (i=0; i<freqTableSize; i++)
			if (freqTable[i]>1) nzFreqTableSize++;  			// Determine how many polygraphs have frequency greater than 1.
		nzFreqTable =  new int[nzFreqTableSize][2];
		n=0;
		for (i=0; i<freqTableSize; i++)
			if (freqTable[i]>1){
				nzFreqTable[n][0] = i;							// Integer identifying the string.
				nzFreqTable[n][1]=freqTable[i];					// Frequency of string.
				//System.out.println(""+n+": "+freqTable[i]);
				n++;
			}
		for (i=0; i<nzFreqTableSize-1; i++){					// Sort by frequency.
			int t;
			idx=i;
			for(n=i+1; n<nzFreqTableSize; n++)
				if (nzFreqTable[n][1]>nzFreqTable[idx][1]) idx = n;
			t = nzFreqTable[i][0];
			nzFreqTable[i][0] = nzFreqTable[idx][0];
			nzFreqTable[idx][0] = t;
			t = nzFreqTable[i][1];
			nzFreqTable[i][1] = nzFreqTable[idx][1];
			nzFreqTable[idx][1] = t;	
		}
		//System.out.println(nzFreqTableSize);
		if (nzFreqTableSize==0){
			maxFreq = 0;
		}
		else{
			maxFreq = nzFreqTable[0][1];							// Determine maximum frequency.
		}

		int[] startVector = new int[maxFreq];
		int thisFreq;
		polyList = null;										// Delete any old list
		polyListTail = null;
		initpolys = new int[nzFreqTableSize][maxFreq + 2];		// Each row of initpolys has the form "StringID  StringLength Start1 Start2 ...".
		for (i=0; i<nzFreqTableSize; i++){						// Fill in initpolys.
			initpolys[i][0] = nzFreqTable[i][0];
			initpolys[i][1] = startSize;
			counter = 0;
			thisFreq = 0;
			Poly p = new Poly();
			p.polygraph = numToString(initpolys[i][0], alphaText.length(), startSize);
			p.next = null;
			substring = numToString(nzFreqTable[i][0],modulus,polyGraphSize);
			for (j=0; j<nzFreqTable[i][1]; j++){
				initpolys[i][j+2] = data.indexOf(substring,counter);
				startVector[j] = initpolys[i][j+2];
				thisFreq++;
				counter = initpolys[i][j+2]+1;
			}
			p.starts = new int[thisFreq];
			for (j=0; j<thisFreq; j++)
				p.starts[j]=startVector[j];
			if (polyList==null)
				polyList = p;
			else
				polyListTail.next = p;
			polyListTail = p;
			for (j=nzFreqTable[i][1]; j<maxFreq; j++) 
				initpolys[i][j+2] = -9999;  						// Fill in table with -9999.
		}
		numPolys = nzFreqTableSize;
		
		polyGraphFreqTable = new int[1000][maxFreq];
		for(i=0; i<numPolys; i++){
			thisFreq = 0;
			polys[i]=numToString(initpolys[i][0], 26, 4);
			for (j=0; j<maxFreq; j++){
				polyGraphFreqTable[i][j] = initpolys[i][j+2];
			}
		}
	}

	// Code copied from www.ehow.com/how_8576903_factor-integers-java.html#ixzz1ySmjC92y
	public static String factor(int yourNumber){
		boolean flag = true;
		int tempNumber;
		int n = 2;
		String out="";
		ArrayList<Integer> factors = new ArrayList();
		ArrayList<Integer> toBeFactored = new ArrayList();
		toBeFactored.add(yourNumber);

		while(flag == true){
			if(toBeFactored.isEmpty()){
				flag = false;
			}
			else{
				tempNumber = toBeFactored.remove(0);
				if(tempNumber > n){
					if(tempNumber%n == 0){
						toBeFactored.add(n);
						toBeFactored.add(tempNumber / n);
					}
					else{
						toBeFactored.add(tempNumber);
						n++;
					}
				}
				else{
					factors.add(tempNumber);
				}
			}
		}

		//System.out.print(yourNumber + " = ");
		for(int i = 0; i < factors.size(); i++){
			if(i == factors.size() - 1 ){
				out+=factors.get(i);
				//System.out.print(factors.get(i));
			}
			else{
				out+=factors.get(i) + " * ";
				//System.out.print(factors.get(i) + " * ");
			}
		}
		return out;
	}

	private class Poly {
		public String polygraph;
		public int[] starts;
		public Poly next;
	}

	public int stringToNum(String str, int modulus, int polyGraphSize){
		int n=0;
		for (int j=0; j<polyGraphSize; j++)
		{
			n*=modulus;
			n += alphaText.indexOf(str.charAt(j));
		}
		return n;
	}

	public String numToString(int n, int base, int size){
		String str="",result="";
		int d,i;
		for (i=0; i<size; i++){
			d = n%base;
			n /= base;
			str += alphaText.charAt(d);
		}
		for (i=str.length()-1; i>=0; i--)	// Reverse the digit string
		{
			result += str.charAt(i);
		}
		return result;
	}

	public class DisplayListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//startSize = PolySizeBox.getSelectedIndex() + 4;
			//System.out.println("\nNEW Starting Size is " + startSize + "\n");
			initialize();
			extend();
		}
	}

	public class CalculateButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			initialize();
			extend();
		}
	}

	// Test Text: eghwaaakeenvdeghwswmewweghwypmvdypmvdensphkluanysuhnhluanysohhzeghwaaeghwanysluanluan

}
