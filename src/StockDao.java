interface StockDao {

    void show () throws Exception ;

    String menu();

    boolean generate(String cmd) throws Exception;

    boolean displayProduct(String cmd) throws Exception;

    boolean viewProduct(String cmd) throws Exception;

    boolean insertProduct(String cmd);

    boolean updateProduct(String cmd) throws Exception;

    boolean deleteProduct(String cmd) throws Exception;

    boolean searchProduct(String cmd) throws Exception;

    boolean setting(String cmd) throws Exception;

    boolean backup(String cmd) throws InterruptedException;

    boolean save(String cmd) throws InterruptedException;

    boolean restore(String cmd) throws InterruptedException;

    boolean help(String cmd);

    boolean exit(String cmd) throws InterruptedException;
}
