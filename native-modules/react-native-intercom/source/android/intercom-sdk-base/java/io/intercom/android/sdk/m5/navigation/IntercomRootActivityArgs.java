package io.intercom.android.sdk.m5.navigation;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\b\b\t\n\u000b\f\r\u000e\u000fB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\b\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "Landroid/os/Parcelable;",
         "<init>",
         "()V",
         "route",
         "",
         "getRoute",
         "()Ljava/lang/String;",
         "HomeScreenArgs",
         "MessagesScreenArgs",
         "TicketsScreenArgs",
         "TicketDetailsScreenArgs",
         "ConversationScreenArgs",
         "HelpCenterCollectionsArgs",
         "HelpCenterCollectionArgs",
         "NoContent",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$ConversationScreenArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$HelpCenterCollectionArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$HelpCenterCollectionsArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$HomeScreenArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$MessagesScreenArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$NoContent;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$TicketDetailsScreenArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$TicketsScreenArgs;",
         "intercom-sdk-base_release"
   }
)
@SuppressLint("ParcelCreator")
@Parcelize
@StabilityInferred(parameters = 1)
public abstract class IntercomRootActivityArgs implements Parcelable {
   public static int $stable;

   static {}

   private IntercomRootActivityArgs() {
   }

   @NotNull
   public abstract String getRoute();

   // $VF: synthetic method
   public IntercomRootActivityArgs(DefaultConstructorMarker var1) {
      this();
   }
}
