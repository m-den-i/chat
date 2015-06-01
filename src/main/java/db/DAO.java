package db;

import dbaccess.DAOException;

import java.sql.*;
import java.time.LocalDate;

/**
 * Created by denis on 2.3.15.
 * Data access object
 */
public class DAO {
    protected JDBCConnector connector;
    private Connection conn;

    /**
     * DAO constructor
     * @throws dbaccess.DAOException
     */
    public DAO() throws dbaccess.DAOException {
        connector = new JDBCConnector();
        try {
            conn = connector.getConnection();
        } catch (JDBCConnectionException e){
            throw new dbaccess.DAOException("Can't connect to database!", e);
        }
    }

    /**
     * Closing connection
     * @throws dbaccess.DAOException
     */
    public void closeConnection() throws dbaccess.DAOException {
        try {
            connector.close();
        } catch (JDBCConnectionException e){
            throw new dbaccess.DAOException("Can't connect to database!", e);
        }
    }

    /**
     * Returns all orders by user ID
     * @param idUser ID of client
     * @return
     * @throws dbaccess.DAOException
     */
    public String getAllOrdersByID(int idUser) throws dbaccess.DAOException {
        String  res = "";
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT Order_ID," +
                    " OrderDate, DeliveryDate FROM `Order` NATURAL JOIN User WHERE User_ID = " + idUser + ";");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                res += "ID = " + rs.getString(1) + ", Order date = " + rs.getString(2) + ", Delivery date = "
                        + rs.getString(3)+ "\n";
            }

        } catch (SQLException e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
        return res;
    }

    /**
     * Returns all orders by user login
     * @param name Login of client
     * @return
     * @throws dbaccess.DAOException
     */
    public String getAllOrdersByUserLogin(String name) throws dbaccess.DAOException {
        return getAllOrdersByID(findUserByLogin(name));
    }

    /**
     * Returns User ID by his login
     * @param login Login of client
     * @return User ID
     * @throws dbaccess.DAOException
     */
    public int findUserByLogin(String login) throws dbaccess.DAOException {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT User_ID" +
                    " FROM User WHERE Name = \"" + login + "\";");
            ResultSet rs = statement.executeQuery();
            return rs.getInt(1);
        } catch (SQLException e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Returns info about all users
     * @return Info about all users
     * @throws dbaccess.DAOException
     */
    public String findAllUsers()throws dbaccess.DAOException {
        try {
            String res = "";
            PreparedStatement statement = conn.prepareStatement("SELECT User_ID," +
                    " Name FROM User;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                res += "ID = " + rs.getInt(1) + ", Name = " + rs.getString(2) + "\n";
            }
            return res;
        } catch (SQLException e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Get info about all products
     * @return Info about available products
     * @throws dbaccess.DAOException
     */
    public String getProducts() throws dbaccess.DAOException {
        try {
            String res = "";
            PreparedStatement statement = conn.prepareStatement("SELECT Product_ID," +
                    " Name FROM Product;");
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                res += "ID = " + rs.getString(1) + ", Name = " + rs.getString(2) + "\n";
            }
            return res;
        } catch (SQLException e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Returns all orders that should be delivered in
     * period between "from" and "to"
     * @param stringDateFrom
     * @param stringDateTo
     * @return String with data
     * @throws dbaccess.DAOException
     */
    public String getByDeliveryDate(String stringDateFrom, String stringDateTo) throws dbaccess.DAOException {
        try {
            String res = "";
            Date dateFrom = Date.valueOf(stringDateFrom), dateTo = Date.valueOf(stringDateTo);
            PreparedStatement statement = conn.prepareStatement("select Order_ID, User_ID, Admin_ID from " +
                    "`Order` where DeliveryDate between \"" +
                   dateFrom.toString() + "\" AND \"" + dateTo.toString() + "\";" );
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                res += "Order = " + rs.getString(1) + ", User = " + rs.getString(2) +
                        ", Admin = " + rs.getString(3) +"\n";
            }
            return res;
        } catch (Exception e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Returns all info about overdued orders
     * @return String with data
     * @throws dbaccess.DAOException
     */
    public String overdueOrders() throws dbaccess.DAOException {
        try {
            String res = "";

            Date now = Date.valueOf(LocalDate.now());
            PreparedStatement statement = conn.prepareStatement("select Order_ID, User_ID, Admin_ID from " +
                    "`Order` where DeliveryDate < \"" +
                    now.toString() + "\" or ApprovingDate > DeliveryDate;" );
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                res += "Order = " + rs.getString(1) + ", User = " + rs.getString(2) +
                        ", Admin = " + rs.getString(3) +"\n";
            }
            return res;
        } catch (Exception e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Registering order
     * @param idUser ID of User whose order
     * @param idProduct ID of product in order
     * @param amount Amount of products
     * @param deliveryDays Days to deliver orders
     * @return boolean true if successful false otherwise
     * @throws dbaccess.DAOException
     */
    public boolean makeOrder(int idUser, int [] idProduct, int [] amount, int deliveryDays) throws dbaccess.DAOException {
        try {
            if (amount.length != idProduct.length) return false;
            PreparedStatement orderStatement = conn.prepareStatement("select order_id from `Order` " +
                    "order by order_id desc;");
            ResultSet rs = orderStatement.executeQuery();
            rs.next();
            int orderNum = rs.getInt(1) + 1;
            String sqlInsert = "insert into `Order` values (?, Now(), (Now() + interval " + deliveryDays + " day), 1, ?, null)";
            orderStatement = conn.prepareStatement(sqlInsert);
            orderStatement.setInt(1, orderNum);
            orderStatement.setInt(2, idUser);
            orderStatement.executeUpdate();
            sqlInsert = "insert into `Order_Product` (Order_ID, Product_ID, Amount) value (?, ?, ?)";
            PreparedStatement orderProductStatement = conn.prepareStatement(sqlInsert);
            for (int i = 0; i < idProduct.length; i++){
                orderProductStatement.setInt(1, orderNum);
                orderProductStatement.setInt(2, idProduct[i]);
                orderProductStatement.setInt(3, amount[i]);
            }
            orderProductStatement.executeUpdate();
            return true;
        } catch (SQLException e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Returns all orders that haven't been approved
     * @return String with not approved orders
     * @throws dbaccess.DAOException
     */
    public String uncheckedOrders() throws dbaccess.DAOException {
        try {
            String res = "";
            Date now = Date.valueOf(LocalDate.now());
            PreparedStatement statement = conn.prepareStatement("select Order_ID, User_ID, Admin_ID from " +
                    "`Order` where ApprovingDate is NULL;" );
            ResultSet rs = statement.executeQuery();
            while (rs.next()){
                res += "Order = " + rs.getString(1) + ", User = " + rs.getString(2) +
                        ", Admin = " + rs.getString(3) +"\n";
            }
            return res;
        } catch (Exception e){
            throw new dbaccess.DAOException("Can't execute query", e);
        }
    }

    /**
     * Approve orders
     * @param idOrder ID of order to be approved
     * @throws dbaccess.DAOException
     */
    public void fixOrder(int idOrder) throws dbaccess.DAOException {
        try {
            PreparedStatement orderStatement = conn.prepareStatement("update `Order` set ApprovingDate = Now() where order_id = ?;");
            orderStatement.setInt(1, idOrder);
            orderStatement.executeUpdate();
        } catch (SQLException e){
            throw new DAOException("Can't execute query", e);
        }
    }
}