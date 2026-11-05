package android.database;
public interface Cursor {
    int getCount();
    boolean moveToFirst();
    String getString(int columnIndex);
    int getColumnIndex(String columnName);
    void close();
}
