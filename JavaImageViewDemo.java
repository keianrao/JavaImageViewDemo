
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;


class JavaImageViewDemo implements KeyListener {

private JFrame mainframe;
private ImageView imageView;
private JFileChooser fileChooser;

private BufferedImage image;

//  \\  //  \\  //  \\  //  \\  //  \\

public void keyPressed(KeyEvent eK) {
	// We should get a menubar instead, actually..
	if (eK.getKeyCode() == KeyEvent.VK_F1) {
		if (fileChooser.showOpenDialog(mainframe) 
				== JFileChooser.APPROVE_OPTION) try {
			image = ImageIO.read(fileChooser.getSelectedFile());
		}
		catch (IOException eIo) {
			JOptionPane.showMessageDialog(
				mainframe,
				"Could not open file!",
				"Sorry! Our image loader says it had a problem loading the file you selected..",
				JOptionPane.ERROR_MESSAGE
			);
			eIo.printStackTrace(System.err);
		}
	}
}

private void setCaption(String caption)
{
	mainframe.setTitle(caption + " - JavaImageViewDemo");
}

public void keyReleased(KeyEvent eK) { }
public void keyTyped(KeyEvent eK) { }

//  \\  //  \\  //  \\  //  \\  //  \\

private JavaImageViewDemo() {
	mainframe = new JFrame();
	
	imageView = new ImageView();
	imageView.addKeyListener(this);
	mainframe.setContentPane(imageView);
	
	mainframe.setSize(800, 600);
	mainframe.setLocationRelativeTo(null);	
	mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	fileChooser = new JFileChooser();
	
	setCaption("(No image loaded)");
	
	mainframe.setVisible(true);
	
	// We need to set up a timer, which will call for repaints.
}

//  \\  //  \\  //  \\  //  \\  //  \\

public static void main(String... args) {
	new JavaImageViewDemo();
}
	
//  \\  //  \\  //  \\  //  \\  //  \\	

private class ImageView extends JPanel implements KeyListener, ComponentListener {
	int topLeftX, topLeftY;
	int width, height;	
	int xSpeed, ySpeed;
	double aspectRatio;
	int zoomSpeed;
	
	//  \\  //  \\  //  \\  //  \\  //  \\

	public void paintComponent(Graphics g) {	
		if (image == null) return;
	
		// If zooming in, update width and height.
		if (zoomSpeed != 0) {
			width -= zoomSpeed;			
			if (width < aspectRatio) {
				width = (int)Math.floor(aspectRatio);
				// So if our aspect ratio is 16:9, 
				// our minimum width is 16.
			}
			if (width > image.getWidth()) {
				width = image.getWidth();
			}
		
			height = (int)Math.floor(width / aspectRatio);
		}
		
		// If moving around, update topLeftX and topLeftY.
		if (xSpeed != 0) {
			topLeftX += xSpeed;
			if (topLeftX < 0) {
				topLeftX = 0;
			}
			if ((topLeftX + width) > image.getWidth()) {
				topLeftX = image.getWidth() - width;
			}
		}		
		if (ySpeed != 0) {
			topLeftY += ySpeed;
			if (topLeftY < 0) {
				topLeftY = 0;
			}
			if ((topLeftY + width) > image.getHeight()) {
				topLeftY = image.getHeight() - height;
			}
		}
		
		// What if image is smaller than our window?
		// We should centre it.
		
		// Okay, done adjusting. Render current frame
		g.drawImage(
			(JavaImageViewDemo.this).image,
			0, 0, getWidth(), getHeight(),
			topLeftX, topLeftY, width, height,
			this
		);
	}
	
	public void componentResized(ComponentEvent e) {
		aspectRatio = (double)getWidth() / getHeight();
		height = (int)Math.floor(width / aspectRatio);
	}

	public void keyPressed(KeyEvent eK) {
		switch (eK.getKeyCode()) {
			case KeyEvent.VK_UP:
				ySpeed = -8;
				break;		
			case KeyEvent.VK_DOWN:
				ySpeed = 8;
				break;
			case KeyEvent.VK_LEFT:
				xSpeed = -8;
				break;
			case KeyEvent.VK_RIGHT:
				xSpeed = 8;
				break;
			case KeyEvent.VK_R:
				zoomSpeed = 1;
				break;
			case KeyEvent.VK_F:
				zoomSpeed = -1;
				break;
		}
	}
	public void keyReleased(KeyEvent eK) {
		switch (eK.getKeyCode()) {
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
				ySpeed = 0;
				break;
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_RIGHT:
				xSpeed = 0;
				break;
			case KeyEvent.VK_R:
			case KeyEvent.VK_F:
				zoomSpeed = 0;
				break;
		}
	}
	
	public void componentMoved(ComponentEvent eC) { }
	public void componentShown(ComponentEvent eC) { }
	public void componentHidden(ComponentEvent eC) { }
	public void keyTyped(KeyEvent eK) { }
	
	//  \\  //  \\  //  \\  //  \\  //  \\
	
	ImageView() {
		this.addComponentListener(this);
		this.addKeyListener(this);
		setFocusable(true);
	}
}

}
