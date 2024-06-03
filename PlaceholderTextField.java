package System;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

class PlaceholderTextField extends JTextField {
	final private String placeholder;

	public PlaceholderTextField(String placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (getText().isEmpty()) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(Color.GRAY);
			g2.drawString(placeholder, getInsets().left, g.getFontMetrics().getMaxAscent() + getInsets().top);
			g2.dispose();
		}
	}
}