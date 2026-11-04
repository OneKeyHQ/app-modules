package android.content;
public class ContextWrapper extends Context {
    private final Context base;

    public ContextWrapper(Context base) {
        this.base = base;
    }

    public Context getBaseContext() {
        return base;
    }

    @Override
    public Context getApplicationContext() {
        return base != null ? base.getApplicationContext() : this;
    }
}
