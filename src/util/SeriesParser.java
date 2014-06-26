package util;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;



public class SeriesParser {

	public ArrayList<Series> List = new ArrayList<Series>();
	public String status="";
	
	public ArrayList<Series> getPopData() {
			
			try {
				
				URL url = new URL("http://halley.exp.sis.pitt.edu/comet/utils/popSeriesXML.jsp?rows=20&start=0");
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser saxParser = spf.newSAXParser();
				XMLReader xr = saxParser.getXMLReader();

				SeriesPopParseHandler shandler = new SeriesPopParseHandler();
				xr.setContentHandler(shandler);
		
				InputStreamReader isr = new InputStreamReader(url.openStream(), "utf-8");

				xr.parse(new InputSource(isr));
				isr.close();
		
				status = "ok";
			} catch (Exception ee) {
				status = ee.toString();
			}
			
			return List;
		}
	public ArrayList<Series> getRecentData() {
		
		try {
			
			URL url = new URL("http://halley.exp.sis.pitt.edu/comet/utils/recentSeriesXML.jsp?rows=20&start=0");
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xr = saxParser.getXMLReader();

			SeriesRecentParseHandler shandler = new SeriesRecentParseHandler();
			xr.setContentHandler(shandler);
	
			InputStreamReader isr = new InputStreamReader(url.openStream(), "utf-8");

			xr.parse(new InputSource(isr));
			isr.close();
	

			status = "ok";
		} catch (Exception ee) {
			status = ee.toString();
		}
		
		return List;
	}
		private class SeriesPopParseHandler extends DefaultHandler {
			private int state = 0;
			private Series se;
			
			public void startDocument() throws SAXException {
			}

			public void endDocument() throws SAXException {
			}

			public void startElement(String namespaceURI, String localName,
					String qName, Attributes atts) throws SAXException {
				if(localName.equals("popseries")){
					List = new ArrayList<Series>();
					return;
				}
				if(localName.equals("series")){
					se = new Series();
					se.cate = "pop";
					se.id= atts.getValue("id").toString();
					return;
				}
				if (localName.equals("name")) {
					state =1;
					return;
				}
				if (localName.equals("bookmark")) {
					state = 2;
					return;
				}
					
			}

			public void endElement(String namespaceURI, String localName,
					String qName) throws SAXException {
				if (localName.equals("series")) {
					List.add(se);
					return;
				}
			}

			public void characters(char ch[], int start, int length) {

				String content = new String(ch, start, length);
				switch (state) {
				case 1:
					se.name = content;
					state=0;
					break;
				case 2:
					se.bookmarkno = content;
					state=0;
					break;
				default:
					state=0;
					return;
				}
			}

		}

		private class SeriesRecentParseHandler extends DefaultHandler {
			private int state = 0;
			private Series se;
			
			public void startDocument() throws SAXException {
			}

			public void endDocument() throws SAXException {
			}

			public void startElement(String namespaceURI, String localName,
					String qName, Attributes atts) throws SAXException {
				if(localName.equals("recentseries")){
					List = new ArrayList<Series>();
					return;
				}
				if(localName.equals("series")){
					se = new Series();
					se.cate = "recent";
					se.id= atts.getValue("id").toString();
					return;
				}
				if (localName.equals("name")) {
					state =1;
					return;
				}
				if (localName.equals("bookmark")) {
					state = 2;
					return;
				}
					
			}

			public void endElement(String namespaceURI, String localName,
					String qName) throws SAXException {
				if (localName.equals("series")) {
					List.add(se);
					return;
				}
			}

			public void characters(char ch[], int start, int length) {

				String content = new String(ch, start, length);
				switch (state) {
				case 1:
					se.name = content;
					state=0;
					break;
				case 2:
					se.bookmarkno = content;
					state=0;
					break;
				default:
					state=0;
					return;
				}
			}

		}
}

