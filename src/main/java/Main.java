import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public List<Product> select(Connection conn, String sql, String className) throws Exception {

        try (Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(sql);

            Product product = new Product();
            List<Product> products = new ArrayList<>();
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
                product = (Product) obj;
                products.add(product);
            }
            rs.close();

            return products;
        }
    }
}
