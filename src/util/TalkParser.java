package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class TalkParser {

	private ArrayList<Talk> talks;
	private TalkDesParser tdp = new TalkDesParser();
	public String status="";
	
	public ArrayList<Talk> DOMParser(String urlstr){
		talks = new ArrayList<Talk>();
		try {			
			URL url = new URL(urlstr);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStreamReader is = new InputStreamReader(url.openStream(), "utf-8");
			Document doc = db.parse(new InputSource(is));
			doc.getDocumentElement().normalize();
			
			NodeList nodeList = doc.getElementsByTagName("talk");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Talk t = new Talk();
				Node node = nodeList.item(i);
				

				//ID
				Element TalkEle = (Element) node;
				t.id = TalkEle.getAttributeNode("id").getNodeValue();
				
				//title
				NodeList title = TalkEle.getElementsByTagName("title");
				if(title.getLength()!=0){
				Element titleEle = (Element) title.item(0);
				title = titleEle.getChildNodes();
				t.title = ((Node) title.item(0)).getNodeValue();
				}
				
				//speaker
				NodeList speaker = TalkEle.getElementsByTagName("speaker");
				if(speaker.getLength()!=0){
				Element speakerEle = (Element) speaker.item(0);
				speaker = speakerEle.getChildNodes();
				t.speaker = ((Node) speaker.item(0)).getNodeValue();
				}

				//affiliation
				NodeList affili = TalkEle.getElementsByTagName("affiliation");
				if(affili.getLength()!=0){
				Element affiliEle = (Element) affili.item(0);
				affili = affiliEle.getChildNodes();
				t.affiliation = ((Node) affili.item(0)).getNodeValue();
				}
				//date, dayid, year
				NodeList date = TalkEle.getElementsByTagName("date");
				if(date.getLength()!=0){
				Element dateEle = (Element) date.item(0);
				date = dateEle.getChildNodes();
				t.date = ((Node) date.item(0)).getNodeValue();
				SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("D");
				SimpleDateFormat   formatter_year   =   new   SimpleDateFormat   ("yyyy");
				String str = "0";
				String str_year = "1";
				try {
					Date d=new SimpleDateFormat("EEEE, MMM dd, yyyy").parse(t.date);
					str   =   formatter.format(d);
					str_year = formatter_year.format(d);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				t.dateid= str;
				t.year=str_year;
				}
				
				//begintime, endtime
				NodeList beginTime = TalkEle.getElementsByTagName("begintime");
				if(beginTime.getLength()!=0){
				Element beginTimeEle = (Element) beginTime.item(0);
				beginTime = beginTimeEle.getChildNodes();
				t.begintime = ((Node) beginTime.item(0)).getNodeValue();
				}
				
				NodeList endTime = TalkEle.getElementsByTagName("endtime");
				if(endTime.getLength()!=0){
				Element endTimeEle = (Element) endTime.item(0);
				endTime = endTimeEle.getChildNodes();
				t.endtime = ((Node) endTime.item(0)).getNodeValue();
				}
				
				//location
				NodeList location = TalkEle.getElementsByTagName("location");
				if(location.getLength()!=0){
				Element locationEle = (Element) location.item(0);
				location = locationEle.getChildNodes();
				t.location = ((Node) location.item(0)).getNodeValue();
				}
				
				//bookmark
				NodeList bookmarkNo = TalkEle.getElementsByTagName("bookmarkno");
				if(bookmarkNo.getLength() != 0){
				Element bookmarkNoEle = (Element) bookmarkNo.item(0);
				bookmarkNo = bookmarkNoEle.getChildNodes();
				t.bookmarkno = ((Node) bookmarkNo.item(0)).getNodeValue();
				}
				
				//email
				NodeList emailNo = TalkEle.getElementsByTagName("emailno");
				if(emailNo.getLength() !=0){
				Element emailNoEle = (Element) emailNo.item(0);
				emailNo = emailNoEle.getChildNodes();
				t.emailno = ((Node) emailNo.item(0)).getNodeValue();
				}
				
				//view
				NodeList viewNo = TalkEle.getElementsByTagName("viewno");
				if(viewNo.getLength() != 0){
				Element viewNoEle = (Element) viewNo.item(0);
				viewNo = viewNoEle.getChildNodes();
				t.viewno = ((Node) viewNo.item(0)).getNodeValue();
				}
				
				//picURL
				NodeList picURL = TalkEle.getElementsByTagName("picurl");
				if(picURL.getLength() != 0){
				Element picURLEle = (Element) picURL.item(0);
				picURL = picURLEle.getChildNodes();
				t.picurl = ((Node) picURL.item(0)).getNodeValue();
				}
				
				//owner
				NodeList owner = TalkEle.getElementsByTagName("owner");
				if(owner.getLength()!=0){
				Element ownerEle = (Element) owner.item(0);
				owner = ownerEle.getChildNodes();
				t.ownerid = ownerEle.getAttributeNode("id").getNodeValue();
				t.pubtime = ownerEle.getAttributeNode("id").getNodeValue();
				t.ownername = ((Node) owner.item(0)).getNodeValue();
				}
				
				
				talks.add(t);
			}
			status="ok";
			is.close();
		} catch (Exception ee) {
			status = ee.toString();
		}
		
		return talks;
	}
	public ArrayList<Talk> getRecommendTalks(String uid){

			Calendar c = Calendar.getInstance();			
			String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?user_id="+uid+"&week="+(c.get(Calendar.WEEK_OF_MONTH)+1)+"&month="+(c.get(Calendar.MONTH)+1)+"&year="+c.get(Calendar.YEAR)+"&recommend=1";
			return DOMParser(s);
	}
	
	public ArrayList<Talk> getbookmarkedTalks(String uid){
		
		Calendar c = Calendar.getInstance();			
		String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?user_id="+uid+"&month="+(c.get(Calendar.MONTH)+1)+"&year="+c.get(Calendar.YEAR);
		return DOMParser(s);
	}
	public ArrayList<Talk> getDayTalks(int day, int month, int year){			
		String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?day="+day+"&month="+month+"&year="+year;
		return DOMParser(s);
	}
	public ArrayList<Talk> getSeriesTalks(String id){
		
		Calendar c = Calendar.getInstance();			
		String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?series_id="+id+"&month="+(c.get(Calendar.MONTH)+1)+"&year="+(c.get(Calendar.YEAR));
		return DOMParser(s);
	}
	public ArrayList<Talk> getGroupTalks(String id){
		Calendar c = Calendar.getInstance();			
		String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?comm_id="+id+"&month="+(c.get(Calendar.MONTH)+1)+"&year="+c.get(Calendar.YEAR);
		return DOMParser(s);
	}
	public ArrayList<Talk> getTalks(int week, int month, int year){			
		String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?week="+week+"&month="+month+"&year="+year;
		return DOMParser(s);
	}
	public ArrayList<Talk> getMonthTalks(int month, int year){
		String s="http://halley.exp.sis.pitt.edu/comet/utils/loadTalkXML.jsp?month="+month+"&year="+year;
		return DOMParser(s);
	}
	public String convertToString(InputStream is){
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			}
			catch(Exception e)
			{
				System.out.print(e.getMessage());
			}
			finally {
				try{
				is.close();
				}
				catch(Exception e)
				{
					
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}
}
