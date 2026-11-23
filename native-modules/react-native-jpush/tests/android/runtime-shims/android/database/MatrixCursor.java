package android.database;
import java.util.ArrayList;
import java.util.List;

public class MatrixCursor implements Cursor {
    private final String[] columns;
    private final List<Object[]> rows = new ArrayList<Object[]>();
    private int pos = -1;

    public MatrixCursor(String[] columnNames, int initialCapacity) {
        this.columns = columnNames != null ? columnNames : new String[0];
    }

    public MatrixCursor(String[] columnNames) {
        this(columnNames, 16);
    }

    public void addRow(Object[] columnValues) {
        rows.add(columnValues);
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public boolean moveToFirst() {
        if (rows.isEmpty()) {
            return false;
        }
        pos = 0;
        return true;
    }

    @Override
    public String getString(int columnIndex) {
        if (pos < 0 || pos >= rows.size()) {
            return null;
        }
        Object[] row = rows.get(pos);
        if (columnIndex < 0 || columnIndex >= row.length || row[columnIndex] == null) {
            return null;
        }
        return String.valueOf(row[columnIndex]);
    }

    @Override
    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void close() {}
}
