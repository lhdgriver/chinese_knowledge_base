package gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.Toolkit;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;


import components.CKBButton;

/**
 * @date 2012-7-12
 * @author lsl
 * @description main frame of this program , mainly get query and present results 
 */

public class MainFrame
{
	private static String frameTitle = "中文知识库查询测试";
	private JFrame mainFrame = new JFrame(frameTitle);
	private JTextArea queryArea = new JTextArea(3, 40);
	private JButton queryBt = new JButton(new ImageIcon("material/search-icon.png", "Query"));
	//private CKBButton queryBt = new CKBButton("material/search-icon.png");
	private JTextArea resultArea = new JTextArea(20,50);
	
	public MainFrame()
	{
		Initialize();
	}
	
	private void Initialize()
	{
		try 
		{  
            JFrame.setDefaultLookAndFeelDecorated(true);  
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  
        }
		catch(Exception ex)
		{}
		queryBt.setMargin(new Insets(0,0,0,0));
		queryBt.setBorderPainted(false);
		resultArea.setEditable(false);
		
		
		Box queryBox = Box.createHorizontalBox();
		queryBox.add(Box.createHorizontalStrut(10));
		queryBox.add(new JScrollPane(queryArea));
		queryBox.add(Box.createHorizontalGlue());
		queryBox.add(queryBt);
		queryBox.add(Box.createHorizontalGlue());
		queryBox.add(Box.createHorizontalStrut(10));
		queryBox.setBorder(new TitledBorder("Query"));
		Box resultBox = Box.createHorizontalBox();
		resultBox.add(Box.createHorizontalStrut(10));
		resultBox.add(new JScrollPane(resultArea));
		resultBox.add(Box.createHorizontalStrut(10));
		resultBox.setBorder(new TitledBorder("Result"));
		Box frameBox = Box.createVerticalBox();
		frameBox.add(Box.createVerticalStrut(6));
		frameBox.add(queryBox);
		frameBox.add(Box.createVerticalStrut(8));
		frameBox.add(resultBox);
		frameBox.add(Box.createVerticalStrut(6));
		
		
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.add(frameBox);
		mainFrame.pack();
		int windowWidth = mainFrame.getWidth(); 
        int windowHeight = mainFrame.getHeight();
        Toolkit kit = Toolkit.getDefaultToolkit(); 
        Dimension screenSize = kit.getScreenSize(); 
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        mainFrame.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
		mainFrame.setVisible(true);
	}
	
	public static void main(String args[])
	{
		MainFrame frame = new MainFrame();
			
	}
}


