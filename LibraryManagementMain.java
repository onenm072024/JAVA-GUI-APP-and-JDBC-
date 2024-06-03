package System;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;

public class LibraryManagementMain extends JFrame {
//declarations
	PlaceholderTextField bookYear, bookId, bookTitle, writer;
	DefaultTableModel tableModel;
	JTable bookTable;
	JButton btnAdd, btnDelete, btnRefresh;

	Connection con;
	ResultSet rs;
	Statement s;

	// this is our model class
	public LibraryManagementMain() {
		JPanel SystemLayout = new JPanel(new BorderLayout()); // container used to hold and organize app components like
																// buttons
		setTitle("Library System");
		setSize(800, 600);// takes width and height of the application
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null); // used to place the application in the center of the screen
		getContentPane().setLayout(new BorderLayout());

		// Buttons
		JPanel buttons = new JPanel();
		btnAdd = new JButton("Add Book");
		btnDelete = new JButton("Delete Book");
		btnRefresh = new JButton("Refresh");
		buttons.add(btnAdd);
		buttons.add(btnDelete);
		buttons.add(btnRefresh);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		// SystemLayout.add(buttons, BorderLayout.SOUTH);

		// books table
		tableModel = new DefaultTableModel(new String[] { "BookID", "Title", "Author", "Year" }, 0);

		bookTable = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(bookTable);
		bookTable.setDefaultRenderer(Object.class, new AlternatingRowColorRenderer());
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		// SystemLayout.add(scrollPane, BorderLayout.CENTER);

		// Adding new books to form
		JPanel mainForm = new JPanel(new GridLayout(4, 2));
		mainForm.add(new JLabel("BookID:"));
		bookId = new PlaceholderTextField("Auto-generated");
		mainForm.add(bookId);
		bookId.setEditable(false);

		mainForm.add(new JLabel("Title:"));
		bookTitle = new PlaceholderTextField("Enter book title");
		mainForm.add(bookTitle);

		mainForm.add(new JLabel("Author:"));
		writer = new PlaceholderTextField("Enter book author");
		mainForm.add(writer);

		mainForm.add(new JLabel("Year:"));
		bookYear = new PlaceholderTextField("Enter publication year");

		// Set the document filter to only allow numeric input
		PlainDocument yearDocument = (PlainDocument) bookYear.getDocument();
		yearDocument.setDocumentFilter(new NumericDocumentFilter());
		mainForm.add(bookYear);

		getContentPane().add(mainForm, BorderLayout.NORTH);
		// SystemLayout.add(mainForm, BorderLayout.NORTH);

		// Event listeners
		btnAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addBook();
			}
		});

		btnDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteBook();
			}
		});

		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshBookList();
			}
		});

		// Connect to the database
		try {
			connectToDatabase();
		} catch (ClassNotFoundException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		refreshBookList();

	}

	private void deleteBook() {
		int selectedRow = bookTable.getSelectedRow();
		if (selectedRow >= 0) {
			int option = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete this book?\nThis action cannot be undone.", "Confirm Deletion",
					JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				int bookID = (int) tableModel.getValueAt(selectedRow, 0);

				try {
					String query = "DELETE FROM Books WHERE BookID = ?";
					PreparedStatement statement = con.prepareStatement(query);
					statement.setInt(1, bookID);
					statement.executeUpdate();

					JOptionPane.showMessageDialog(this, "Book deleted successfully!");
					refreshBookList();
				} catch (SQLException e) {
					JOptionPane.showMessageDialog(this, "Error deleting book: " + e.getMessage());
				}
			}
		} else {
			JOptionPane.showMessageDialog(this, "Please select a book to delete.");
		}
	}

	private void connectToDatabase() throws ClassNotFoundException, SQLException { // for proper DB handling
		if (con != null) {// closes the connection
			try {
				con.close();
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Error connecting to the database: " + e.getMessage());
			}
		}
		try {
			// Load JDBC driver
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			// Connection URL
			String url = "jdbc:ucanaccess://C:\\Users\\usetr\\Desktop\\gb.accdb";
			// Establish connection
			con = DriverManager.getConnection(url);
			s = con.createStatement();
			System.out.println("Connected to database");
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error connecting to the databae " + e.getMessage());
		}
	}

	private void refreshBookList() {
		tableModel.setRowCount(0); // clear the table
		try {
			String query = "SELECT * FROM Books";
			rs = s.executeQuery(query);
			while (rs.next()) {
				int bookID = rs.getInt("BookID");
				String title = rs.getString("Title");
				String author = rs.getString("Author");
				String year = rs.getString("Year");
				tableModel.addRow(new Object[] { bookID, title, author, year });
				clearForm();
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error retrieving book list: " + e.getMessage());
		}
	}

	private void addBook() {
		if (!validateForm()) { // checks the validation
			return;
		}
		String bookID = bookId.getText();
		String title = bookTitle.getText();
		String author = writer.getText();
		int year = Integer.parseInt(bookYear.getText().trim());

		try {
			year = Integer.parseInt(bookYear.getText());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter a valid year.");
			return;
		}

		if (isBookInDatabase(title, author, year)) {
			JOptionPane.showMessageDialog(this, "This book already exists in the database.");
			return;
		}

		try {
			String query = "INSERT INTO Books (Title, Author, Year) VALUES(?,?,?)";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, title);
			statement.setString(2, author);
			statement.setInt(3, year);
			statement.executeUpdate();

			// s.executeUpdate(query);
			// System.out.println("Book added successfully");

			JOptionPane.showMessageDialog(this, "Book added successfully!");
			clearForm();
			refreshBookList();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error adding book: " + e.getMessage());
		}
	}

	// clears form once the book is successfully added to the DB
	private void clearForm() {
		bookYear.setText("");
		bookTitle.setText("");
		writer.setText("");

	}

//validates the form
	private boolean validateForm() {
		if (bookTitle.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Title cannot be empty.");
			return false;
		}
		if (writer.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Author cannot be empty.");
			return false;
		}
		if (bookYear.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Year cannot be empty.");
			return false;
		}
		try {
			Integer.parseInt(bookYear.getText().trim());
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Please enter a valid year.");
			return false;
		}
		return true;
	}

//checks if the book being added is already in the DB
	private boolean isBookInDatabase(String title, String author, int year) {
		try {
			String query = "SELECT COUNT(*) FROM Books WHERE Title = ? AND Author = ? AND Year = ?";
			PreparedStatement statement = con.prepareStatement(query);
			statement.setString(1, title);
			statement.setString(2, author);
			statement.setInt(3, year);

			ResultSet resultSet = statement.executeQuery();
			resultSet.next(); // Move to the first row
			int count = resultSet.getInt(1);

			return count > 0; // Return true if the count is greater than 0
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Error checking book existence: " + e.getMessage());
			return false;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new LibraryManagementMain().setVisible(true);
			}
		});
	}

}
