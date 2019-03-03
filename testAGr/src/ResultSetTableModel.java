import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel {
    private final Connection connection;
    private final Statement statement;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private int numberOfRows;
    private boolean connectedToDatabase;

    public ResultSetTableModel(String url, String deleteQuery, String insert, String query) throws SQLException {
        connection = DriverManager.getConnection(url, null, null);
        statement = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        statement.execute(deleteQuery);
        statement.execute(insert);
        connectedToDatabase = true;
        setQuery(query);
    }

    public int getColumnCount() throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        try {
            return metaData.getColumnCount();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return 0;
    }
    public int getRowCount() throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        return numberOfRows;
    }

    public Object getValueAt(int row, int column)
            throws IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        try {
            resultSet.absolute(row + 1);
            return resultSet.getObject(column + 1);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return "";
    }

    public void insertFile(String[] column) throws SQLException, IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        final String INSERT_QUERY = "insert into files(fileName, tags) values ('" + column[0] + "','" + column[1] + "')";
        statement.execute(INSERT_QUERY);
    }

    public void setQuery(String query) throws SQLException, IllegalStateException {
        if (!connectedToDatabase)
            throw new IllegalStateException("Not Connected to Database");
        resultSet = statement.executeQuery(query);
        metaData = resultSet.getMetaData();
        resultSet.last();
        numberOfRows = resultSet.getRow();
        fireTableStructureChanged();
    }

    public void disconnectFromDatabase() {
        if (connectedToDatabase) {
            try {
                resultSet.close();
                statement.close();
                connection.close();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            } finally {
                connectedToDatabase = false;
            }
        }
    }
}
