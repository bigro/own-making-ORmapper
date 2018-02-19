import org.junit.Test;

import java.sql.*;
import java.util.List;

public class JdbcTest {
    String hostname = "localhost"; // 自分のものに書き換える
    String dbname = "hello_database"; // 自分のものに書き換える
    String username = "hello"; // 自分のものに書き換える
    String password = "abcdef"; // 自分のものに書き換える

    @Test
    public void mapping() throws Exception {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            prepare(stmt);

            String sql = "SELECT * FROM products WHERE price >= 100";
            BasicQuery basicQuery = new BasicQuery();
            List<Product> products = basicQuery.select(conn, sql, Product.class);
            products.forEach(System.out::println);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://" + hostname + ":5432/" + dbname, username, password);
    }

    private void prepare(Statement stmt) throws SQLException {
        System.out.println("接続成功");

        stmt.executeUpdate("DROP TABLE IF EXISTS products");

        stmt.executeUpdate("CREATE TABLE products (pid INTEGER, name VARCHAR(20), price INTEGER, PRIMARY KEY (pid))");
        System.out.println("テーブル作成");

        stmt.executeUpdate("INSERT INTO products VALUES(1, 'AAA', 100)");
        stmt.executeUpdate("INSERT INTO products VALUES(2, 'BBB', 80)");
        stmt.executeUpdate("INSERT INTO products VALUES(3, 'CCC', 220)");
        System.out.println("データ挿入");
    }
}
