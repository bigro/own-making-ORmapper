import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcTest {
    String hostname = "localhost"; // 自分のものに書き換える
    String dbname = "hello_database"; // 自分のものに書き換える
    String username = "hello"; // 自分のものに書き換える
    String password = "abcdef"; // 自分のものに書き換える

    @Test
    public void name() throws Exception {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()
        ) {

            prepare(stmt);

            ResultSet rs = stmt
                    .executeQuery("SELECT * FROM products WHERE price >= 100");
            System.out.println("選択");

            List<Products> products = new ArrayList<>();

            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                Products product = new Products();
                Class clazz = Class.forName("Products");
                Object obj = clazz.newInstance();

                for (int i = 1; i <= columnCount; i++) {

                    String columnLabel = metaData.getColumnLabel(i);

                    //フィールド取得
                    Field field = clazz.getDeclaredField(columnLabel);

                    //アクセス可能にする
                    field.setAccessible(true);
                    Object object1 = rs.getObject(i);

                    //フィールドにセットする
                    field.set(obj, object1);
                }
                product = (Products) obj;
                System.out.println(product);
            }
            rs.close();

            stmt.executeUpdate("DROP TABLE products");
            System.out.println("テーブル削除");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void mapping() throws Exception {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM products WHERE price >= 100";
            Products products = select(conn, sql, Products.class.getSimpleName());
            System.out.println(products);
        }
    }

    private Products select(Connection conn, String sql, String className) throws Exception {

        try (Statement stmt = conn.createStatement()) {

            prepare(stmt);

            ResultSet rs = stmt.executeQuery(sql);

            Products product = new Products();

            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                Class clazz = Class.forName(className);
                Object obj = clazz.newInstance();

                for (int i = 1; i <= columnCount; i++) {

                    String columnLabel = metaData.getColumnLabel(i);

                    Field field = clazz.getDeclaredField(columnLabel);

                    field.setAccessible(true);
                    Object object1 = rs.getObject(i);

                    field.set(obj, object1);
                }
                product = (Products) obj;
            }
            rs.close();

            stmt.executeUpdate("DROP TABLE products");

            return product;
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://" + hostname + ":5432/" + dbname, username, password);
    }

    private void prepare(Statement stmt) throws SQLException {
        System.out.println("接続成功");

        stmt.executeUpdate("CREATE TABLE products (pid INTEGER, name VARCHAR(20), price INTEGER, PRIMARY KEY (pid))");
        System.out.println("テーブル作成");

        stmt.executeUpdate("INSERT INTO products VALUES(1, 'AAA', 100)");
        stmt.executeUpdate("INSERT INTO products VALUES(2, 'BBB', 80)");
        stmt.executeUpdate("INSERT INTO products VALUES(3, 'CCC', 220)");
        System.out.println("データ挿入");
    }
}
