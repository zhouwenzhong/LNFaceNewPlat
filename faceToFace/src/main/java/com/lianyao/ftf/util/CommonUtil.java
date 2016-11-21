
package com.lianyao.ftf.util;

import org.w3c.dom.Document;

import java.io.StringWriter;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

public class CommonUtil {

	// 判断是否为空
	public static boolean isEmpty(String str) {
		if (str == null || str.equals("") || str.equals("null")) {
			return true;
		}
		return false;
	}
		
	// Document转String
	public static String doc2String(Document document) {
		StringWriter writer = new StringWriter();
		try {
			javax.xml.transform.TransformerFactory.newInstance().newTransformer().transform(new javax.xml.transform.dom.DOMSource(document), new javax.xml.transform.stream.StreamResult(writer));
		} catch (TransformerConfigurationException e) {
			Logger.e("Document转string错误1：" + e);
		} catch (TransformerException e) {
			Logger.e("Document转string错误2：" + e);
		} catch (TransformerFactoryConfigurationError e) {
			Logger.e("Document转string错误3：" + e);
		}
		return writer.toString();
	}

	public static String formatTimeBySecond(long second) {
		if(second < 60) {
			return "00:" + "00:" + fillBefore(Long.toString(second), 2, "0") ;
		}else if(second < 3600){
			long minute = second/60;
			long rSecond = second%60;
			return "00:" + fillBefore(Long.toString(minute), 2, "0") + ":"
					+ fillBefore(Long.toString(rSecond), 2, "0");
		}else {
			long hour = second/3600;
			long minute = second%3600;
			long rMinute = minute/60;
			long rSecond = minute%60;
			return fillBefore(Long.toString(hour), 2, "0") + ":"
					+ fillBefore(Long.toString(rMinute), 2, "0") + ":"
					+ fillBefore(Long.toString(rSecond), 2, "0") ;
		}
	}

	public static String fillBefore(String str, int len, String fill) {
		if(str.length() >= len) {
			return  str;
		}

		for(int i=str.length(); i<len; i++ ) {
			str = fill + str;
		}

		return  str;
	}
}
