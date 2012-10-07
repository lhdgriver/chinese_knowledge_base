package components;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JTextArea;

public class CKBLogger
{
	private static SimpleDateFormat sdf = new SimpleDateFormat(" [yyyy-MM-dd HH:mm:ss]  ",Locale.SIMPLIFIED_CHINESE);
	private static JTextArea instance = null;
	
	public static void setTextArea(JTextArea ta)
	{
		instance = ta;
	}

	public static void log(String l)
	{
		String timeStr = sdf.format(new Date());
		instance.append(timeStr + "  " + l + "\n");
		instance.selectAll();
	}
	
	public static void clear()
	{
		instance.setText("");
	}
}
