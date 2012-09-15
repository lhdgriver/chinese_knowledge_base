package components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class CKBButton extends JButton 
{
	private String picFileName;
	private Image img;
	
	public CKBButton(String picFileName)   
	{ 
		this.picFileName = picFileName;
		ImageIcon imageIcon = new ImageIcon(picFileName); 
		img = imageIcon.getImage(); 
		this.setSize(new Dimension(280,280));
	} 

	public void paint(Graphics g)   
	{ 
		
		//int picX = this.getWidth(); 
		//int picY = this.getWidth(); 
		g.drawImage(img, -50, -50, 280, 280, this); 
		//g.setColor(Color.BLACK); 
		//super.paint(g); 
	} 

}
