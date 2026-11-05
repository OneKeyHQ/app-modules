package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0002\r\u000eB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0012\u0010\b\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007R\u0014\u0010\n\u001a\u0004\u0018\u00010\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f\u0082\u0001\u0002\u000f\u0010¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "",
         "<init>",
         "()V",
         "iconId",
         "",
         "getIconId",
         "()I",
         "messageResId",
         "getMessageResId",
         "additionalMessageResId",
         "getAdditionalMessageResId",
         "()Ljava/lang/Integer;",
         "WithCTA",
         "WithoutCTA",
         "Lio/intercom/android/sdk/m5/components/ErrorState$WithCTA;",
         "Lio/intercom/android/sdk/m5/components/ErrorState$WithoutCTA;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class ErrorState {
   public static int $stable;

   static {}

   private ErrorState() {
   }

   public abstract int getIconId();

   public abstract int getMessageResId();

   @Nullable
   public abstract Integer getAdditionalMessageResId();

   // $VF: synthetic method
   public ErrorState(DefaultConstructorMarker var1) {
      this();
   }
}
