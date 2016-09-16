import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;


public class HighlightText extends JFrame{
	JPanel searchPanel = new JPanel();
	JLabel searchLabel = new JLabel("Highlight: ");
	JTextField searchText = new JTextField(20);
	JTextArea str;
	JButton HighlightButton = new JButton("Highlight");
	JPanel buttonPanel = new JPanel();

	// Constructor
	public HighlightText(String title, JTextArea str, int xLoc, int yLoc){
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		setTitle(title + " Highlight");
		setSize(340,100);
		setLocation(xLoc,yLoc);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		// Make sure highlighting is cleared when highlight window is closed.
		addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent arg0){
				//System.out.println("Window Closed");
				highlight("");
			}
		});
		setLayout(new BorderLayout());
		searchPanel.add(searchLabel);
		searchPanel.add(searchText);
		add(searchPanel,BorderLayout.NORTH);
		HighlightButton.addActionListener(new HighlightButtonListener());
		buttonPanel.add(HighlightButton);
		add(BorderLayout.SOUTH,buttonPanel);

		setVisible(true);

		this.str = str;
		searchText.addKeyListener(new EnterEvent());
	}

	public void highlight(String substr){
		// See http://www.exampledepot.com/egs/javax.swing.text/style_HiliteWords.html  This site allows the user to highlight text and then it makes the highlight permanent.  It also uses html.
		//Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.red);
		int start, end;
		new DefaultHighlighter.DefaultHighlightPainter( Color.yellow );	// This doesn't appear to work.
		Highlighter hilite = str.getHighlighter();
		int counter;

		hilite.removeAllHighlights();
		counter = 0;

		if (substr.length()>0){
			while (counter>=0){
				start = str.getText().indexOf(substr,counter);
				end = start + substr.length();
				if (start>=0)
				{
					counter = end;
					try {
						hilite.addHighlight(start, end, DefaultHighlighter.DefaultPainter);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
				else 
					counter = -9999;
			}
		}
	}

	public class HighlightButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			highlight(searchText.getText());
		}
	}

	public class EnterEvent implements KeyListener {
		public void keyTyped(KeyEvent e){
			char ch;
			ch = e.getKeyChar();
			//			System.out.print(ch);
			if (ch=='\n')
				highlight(searchText.getText());
		}

		@Override
		public void keyPressed(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}
	}

}
