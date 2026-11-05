package android.text;
public class TextUtils {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static String join(CharSequence delimiter, Iterable tokens) {
        if (tokens == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object token : tokens) {
            if (!first) {
                sb.append(delimiter);
            }
            first = false;
            sb.append(token);
        }
        return sb.toString();
    }
}
