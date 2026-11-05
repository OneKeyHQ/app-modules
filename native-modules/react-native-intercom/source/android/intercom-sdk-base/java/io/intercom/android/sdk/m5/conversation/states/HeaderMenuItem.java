package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.StringProvider;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0005\f\r\u000e\u000f\u0010B\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0012\u0010\b\u001a\u00020\tX¦\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b\u0082\u0001\u0004\u0011\u0012\u0013\u0014¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "",
         "<init>",
         "()V",
         "enabled",
         "",
         "getEnabled",
         "()Z",
         "label",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "getLabel",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "TotalCountIndicator",
         "Messages",
         "StartNewConversation",
         "Tickets",
         "Help",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$Help;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$Messages;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$StartNewConversation;",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem$Tickets;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class HeaderMenuItem {
   public static int $stable;

   static {}

   private HeaderMenuItem() {
   }

   public abstract boolean getEnabled();

   @NotNull
   public abstract StringProvider getLabel();

   // $VF: synthetic method
   public HeaderMenuItem(DefaultConstructorMarker var1) {
      this();
   }
}
