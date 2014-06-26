package util;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


public class SearchParser {
	
		public String status="";

		public ArrayList<Talk> talks;
		public ArrayList<Talk> getSearchResult(String query){
			talks = new ArrayList<Talk>();
			URL url;
			try {
			url = new URL("http://halley.exp.sis.pitt.edu/solr/db/select/?q="+query);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xr = saxParser.getXMLReader();

			ParseHandler handler = new ParseHandler();
			xr.setContentHandler(handler);
			InputStreamReader is = new InputStreamReader(url.openStream(), "utf-8");
			
			xr.parse(new InputSource(is));
			is.close();
			status = "ok";
			} catch (Exception ee) {
				status = ee.toString();
				//System.out.println(ee.toString());
			}
			return talks;
		}
		
		private class ParseHandler extends DefaultHandler {
			private int state = 0;
			private Talk t;

			
			public void startDocument() throws SAXException {
			}

			public void endDocument() throws SAXException {
			}

			public void startElement(String namespaceURI, String localName,
					String qName, Attributes atts) throws SAXException {
				if (localName.equals("doc")) {
					t = new Talk();
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("begintime")) {
					state =9;
					return;
				}
				if (localName.equals("int") && atts.getValue("name").equals("bookmark_no")) {
					state =1;
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("col_id")) {
					state =2;
					return;
				}
				if (localName.equals("date") && atts.getValue("name").equals("date")) {
					state =10;
					return;
				}
				if (localName.equals("int") && atts.getValue("name").equals("email_no")) {
					state =3;
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("endtime")) {
					state =11;
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("location")) {
					state =4;
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("speaker_affiliation")) {
					state =5;
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("speaker_name")) {
					state =6;
					return;
				}
				if (localName.equals("str") && atts.getValue("name").equals("title")) {
					state =7;
					return;
				}
				if (localName.equals("long") && atts.getValue("name").equals("view_no")) {
					state =8;
					return;
				}
			}

			public void endElement(String namespaceURI, String localName,
					String qName) throws SAXException {
				if(localName.equals("doc")){
					talks.add(t);
					return;
				}
			}

			public void characters(char ch[], int start, int length) {

				String content = new String(ch, start, length);
				switch (state) {
				case 1:
					t.bookmarkno = content;		
					state=0;
					break;
				case 2:
					t.id = content;
					//System.out.println("Id is: "+ content);	
					state=0;
					break;
				case 3:
					t.emailno=content;
					
					state=0;
					break;
				case 4:
					t.location = content;
					//System.out.println("Location: "+ content);	
					state=0;
					break;
				case 5:
					t.affiliation = content;
					//System.out.println("Affiliation: "+ content);	
					state=0;
					break;
				case 6:
					t.speaker= content;
					//System.out.println("Speaker: "+ content);	
					state=0;
					break;
				case 7:
					t.title = content;
					//System.out.println("Title: "+ content);	
					state=0;
					break;
				case 8:
					t.viewno = content;
					//System.out.println("View no: "+ content);	
					state=0;
					break;
				case 9:
					SimpleDateFormat   formatter1   =   new   SimpleDateFormat   ("D");
					SimpleDateFormat   formatter6   =   new   SimpleDateFormat   ("yyyy");
					SimpleDateFormat   formatter2   =   new   SimpleDateFormat   ("EEEE, MMM dd, yyyy");
					SimpleDateFormat   formatter3   =   new   SimpleDateFormat   ("h:mm a");
					String str_id = "0";
					String str_date = "0";
					String str_begintime = "0";
					String str_year ="0";
					try {
						Date date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
						str_id   =   formatter1.format(date);
						str_date = formatter2.format(date);
						str_begintime = formatter3.format(date);
						str_year = formatter6.format(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t.dateid= str_id;
					t.date = str_date;
					t.begintime = str_begintime;
					t.year= str_year;
					//System.out.println(t.date+"\t"+t.begintime);	
					state=0;
					break;
				case 10:
					/*SimpleDateFormat   formatter5   =   new   SimpleDateFormat   ("D");
					String str_begintime = "0";
					try {
						Date date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
						str_id   =   formatter1.format(date);
						str_date = formatter2.format(date);
						str_begintime = formatter3.format(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t.dateid= str_id;
					t.date = str_date;
					t.begintime = str_begintime;*/
					t.pubtime= content;			
					state=0;
					break;
				case 11:
					SimpleDateFormat   formatter4   =   new   SimpleDateFormat   ("h:mm a");
					
					String str_endtime = "0";
					try {
						Date date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").parse(content);
						str_endtime = formatter4.format(date);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					t.endtime = str_endtime;
					//System.out.println(t.endtime);	
					state=0;
					break;
				default:
					state=0;
					return;
				}
			}
		}
		
	}
