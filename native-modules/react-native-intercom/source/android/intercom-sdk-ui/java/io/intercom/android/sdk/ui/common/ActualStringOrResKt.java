package io.intercom.android.sdk.ui.common;

import android.content.Context;
import androidx.annotation.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u001a4\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\u001a\b\u0002\u0010\u0006\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00010\b0\u0007¨\u0006\t",
   d2 = {"parseString", "", "context", "Landroid/content/Context;", "stringRes", "", "params", "", "Lkotlin/Pair;", "intercom-sdk-ui_release"}
)
@SourceDebugExtension(
   "SMAP\nActualStringOrRes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ActualStringOrRes.kt\nio/intercom/android/sdk/ui/common/ActualStringOrResKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,41:1\n1869#2,2:42\n*S KotlinDebug\n*F\n+ 1 ActualStringOrRes.kt\nio/intercom/android/sdk/ui/common/ActualStringOrResKt\n*L\n36#1:42,2\n*E\n"
)
public final class ActualStringOrResKt {
   @NotNull
   public static final String parseString(@NotNull Context var0, @StringRes int var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var2, "params");
      Object var3 = null;
      String var10000 = var0.getString(var1);
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      var3 = var10000;
      Iterable var4 = var2;
      int var5 = 0;

      for (Object var7 : var4) {
         Pair var8 = (Pair)var7;
         int var9 = 0;
         var3 = StringsKt.replace$default((String)var3, '{' + (String)var8.getFirst() + '}', (String)var8.getSecond(), false, 4, null);
      }

      return (String)var3;
   }

   // $VF: synthetic method
   public static String parseString$default(Context var0, int var1, List var2, int var3, Object var4) {
      if ((var3 & 4) != 0) {
         var2 = CollectionsKt.emptyList();
      }

      return parseString(var0, var1, var2);
   }
}
