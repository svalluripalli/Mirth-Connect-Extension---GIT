package com.mirth.connect.plugins.gitplugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XmlUtility {

	private static Logger logger = Logger.getLogger(XmlUtility.class);
	
	public static String readXmlFileData(Path directory, String channelName, String channelId) throws IOException, SAXException {
		List<XmlElementMapper> xmlDataList = new ArrayList<>();
		String fileName = null;
		File fXmlFile = new File(directory.toString());
		String[] pathnames = fXmlFile.list();

		for (String pathname : pathnames) {
			XmlElementMapper elementMapper = new XmlElementMapper();
			if (pathname.substring(pathname.length() - 4).equals(GitConstants.FILEFORMAT_XML)) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				
				String FEATURE = null;
				try {
				    FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
				    dbFactory.setFeature(FEATURE, true);
				   
				    FEATURE = "http://xml.org/sax/features/external-general-entities";
				    dbFactory.setFeature(FEATURE, false);

				    FEATURE = "http://xml.org/sax/features/external-parameter-entities";
				    dbFactory.setFeature(FEATURE, false);

				    // Disable external DTDs as well
				    FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
				    dbFactory.setFeature(FEATURE, false);

				    // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
				    dbFactory.setXIncludeAware(false);
				    dbFactory.setExpandEntityReferences(false);

				} catch (ParserConfigurationException e) {
				    // This should catch a failed setFeature feature
				    logger.info("ParserConfigurationException was thrown. The feature '" + FEATURE
				    + "' is probably not supported by your XML processor.");
				}
				
				DocumentBuilder dBuilder;
				Document doc = null;
				try {
					dBuilder = dbFactory.newDocumentBuilder();
					doc = dBuilder.parse(directory + GitConstants.SEP_PATH_DBS + pathname);
				} catch (ParserConfigurationException e) {
					logger.error(e);
				} catch (SAXException e) {
					logger.error(e);
				} catch (IOException e) {
					logger.error(e);
				}
				if (null != doc) {
					doc.getDocumentElement().normalize();
					String id = doc.getElementsByTagName("id").item(0).getTextContent();

					elementMapper.setId(id);
					xmlDataList.add(elementMapper);
				} else {
					logger.error("XML file path cannot be null");
				}
			}
		}
		return fileName = createFileName(xmlDataList, directory, channelName, channelId);
	}

	private static String createFileName(List<XmlElementMapper> xmlDataList, Path directory, String channelName,
			String channelId) {
		String fileName = null;
		if (!xmlDataList.isEmpty()) {
			for (XmlElementMapper xmlElementMapper : xmlDataList) {
				if (channelId.equals(xmlElementMapper.getId())) {
					if ((channelId.equals(xmlElementMapper.getId()))
							&& (channelName.equals(xmlElementMapper.getName()))) {
						fileName = directory + GitConstants.SEP_PATH_DBS + channelId + GitConstants.FILEFORMAT_XML;
					} else if ((channelId.equals(xmlElementMapper.getId()))
							&& (!channelName.equals(xmlElementMapper.getName()))) {
						fileName = directory + GitConstants.SEP_PATH_DBS + channelId  + GitConstants.FILEFORMAT_XML;
					} else {
						fileName = directory + GitConstants.SEP_PATH_DBS + channelId + GitConstants.FILEFORMAT_XML;
					}
					break;
				} else {
					fileName = directory + GitConstants.SEP_PATH_DBS + channelId + GitConstants.FILEFORMAT_XML;
				}
			}
		} else {
			fileName = directory + GitConstants.SEP_PATH_DBS + channelId + GitConstants.FILEFORMAT_XML;
		}
		return fileName;
	}
}
