package cn.jpush.android.api;

import java.util.Set;

@Deprecated
public interface TagAliasCallback {
    void gotResult(int responseCode, String alias, Set<String> tags);
}
