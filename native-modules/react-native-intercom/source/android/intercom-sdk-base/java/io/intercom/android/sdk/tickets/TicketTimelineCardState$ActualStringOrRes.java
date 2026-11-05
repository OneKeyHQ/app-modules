package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.res.StringResources_androidKt;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** @deprecated */
@Deprecated(message = "Use StringProvider instead")
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0002\u0007\bB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u0004\u001a\u00020\u0005H\u0007¢\u0006\u0002\u0010\u0006\u0082\u0001\u0002\t\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ActualStringOrRes;",
         "",
         "<init>",
         "()V",
         "getText",
         "",
         "(Landroidx/compose/runtime/Composer;I)Ljava/lang/String;",
         "StringRes",
         "ActualString",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ActualStringOrRes$ActualString;",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ActualStringOrRes$StringRes;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class TicketTimelineCardState$ActualStringOrRes {
   public static int $stable;

   static {}

   private TicketTimelineCardState$ActualStringOrRes() {
   }

   @Composable
   @NotNull
   public final String getText(@Nullable Composer var1, int var2) {
      var1.startReplaceGroup(796462681);
      ComposerKt.sourceInformation(var1, "C(getText)53@1813L30:TicketDetailState.kt#3ofz5f");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            796462681, var2, -1, "io.intercom.android.sdk.tickets.TicketTimelineCardState.ActualStringOrRes.getText (TicketDetailState.kt:51)"
         );
      }

      TicketTimelineCardState$ActualStringOrRes var4 = this;
      String var10000;
      if (var4 instanceof TicketTimelineCardState$ActualStringOrRes$ActualString) {
         var10000 = ((TicketTimelineCardState$ActualStringOrRes$ActualString)this).getString();
      } else {
         if (!(var4 instanceof TicketTimelineCardState$ActualStringOrRes$StringRes)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = StringResources_androidKt.stringResource(((TicketTimelineCardState$ActualStringOrRes$StringRes)this).getStringRes(), var1, 0);
      }

      String var3 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
      return var3;
   }

   // $VF: synthetic method
   public TicketTimelineCardState$ActualStringOrRes(DefaultConstructorMarker var1) {
      this();
   }
}
