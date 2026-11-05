package io.intercom.android.sdk.m5.bubble;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.activities.IntercomActivity;
import kotlin.Metadata;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u00012\u00020\u0002B\t\b\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0012\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0014¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/bubble/IntercomBubbleActivity;",
         "Landroidx/activity/ComponentActivity;",
         "Lio/intercom/android/sdk/activities/IntercomActivity;",
         "<init>",
         "()V",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomBubbleActivity extends ComponentActivity implements IntercomActivity {
   public static int $stable;

   static {}

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      EdgeToEdge.enable$default(this, null, null, 3, null);
      super.onCreate(var1);
      ComponentActivityKt.setContent$default(
         this, null, ComposableLambdaKt.composableLambdaInstance(947575690, true, new IntercomBubbleActivity$onCreate$1(this)), 1, null
      );
   }
}
