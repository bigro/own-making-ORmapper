import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

public class JdbcTest {
    String hostname = "localhost"; // 自分のものに書き換える
    String dbname = "hello_database"; // 自分のものに書き換える
    String username = "hello"; // 自分のものに書き換える
    String password = "abcdef"; // 自分のものに書き換える

    @Test
    public void name() throws Exception {
        try (
                Connection conn = DriverManager.getConnection("jdbc:postgresql://" + hostname
                        + ":5432/" + dbname, username, password);
                Statement stmt = conn.createStatement()
        ) {

            prepare(stmt);

            ResultSet rs = stmt
                    .executeQuery("SELECT * FROM products WHERE price >= 100");
            System.out.println("選択");
            while (rs.next()) {
                ResultSetMetaData metaData = rs.getMetaData();
                String columnLabel = metaData.getColumnLabel(1);
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    Class clazz = Class.forName("Products");
                    Object obj = clazz.newInstance();
                    //フィールド取得
                    Field field = clazz.getDeclaredField(columnLabel);
                    //アクセス可能にする
                    field.setAccessible(true);
                    Object object1 = rs.getObject(1);
                    //フィールドにセットする
                    field.set(obj, object1);
                    Products products = (Products) obj;

                    System.out.println(columnLabel);
                }
            }
            rs.close();

            stmt.executeUpdate("DROP TABLE products");
            System.out.println("テーブル削除");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
