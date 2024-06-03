package System;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class AlternatingRowColorRenderer extends DefaultTableCellRenderer {
	private final Color evenColor = new Color(173, 216, 230); // Light blue color for even rows
	private final Color oddColor = Color.WHITE; // Default color for odd rows

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (!isSelected) {
			c.setBackground(row % 2 == 0 ? evenColor : oddColor);
		}
		return c;
	}
}