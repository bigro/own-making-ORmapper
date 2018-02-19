public class Product {
    private int pid;
    private String name;
    private int price;

    public int pid() {
        return pid;
    }

    @Override
    public String toString() {
        return "Product{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
