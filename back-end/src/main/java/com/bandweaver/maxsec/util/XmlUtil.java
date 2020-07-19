package com.bandweaver.maxsec.util;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class XmlUtil {
    public static Map<String, String> getNode(String filaName, String nodeName) {
        Map<String, String> result = new HashMap<>();
        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //2.创建DocumentBuilder对象
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filaName);
            NodeList nodeList = document.getElementsByTagName(nodeName);
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    NodeList childNodes = node.getChildNodes();
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                            String key = childNodes.item(j).getNodeName();
                            Object value = childNodes.item(j).getFirstChild().getNodeValue();
                            result.put(key, value.toString());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("getNode", ex);
        }
        return result;
    }

    public static Map<String, String> getSystemConfig(String nodeName) {
        return getNode("src/main/resources/system-config.xml", nodeName);
    }
}
