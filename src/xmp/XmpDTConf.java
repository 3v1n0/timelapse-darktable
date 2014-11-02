package xmp;

import java.util.ArrayList;

import operations.DTConfiguration;
import operations.DTOperation;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmpDTConf {

	/**
	 * Read XMP data raw data between nodes
	 * */
	public ArrayList<String> histVer;
	public ArrayList<String> histEna;
	public ArrayList<String> histOp;
	public ArrayList<String> histPar;
	public ArrayList<String> blopPar;
	public ArrayList<String> blopVer;
	public ArrayList<String> multPrio;
	public ArrayList<String> multName;
	public String srcFile; // source RAW file
	public Integer index;
	public String ratingStr; // rating stars
	public Integer rating; // rating stars number
	public XmpFile xmpFile;

	public XmpDTConf(String xmpFileName) {
		super();

		xmpFile = new XmpFile(xmpFileName);

		// get info from NodeList (parsed xmp)
		histVer = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:history_modversion",
				"rdf:Seq" }, "rdf:li");
		histEna = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:history_enabled",
				"rdf:Seq" }, "rdf:li");
		histOp = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:history_operation",
				"rdf:Seq" }, "rdf:li");
		histPar = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:history_params",
				"rdf:Seq" }, "rdf:li");
		blopPar = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:blendop_params",
				"rdf:Seq" }, "rdf:li");
		blopVer = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:blendop_version",
				"rdf:Seq" }, "rdf:li");
		multPrio = getValuesOfNode(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF", "rdf:Description", "darktable:multi_priority",
				"rdf:Seq" }, "rdf:li");
		multName = getValuesOfNode(xmpFile.nList,
				new String[] { "x:xmpmeta", "rdf:RDF", "rdf:Description",
						"darktable:multi_name", "rdf:Seq" }, "rdf:li");
		srcFile = getNodeAttribute(xmpFile.nList, new String[] { "x:xmpmeta",
				"rdf:RDF" }, "rdf:Description", "xmpMM:DerivedFrom");
		index = Integer.parseInt(srcFile.replaceAll("(.*\\D)(\\d+)(\\D.*)",
				"$2"));
		ratingStr = getNodeAttribute(xmpFile.nList, new String[] { "x:xmpmeta",
		"rdf:RDF" }, "rdf:Description", "xmp:Rating");
		rating = Integer.parseInt(ratingStr);
	}

	public void write(String outFolder) {
		// update xmpFile from ArrayList<String> of values
		setValuesOfNode(new String[] { "x:xmpmeta", "rdf:RDF",
				"rdf:Description", "darktable:history_params", "rdf:Seq" },
				"rdf:li", this.histPar);
		setValuesOfNode(new String[] { "x:xmpmeta", 	"rdf:RDF",
				"rdf:Description", "darktable:history_enabled", "rdf:Seq" },
				"rdf:li", this.histEna);
		setNodeAttribute(new String[] { "x:xmpmeta", "rdf:RDF" },
				"rdf:Description", "xmpMM:DerivedFrom", this.srcFile);
		setNodeAttribute(new String[] { "x:xmpmeta", "rdf:RDF" },
				"rdf:Description", "xmp:Rating", this.ratingStr);
		this.xmpFile.write(outFolder, this.srcFile);
	}

	/*
	 * methods below are used to read XML nodes/attributes
	 */

	public static ArrayList<String> getValuesOfNode(NodeList nList,
			String[] path, String nodeName) {
		/**
		 * Get all content values (ArrayList of String) of specified "nodeName"
		 * in node list "nList" after filtering nodes by each string of "path"
		 * array
		 **/
		NodeList subList = getNodeListWithNames(nList, path);
		return getNodesContent(subList, nodeName);
	}

	public static NodeList getNodeListWithNames(NodeList nodeList,
			String[] names) {
		/**
		 * Get successively, for each string of "names", the child nodes of
		 * "nodeList"
		 **/
		for (String name : names) {
			nodeList = getFirstNodeWithName(nodeList, name).getChildNodes();
		}
		return nodeList;
	}

	public static Node getFirstNodeWithName(NodeList nodeList, String name) {
		/** Get first node with the specified "name" **/
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(name)) {
				return nodeList.item(i);
			}
		}
		throw new Error("Node with name " + name + "not found");
	}

	public static ArrayList<String> getNodesContent(NodeList nodeList,
			String nodeName) {
		/**
		 * Get text content of all nodes of "nodeList" with specified "nodeName"
		 **/
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(nodeName)) {
				Node node = nodeList.item(i);
				ret.add(node.getTextContent());
			}
		}
		return ret;
	}

	public void setValuesOfNode(String[] path, String nodeName,
			ArrayList<String> nodesValue) {
		// NodeList nodeList = this.xmpFile.nList;
		// NodeList rdf = getNodeListWithNames(this.xmpFile.nList,path);
		setNodesContent(getNodeListWithNames(this.xmpFile.nList, path),
				nodeName, nodesValue);
	}

	public void setNodesContent(NodeList nodeList, String nodeName,
			ArrayList<String> nodesValue) {
		int idxNode = 0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (nodeList.item(i).getNodeName().equals(nodeName)) {
				Node node = nodeList.item(i);
				// System.out.println("before "+nodeList.item(i).getTextContent());
				node.setTextContent(nodesValue.get(idxNode));
				// System.out.println("after "+nodeList.item(i).getTextContent());
				idxNode += 1;
			}
		}
	}

	public String getNodeAttribute(NodeList nodeList, String[] parentNodeNames,
			String nodeName, String attributeName) {
		/* Get the parent node first */
		NodeList rdf = getNodeListWithNames(nodeList, parentNodeNames);
		/* Get the interest node */
		Node desc = getFirstNodeWithName(rdf, nodeName);
		String attribute = desc.getAttributes().getNamedItem(attributeName)
				.getNodeValue();
		return attribute;
	}

	public void setNodeAttribute(String[] parentNodeNames, String nodeName,
			String attributeName, String setValue) {
		NodeList nodeList = this.xmpFile.nList;
		/* Get the parent node first */
		NodeList rdf = getNodeListWithNames(nodeList, parentNodeNames);
		/* Set new value of the interest node attribute */
		Node desc = getFirstNodeWithName(rdf, nodeName);
		desc.getAttributes().getNamedItem(attributeName).setNodeValue(setValue);
	}
	
	public void addNode(DTOperation dto){
		String defaultOpName = "temperature"; // default filter to copy
		for (int i = 0; i < this.histOp.size(); i++) {
			if (this.histOp.get(i).equals(defaultOpName)) {
				this.blopPar.add(this.blopPar.get(i));
				this.blopVer.add(this.blopVer.get(i));
				this.histEna.add(DTOperation.writeEnable(dto));
				this.histOp.add(dto.name);
				this.histPar.add(DTOperation.writeParams(dto));
				this.histVer.add(dto.version);
				this.multName.add(dto.multiName);
				this.multPrio.add(dto.multiPriority);
			}
		}
		// add a node in NodeList... TODO
	}

}
