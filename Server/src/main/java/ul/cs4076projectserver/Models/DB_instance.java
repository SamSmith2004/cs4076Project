package ul.cs4076projectserver.Models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_instance {
    private String url;
    private String user;
    private String password;

    public void setUrl(String url) {this.url = url;}
    public void setUser(String user) {this.user = user;}
    public void setPassword(String password) {this.password = password;}

    public String getUrl() {return this.url;}
    public String getUser() {return this.user;}
    public String getPassword() {return this.password;}

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
