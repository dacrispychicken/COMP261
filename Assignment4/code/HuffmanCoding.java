/**
 * A new instance of HuffmanCoding is created for every run. The constructor is
 * passed the full text to be encoded or decoded, so this is a good place to
 * construct the tree. You should store this tree in a field and then use it in
 * the encode and decode methods.
 */

import java.util.*;
public class HuffmanCoding {

	private Node root; // Root of the Huffman tree
	private final String text; // Text to be compressed
	private Map<Character, Integer> charFrequencies; // Map to store frequencies of characters
	private final Map<Character, String> huffmanCodes; // Map to store the Huffman codes of characters

	/**
	 * Computing and storing tree inside constructor
	 */
	public HuffmanCoding(String text) {
		this.text = text; // Initialize text with input
		computeCharFrequencyMap();
		huffmanCodes = new HashMap<>();
		Queue<Node> queue = new PriorityQueue<>();
		// Build the priority queue with leaves for each character
		charFrequencies.forEach((character, frequency) ->
				queue.add(new Leaf(character, frequency)));

		// Build the Huffman tree
		while(queue.size() > 1){
			queue.add(new Node(queue.poll(),queue.poll()));
		}

		// Generate the Huffman codes for each character
		generateHuffmanCodes(root = queue.poll(), "");
	}

	/**
	 * Important part of Huffman coding,
	 * for a given String:
	 *   iterate over all characters in string,
	 *   add each char into the hashmap charFrequencies as well as an integer value
	 *   for each instance of the key in map, increment the value of map by one
	 *   thus creating frequency map.
	 */
	private void computeCharFrequencyMap(){
		charFrequencies = new HashMap<>();

		// Iterate over text field
		for(int i = 0; i < this.text.length(); i++){
			char ch = text.charAt(i);
			Integer integer = charFrequencies.get(ch);
			charFrequencies.put(ch, integer != null ? integer + 1 : 1);
		}
	}

	private void generateHuffmanCodes(Node node, String code){
		if(node instanceof Leaf){
			// If leaf node, add code to map
			huffmanCodes.put(((Leaf) node).getCharacter(), code);
		} else {
			// Traverse left and right children
			generateHuffmanCodes(node.getLeftNode(), code + "0");
			generateHuffmanCodes(node.getRightNode(), code + "1");
		}
	}

	/**
	 * Take an input string, text, and encode it with the stored tree. Should
	 * return the encoded text as a binary string, that is, a string containing
	 * only 1 and 0.
	 *
	 * @param text
	 * @return String
	 */
	public String encode(String text) {
		return getEncoded();
	}

	/**
	 * Get the encoded String
	 *
	 * @return String
	 */
	private String getEncoded(){
		StringBuilder sb = new StringBuilder();

		// For each char in text, append it's huffman codwe to string builder
		for(int i = 0; i < text.length(); i++){
			char ch = text.charAt(i);
			sb.append(huffmanCodes.get(ch));
		}

		return sb.toString();
	}

	/**
	 * Take encoded input as a binary string, decode it using the stored tree,
	 * and return the decoded text as a text string.
	 *
	 * @param encoded
	 * @return String
	 */
	public String decode(String encoded) {
		StringBuilder sb = new StringBuilder();
		Node current = root;

		// For each digit in the encoded string, traverse the Huffman tree and append corresponding character to the StringBuilder
		for(char ch : encoded.toCharArray()){
			current = ch == '0' ? current.getLeftNode() : current.getRightNode();
			if(current instanceof Leaf){
				sb.append(((Leaf) current).getCharacter());
				current = root;
			}
		}
		return sb.toString();
	}

	/**
	 * The getInformation method is here for your convenience, you don't need to
	 * fill it in if you don't wan to. It is called on every run and its return
	 * value is displayed on-screen. You could use this, for example, to print
	 * out the encoding tree.
	 *
	 * @return String
	 */
	public String getInformation() {
		return huffmanCodes.toString() + "\n" + charFrequencies.toString();
	}
}




// ---------------------------------------------------------------------------------------------------------------------
 class Node implements Comparable<Node>{

	// Fields
	private final int frequency;
	private Node leftNode;
	private Node rightNode;

	/**
	 * Constructor so we can call super(frequency) in Leaf.java
	 *
	 * @param frequency
	 */
	public Node(int frequency){
		this.frequency = frequency;
	}

	/**
	 * Main constructor
	 *
	 * @param leftNode
	 * @param rightNode
	 */
	public Node(Node leftNode, Node rightNode){
		this.frequency = leftNode.getFrequency() + rightNode.getFrequency();
		this.leftNode = leftNode;
		this.rightNode = rightNode;
	}

	@Override
	public int compareTo(Node node){
		return Integer.compare(frequency, node.getFrequency());
	}

	/**
	 * Getter method
	 *
	 * @return integer value of frequency
	 */
	public int getFrequency(){
		return frequency;
	}

	 /**
	  * Getter method
	  *
	  * @return leftNode
	  */
	public Node getLeftNode(){
		return leftNode;
	}

	 /**
	  * Getter method
	  *
	  * @return rightNode
	  */
	public Node getRightNode() {
		return rightNode;
	}


}




// ---------------------------------------------------------------------------------------------------------------------
 class Leaf extends Node{

	private final char character; // Leaf nodes are the nodes that have characters

	public Leaf(char character, int frequency){
		super(frequency); // Inherit from superclass
		this.character = character;
	}

	/**
	 *Getter method for character of leaf node
	 *
	 * @return char
	 */
	public char getCharacter(){
		return character;
	}

	@Override
	public int compareTo(Node node){
		int frequencyComparison = Integer.compare(this.getFrequency(), node.getFrequency());
		if (frequencyComparison == 0) {
			if(node instanceof Leaf) {
				return Character.compare(this.character, ((Leaf) node).getCharacter());
			}
			return -1; // Leaf nodes are considered smaller than inner nodes when frequencies are same.
		} else {
			return frequencyComparison;
		}
	}

}

