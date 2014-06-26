package util;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class TagsandComments {
	
public ArrayList<tag> List= new ArrayList<tag> ();
public ArrayList<Comments> cList= new ArrayList<Comments> ();
public String server_return= "";
	
	public ArrayList<tag> getTags(String id) {
			
			try {
				URL url = new URL("http://halley.exp.sis.pitt.edu/comet/utils/loadTagsXML.jsp?col_id="+id);
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputStreamReader is = new InputStreamReader(url.openStream(), "utf-8");
				Document doc = db.parse(new InputSource(is));
				doc.getDocumentElement().normalize();
				
				NodeList nodeList = doc.getElementsByTagName("tag");
				System.out.println(nodeList.getLength());
				for (int i = 0; i < nodeList.getLength(); i++) {
					tag t = new tag();
					Node node = nodeList.item(i);
					

					//ID
					Element TagEle = (Element) node;
					t.id = TagEle.getAttributeNode("id").getNodeValue();
					t.name = TagEle.getNodeValue();
					
					
					List.add(t);
				}
				server_return = "ok";
			} catch (Exception ee) {
				server_return = ee.toString();
				Log.d("lelele", ee.toString());
			}
			
			return List;
		}

		private class TagParseHandler extends DefaultHandler {
			private int state = 0;
			private tag tag;
			
			public void startDocument() throws SAXException {
			}

			public void endDocument() throws SAXException {
			}

			public void startElement(String namespaceURI, String localName,
					String qName, Attributes atts) throws SAXException {
				if(localName.equals("tags")){
					List = new ArrayList<tag>();
					return;
				}
				if(localName.equals("tag")){
					tag = new tag();
					tag.id = atts.getValue("id").toString();
					state = 1;
					return;
				}
					
					
			}

			public void endElement(String namespaceURI, String localName,
					String qName) throws SAXException {
				if (localName.equals("tag")) {
					List.add(tag);
					return;
				}
			}

			public void characters(char ch[], int start, int length) {

				String content = new String(ch, start, length);
				switch (state) {
				case 1:
					tag.name = content;
					state=0;
					break;
				
				default:
					state=0;
					return;
				}
			}

		}

		
public ArrayList<Comments> getComments(String id) {
			
			try {
				
				URL url = new URL("http://halley.exp.sis.pitt.edu/comet/utils/loadCommentsXML.jsp?col_id="+id);
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser saxParser = spf.newSAXParser();
				XMLReader xr = saxParser.getXMLReader();

				CommentParseHandler shandler = new CommentParseHandler();
				xr.setContentHandler(shandler);
		
				InputStreamReader isr = new InputStreamReader(url.openStream(), "utf-8");

				xr.parse(new InputSource(isr));
				isr.close();
		
				server_return= "ok";
			} catch (Exception ee) {
				server_return = ee.toString();
			}
			
			return cList;
		}

		private class CommentParseHandler extends DefaultHandler {
			private int state = 0;
			private Comments co;
			
			public void startDocument() throws SAXException {
			}

			public void endDocument() throws SAXException {
			}

			public void startElement(String namespaceURI, String localName,
					String qName, Attributes atts) throws SAXException {
				if(localName.equals("comments")){
					cList = new ArrayList<Comments>();
					return;
				}
				if(localName.equals("comment")){
					co = new Comments();
					return;
				}
				if (localName.equals("comment_id")) {
					state = 1;
					return;
				}
				if (localName.equals("user_id")) {
					state = 2;
					return;
				}
				if (localName.equals("name")) {
					state = 3;
					return;
				}
				if (localName.equals("date")) {
					state = 4;
					return;
				}
				if (localName.equals("c")) {
					state = 5;
					return;
				}
					
			}

			public void endElement(String namespaceURI, String localName,
					String qName) throws SAXException {
				if (localName.equals("comment")) {
					cList.add(co);
					return;
				}
			}

			public void characters(char ch[], int start, int length) {

				String content = new String(ch, start, length);
				switch (state) {
				case 1:
					co.id = content;
					state=0;
					break;
				case 2:
					co.user_id = content;
					state=0;
					break;
				case 3:
					co.user_name = content;
					state=0;
					break;
				case 4:
					co.date = content;
					state=0;
					break;
				case 5:
					co.cotent = content;
					state=0;
					break;
				default:
					state=0;
					return;
				}
			}

		}
		
		public String addComment(String id, String userid, String comment) {

			String status = "";
			String url = "http://halley.exp.sis.pitt.edu/comet/utils/addCommentXML.jsp?";
			HttpPost httpRequest = new HttpPost(url);
			
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", userid));
			params.add(new BasicNameValuePair("col_id", id));
			params.add(new BasicNameValuePair("comment", comment));
			try {

				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == 200) {

					String result = EntityUtils.toString(httpResponse.getEntity());
					int start = result.indexOf("<status>");
					int end = result.indexOf("</status>");
					result = result.substring(start+8, end);
					if (result
							.compareTo("OK") == 0)
						status = "yes";
					else
						status = result;

				} else {
					// System.out.print("error: status code not 200");
				}
			} catch (Exception e) {
				System.out.print("exception" + e);
			}

			return status;

		}
		public String addTag(String id, String userid, String tag) {

			String status = "";
			String url = "http://halley.exp.sis.pitt.edu/comet/utils/postTags.jsp?";
			HttpPost httpRequest = new HttpPost(url);
			
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("user_id", userid));
			params.add(new BasicNameValuePair("col_id", id));
			params.add(new BasicNameValuePair("tag", tag));
			params.add(new BasicNameValuePair("outputMode", "xml"));
			try {
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(httpRequest);

				if (httpResponse.getStatusLine().getStatusCode() == 200) {

					String result = EntityUtils.toString(httpResponse.getEntity());
					Log.d("lelele", result);
					int start = result.indexOf("<status>");
					int end = result.indexOf("</status>");
					result = result.substring(start+8, end);
					if (result
							.compareTo("OK") == 0)
						status = "yes";
					else
						status = "not OK";//result;

				} else {
					status="OK";// System.out.print("error: status code not 200");
				}
			} catch (Exception e) {
				Log.d("lelele", e.toString());
			}

			return status;

		}
}
