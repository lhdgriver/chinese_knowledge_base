package components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.sun.java_cup.internal.runtime.Scanner;

public class CKBTextPane extends JTextPane
{
	protected StyledDocument doc;
	private SimpleAttributeSet normalAttr = new SimpleAttributeSet();
	private SimpleAttributeSet quotAttr = new SimpleAttributeSet();
	
	private int docChangeStart = 0;
	private int docChangeLength = 0;
	private ArrayList<String> query = new ArrayList<String>();
	
	public CKBTextPane(int w, int l)
	{
		super();
		this.setPreferredSize(new Dimension(w, l));
		
		StyleConstants.setForeground(quotAttr, new Color(255, 0, 255));
		StyleConstants.setFontSize(quotAttr, 16);
		StyleConstants.setForeground(normalAttr, Color.black);
		StyleConstants.setFontSize(normalAttr, 16);
		this.doc = super.getStyledDocument();
		doc.addDocumentListener(new DocumentListener()
		{

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				docChangeStart = e.getOffset();
				docChangeLength = e.getLength();
				//syntaxParse();
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		
	}
	
	public void syntaxParse()
	{
		try
		{
			Element root = doc.getDefaultRootElement();
			int cursorPos = this.getCaretPosition();
			int line = root.getElementIndex(cursorPos);
			Element para = root.getElement(line);
			int start = para.getStartOffset();
			if(start > docChangeStart)
				start = docChangeStart;
			int length = para.getEndOffset() -start;
			if(length < docChangeLength)
				length = docChangeLength + 1;
			String s = doc.getText(start, length);
			doc.setCharacterAttributes(start, length, normalAttr, false);
			//String tokens[] = s.split("-----");
			//int curStart = 0;
			//boolean isQuot = false;
			//for(String token : tokens)
			//{
				//int tokenPos = s.indexOf(token, curStart);
				//if(token.startsWith("\""))
				//{
					for(String tk : query)
					{
						int startPos = 0;
						while(s.indexOf(tk, startPos) >= 0)
						{
							startPos = s.indexOf(tk, startPos) ;
							doc.setCharacterAttributes(start + startPos, tk.length(), quotAttr, false);
							startPos++;
						}
					}
				//}
				//curStart = tokenPos + token.length();
			//}
		}
		catch(Exception ex)
		{
			CKBLogger.log(ex.getMessage());
		}
	}
	
	public void setQuery(String q)
	{
		for(String qtk : q.split("[ ]+"))
			query.add(qtk);
	}
}
