import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.regex.PatternSyntaxException;

public class DisplayQueryResults extends JFrame {
    private static final String DATABASE_URL =
            "jdbc:derby:C:\\---PATH---\\organizer\\lib\\files; create = true";
    private static final String DROP_QUERY = "DROP TABLE files";
    private static final String CREATE_QUERY = "CREATE TABLE files (" +
            "   fileID INT NOT NULL GENERATED ALWAYS AS IDENTITY," +
            "   fileName varchar (20) NOT NULL," +
            "   tags varchar (200) NOT NULL," +
            "   PRIMARY KEY (fileID))";

    private static final String INSERT_QUERY =
            "insert into files(fileName, tags) values ('Jonh Rod','histoty of compiler; costructor; meta data')";
    private static final String DEFAULT_QUERY = "SELECT * FROM files";
    private static ResultSetTableModel tableModel;

    public static void main(String[] args) {
        try {
            tableModel = new ResultSetTableModel(DATABASE_URL,
                    DROP_QUERY, CREATE_QUERY, INSERT_QUERY, DEFAULT_QUERY);
            final JTextArea queryArea = new JTextArea(null, 1, 50);
            queryArea.setWrapStyleWord(true);
            queryArea.setLineWrap(true);
            JScrollPane scrollPane = new JScrollPane(queryArea,
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            JButton submitButton = new JButton("Add file");
            Box boxNorth = Box.createHorizontalBox();
            boxNorth.add(scrollPane);
            boxNorth.add(submitButton);
            JTable resultTable = new JTable(tableModel);
            JLabel filterLabel = new JLabel("Filter:");
            final JTextField filterText = new JTextField();
            JButton filterButton = new JButton("Apply Filter");
            Box boxSouth = Box.createHorizontalBox();
            boxSouth.add(filterLabel);
            boxSouth.add(filterText);
            boxSouth.add(filterButton);
            JFrame window = new JFrame("Displaying Query Results");
            window.add(boxNorth, BorderLayout.NORTH);
            window.add(new JScrollPane(resultTable), BorderLayout.CENTER);
            window.add(boxSouth, BorderLayout.SOUTH);

            submitButton.addActionListener(
                    event -> {
                        try {
                            tableModel.insertFile(queryArea.getText().split(","));
                            tableModel.setQuery(DEFAULT_QUERY);
                            queryArea.setText("");
                        } catch (SQLException sqlException) {
                            JOptionPane.showMessageDialog(null,
                                    sqlException.getMessage(), "Database error",
                                    JOptionPane.ERROR_MESSAGE);
                            try {
                                tableModel.setQuery(DEFAULT_QUERY);
                                queryArea.setText("");
                            } catch (SQLException sqlException2) {
                                JOptionPane.showMessageDialog(null,
                                        sqlException2.getMessage(), "Database error",
                                        JOptionPane.ERROR_MESSAGE);
                                tableModel.disconnectFromDatabase();
                                System.exit(1);
                            }
                        }
                    }
            );
            final TableRowSorter<TableModel> sorter =
                    new TableRowSorter<>(tableModel);
            resultTable.setRowSorter(sorter);
            filterButton.addActionListener(
                    e -> {
                        String text = filterText.getText();
                        if (text.length() == 0)
                            sorter.setRowFilter(null);
                        else {
                            try {
                                sorter.setRowFilter(
                                        RowFilter.regexFilter(text, 2));
                            } catch (PatternSyntaxException pse) {
                                JOptionPane.showMessageDialog(null,
                                        "Bad regex pattern", "Bad regex pattern",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
            );
            window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            window.setSize(700, 250);
            window.setVisible(true);
            window.addWindowListener(
                    new WindowAdapter() {
                        public void windowClosed(WindowEvent event) {
                            tableModel.disconnectFromDatabase();
                            System.exit(0);
                        }
                    }
            );
        } catch (SQLException sqlException) {
            JOptionPane.showMessageDialog(null, sqlException.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            tableModel.disconnectFromDatabase();
            System.exit(1);
        }
    }
}

