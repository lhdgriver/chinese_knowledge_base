package gui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import components.CKBLogger;

/**
 * @date 2012-7-12
 * @author lsl
 * @description main frame of this program , mainly get query and present results 
 */

public class MainFrame
{
	private static String frameTitle = "中文知识库查询测试";
	private JFrame mainFrame = new JFrame(frameTitle);
	private JTextArea queryArea = new JTextArea(3, 48);
	private JButton queryStartBt = new JButton(new ImageIcon("material/start.png", "Query"));
	private JButton queryStopBt = new JButton(new ImageIcon("material/stop.png", "Stop"));
	//private CKBButton queryBt = new CKBButton("material/search-icon.png");
	private JTextArea resultArea = new JTextArea(20,60);
	public JTextArea logger = new JTextArea(6, 60); 
	
	//listening thread: in this thread, a new query thread is created and this thread listens to it
	private Thread queryThread = null;
	private MainFrame self = this;
	
	public MainFrame()
	{
		initialize();
		addListener();
	}
	
	public void setQueryButtonEnabled(boolean start, boolean stop)
	{
		this.queryStartBt.setEnabled(start);
		this.queryStopBt.setEnabled(stop);
	}
	
	private void initialize()
	{
		try 
		{  
            JFrame.setDefaultLookAndFeelDecorated(true);  
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");  
        }
		catch(Exception ex)
		{}
		queryStartBt.setMargin(new Insets(0,0,0,0));
		queryStartBt.setBorderPainted(false);
		queryStopBt.setMargin(new Insets(0,0,0,0));
		queryStopBt.setBorderPainted(false);
		resultArea.setEditable(false);
		logger.setEditable(false);
		CKBLogger.setTextArea(logger);
		
		Box queryBox = Box.createHorizontalBox();
		queryBox.add(Box.createHorizontalStrut(10));
		queryBox.add(new JScrollPane(queryArea));
		queryBox.add(Box.createHorizontalGlue());
		queryBox.add(queryStartBt);
		queryBox.add(Box.createHorizontalGlue());
		queryBox.add(queryStopBt);
		queryBox.add(Box.createHorizontalGlue());
		queryBox.add(Box.createHorizontalStrut(10));
		queryBox.setBorder(new TitledBorder("Query"));
		Box resultBox = Box.createHorizontalBox();
		resultBox.add(Box.createHorizontalStrut(10));
		resultBox.add(new JScrollPane(resultArea));
		resultBox.add(Box.createHorizontalStrut(10));
		resultBox.setBorder(new TitledBorder("Result"));
		Box logBox = Box.createHorizontalBox();
		logBox.add(Box.createHorizontalStrut(10));
		logBox.add(new JScrollPane(logger));
		logBox.add(Box.createHorizontalStrut(10));
		logBox.setBorder(new TitledBorder("Log"));
		Box frameBox = Box.createVerticalBox();
		frameBox.add(Box.createVerticalStrut(6));
		frameBox.add(queryBox);
		frameBox.add(Box.createVerticalStrut(8));
		frameBox.add(resultBox);
		frameBox.add(Box.createVerticalStrut(8));
		frameBox.add(logBox);
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
	
	private void addListener()
	{
		this.queryStartBt.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				String query = queryArea.getText();
				query = query.replaceAll("\n", " ").trim();
				if(query.length() == 0)
				{
					CKBLogger.log("Empty Query, No Result Returned");
					queryStartBt.setEnabled(true);
					return;
				}
				//////////
				CKBLogger.log(query);
				queryThread = new QueryDaemon(self);
				((QueryDaemon) queryThread).setQuery(query);
				queryThread.start();
			}
			
		});
		
		this.queryStopBt.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(queryThread != null)
				{
					if(queryThread.isAlive())
					{	
						queryThread.interrupt();
					}
				}
			}
			
		});
	}
	
	
	public static void main(String args[])
	{
		MainFrame frame = new MainFrame();
			
	}

}

