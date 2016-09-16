import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

// Issues:
//		1. It would be good to put scroll bars on big images.

public class ImageDisplayWindow extends JFrame{
	ImageDisplayWindow me = this;
	public ViewButtonListener buttonListener = new ViewButtonListener();
	// create a menu bar and use it in this JFrame
	JMenuBar menuBar = new JMenuBar(  );
	BufferedImage img;
	LoadImageApp thisImage = null;
	static String filePath=null;
//	JScrollBar barH = new JScrollBar(JScrollBar.HORIZONTAL);
//	JScrollBar barV = new JScrollBar(JScrollBar.VERTICAL);
//	private JScrollPane windowPane = new JScrollPane();

	// Constructor
	public ImageDisplayWindow(String title, int yLoc){
		//Font font = new Font("Courier", Font.PLAIN, 12); 		// Font settings:  PLAIN, BOLD, ITALIC
		setIconImage(MainWindow.ECryptIcon.getImage());
//		setIconImage(Toolkit.getDefaultToolkit().getImage("ECryptIcon.png"));
		
//		Container contentPane = this.getContentPane();
//		contentPane.add(barH,BorderLayout.SOUTH);
//		contentPane.add(barV,BorderLayout.EAST);

		setSize(580,300);
		setTitle(title);
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem clearText = new JMenuItem("Clear");
		JMenuItem openFile = new JMenuItem("Open");
		JMenuItem saveFile = new JMenuItem("Save");
		fileMenu.add(clearText);
		fileMenu.addSeparator();	
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		clearText.addActionListener(new ClearListener());
		openFile.addActionListener(new OpenFileListener());
		saveFile.addActionListener(new SaveFileListener());
		menuBar.add(fileMenu);
		menuBar.add(makeAnalysisMenu());
		setJMenuBar(menuBar);
		thisImage = new LoadImageApp();
		add(thisImage);
	//	windowPane.add(thisImage);
	//	add(windowPane);
		pack();
		Dimension s=this.getSize();
		if (s.width<250) s.width = 250;
		setSize(s);

		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLocation(200,yLoc);
		//text.setFont(font);
		setVisible(false);
	}


	public JMenu makeAnalysisMenu(){
		JMenu analyzeMenu = new JMenu("Analysis");
		JMenuItem monographFreq = new JMenuItem("Byte Frequencies");
		JMenuItem IoC = new JMenuItem("Index of Coincidence");
		//	analyzeMenu.addSeparator();
		analyzeMenu.add(monographFreq); monographFreq.addActionListener(new MonographActionListener());
		analyzeMenu.add(IoC); IoC.addActionListener(new IoCActionListener());
		return analyzeMenu;
	}

	public void changeTitle(String newTitle){
		setTitle(newTitle);
	}

	private class OpenFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{
			readFile();
		}
	}

	private class SaveFileListener implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{
			saveFile();
		}
	}

	public class ViewButtonListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			setVisible(true);
		}
	}


	private class ClearListener implements ActionListener{
		public void actionPerformed(ActionEvent e){

			int w = img.getWidth();
			int h = img.getHeight();
			WritableRaster raster = img.copyData(img.getRaster());

			int x, y;
			for (x=0; x<w; x++)
				for (y=0; y<h; y++)
					raster.setSample(x,y,0,0xff);
			setVisible(false);
			setVisible(true);
		}
	}

	public class MonographActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int x, y;
			int w = img.getWidth();
			int h = img.getHeight();
			int freqs[]= new int[256];
			for (x=0; x<256; x++) freqs[x]=0;
			for (x=0; x<w; x++){
				for (y=0; y<h; y++){
					freqs[img.getRaster().getSample(x,y,0)&0xff]++;
				}
			}
			int xLoc, yLoc, frameWidth;
			Point temp = getContentPane().getLocationOnScreen();
			frameWidth = getContentPane().getSize().width;
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + frameWidth + 8;
			if (xLoc>1000) xLoc = 0;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new Histogram(freqs,xLoc,yLoc);
		}
	}

	public class IoCActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			int x, y, i;
			int w = img.getWidth();
			int h = img.getHeight();
			int data[]= new int[w*h];
			i=0;
			for (x=0; x<w; x++){
				for (y=0; y<h; y++){
					data[i] = img.getRaster().getSample(x,y,0)&0xff;
					i++;
				}
			}
			int xLoc, yLoc, frameWidth;
			Point temp = getContentPane().getLocationOnScreen();
			frameWidth = getContentPane().getSize().width;
			xLoc = (int)Math.round(temp.getX());
			xLoc = xLoc + frameWidth + 8;
			if (xLoc>1000) xLoc = 0;
			yLoc = (int)Math.round(temp.getY());
			yLoc = yLoc - 53;
			new IoC(getTitle(),data, xLoc, yLoc);
		}
	}

	protected void readFile() 
	{
		final JFileChooser fc = new JFileChooser(filePath);
		final FileFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "bmp", "png", "tif", "tiff", "gif");
		final FileFilter filterjpg = new FileNameExtensionFilter("JPG Files", "jpg");
		final FileFilter filterbmp = new FileNameExtensionFilter("Bit Map Files", "bmp");
		final FileFilter filterpng = new FileNameExtensionFilter("PNG Files", "png");

		fc.addChoosableFileFilter(filterjpg);
		fc.addChoosableFileFilter(filterpng);
		fc.addChoosableFileFilter(filterbmp);
		fc.addChoosableFileFilter(filter);
		//		int returnVal = fc.showOpenDialog(getParent());
		int returnVal = fc.showOpenDialog(me);
		File file = fc.getSelectedFile();

		String ext;
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			filePath = file.getPath();
			/*		try{
				filePath = file.getPath();
			} catch(NullPointerException e1){
				filePath = "";
			};

			if (filePath.length()>3)
				ext = filePath.substring(filePath.length()-4,filePath.length());
			else
				ext="";

			if (!ext.equals(".wav")){
				filename += ".wav";
				file = new File(filename);
			}
			 */

			thisImage = new LoadImageApp(filePath);
			pack();
			Dimension s=this.getSize();
			if (s.width<250) s.width = 250;
			setSize(s);
		}
		else
			if (returnVal != JFileChooser.CANCEL_OPTION)
				JOptionPane.showMessageDialog(null, "Error locating the selected file " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
	}

	protected void saveFile() {
		final JFileChooser fc = new JFileChooser(filePath);

		final FileFilter filterjpg = new FileNameExtensionFilter("JPG (*.jpg)", "jpg");
		final FileFilter filterbmp = new FileNameExtensionFilter("BMP (*.bmp)", "bmp");
		final FileFilter filtergif = new FileNameExtensionFilter("GIF (*.gif)", "gif");
		final FileFilter filtertif = new FileNameExtensionFilter("TIFF (*.tif)", "tif");
		final FileFilter filterpng = new FileNameExtensionFilter("PNG (*.png)", "png");
		FileFilter selectedFilter;

		fc.addChoosableFileFilter(filterjpg);
		fc.addChoosableFileFilter(filterbmp);
		fc.addChoosableFileFilter(filtergif);
		fc.addChoosableFileFilter(filtertif);
		fc.addChoosableFileFilter(filterpng);

		//		int returnVal = fc.showDialog(getParent(),"Save");
		int returnVal = fc.showDialog(me,"Save");		

		File file = fc.getSelectedFile();
		String filename = file.getPath();
		String filetype="png";
		selectedFilter = fc.getFileFilter();
		if (selectedFilter == filterjpg) 
			filetype = "jpg";
		else if (selectedFilter == filtergif)
			filetype = "gif";
		else if (selectedFilter == filterpng)
			filetype = "png";
		else if (selectedFilter == filterbmp)
			filetype = "bmp";
		else if (selectedFilter == filtertif)
			filetype = "tif";

		// If user supplies an extension, don't repeat it.
		String ext="";
		if (filename.length()>4){
			ext = filename.substring(filename.length()-4,filename.length());
			if (!ext.equals("."+filetype))
			{
				filename += "."+filetype;
			}
		}
		if(returnVal == JFileChooser.APPROVE_OPTION) 
		{
			thisImage.StoreImage(filename,filetype);
		}
		else
			if (returnVal != JFileChooser.CANCEL_OPTION)
				JOptionPane.showMessageDialog(null, "Error locating the selected file " + file.getPath(),"Error",JOptionPane.ERROR_MESSAGE);
	}

	public byte[] getData()
	{
		int x,y,i;
		int w = img.getWidth(null);
		int h = img.getHeight(null);
		byte[] data = new byte[w*h];
		WritableRaster raster = img.getRaster();
		i=0;
		for (x=0; x<w; x++)
			for (y=0; y<h; y++){
				//d = img.getRGB(x, y);
				data[i++] = (byte)raster.getSample(x,y,0);
			}
		return data;
	}

	public void setData(BufferedImage oldimg, byte[] data)
	{
		if (data==null) return;
		int x,y,i;
		int w = oldimg.getWidth(null);
		int h = oldimg.getHeight(null);
		int imageType = oldimg.getType();
		img = new BufferedImage(w, h, imageType);
		WritableRaster raster = img.getRaster();
		i=0;
		for (x=0; x<w; x++)
			for (y=0; y<h; y++){
				raster.setSample(x, y, 0, data[i++]);
			}
		pack();

		Dimension s=this.getSize();
		if (s.width<250) s.width = 250;
		this.setSize(s);
	}

	/**  * This class loads an Image from an external file  */
	public class LoadImageApp extends Component {
		//BufferedImage img;
		ColorModel clr;
		int height, width;
		Raster raster;
		DataBuffer dataBuffer;


		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, null);
		}

		public void StoreImage(String filename, String format) {
			try {
				ImageIO.write(img, format, new File(filename));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error writing the selected file, " + filename,"Error",JOptionPane.ERROR_MESSAGE);
			}
		}

		public LoadImageApp()
		{
			int x,y;
			height = 280;
			width  = 250;
			img = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
			WritableRaster raster = img.getRaster();
			for (x=0; x<width; x++)
				for (y=0; y<height; y++)
					raster.setSample(x,y,0,0xff);
		}

		public LoadImageApp(String filename) {
			BufferedImage raw_img=null;
			try {
				raw_img = ImageIO.read(new File(filename));
				//System.out.println("Read the file: " + filename);
			} catch (NullPointerException e2){
				JOptionPane.showMessageDialog(null, filename + " does not appear to be an image file.","Error",JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error reading the selected file, " + filename,"Error",JOptionPane.ERROR_MESSAGE);
			}
			if (raw_img==null)
			{
				//System.out.println("    Image is empty");
				// TIFF files should load, but the raw_img seems to be null.
				JOptionPane.showMessageDialog(null, filename + " does not appear to be an image file.","Error",JOptionPane.ERROR_MESSAGE);
				return;
			}
			height = raw_img.getHeight();
			width  = raw_img.getWidth();
			img = new BufferedImage(width,height,BufferedImage.TYPE_BYTE_GRAY);
			ColorConvertOp xformOp = new ColorConvertOp(null);
			xformOp.filter(raw_img, img);


		}
		/*		
		private int rgb2gray(int rgb){
			int redLevel, greenLevel, blueLevel, tLevel, colorAverage, out;

			blueLevel = rgb & 0x0FF;        	//  Mask rgb leaving only the bottom 8 bits
			greenLevel = (rgb >>> 8) & 0x0FF;  	//  Unsigned shift rgb to the right 8 bits and clear all but the bottom 8 bits
			redLevel = (rgb >>> 16) & 0x0FF;    //  Unsigned shift rgb to the right 16 bits and clear all but the bottom 8 bits
			tLevel = (rgb >>> 24) & 0x0FF;     	//  Unsigned shift rgb to the right 24 bits and clear (the clear operation "& 0xFF" is really not necessary)
			colorAverage = 	(int)(0.30*redLevel + 0.59*greenLevel + 0.11*blueLevel);					// (blueLevel + greenLevel + redLevel) / 3;  // Average the three color levels
			blueLevel = greenLevel = redLevel = colorAverage;          // Set the three colors to the average
			out = (tLevel << 24) | (redLevel <<16) | (greenLevel << 8) | (blueLevel);  //  Put it all back together
			return out;
		}

		private int gray2rgb(int gray){
			int out;
			out = (0x0FF << 24) | (gray <<16) | (gray << 8) | (gray);  //  Put it all back together
			return out;
		}
		 */

		public Dimension getPreferredSize() {
			if (img == null) {
				return new Dimension(100,100);
			} else {
				return new Dimension(img.getWidth(null), img.getHeight(null));
			}
		}
	}

}
