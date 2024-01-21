import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TablePagination implements TablePaginationDao {

    private List<Product> products;

    private List<Product> list;

    private int totalRecord;

    private int numberOfRow;

    private int currentPage;

    private int getTotalPage() {
        return (int) Math.ceil((double) products.size() / (double) numberOfRow);
    }

    private List<Product> getList(int page) {
        int skip = (page - 1) * numberOfRow;
        return products.stream().skip(skip).limit(numberOfRow).collect(Collectors.toList());
    }

    @Override
    public void printTable() {
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        Table table = new Table(5, BorderStyle.UNICODE_DOUBLE_BOX, ShownBorders.ALL);
        table.setColumnWidth(0, 15, 15);
        table.setColumnWidth(1, 20, 20);
        table.setColumnWidth(2, 15, 15);
        table.setColumnWidth(3, 11, 11);
        table.setColumnWidth(4, 15, 15);

        table.addCell("ID", cellStyle);
        table.addCell("Name", cellStyle);
        table.addCell("Unit Price", cellStyle);
        table.addCell("Quantity", cellStyle);
        table.addCell("Imported Date", cellStyle);
        for (Product product : list) {
            table.addCell("" + product.getId() + "", cellStyle);
            table.addCell(product.getName());
            table.addCell("" + product.getUnitPrice() + "", cellStyle);
            table.addCell("" + product.getQuantity() + "", cellStyle);
            table.addCell(product.getImportedDate(), cellStyle);
        }
        table.addCell("Page : " + currentPage + " of " + getTotalPage(), cellStyle, 2);
        table.addCell("Total Record : " + totalRecord, cellStyle, 3);

        System.out.println(table.render());
    }

    @Override
    public String menu() {
        Table menu = new Table(7, BorderStyle.DESIGN_PAPYRUS, ShownBorders.ALL);
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        menu.setColumnWidth(0, 8, 8);
        menu.setColumnWidth(1, 10, 10);
        menu.setColumnWidth(2, 8, 8);
        menu.setColumnWidth(3, 8, 8);
        menu.setColumnWidth(4, 8, 8);
        menu.setColumnWidth(5, 10, 10);
        menu.setColumnWidth(6, 10, 10);

        menu.addCell("(F)irst", cellStyle);
        menu.addCell("(P)revious", cellStyle);
        menu.addCell("(N)ext", cellStyle);
        menu.addCell("(L)ast", cellStyle);
        menu.addCell("(G)oto", cellStyle);
        menu.addCell("(Se)t row", cellStyle);
        menu.addCell("(B)ack", cellStyle);

        System.out.println(menu.render());
        return Helper.inputText("=> Command : ");
    }

    @Override
    public boolean first(String cmd) {
        currentPage = 1;
        list = getList(currentPage);
        return true;
    }

    @Override
    public boolean previous(String cmd) {
        currentPage--;
        if (currentPage < 1) currentPage = 1;
        list = getList(currentPage);
        return true;
    }

    @Override
    public boolean next(String cmd) {
        currentPage++;
        int totalPage = getTotalPage();
        if (currentPage > totalPage) currentPage = totalPage;
        list = getList(currentPage);

        return true;
    }

    @Override
    public boolean last(String cmd) {
        currentPage = getTotalPage();
        list = getList(currentPage);
        return true;
    }

    @Override
    public boolean setNumberOfRow(String cmd) throws Exception {
        if (cmd.matches(Constant.ROW_REX)) {
            numberOfRow = Helper.parseInt(cmd.replace("se:", ""));
        } else
            numberOfRow = Helper.inputInteger("Please set number of row: ");
        list = getList(currentPage);
        return true;
    }

    @Override
    public boolean gotoPage(String cmd) throws Exception {
        int page;
        if (cmd.matches(Constant.GOTO_REX)) {
            page = Helper.parseInt(cmd.replace("g:", ""));
        } else
            page = Helper.inputInteger("Please enter page number: ");

        if (page > getTotalPage()) {
            Helper.showMessage("Page number is out of rage.");
        } else {
            currentPage = page;
            list = getList(currentPage);
        }
        return true;
    }

    @Override
    public void show(List<Product> products) throws Exception {
        this.numberOfRow = 5;
        this.currentPage = 1;
        this.products = products;
        this.list = getList(currentPage);
        this.totalRecord = this.products.size();

        Map<String, Command> commandMap = new CommandHashMap<>();
        commandMap.put("f", this::first);
        commandMap.put("p", this::previous);
        commandMap.put("n", this::next);
        commandMap.put("l", this::last);
        commandMap.put("g|" + Constant.GOTO_REX, this::gotoPage);
        commandMap.put("se|" + Constant.ROW_REX, this::setNumberOfRow);
        commandMap.put("b", (cmd) -> false);

        String command;
        do {
            this.printTable();
            command = this.menu();
            Command commandAction = commandMap.get(command);
            if (commandAction != null) {
                commandAction.execute(command);
            } else {
                System.out.println("Command not found.");
            }
        } while (!command.matches("b|B"));

    }

}
