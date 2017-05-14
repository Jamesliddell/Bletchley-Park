import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.*;

public class MainGUI extends JFrame{
	private JPanel contentPane;
	private Color backgroundColour;
	private static int counter;
	private static String contents;
	private static ArrayList<String> contentsList;
	private JComboBox<String> operationsSelection;
	private JComboBox<String> offsetSelection;
	private JTextField plaintextEntry;
	private JTextField codewordTextField;
	private SettingsMenu settingsMenu;
	private static JPanel bGridPanel;
	private static Encoder encoder;
	
//	Setup the GUI
	public MainGUI() {
		backgroundColour = new Color(87, 87, 87);
		counter = 0;
//		Fetch the current grid and store it in contents
		updateContents();
//		Create a new encoder for when the user wants to encrypt a message
		encoder = new Encoder(contentsList);

//		Design choices for the GUI
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		Auto full screen
		setSize(dim);
//		Non-resizable
		setResizable(false);
//		Remove OS header
		setUndecorated(true);
		
		contentPane = new JPanel(new GridBagLayout());
		contentPane.setBackground(backgroundColour);
		GridBagConstraints gc = new GridBagConstraints();
		
		// toolbarPanel
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weighty = 0.01;
		gc.weightx = 1;
		gc.anchor = GridBagConstraints.NORTHEAST;
		
		JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		toolbarPanel.setBackground(backgroundColour);
		
//		When the user wants to close the program, exit.
		Action CloseWindow = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		};
		
//		Exit "x" button
		JButton exitButton = new JButton("x");
		exitButton.setBackground(backgroundColour);
		exitButton.setForeground(new Color(157, 157, 157));
		exitButton.setFont(new Font("Calibri", Font.BOLD, 30));
		exitButton.setPreferredSize(new Dimension(20, 20));
		exitButton.setBorder(null);
		exitButton.addActionListener(CloseWindow);
		
		exitButton.addMouseListener(new MouseAdapter() {
		    public void mouseEntered(MouseEvent e) {
		        exitButton.setForeground(Color.WHITE);
		    }

		    public void mouseExited(MouseEvent e) {
		        exitButton.setForeground(new Color(157, 157, 157));
		    }
		});
		
//		Settings menu button
		JButton settingsButton = new JButton();
		settingsButton.setBackground(backgroundColour);
		settingsButton.setPreferredSize(new Dimension(20, 20));
		settingsButton.setBorder(null);
		ImageIcon settingsIconGrey = new ImageIcon("images/gearGrey.png");
		ImageIcon settingsIconWhite = new ImageIcon("images/gearWhite.png");
		settingsButton.setIcon(settingsIconGrey);
		
		toolbarPanel.add(settingsButton);
		toolbarPanel.add(exitButton);
		contentPane.add(toolbarPanel, gc);
		
		// Main panel
		
		gc.gridx = 0;
		gc.gridy = 1;
		gc.anchor = GridBagConstraints.NORTH;
		JPanel mainPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gcMain = new GridBagConstraints();
		
		gcMain.gridx = 0;
		gcMain.gridy = 0;
		
		bGridPanel = new JPanel(new GridLayout(6, 6));
		JButton[][] grid = new JButton[6][6];

//		Setup a 6 by 6 grid
		for (int i = 0; i < 6; i ++) {
			for (int j = 0; j < 6; j ++) {
//				counter / 6 is the row, counter % 6 is the column
				BletchleySquare square = new BletchleySquare(contentsList.get(counter), counter / 6, counter % 6);
				counter = counter + 1;
				grid[j][i] = square;
				bGridPanel.add(grid[j][i]);
			}
		}
		
//		Add the grid to the GUI
		mainPanel.add(bGridPanel, gcMain);
		
		gcMain.gridy = 1;
		
//		Options panel
		Border paddingBorder = BorderFactory.createEmptyBorder(0, 0, 0, 20);
		JPanel optionsPanel = new JPanel();
		
//		Operations
		JLabel operationsLabel = new JLabel("Operation:");
		String[] operationsList = {"Left", "Right", "Up", "Down", "X"};
		operationsSelection = new JComboBox<String>(operationsList);
		operationsSelection.setBorder(paddingBorder);
		optionsPanel.add(operationsLabel);
		optionsPanel.add(operationsSelection);
		
//		Offsets
		JLabel offsetLabel = new JLabel("Offset:");
		String[] offsetList = {"1", "2", "3", "4", "5"};
		offsetSelection = new JComboBox<String>(offsetList);
		offsetSelection.setBorder(paddingBorder);
		optionsPanel.add(offsetLabel);
		optionsPanel.add(offsetSelection);
		
//		Input plaintext
		JLabel plaintextLabel = new JLabel("Plaintext:");
		plaintextEntry = new JTextField(14);
		optionsPanel.add(plaintextLabel);
		optionsPanel.add(plaintextEntry);
		
		operationsSelection.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (operationsSelection.getSelectedItem() == "X") {
//							If the operation is x (mirrored) an offset is not needed
							offsetSelection.setEnabled(false);
						}
						else {
							offsetSelection.setEnabled(true);
						}
						
//						Encode the plaintext into cyphertext
						if (plaintextEntry.getText().length() != 0) {
							encode();
						}
					}
				});
		
//		Upon changing the offset, re encode the plaintext
		offsetSelection.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (plaintextEntry.getText().length() != 0) {
							encode();
						}
					}
			});
		
		mainPanel.add(optionsPanel, gcMain);
		
		gcMain.gridy = 2;
		JPanel resultPanel = new JPanel();
		
//		Resulting cyphertext
		JLabel codewordLabel = new JLabel("Codeword:");
		codewordTextField = new JTextField(14);
		codewordTextField.setEditable(false);
		resultPanel.add(codewordLabel);
		resultPanel.add(codewordTextField);
		mainPanel.add(resultPanel, gcMain);
		
//		When the plaintext input by the user changes, encode again
		plaintextEntry.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				encode();
			}
			public void removeUpdate(DocumentEvent e) {
				encode();
			}
			public void insertUpdate(DocumentEvent e) {
				encode();
			}
		});
		
//		Settings Menu
		settingsButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
//				If the settingsMenu hasn't already been created
				if (settingsMenu == null) {
					settingsMenu = new SettingsMenu();
					for (Component component : mainPanel.getComponents()) {
						component.setVisible(false);
					}
					mainPanel.add(settingsMenu);
				}
				else {
//					Close the settings menu
					if (settingsMenu.isVisible()) {
						for (Component component : mainPanel.getComponents()) {
							component.setVisible(true);
						}
						settingsMenu.setVisible(false);
					}
//					Open the settings menu
					else {
						for (Component component : mainPanel.getComponents()) {
							component.setVisible(false);
						}
						settingsMenu.setVisible(true);
					}
				}
			}
		    public void mouseEntered(MouseEvent e) {
		    	settingsButton.setIcon(settingsIconWhite);;
		    }

		    public void mouseExited(MouseEvent e) {
		        settingsButton.setIcon(settingsIconGrey);
		    }
		});
		
		contentPane.add(mainPanel, gc);
		setContentPane(contentPane);
		setVisible(true);
	}
	
//	Function to update all of the squares with the current grid.txt
	public static void updateSquares() {
//		Ensure that contents of grid.txt is up to date
		updateContents();
		counter = 0;
//		Set each square to have correct text
		for (Component component : bGridPanel.getComponents()) {
			BletchleySquare square = (BletchleySquare)component;
			square.setText(contentsList.get(counter));
			counter = counter + 1;
		}
	}
	
	public static void updateContents() {
		FileReader file = null;
		try {
//			Open grid.txt
			file = new FileReader("Grids/Grid.txt");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//CREATE FILE HERE
			e1.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(file);
		try {
//			update contents with the Grid.txt file
			contents = reader.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		contentsList = new ArrayList<String>(Arrays.asList(contents.split("\\s+")));
//		Create a new encoder for the new grid.
		encoder = new Encoder(contentsList);
	}
	
//	Encrypt the plaintext into cyphertext via the encoder class
	public void encode() {
		codewordTextField.setText(encoder.encode(plaintextEntry.getText(), (String)operationsSelection.getSelectedItem(), Integer.parseInt((String) offsetSelection.getSelectedItem())));
	}
	
	public static ArrayList<String> getContents() {
		return contentsList;
	}
	
	public static void main(String[] args) {
		new MainGUI();
	}
}
