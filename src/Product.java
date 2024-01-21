import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.util.Objects;

class Product implements Cloneable {
    private int id;
    private String name;
    private double unitPrice;
    private int quantity;
    private String importedDate;

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Product(this.id, this.name, this.unitPrice, this.quantity, this.importedDate);
    }

    public void copy(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setUnitPrice(product.getUnitPrice());
        this.setQuantity(product.getQuantity());
        this.setImportedDate(product.getImportedDate());
    }

    public Product() {
    }

    ;

    public Product(int id, String name, double unitPrice, int quantity, String importedDate) {
        setId(id);
        setName(name);
        setUnitPrice(unitPrice);
        setQuantity(quantity);
        setImportedDate(importedDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImportedDate() {
        return importedDate;
    }

    public void setImportedDate(String importedDate) {
        this.importedDate = importedDate;
    }

    @Override
    public String toString() {
        Table table = new Table(1, BorderStyle.UNICODE_DOUBLE_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
        table.setColumnWidth(0, 30, 30);
        table.addCell("Product Detail", new CellStyle(CellStyle.HorizontalAlign.center));
        table.addCell("ID: " + getId());
        table.addCell("Name: " + getName());
        table.addCell("Unit Price: " + getUnitPrice());
        table.addCell("Quantity: " + getQuantity());
        table.addCell("Imported Date: " + getImportedDate());
        return table.render();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id && Double.compare(product.unitPrice, unitPrice) == 0
                && quantity == product.quantity && Objects.equals(name, product.name)
                && Objects.equals(importedDate, product.importedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, unitPrice, quantity, importedDate);
    }
}
