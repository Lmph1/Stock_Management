import java.util.List;

interface TablePaginationDao {
    void show(List<Product> products) throws Exception;

    void printTable();

    String menu();

    boolean first(String cmd);

    boolean previous(String cmd);

    boolean next(String cmd);

    boolean last(String cmd);

    boolean setNumberOfRow(String cmd) throws Exception;

    boolean gotoPage(String cmd) throws Exception;
}
