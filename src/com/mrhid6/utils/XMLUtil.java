package com.mrhid6.utils;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class XMLUtil {

	public static Document loadDocument(InputStream in, boolean exitOnError) {
		Document doc = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	public static Document loadDocument(InputStream in) {
		return loadDocument(in, true);
	}
}
