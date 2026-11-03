package cn.jiguang.common.app.helper;

import java.io.File;
import java.util.regex.Pattern;

public class AndroidAppProcess extends AndroidProcess {
   // Source-only constructor; the ABI restoration step removes it because the optimized original has none.
   private AndroidAppProcess(android.os.Parcel parcel) { super(parcel); }
   private static final boolean recoverField_c = new File("/dev/cpuctl/tasks").exists();
   private static final Pattern recoverField_d = Pattern.compile("^([A-Za-z]{1}[A-Za-z0-9_]*[\\.|:])*[A-Za-z][A-Za-z0-9_]*$");
}
