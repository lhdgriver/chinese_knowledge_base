package gui;

import search.Naive2CliqueSearcher;
import components.CKBLogger;

public class QueryDaemon extends Thread
{
	private MainFrame frame = null;
	private CKBLogger logger = null;
	private String query = null;

	public boolean startButtonEnable = true;
	public boolean stopButtonEnable = false;
	
	public QueryDaemon(MainFrame frame)
	{
		this.frame = frame;
		//this.logger = MainFrame.logger;
	}
	
	public QueryDaemon(MainFrame frame, String query)
	{
		this.frame = frame;
		//this.logger = MainFrame.logger;
		this.query = query;
	}
	
	public void setQuery(String query)
	{
		this.query = query;
	}
	
	@Override
	public void run()
	{
		startButtonEnable = false;
		stopButtonEnable = true;
		frame.setQueryButtonEnabled(this.startButtonEnable, this.stopButtonEnable);
		Naive2CliqueSearcher queryThread = new Naive2CliqueSearcher();
		queryThread.setQuery(query);
		Thread q = new Thread(queryThread);
		q.start();
		try 
		{
			q.join();
			CKBLogger.log("Search Process Finish");
			frame.setNewResult(queryThread.result);
		} 
		catch (InterruptedException e) 
		{
			CKBLogger.log("Query Interupted");
			queryThread.clearAndStop();
		}
		startButtonEnable = true;
		stopButtonEnable = false;
		frame.setQueryButtonEnabled(this.startButtonEnable, this.stopButtonEnable);
	}
}
