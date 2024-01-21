import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Stock implements StockDao {

    private List<Product> products;

    private List<Product> recoveries;

    private Setting setting;

    private TablePaginationDao tablePagination;

    public Stock() throws InterruptedException {
        init();
    }

    private void init() throws InterruptedException {
        loadLogo();
        tablePagination = new TablePagination();
        long start = System.currentTimeMillis();
        System.out.println("Loading...");
        Task task = new Task((o) -> {
            loadSetting();
            checkRecovery();
        });
        task.start();
        task.join();
        System.out.println("Done. " + (System.currentTimeMillis() - start) + "ms");
    }

    private void checkRecovery() {
        products = loadProducts();
        if (setting.recovery) {
            recoveries = loadRecovery();
            boolean equal = Arrays.equals(recoveries.toArray(), products.toArray());
            if (!equal) {
                if (Helper.yesNoMessage("Do you want to save recovery data?") == 1) {
                    save(recoveries);
                    products = new LinkedList<>(recoveries);
                } else {
                    saveRecovery(products);
                    recoveries = new LinkedList<>(products);
                }
            }
        }

    }

    private void updateSetting(Setting setting) {
        try {
            File file = new File("data/setting.txt");
            File dir = new File(file.getPath().replace(file.getName(), ""));
            if (!dir.exists()) dir.mkdir();
            FileOutputStream output = new FileOutputStream(file);
            ObjectOutputStream objOutput = new ObjectOutputStream(output);
            objOutput.writeObject(setting);
            objOutput.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadSetting() {
        try {
            FileInputStream input = new FileInputStream("data/setting.txt");
            ObjectInputStream objInput = new ObjectInputStream(input);
            setting = (Setting) objInput.readObject();
            objInput.close();
        } catch (Exception e) {
            setting = new Setting();
            updateSetting(setting);
        }
    }

    private List<Product> loadRecovery() {
        return productReader("recovery/stock.txt");
    }

    private List<Product> loadProducts() {
        return productReader("data/stock.txt");
    }

    private void loadLogo() {
        System.out.println("""
                             
                ███████╗██╗███████╗███╗   ███╗    ██████╗ ███████╗ █████╗ ██████╗      ██████╗ ██████╗\s
                ██╔════╝██║██╔════╝████╗ ████║    ██╔══██╗██╔════╝██╔══██╗██╔══██╗    ██╔════╝ ╚════██╗
                ███████╗██║█████╗  ██╔████╔██║    ██████╔╝█████╗  ███████║██████╔╝    ██║  ███╗ █████╔╝
                ╚════██║██║██╔══╝  ██║╚██╔╝██║    ██╔══██╗██╔══╝  ██╔══██║██╔═══╝     ██║   ██║██╔═══╝\s
                ███████║██║███████╗██║ ╚═╝ ██║    ██║  ██║███████╗██║  ██║██║         ╚██████╔╝███████╗
                ╚══════╝╚═╝╚══════╝╚═╝     ╚═╝    ╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝╚═╝          ╚═════╝ ╚══════╝              
                """);
    }

    @Override
    public boolean generate(String cmd) throws Exception {
        if (cmd.matches(Constant.M_REX)) {
            int record = Helper.parseInt(cmd.replace("M", "")) * 1000000;
            if (record > 0) {
                System.out.println("Generating...");
                long startAt = System.currentTimeMillis();
                Task task = new Task((o) -> {
                    try {
                        File dr = new File("recovery");
                        File ds = new File("data");
                        if (!dr.exists()) dr.mkdir();
                        if (!ds.exists()) ds.mkdir();

                        PrintWriter writerR = new PrintWriter("recovery/stock.txt");
                        PrintWriter writer = new PrintWriter("data/stock.txt");
                        List<Product> list = new LinkedList<>();
                        for (int i = 0; i < record; i++) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            Product product = new Product((i + 1), "Record" + (i + 1), 2.5, 100, simpleDateFormat.format(new Date()));
                            list.add(product);
                            String data = product.getId() + "," + product.getName() + "," + product.getUnitPrice() + "," + product.getQuantity() + "," + simpleDateFormat.format(new Date());
                            writer.println(data);
                            writerR.println(data);
                        }
                        products = new LinkedList<>(list);
                        recoveries = new LinkedList<>(list);
                        writer.close();
                        writerR.close();

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        o.interrupt();
                    }
                });
                task.start();
                task.join();
                System.out.println("Done. " + (System.currentTimeMillis() - startAt) + "ms");

            }
        }
        return true;
    }

    @Override
    public boolean setting(String cmd) throws Exception {
        Map<String, Command> commandMap = new CommandHashMap<>();
        commandMap.put("r:(true|false)", (c) -> {
            String v = c.replace("[^true|false]+", "").replace("r:", "");
            setting.recovery = Boolean.parseBoolean(v);
            updateSetting(setting);
            return true;
        });
        commandMap.put("a:(true|false)", (c) -> {
            String v = c.replace("[^true|false]+", "").replace("a:", "");
            setting.autoSave = Boolean.parseBoolean(v);
            updateSetting(setting);
            return true;
        });
        commandMap.put("b", (c) -> false);

        String command;
        do {
            System.out.println(setting.toString());
            Table menu = new Table(7, BorderStyle.DESIGN_PAPYRUS, ShownBorders.ALL);
            CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
            menu.setColumnWidth(0, 26, 26);
            menu.setColumnWidth(1, 26, 26);
            menu.setColumnWidth(2, 10, 10);

            menu.addCell("(R)ecovery (:true :false)", cellStyle);
            menu.addCell("(A)uto Save (:true :false)", cellStyle);
            menu.addCell("(B)ack", cellStyle);


            System.out.println(menu.render());
            command = Helper.inputText("=> command: ");
            Command commandAction = commandMap.get(command);
            if (commandAction != null) {
                commandAction.execute(command);
            } else {
                System.out.println("Command not found.");
            }

        } while (!command.matches("b|B"));

        return false;
    }

    @Override
    public String menu() {
        Table menu = new Table(6, BorderStyle.DESIGN_PAPYRUS, ShownBorders.ALL);
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        menu.setColumnWidth(0, 13, 13);
        menu.setColumnWidth(1, 9, 9);
        menu.setColumnWidth(2, 10, 10);
        menu.setColumnWidth(3, 13, 13);
        menu.setColumnWidth(4, 9, 9);
        menu.setColumnWidth(5, 10, 10);

        menu.addCell("---- Stock Management ----", cellStyle, 6);
        menu.addCell("(Di)splay", cellStyle);
        menu.addCell("(W)rite", cellStyle);
        menu.addCell("(D)elete", cellStyle);
        menu.addCell("(Se)tting", cellStyle);
        menu.addCell("(Sa)ve", cellStyle);
        menu.addCell("(H)elp", cellStyle);
        menu.addCell("(ID)Product", cellStyle);
        menu.addCell("(U)pdate", cellStyle);
        menu.addCell("(S)earch", cellStyle);
        menu.addCell("(B)ackup", cellStyle);
        menu.addCell("(Re)store", cellStyle);
        menu.addCell("(E)xit", cellStyle);
        System.out.println(menu.render());
        return Helper.inputText("=> Command : ");
    }

    @Override
    public boolean displayProduct(String cmd) throws Exception {
        if (products.size() > 0)
            tablePagination.show(products);
        else Helper.showMessage("Product is empty.");
        return true;
    }

    @Override
    public boolean viewProduct(String cmd) throws Exception {
        int id;
        if (cmd.matches(Constant.ID_REX)) {
            id = Helper.parseInt(cmd.replace("#", ""));
        } else id = Helper.inputInteger("=> Please enter Product ID: ");
        Product product = products.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (product != null) {
            System.out.println(product.toString());
            Helper.showMessage(null);
        } else {
            Helper.showMessage("Product ID: '" + id + "' not found.");
        }
        return true;
    }

    private int generateId() {
        if (products.size() == 0) return 1;
        return products.stream().mapToInt(x -> x.getId()).max().getAsInt() + 1;
    }

    @Override
    public boolean insertProduct(String cmd) {
        int id = generateId();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(new Date());
        Product product = null;
        if (cmd.matches(Constant.ADD_REX)) {
            String[] fields = cmd.replace("add:", "").split(",");
            product = new Product();
            product.setId(id);
            product.setImportedDate(date);
            if (!fields[0].trim().isEmpty()) {
                product.setName(fields[0]);
            } else {
                Helper.showMessage("Name can't empty.");
                return false;
            }
            if (!fields[1].trim().isEmpty()) {
                try {
                    double price = Helper.parseDouble(fields[1]);
                    product.setUnitPrice(price);
                } catch (Exception e) {
                    Helper.showMessage("Unit Price is invalid.");
                    return false;
                }
            } else {
                Helper.showMessage("Unit Price can't empty.");
                return false;
            }
            if (!fields[2].trim().isEmpty()) {
                try {
                    int qty = Helper.parseInt(fields[2]);
                    product.setQuantity(qty);
                } catch (Exception e) {
                    Helper.showMessage("Quantity is invalid.");
                    return false;
                }
            } else {
                Helper.showMessage("Quantity can't empty.");
                return false;
            }
        }
        if (product == null) {
            System.out.println("=> Product ID: " + id);
            product = new Product(id,
                    Helper.inputText("=> Product Name: "),
                    Helper.inputDouble("=> Product Unit Price: "),
                    Helper.inputInteger("=> Product Quantity: "),
                    date
            );
        }
        System.out.println(product.toString());

        if (Helper.yesNoMessage("Do you want to save?") == 1) {
            products.add(product);
            saveRecovery(products);
            Helper.showMessage("Product insert successfully.");
        } else {
            Helper.showMessage("Insert canceled.");
        }
        return true;
    }

    @Override
    public boolean updateProduct(String cmd) throws Exception {
        if (cmd.matches(Constant.UP_REX)) {
            String[] fields = cmd.split(":");
            int id = Helper.parseInt(fields[0].replace("#", ""));
            Product product = products.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
            if (product != null) {
                Product clone = (Product) product.clone();
                String[] data = fields[1].split(",");
                Map<Integer, Command> map = new HashMap<>();
                map.put(0, (v) -> {
                    String value = data[0].trim();
                    if (!value.isEmpty()) {
                        clone.setName(value);
                    }
                    return true;
                });
                map.put(1, (v) -> {
                    String value = data[1].trim();
                    if (!value.isEmpty()) {
                        try {
                            double price = Helper.parseDouble(value);
                            clone.setUnitPrice(price);
                        } catch (Exception e) {
                            Helper.showMessage("Price is invalid.");
                            return false;
                        }
                    }
                    return true;
                });
                map.put(2, (v) -> {
                    String value = data[2].trim();
                    if (!value.isEmpty()) {
                        try {
                            int qty = Helper.parseInt(value);
                            clone.setQuantity(qty);
                        } catch (Exception e) {
                            Helper.showMessage("Quantity is invalid.");
                            return false;
                        }
                    }
                    return true;

                });
                map.put(3, (v) -> {
                    String value = data[3].trim();
                    if (!value.isEmpty()) {
                        clone.setImportedDate(value);
                    }
                    return true;
                });
                int length = data.length;
                if (length > 0)
                    for (int i = 0; i < length; i++) {
                        Command command = map.get(i);
                        if (command != null) {
                            if (!command.execute(data[i])) return false;
                        }
                    }
                else {
                    Helper.showMessage("Update invalid.");
                    return false;
                }
                System.out.println(clone.toString());
                if (Helper.yesNoMessage("Do you want to save?") == 1) {
                    product.copy(clone);
                    saveRecovery(products);
                    Helper.showMessage("Product ID: '" + id + "' has been update.");
                } else Helper.showMessage("Update canceled.");
            } else {
                Helper.showMessage("Product ID: '" + id + "' not found.");
            }
        } else {
            int id = Helper.inputInteger("=> Please enter Product ID: ");
            Product product = products.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
            if (product != null) {
                System.out.println(product.toString());
                UpdateProductDao updateDao = new UpdateProductDao() {

                    @Override
                    public boolean all(com.company.Product product) {
                        return false;
                    }

                    @Override
                    public boolean name(com.company.Product product) {
                        return false;
                    }

                    @Override
                    public boolean unitPrice(com.company.Product product) {
                        return false;
                    }

                    @Override
                    public boolean quantity(com.company.Product product) {
                        return false;
                    }

                    @Override
                    public boolean importDate(com.company.Product product) {
                        return false;
                    }

                    @Override
                    public boolean all(Product product) {
                        try {
                            final Product clone = (Product) product.clone();
                            clone.setName(Helper.inputText("=> Product Name: "));
                            clone.setUnitPrice(Helper.inputDouble("=> Product Unit Price: "));
                            clone.setQuantity(Helper.inputInteger("=> Product Quantity: "));
                            clone.setImportedDate(Helper.inputText("=> Product Import Date: "));
                            if (Helper.yesNoMessage("Do you want to save?") == 1) {
                                System.out.println(clone.toString());
                                product.copy(clone);
                            } else Helper.showMessage("Update canceled.");
                            return true;

                        } catch (CloneNotSupportedException e) {
                            return false;
                        }
                    }

                    @Override
                    public boolean name(Product product) {
                        try {
                            final Product clone = (Product) product.clone();
                            clone.setName(Helper.inputText("=> Product Name: "));
                            if (Helper.yesNoMessage("Do you want to save?") == 1) {
                                System.out.println(clone.toString());
                                product.copy(clone);
                            } else Helper.showMessage("Update canceled.");
                            return true;

                        } catch (CloneNotSupportedException e) {
                            return false;
                        }
                    }

                    @Override
                    public boolean unitPrice(Product product) {
                        try {
                            final Product clone = (Product) product.clone();
                            clone.setUnitPrice(Helper.inputDouble("=> Product Unit Price: "));
                            if (Helper.yesNoMessage("Do you want to save?") == 1) {
                                System.out.println(clone.toString());
                                product.copy(clone);
                            } else Helper.showMessage("Update canceled.");
                            return true;

                        } catch (CloneNotSupportedException e) {
                            return false;
                        }
                    }

                    @Override
                    public boolean quantity(Product product) {
                        try {
                            final Product clone = (Product) product.clone();
                            clone.setQuantity(Helper.inputInteger("=> Product Quantity: "));
                            if (Helper.yesNoMessage("Do you want to save?") == 1) {
                                System.out.println(clone.toString());
                                product.copy(clone);
                            } else Helper.showMessage("Update canceled.");
                            return true;

                        } catch (CloneNotSupportedException e) {
                            return false;
                        }


                    }

                    @Override
                    public boolean importDate(Product product) {
                        try {
                            final Product clone = (Product) product.clone();
                            clone.setImportedDate(Helper.inputText("=> Product Import Date: "));
                            if (Helper.yesNoMessage("Do you want to save?") == 1) {
                                System.out.println(clone.toString());
                                product.copy(clone);
                            } else Helper.showMessage("Update canceled.");
                            return true;

                        } catch (CloneNotSupportedException e) {
                            return false;
                        }

                    }
                };
                Map<String, UpdateProductCommand> commandMap = new CommandHashMap<>();
                commandMap.put("a", updateDao::all);
                commandMap.put("n", updateDao::name);
                commandMap.put("u", updateDao::unitPrice);
                commandMap.put("q", updateDao::quantity);
                commandMap.put("i", updateDao::importDate);
                commandMap.put("b", (s) -> false);
                String command;
                do {
                    Table menu = new Table(6, BorderStyle.DESIGN_PAPYRUS, ShownBorders.ALL);
                    CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
                    menu.setColumnWidth(0, 7, 11);
                    menu.setColumnWidth(1, 11, 11);
                    menu.setColumnWidth(2, 15, 11);
                    menu.setColumnWidth(3, 11, 11);
                    menu.setColumnWidth(4, 13, 11);
                    menu.setColumnWidth(5, 7, 11);

                    menu.addCell("(A)ll", cellStyle);
                    menu.addCell("(N)ame", cellStyle);
                    menu.addCell("(U)nit Price", cellStyle);
                    menu.addCell("(Q)uantity", cellStyle);
                    menu.addCell("(I)mort Date", cellStyle);
                    menu.addCell("(B)ack", cellStyle);

                    System.out.println(menu.render());
                    command = Helper.inputText("=> Command : ");
                    UpdateProductCommand commandAction = commandMap.get(command);
                    if (commandAction != null) {
                        if (commandAction.execute(product)) {
                            saveRecovery(products);
                            Helper.showMessage("Product ID: '" + id + "' has been update.");
                        }
                    } else {
                        System.out.println("Command not found.");
                    }
                } while (!command.matches("b|B"));

            } else {
                Helper.showMessage("Product ID: '" + id + "' not found.");
            }
        }
        return true;
    }

    @Override
    public boolean deleteProduct(String cmd) throws Exception {
        int id;
        if (cmd.matches(Constant.DEL_REX)) {
            id = Helper.parseInt(cmd.replace("del:", ""));
        } else id = Helper.inputInteger("=> Please enter Product ID: ");
        Product product = products.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (product != null) {
            System.out.println(product.toString());
            if (Helper.yesNoMessage("Do you want to delete?") == 1) {
                products.remove(product);
                saveRecovery(products);
                Helper.showMessage("Product ID: '" + id + "' has been deleted.");
            } else System.out.println("Delete canceled.");
        } else {
            Helper.showMessage("Product ID: '" + id + "' not found.");
        }
        return true;
    }

    @Override
    public boolean searchProduct(String cmd) throws Exception {
        String search;
        if (products.size() == 0) {
            Helper.showMessage("Product is empty.");
            return false;
        }
        if (cmd.matches(Constant.SEARCH_REX)) {
            search = cmd.replace("s:", "");
        } else
            search = Helper.inputText("=> Please enter Product Name: ");
        List<Product> searched = products.stream()
                .filter(x -> x.getName().toUpperCase().contains(search.toUpperCase()))
                .collect(Collectors.toList());
        if (searched.size() == 0) Helper.showMessage("Product Name: '" + search + "' not found.");
        else tablePagination.show(searched);
        return true;
    }

    private void productWriter(List<Product> products, String filename) {
        try {
            File file = new File(filename);
            File dir = new File(file.getPath().replace(file.getName(), ""));
            if (!dir.exists()) dir.mkdir();
            PrintWriter writer = new PrintWriter(file);
            for (Product product : products) {
                writer.println(product.getId() + ","
                        + product.getName() + ","
                        + product.getUnitPrice() + ","
                        + product.getQuantity() + ","
                        + product.getImportedDate());
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private List<Product> productReader(String filename) {
        List<Product> products = new LinkedList<>();
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(",");
                Product product = new Product(
                        Helper.parseInt(data[0]),
                        data[1],
                        Helper.parseDouble(data[2]),
                        Helper.parseInt(data[3]),
                        data[4]);
                products.add(product);
            }
            return products;
        } catch (Exception e) {
            return products;
        }
    }

    @Override
    public boolean backup(String cmd) throws InterruptedException {
        System.out.println("Backuping...");
        Task task = new Task((o) -> {
            try {
                String pattern = "dd-MM-yy-hh-mm-ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());
                String path = "backup/" + date + ".bak";
                productWriter(products, path);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                o.interrupt();
            }
        });
        task.start();
        task.join();
        Helper.showMessage("Backup successfully.");
        return true;
    }

    private boolean save(List<Product> products) {
        productWriter(products, "data/stock.txt");
        return true;
    }

    private boolean saveRecovery(List<Product> products) {
        if (setting.autoSave) {
            save(products);
        }
        if (setting.recovery) {
            productWriter(products, "recovery/stock.txt");
        }
        return true;
    }

    @Override
    public boolean save(String cmd) throws InterruptedException {
        System.out.println("Saving...");
        Task task = new Task((o) -> {
            save(products);
            if (setting.recovery) {
                saveRecovery(products);
                this.recoveries = new LinkedList<>(products);
            }
        });
        task.start();
        task.join();

        Helper.showMessage("Save successfully.");

        return true;
    }

    @Override
    public boolean restore(String cmd) throws InterruptedException {
        File[] files = new File("backup").listFiles();
        Table table = new Table(1, BorderStyle.UNICODE_DOUBLE_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
        table.setColumnWidth(0, 30, 30);
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        table.addCell("Choose restore file", cellStyle);
        int length = files != null ? files.length : 0;
        if (length > 0) {
            int index = 1;
            for (File file : files) {
                table.addCell("(" + index + ")" + file.getName(), cellStyle);
                index++;
            }
            System.out.println(table.render());
            int number = Helper.inputInteger("=> Choose file (1-" + files.length + "): ");
            System.out.println("Restoring...");
            Task task = new Task((o) -> {
                try {
                    products = productReader(files[number - 1].getPath());
                    saveRecovery(products);
                } catch (Exception e) {
                    Helper.showMessage("File invalid.");
                    o.interrupt();
                }
            });
            task.start();
            task.join();
            Helper.showMessage("Restore successfully.");

        } else Helper.showMessage("Restore file is empty.");
        return true;
    }

    @Override
    public boolean help(String cmd) {

        Table table = new Table(2, BorderStyle.UNICODE_BOX_DOUBLE_BORDER, ShownBorders.ALL);
        CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.center);
        table.setColumnWidth(0, 38, 100);
        table.setColumnWidth(1, 38, 100);
        table.addCell("---- Help ----", cellStyle, 2);
        table.addCell("Shortcut keys", cellStyle);
        table.addCell("Description", cellStyle);
        table.addCell("Di , Ds ");
        table.addCell("Display all product in stock.");
        table.addCell("F");
        table.addCell("Goto first page.");
        table.addCell("P");
        table.addCell("Goto previous page.");
        table.addCell("N");
        table.addCell("Goto next page.");
        table.addCell("L");
        table.addCell("Goto last page.");
        table.addCell("g:{Number}");
        table.addCell("Goto page by specific number.");
        table.addCell("se , se:{Number}");
        table.addCell("Set number of row for display.");
        table.addCell("W , add:{Name},{Unit Price},{Quantity}");
        table.addCell("Add product to stock.");
        table.addCell("{Number}M");
        table.addCell("Generate million record.");
        table.addCell("D , del:{Product ID}");
        table.addCell("Delete product from stock.");
        table.addCell("Se");
        table.addCell("Goto setting.");
        table.addCell("r:{true|false}");
        table.addCell("Auto recovery data.");
        table.addCell("a:{true|false}");
        table.addCell("Auto save data to file.");
        table.addCell("Sa");
        table.addCell("Save data to file.");
        table.addCell("H");
        table.addCell("Goto help.");
        table.addCell("ID , #{Product ID}");
        table.addCell("View product detail by ID.");
        table.addCell("U , #{Product ID}:{Name},{Unit price},{Quantity},{Imported Date}");
        table.addCell("Update product information.");
        table.addCell("S, s:{Keyword}");
        table.addCell("Search product in stock.");
        table.addCell("B");
        table.addCell("Backup data.");
        table.addCell("Re");
        table.addCell("Restore data from file.");
        table.addCell("E");
        table.addCell("Quit the application.");
        System.out.println(table.render());
        Helper.showMessage(null);
        return true;
    }

    @Override
    public boolean exit(String cmd) throws InterruptedException {
        if (!setting.autoSave && setting.recovery) {
            boolean equal;
            if ((recoveries != null && products != null))
                equal = Arrays.equals(recoveries.toArray(), products.toArray());
            else equal = true;
            if (!equal) {
                System.out.println("Saving...");
                Task task = new Task((o) -> {
                    if (Helper.yesNoMessage("Your data haven't save yet. Do you want to save?") == 1) {
                        save(products);
                    } else saveRecovery(recoveries);
                });
                task.start();
                task.join();
                System.out.println("Done.");
            }
        }
        System.out.println("Good bye!!!");
        System.exit(0);

        return true;
    }

    @Override
    public void show() throws Exception {

        Map<String, Command> commandMap = new CommandHashMap<>();
        commandMap.put("di|ds", this::displayProduct);
        commandMap.put("id|" + Constant.ID_REX, this::viewProduct);
        commandMap.put("w|" + Constant.ADD_REX, this::insertProduct);
        commandMap.put("u|" + Constant.UP_REX, this::updateProduct);
        commandMap.put("d|" + Constant.DEL_REX, this::deleteProduct);
        commandMap.put("s|" + Constant.SEARCH_REX, this::searchProduct);
        commandMap.put("se", this::setting);
        commandMap.put("b", this::backup);
        commandMap.put("sa", this::save);
        commandMap.put("re", this::restore);
        commandMap.put("h", this::help);
        commandMap.put(Constant.M_REX, this::generate);
        commandMap.put("e", this::exit);
        String command;
        do {
            command = this.menu();
            Command commandAction = commandMap.get(command);
            if (commandAction != null) {
                commandAction.execute(command);
            } else {
                System.out.println("Command not found.");
            }

        } while (!command.matches("e|E"));
    }
}
