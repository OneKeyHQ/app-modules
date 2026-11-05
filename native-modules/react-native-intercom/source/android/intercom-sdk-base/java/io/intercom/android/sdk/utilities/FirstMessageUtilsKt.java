package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.widget.Toast;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000\u001a\u0010\u0010\n\u001a\u00020\u00012\u0006\u0010\u0006\u001a\u00020\u0007H\u0000¨\u0006\u000b",
   d2 = {
         "handleFirstMessageToast",
         "",
         "applicationScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "context",
         "Landroid/content/Context;",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "showFirstMessageToast",
         "intercom-sdk-base_release"
   }
)
public final class FirstMessageUtilsKt {
   public static final void handleFirstMessageToast(@NotNull CoroutineScope var0, @NotNull Twig var1, @NotNull Context var2, @NotNull IntercomDataLayer var3) {
      Intrinsics.checkNotNullParameter(var0, "applicationScope");
      Intrinsics.checkNotNullParameter(var1, "twig");
      Intrinsics.checkNotNullParameter(var2, "context");
      Intrinsics.checkNotNullParameter(var3, "dataLayer");
      BuildersKt.launch$default(var0, null, null, new FirstMessageUtilsKt$handleFirstMessageToast$1(var3, var1, var2, null), 3, null);
   }

   public static final void showFirstMessageToast(@NotNull Context var0) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Toast.makeText(var0, var0.getString(string.intercom_android_activated_message), 1).show();
   }
}
