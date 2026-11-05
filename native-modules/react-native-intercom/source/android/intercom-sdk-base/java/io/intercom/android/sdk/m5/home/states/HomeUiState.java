package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.PoweredBy;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0003\b\t\nB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\u0004\u0018\u00010\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0003\u000b\f\r¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "",
         "<init>",
         "()V",
         "poweredBy",
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "getPoweredBy",
         "()Lio/intercom/android/sdk/models/PoweredBy;",
         "Loading",
         "Content",
         "Error",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Loading;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class HomeUiState {
   public static int $stable;

   static {}

   private HomeUiState() {
   }

   @Nullable
   public abstract PoweredBy getPoweredBy();

   // $VF: synthetic method
   public HomeUiState(DefaultConstructorMarker var1) {
      this();
   }
}
