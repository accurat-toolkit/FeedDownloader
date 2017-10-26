package com.sheffield.feedDownloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sheffield.util.Util;

public class RSSReader {

	private static RSSReader instance = null;

	private RSSReader() {
	}

	public static RSSReader getInstance() {
		if (instance == null) {
			instance = new RSSReader();
		}
		return instance;
	}

	public void writeNews(String input, String aLang, String aPathToSave) throws IOException {
			StringBuffer buffer = new StringBuffer();
			Vector<String> enSeeds = Util.getFileContentAsVector(input);
			for (int k = 0; k < enSeeds.size(); k++) {
				try {
					DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					URL u = new URL(enSeeds.get(k)); // your feed url
					Document doc = builder.parse(u.openStream());
					NodeList nodes = doc.getElementsByTagName("item");
					for(int i=0;i<nodes.getLength();i++) {
						//url	relatedTOURL	language	title	topic	relatedTOPICS_URL	publishingDate	publisher	textSnippet	relatedTOPICS_URL_PROCESSED
						Element element = (Element)nodes.item(i);
						if (getElementValue(element,"link") == null || "".equals(getElementValue(element,"link"))) {
							continue;
						}
						if (getElementValue(element,"pubDate") == null || "".equals(getElementValue(element,"pubDate"))) {
							continue;
						}
						if (getElementValue(element,"title") == null || "".equals(getElementValue(element,"title"))) {
							continue;
						}
						buffer.append(getElementValue(element,"link").replaceAll("\n", "").trim()).append("\t");
						buffer.append(aLang).append("\t");
						buffer.append(getElementValue(element,"title").replaceAll("\n", "").trim()).append("\t");
						buffer.append(getElementValue(element,"pubDate").replaceAll("\n", "").trim()).append("\n");
						}// for
					}// try
					catch(Exception ex) {
						System.out.println("Cannot parse the given feed URL " + enSeeds.get(k));
					}
			}
			Util.doSaveUTF(aPathToSave, buffer.toString());	
	}

	private String getCharacterDataFromElement(Element e) {
		try {
		Node child = e.getFirstChild();
		if(child instanceof CharacterData) {
		CharacterData cd = (CharacterData) child;
		return cd.getData();
		}
		}
		catch(Exception ex) {

		}
		return "";
		} // private String getCharacterDataFromElement

	protected float getFloat(String value) {
		if (value != null && !value.equals("")) {
			return Float.parseFloat(value);
		}
		return 0;
	}

	protected String getElementValue(Element parent, String label) {
		return getCharacterDataFromElement((Element) parent
				.getElementsByTagName(label).item(0));
	}

	public static void main(String[] args) throws IOException {
		RSSReader reader = RSSReader.getInstance();
		if (args.length < 3) {
			System.out.println("ERROR in argument list: java -jar feedDownloader pathToInputFile pathToSaveOutput languageCode");
		} else {
			String langCode = args[2];
			String input = args[0];
			String pathToSave = args[1];
			reader.writeNews(input, langCode, pathToSave);			
		}

	}
}
