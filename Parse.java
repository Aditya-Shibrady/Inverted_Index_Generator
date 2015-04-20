import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class Parse {
	
	Cranfield c1 = new Cranfield();
	
	Cranfield perform_parse( String currdoc) throws Exception
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(currdoc);
		
		XPathFactory xpathfactory = XPathFactory.newInstance();
		XPath xpath = xpathfactory.newXPath();

		XPathExpression expr = xpath.compile("//DOC/DOCNO/text()");
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		
		
		c1.docid = nodes.item(0).getNodeValue().trim();
		
		expr = xpath.compile("//DOC/TITLE/text()");
		result = expr.evaluate(doc, XPathConstants.NODESET);
		nodes = (NodeList) result;

		c1.title = nodes.item(0).getNodeValue().trim();
		

		expr = xpath.compile("//DOC/AUTHOR/text()");
		result = expr.evaluate(doc, XPathConstants.NODESET);
		nodes = (NodeList) result;

		c1.author = nodes.item(0).getNodeValue().trim();


		expr = xpath.compile("//DOC/BIBLIO/text()");
		result = expr.evaluate(doc, XPathConstants.NODESET);
		nodes = (NodeList) result;
		
		c1.biblio = nodes.item(0).getNodeValue().trim();
		

		expr = xpath.compile("//DOC/TEXT/text()");
		result = expr.evaluate(doc, XPathConstants.NODESET);
		nodes = (NodeList) result;
		c1.text = nodes.item(0).getNodeValue().trim();
		
			
		return c1;		
		
	}
	

	

}

class Cranfield {
	String docid;
	String title;
	String author;
	String biblio;
	String text;

}
