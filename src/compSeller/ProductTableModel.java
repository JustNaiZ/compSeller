package compSeller;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ProductTableModel extends AbstractTableModel {
    private List<Product> products;
    private final String[] columnNames = {"Название", "Цена"};

    public ProductTableModel(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getRowCount() {
        return products.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Product product = products.get(rowIndex);
        switch (columnIndex) {
            case 0: return product.getProductName();
            case 1: return product.getSellingPrice();
            default: throw new IllegalArgumentException("Invalid column index");
        }
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        fireTableDataChanged(); // Уведомляем таблицу об обновлении данных
    }
}
