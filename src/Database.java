
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Database {
    private Connection conn = null;
    private String protocol = "";
    private String host = "";
    private String db = "";
    private String user = "";
    private String password = "";
    private String driver = "";
    private int transactionCount = 0;

    public Connection getConnection() {
        return this.conn;
    }

    public String getCatalog() {
        return this.db;
    }

    private boolean getConnection(String fileName) throws DLException {
        try {
            if (fileName == null) {
                fileName = "/dbconfig.properties";
            }
            InputStream input = this.getClass().getResourceAsStream(fileName);
            Properties prop = new Properties();
            prop.load(input);
            this.protocol = prop.getProperty("protocol");
            this.host = prop.getProperty("host");
            this.db = prop.getProperty("db");
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
            this.driver = prop.getProperty("driver");
            return true;
        } catch (IOException e) {
            throw new DLException("IO Exception", e);
        }
    }

    private PreparedStatement prepareAndBind(String sql, List<String> params) throws DLException {
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.size(); i++) {
                    pstmt.setString(i + 1, params.get(i));
                }
            }
            return pstmt;
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }

    public Database() throws DLException {
        getConnection(null);
        try {
            Class.forName(this.driver);
            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.conn = DriverManager.getConnection(uri, this.user, this.password);
            //setting default autocommit to true
            this.conn.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new DLException("ClassNotFound", e);
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }

    public Database(String fileName) throws DLException {
        getConnection(fileName);
        try {
            Class.forName(this.driver);
            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.conn = DriverManager.getConnection(uri, this.user, this.password);
            this.conn.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new DLException("ClassNotFound", e);
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }

    public Database(String protocol, String host, String db, String user, String password, String driver) throws DLException {
        this.protocol = protocol;
        this.host = host;
        this.db = db;
        this.user = user;
        this.password = password;
        this.driver = driver;
        try {
            Class.forName(this.driver);
            String uri = this.protocol + "://" + this.host + "/" + this.db;
            this.conn = DriverManager.getConnection(uri, this.user, this.password);
            this.conn.setAutoCommit(true);
        } catch (ClassNotFoundException e) {
            throw new DLException("ClassNotFound", e);
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }

    public boolean close() throws DLException {
        try {
            this.conn.close();
            return true;
        } catch (SQLException e) {
            throw new DLException("SQL ExceptionL", e);
        }
    }

    //Performs Insert/delete update
    public int execute(String sql, List<String> params) throws DLException {
        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }


    public List<List<String>> getTable(String sql, List<String> params) throws DLException {
        return getTable(sql, params, false);
    }

    //Gets all the rows from Table
    public List<List<String>> getTable(String sql, List<String> params, boolean includeHeader) throws DLException {
        List<List<String>> data = new ArrayList<List<String>>();

        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            //Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            ArrayList<String> row = null;

            if (includeHeader) {
                row = new ArrayList<String>();
                for (int i = 1; i <= numCols; i++) {
                    row.add(rsmd.getColumnName(i));
                }
                data.add(row);
            }

            while (rs.next()) {
                row = new ArrayList<String>();
                for (int i = 1; i <= numCols; i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            return data;
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }

    //returns row
    public List<String> getRow(String sql, List<String> params) throws DLException {
        List<String> data = new ArrayList<String>();
        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            if (rs.next()) {
                for (int i = 1; i <= numCols; i++) {
                    data.add(rs.getString(i));
                }
            }
            return data;
        } catch (SQLException e) {
            DLException excep = new DLException("SQL Exception", e);
            excep.writeLog();
            throw excep;
        }
    }

    //Return Single value
    public String getValue(String sql, List<String> params) throws DLException {
        String data = new String();
        try {
            PreparedStatement stmt = prepareAndBind(sql, params);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                data = rs.getString(1);
            }
            return data;
        } catch (SQLException e) {
            throw new DLException("SQL Exception", e);
        }
    }

    //set autocommits to false
    public void startTransaction() throws DLException {
        transactionCount++;
        //checks for nested transaction
        if (transactionCount == 1) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                throw new DLException("SQL Exception", e);
            }
        }
    }

    //commits the sql
    public void commitTransaction() throws DLException {
        transactionCount--;
        if (transactionCount == 0) {
            try {
                conn.commit();
                conn.setAutoCommit(true);

            } catch (SQLException e) {
                throw new DLException("SQL Exception", e);
            }
        }
    }

    //rollbacks the sql
    public void rollbackTransaction() throws DLException {
        transactionCount--;
        if (transactionCount == 0) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
                //reset
                transactionCount = 0;
            } catch (SQLException e) {
                throw new DLException("SQL Exception", e);
            }
        }
    }

    //Simple getnewID calls overloaded method
    public String getNewId(String pkName, String tblName) throws DLException {
        return getNewId(pkName, tblName, null);
    }

    //getNewId
    public String getNewId(String pkName, String tblName, List<List<String>> fKeys) throws DLException {
        try {
            startTransaction();
            String sql = "SELECT MAX(" + pkName + ") FROM " + tblName;
//            List<String> params = new ArrayList<>();
//            params.add(column);
//            params.add(table);

            //increment the MAX by 1
            String id = getValue(sql, null);
            id = String.valueOf(Integer.parseInt(id) + 1);
            //place holders
            String columns = "", values = "";
            if (fKeys != null) {
                for (int i = 0; i < fKeys.size(); i++) {
                    columns += " ," + fKeys.get(i).get(0);
                    values += " ," + fKeys.get(i).get(1);
                }
            }
            String insertSql = "INSERT INTO " + tblName + " ( " + pkName + columns + ") VALUES (" + id + values + ")";
            execute(insertSql, null);
            commitTransaction();
            return id;
        } catch (Exception e) {
            rollbackTransaction();
            throw new DLException("SQL exception", e);
        }
    }

    public static void main(String[] args) throws DLException, SQLException {
        try {
            Database db = new Database();
            System.out.println("Database: " + db.getConnection().getCatalog());
            System.out.println("*****************");
            String id1 = db.getNewId("equipid", "equipment");
            System.out.println("New inserted id1: " + id1);

            List<List<String>> params = new ArrayList<>();
            List<String> param = new ArrayList<>();
            param.add("zip");
            param.add("14623");
            params.add(param);
            String id2 = db.getNewId("passengerid", "passenger", params);
            System.out.println("\nNew inserted id2: " + id2);

            System.out.println("\ndb.close : " + db.close());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//end main
}//end databa