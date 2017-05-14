import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Each individual square on the grid is an extension of swings JButton
public class BletchleySquare extends JButton {
	
//	value is the character of the square, row and column is its position within the grid
	public BletchleySquare(String value, int row, int column) {
//		Design features of the square
		setFont(new Font("Arial", Font.BOLD, 45));
		setText(value);
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(80, 80));
	}
}
