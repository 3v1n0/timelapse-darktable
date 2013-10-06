package xmp;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmpFile extends File {

	/**
	 * XmpFile : class to read / write a XmpFile of darktable
	 */
	
	private static final long serialVersionUID = 4208479880752298126L;
	
	public NodeList nList;
	private Document doc;
	
	/** construct XmpFile class from one file .xmp*/
	public XmpFile(String arg1) {
		super(arg1);
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(this);
			nList = doc.getChildNodes();
			
        } catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write(String folder,String filename){
		NodeList nListWrite = this.nList;
		// clone xmp file and values then print file
		Document docOut = (Document) this.doc.cloneNode(true);
		try {
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = transformerFactory.newTransformer();
			@SuppressWarnings("unused")
			NodeList nListOut = docOut.getChildNodes();
			nListOut = nListWrite; // replace by content of entry
			DOMSource source = new DOMSource(docOut);
			StreamResult result = new StreamResult(new File(folder,filename+".xmp"));
			transformer.transform(source, result);
			System.out.println(filename+" : file saved!");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}
	

	/* Following part is not useful for programs, but can be used for debug */
	
	
	public static void printNodes(String prefix, NodeList nodeList) {
		for(int i = 0 ; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			System.out.println(prefix + "||" + node.getNodeName());
			printNodes(prefix + "||" + node.getNodeName(), node.getChildNodes());
		}
	}
	
	public static void printNodesContent(String prefix, NodeList nodeList) {
		for(int i = 0 ; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			System.out.println(prefix + "||" + node.getNodeName() + "\nText content: " + node.getTextContent() + "\nPrefix: " + node.getPrefix() + "\nLocal name: " + node.getLocalName() + "\nLocal value: " + node.getNodeValue() + "\nAttirubutes: " + node.getAttributes());
			printNodes(prefix + "||" + node.getNodeName(), node.getChildNodes());
		}
	}
	

	
}
