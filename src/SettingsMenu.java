import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.xwpf.usermodel.*;

public class SettingsMenu extends JPanel{
	
	GridBagConstraints gc;
	private BufferedReader reader;
	
//	The default settings menu
	public SettingsMenu() {
		gc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		setBackground(Color.WHITE);
		
//		Change grid option
		JButton changeGridButton = new JButton("Change Grid");
		changeGridButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
//						Function to change the grid layout
						changeGrid();
					}
				});
		add(changeGridButton);
		
//		Auto translate
		JButton autoTranslateButton = new JButton("Auto-Translate");
		autoTranslateButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
//							Function to begin autoTranslate
							autoTranslate();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				});
		add(autoTranslateButton);
	}
	
//	Set up squares so the user can see the grid, this time using text fields to allow the user to edit
	public JTextField createSquare(String contents) {
		JTextField square = new JTextField();
		square.setText(contents);
		square.setFont(new Font("Arial", Font.BOLD, 45));
		square.setHorizontalAlignment(WIDTH/2);
		square.setPreferredSize(new Dimension(80, 80));
		return square;
	}

	public void changeGrid() {
//		Hide all currently visible components
		for (Component component : getComponents()) {
			component.setVisible(false);
		}
		
		JPanel gridPanel = new JPanel(new GridLayout(6, 6));
		JTextField[][] grid = new JTextField[6][6];
		ArrayList<String> contents = MainGUI.getContents();
		int counter = 0;
		
		for (int i = 0; i < 6; i ++) {
			for (int j = 0; j < 6; j ++) {
				JTextField square = createSquare(contents.get(counter));
				counter = counter + 1;
				grid[j][i] = square;
				gridPanel.add(grid[j][i]);
			}
		}
		
//		Reinitialise the change grid button
		JButton changeGridButton = new JButton("Change Grid");
		changeGridButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// Fetch and store the new table for checking
						contents.clear();
						for (Component component: gridPanel.getComponents()) {
							JTextField square = (JTextField)component;
							contents.add(square.getText().toUpperCase());
						}
						
//						If the new grid is valid
						if (checkNewGrid(contents)) {
							String result = "";
							for (String letter : contents) {
								result = result + letter + " ";
							}
							
//							Rewrite the new grid into Grids/Grid.txt file
							PrintWriter writer;
							try {
								writer = new PrintWriter("Grids/Grid.txt");
								writer.print(result);
								writer.close();
								MainGUI.updateSquares();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	
//							Inform the user
							JOptionPane.showMessageDialog(getParent(), "The grid has been succesfully changed", "Grid Changed", JOptionPane.DEFAULT_OPTION);
						}
					}
				});
		
		gc.gridx = 0;
		gc.gridy = 0;
		add(gridPanel, gc);
		
		gc.gridy = 1;
		add(changeGridButton, gc);
	}
	
//	Function that checks if the new grid is valid
	public Boolean checkNewGrid(ArrayList<String> contents) {
		HashMap<String, Integer> checkMap = new HashMap<String, Integer>();
		for (String letter : contents) {
//			Checks for exactly one character per square
			if (letter.length() > 1) {
				JOptionPane.showMessageDialog(getParent(), "There must be only one character per square.", "Multiple characters found", JOptionPane.ERROR_MESSAGE);
				return false;
			}
//			Checks for exactly one character per square
			else if (letter.length() < 1) {
				JOptionPane.showMessageDialog(getParent(), "Each square must have one character.", "No characters found", JOptionPane.ERROR_MESSAGE);
				return false;
			}
//			Checks that the character is a letter or is a number
			else if (!Character.isLetter(letter.charAt(0)) && !Character.isDigit(letter.charAt(0))) {
				JOptionPane.showMessageDialog(getParent(), "Each square must contain one character that is a number or a letter", "Incorrect symbol found", JOptionPane.ERROR_MESSAGE);
				return false;
			}
//			Checks for duplicate characters
			else if (checkMap.containsKey(letter)) {
				JOptionPane.showMessageDialog(getParent(), "Duplicate character found", "Duplicate found", JOptionPane.ERROR_MESSAGE);
				return false;
			}
//			The new grid is valid
			else {
				checkMap.put(letter, 1);
			}
		}
		return true;
	}
	
//	Auto translates a given document and returns it in a better format
//	NOTE: the word document does not produce the same encryption as the text file, both are still valid, it's just so you can have two encryptions of the same plaintext
	public void autoTranslate() throws IOException {
//		Allow the user to select a .txt document to encode
		JFileChooser chooser = new JFileChooser();
		File workingDirectory = new File(System.getProperty("user.dir"));
		chooser.setCurrentDirectory(workingDirectory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		chooser.setFileFilter(filter);
		chooser.showOpenDialog(null);
		File f = chooser.getSelectedFile();
		String filename = f.getAbsolutePath();
		
//		Read the file
		FileReader file = new FileReader(filename);
		reader = new BufferedReader(file);
		String line = reader.readLine();
			
//		Stores each message
		ArrayList<String> contents = new ArrayList<String>();
		while (line != null) {
			line = line.replaceAll("\\s+", "");
			contents.add(line);
			line = reader.readLine();
		}
		
//		Create a new encoder for the current grid
		Encoder encoder = new Encoder(MainGUI.getContents());
//		Create a text document for the results
//		NOTE: the word document does not produce the same encryption as the text file, both are still valid, it's just so you can have two encryptions of the same plaintext
		PrintWriter writer = new PrintWriter("translate_results.txt");
		Random rand = new Random();
		String operation = "";
//		For each word to encrypt
		for (String code : contents) {
//			Select a random operation
			int randomOperation = rand.nextInt(5);
//			Select a random offset
			int randomOffset = rand.nextInt(5) + 1;
			
//			Reformat the values for the encoder
			if (randomOperation == 0) {
				operation = "Left";
			}
			else if (randomOperation == 1) {
				operation = "Right";
			}
			else if (randomOperation == 2) {
				operation = "Up";
			}
			else if (randomOperation == 3) {
				operation = "Down";
			}
			else if (randomOperation == 4) {
				operation = "X";
			}
//			Encode the message and add it to the file
			writer.println(code + " " + operation.substring(0, 1) + randomOffset + " " + encoder.encode(code, operation, randomOffset));
		}
		writer.close();
		JOptionPane.showMessageDialog(getParent(), "All codewords within the file 'translate.txt' have been translated, any unexpected results are due to poor formatting e.g. not having one codeword per line", "Auto-translate successful", JOptionPane.DEFAULT_OPTION);
	
//		Produces a word document for nicer formatting
//		NOTE: the word document does not produce the same encryption as the text file, both are still valid, it's just so you can have two encryptions of the same plaintext
		// Blank document
		XWPFDocument document = new XWPFDocument();
		
		// Write the document into the file system
		FileOutputStream out = new FileOutputStream(new File("Bletchley Questions Sheet.docx"));
		
		// create table
		XWPFTable table = document.createTable();	
	
		// create first row
		XWPFTableRow tableRowOne = table.getRow(0);
		XWPFRun run = tableRowOne.getCell(0).addParagraph().createRun();
		run.setBold(true);
		run.setText("Operation");
		run = tableRowOne.addNewTableCell().addParagraph().createRun();
		run.setBold(true);
		XWPFParagraph paragraph = run.getParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run.setText("Codeword");
//		Set column widths
		table.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1000));
		table.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3000));
		
//		Stores all values (plaintext, operation and offset, cyphertext)
		String[][] finishedRows = new String[contents.size()][3];
		int i = 0;
		// all other rows
		for (String code : contents) {
			finishedRows[i][2] = code;
			int randomOperation = rand.nextInt(5);
			int randomOffset = rand.nextInt(5) + 1;
//			Decides if the offset should be removed (makes the decryption harder for the other person)
			int offsetTruncation = rand.nextInt(3);
			
			if (randomOperation == 0) {
				operation = "Left";
			}
			else if (randomOperation == 1) {
				operation = "Right";
			}
			else if (randomOperation == 2) {
				operation = "Up";
			}
			else if (randomOperation == 3) {
				operation = "Down";
			}
			else if (randomOperation == 4) {
				operation = "X";
			}
			
			finishedRows[i][0] = operation.substring(0, 1) + randomOffset;
			finishedRows[i][1] = encoder.encode(code, operation, randomOffset);
			i = i + 1;
			writer.println(code + " " + operation.substring(0, 1) + randomOffset + " " + encoder.encode(code, operation, randomOffset));
			XWPFTableRow row = table.createRow();
			
			run = row.getCell(0).addParagraph().createRun();
			run.setFontSize(20);
//			1 in 3 chance the offset is truncated
			if ((offsetTruncation == 2 && randomOperation != 4) || randomOperation == 4) {
//				Add the operation to the word table without the offset
				run.setText(operation.substring(0, 1));
			}
//			2 in 3 chance the offset is not truncated
			else {
//				Add the operation and the offset to the word table
				run.setText(operation.substring(0, 1) + randomOffset);
			}
			paragraph = run.getParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			
			run = row.getCell(1).addParagraph().createRun();
			run.setFontSize(20);
			run.setText(encoder.encode(code, operation, randomOffset));
			paragraph = run.getParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
		}
		
		// Write and close document
		document.write(out);
		out.close();
	
		// ANSWER SHEET
		// Blank document
		document = new XWPFDocument();
				
		// Write the document into the file system
		out = new FileOutputStream(new File("Bletchley Answer Sheet.docx"));
				
		// create table
		table = document.createTable();	
			
		// create first row
		tableRowOne = table.getRow(0);
		run = tableRowOne.getCell(0).addParagraph().createRun();
		run.setBold(true);
		run.setText("Operation");
		run = tableRowOne.addNewTableCell().addParagraph().createRun();
		run.setBold(true);
		paragraph = run.getParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run.setText("Codeword");
		run = tableRowOne.addNewTableCell().addParagraph().createRun();
		run.setBold(true);
		paragraph = run.getParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);
		run.setText("Plaintext");
//		Set column widths
		table.getRow(0).getCell(0).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(1000));
		table.getRow(0).getCell(1).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3000));
		table.getRow(0).getCell(2).getCTTc().addNewTcPr().addNewTcW().setW(BigInteger.valueOf(3000));
		
		// all other rows
		for (String[] finishedRow: finishedRows) {
//			Add the cyphertext, operation and plaintext to the word table
			XWPFTableRow row = table.createRow();
			run = row.getCell(0).addParagraph().createRun();
			run.setText(finishedRow[0]);
			paragraph = run.getParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			
			run = row.getCell(1).addParagraph().createRun();
			run.setText(finishedRow[1]);
			paragraph = run.getParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
			
			run = row.getCell(2).addParagraph().createRun();
			run.setText(finishedRow[2]);
			paragraph = run.getParagraph();
			paragraph.setAlignment(ParagraphAlignment.CENTER);
		}
				
		// Write and close document
		document.write(out);
		out.close();		
	}	
}	