import java.util.*;

public class Encoder {
	
	String plaintext;
	String operation;
	int offset;
	ArrayList<String> contentsList;
	
	public Encoder(ArrayList<String> contentsList) {
//		contentsList is the current grid.txt file
		this.contentsList = contentsList;
	}
	
	public String encode(String plaintext, String operation, int offset) {
//		convert plaintext to entirely upper case, remove all whitespace then split
		plaintext = plaintext.toUpperCase();
		plaintext = plaintext.replaceAll("\\s+", "");
		String[] plaintextSplit = plaintext.split("");
		String result = "";
		
//		If there is text to encode
		if (plaintext.length() != 0) {
			if (operation == "Left") {
				for (String character : plaintextSplit) {
					int column = contentsList.indexOf(character) % 6;
					int newColumn = Math.abs(column + offset) % 6;
//					Gets the position of the new character relative to the contents array
					newColumn = contentsList.indexOf(character) - column + newColumn;
//					Appends the string value of the position to the result string
					result = result + contentsList.get(newColumn);
				}
			}
			else if (operation == "Right") {
				for (String character : plaintextSplit) {
					int column = contentsList.indexOf(character) % 6;
					int newColumn = Math.abs(column + 6 - offset) % 6;
//					Gets the position of the new character relative to the contents array
					newColumn = contentsList.indexOf(character) - column + newColumn;
//					Appends the string value of the position to the result string
					result = result + contentsList.get(newColumn);
				}
			}
			else if (operation == "Up") {
				for (String character : plaintextSplit) {
					int newPos = (contentsList.indexOf(character) + (offset * 6)) % 36;
//					Appends the string value of the position to the result string
					result = result + contentsList.get(newPos);
				}
			}
			else if (operation == "Down") {
				for (String character : plaintextSplit) {
					int newPos = (contentsList.indexOf(character)  + ((6 - offset) * 6)) % 36;
//					Appends the string value of the position to the result string
					result = result + contentsList.get(newPos);
				}
			}
			else if (operation == "X") {
				for (String character : plaintextSplit) {
//					Finds the mirrored opposite of the current value e.g. 1 is 36, 13 is 24 etc.
					int newPos = Math.abs(contentsList.indexOf(character) - 35) % 36;
//					Appends the string value of the position to the result string
					result = result + contentsList.get(newPos);
				}
			}
		}
		return result;
	}
}
