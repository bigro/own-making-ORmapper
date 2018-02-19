import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BasicQuery {

    public <T> List<T> select(Connection conn, String sql, Class<T> clazz) throws Exception {

        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stmt = conn.createStatement();
             ResultSet resultSet = stmt.executeQuery(sql)) {

            T object = null;
            List<T> objects = new ArrayList<>();

            while (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                T temp = clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {

                    String columnLabel = metaData.getColumnLabel(i);
                    Field field = clazz.getDeclaredField(columnLabel);

                    field.setAccessible(true);
                    Object columnValue = resultSet.getObject(i);

                    field.set(temp, columnValue);
                }

                object = temp;
                objects.add(object);
            }

            conn.commit();
            return objects;
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
