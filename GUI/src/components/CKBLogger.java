package components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JTextArea;

public class CKBLogger extends JTextArea
{
	private SimpleDateFormat sdf = new SimpleDateFormat(" [yyyy-MM-dd HH:mm:ss]  ",Locale.SIMPLIFIED_CHINESE);
	
	public CKBLogger(int a, int b)
	{
		super(a,b);
	}
	
	public void log(String l)
	{
		String timeStr = sdf.format(new Date());
		this.append(timeStr + "  " + l + "\n");
	}
	
	public void clear()
	{
		this.setText("");
	}
}
