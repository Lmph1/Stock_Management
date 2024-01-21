interface UpdateProductDao {

    boolean all(com.company.Product product);

    boolean name(com.company.Product product);

    boolean unitPrice(com.company.Product product);

    boolean quantity(com.company.Product product);

    boolean importDate(com.company.Product product);

    boolean all(Product product);

    boolean name(Product product);

    boolean unitPrice(Product product);

    boolean quantity(Product product);

    boolean importDate(Product product);
}
