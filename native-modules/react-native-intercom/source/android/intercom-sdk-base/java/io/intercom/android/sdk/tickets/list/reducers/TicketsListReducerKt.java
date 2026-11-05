package io.intercom.android.sdk.tickets.list.reducers;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.paging.LoadState;
import androidx.paging.LoadState.Error;
import androidx.paging.LoadState.Loading;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.models.EmptyState;
import io.intercom.android.sdk.models.Space$Type;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Content;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Empty;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Error;
import io.intercom.android.sdk.tickets.list.data.TicketsScreenUiState$Initial;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a'\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u00022\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0001¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "reduceToTicketsScreenUiState",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;",
         "Landroidx/paging/compose/LazyPagingItems;",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         "config",
         "Lkotlin/Function0;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "(Landroidx/paging/compose/LazyPagingItems;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketsListReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketsListReducer.kt\nio/intercom/android/sdk/tickets/list/reducers/TicketsListReducerKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,68:1\n1047#2,6:69\n1047#2,6:75\n1047#2,6:81\n*S KotlinDebug\n*F\n+ 1 TicketsListReducer.kt\nio/intercom/android/sdk/tickets/list/reducers/TicketsListReducerKt\n*L\n19#1:69,6\n30#1:75,6\n41#1:81,6\n*E\n"
)
public final class TicketsListReducerKt {
   @Composable
   @NotNull
   public static final TicketsScreenUiState reduceToTicketsScreenUiState(
      @NotNull LazyPagingItems var0, @Nullable Function0 var1, @Nullable Composer var2, int var3, int var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      var2.startReplaceGroup(-356015290);
      ComposerKt.sourceInformation(var2, "C(reduceToTicketsScreenUiState)18@771L42:TicketsListReducer.kt#fjyc90");
      if ((var4 & 1) != 0) {
         var2.startReplaceGroup(-374398905);
         ComposerKt.sourceInformation(var2, "CC(remember):TicketsListReducer.kt#9igjgp");
         Composer var6 = var2;
         boolean var7 = false;
         int var8 = 0;
         Object var9 = var6.rememberedValue();
         int var10 = 0;
         Object var10000;
         if (var9 == Composer.Companion.getEmpty()) {
            int var11 = 0;
            Object var66 = ((kotlin.jvm.functions.Function0) () -> TicketsListReducerKt.reduceToTicketsScreenUiState$lambda$1$lambda$0());
            var6.updateRememberedValue(var66);
            var10000 = var66;
         } else {
            var10000 = var9;
         }

         Function0 var5 = (Function0)var10000;
         var2.endReplaceGroup();
         var1 = var5;
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -356015290, var3, -1, "io.intercom.android.sdk.tickets.list.reducers.reduceToTicketsScreenUiState (TicketsListReducer.kt:19)"
         );
      }

      String var57 = ((AppConfig)var1.invoke()).getSpaceLabelIfExists(Space$Type.TICKETS);
      var2.startReplaceGroup(-374395883);
      ComposerKt.sourceInformation(var2, "21@925L58");
      String var59 = var57 == null ? StringResources_androidKt.stringResource(string.intercom_tickets_space_title, var2, 0) : var57;
      var2.endReplaceGroup();
      String var56 = var59;
      TicketsScreenUiState var78;
      if (var0.getItemSnapshotList().size() != 0) {
         var2.startReplaceGroup(-374388312);
         ComposerKt.sourceInformation(var2, "");
         LazyPagingItems var75 = var0;
         boolean recoveredAppendLoading = var0.getLoadState().getAppend() instanceof Loading;
         byte var10001;
         LoadState var64 = var0.getLoadState().getAppend();
         Error var10002 = var64 instanceof Error ? (Error)var64 : null;
         ErrorState var86;
         if ((var64 instanceof Error ? (Error)var64 : null) == null) {
            var86 = null;
         } else {
            Error var12 = var10002;
            boolean var30 = recoveredAppendLoading;
            LazyPagingItems var29 = var0;
            int var13 = 0;
            boolean var14 = var12.getError() instanceof IOException;
            var2.startReplaceGroup(-374380409);
            ComposerKt.sourceInformation(var2, "29@1382L11");
            ErrorState var77;
            if (var14) {
               byte var76 = 0;
               var10001 = 0;
               var10002 = null;
               byte var10003 = 0;
               var2.startReplaceGroup(321780487);
               ComposerKt.sourceInformation(var2, "CC(remember):TicketsListReducer.kt#9igjgp");
               Composer var15 = var2;
               boolean var16 = (var3 & 14 ^ 6) > 4 && var2.changedInstance(var0) || (var3 & 6) == 4;
               int var17 = 0;
               Object var18 = var15.rememberedValue();
               int var19 = 0;
               Object var10004;
               if (!var16 && var18 != Composer.Companion.getEmpty()) {
                  var10004 = var18;
               } else {
                  byte var20 = 0;
                  Object var21 = null;
                  byte var22 = 0;
                  byte var23 = 0;
                  int var24 = 0;
                  final androidx.paging.compose.LazyPagingItems recoveredCapture_1599_0 = var0;
                  Function0 var25 = ((kotlin.jvm.functions.Function0) () -> TicketsListReducerKt.reduceToTicketsScreenUiState$lambda$4$lambda$3$lambda$2(recoveredCapture_1599_0));
                  var76 = var23;
                  var10001 = var22;
                  var10002 = (Error)var21;
                  var10003 = var20;
                  Object var26 = var25;
                  var15.updateRememberedValue(var26);
                  var10004 = var26;
               }

               Function0 var27 = (Function0)var10004;
               var2.endReplaceGroup();
               Object var36 = null;
               byte var37 = 15;
               Function0 var38 = var27;
               byte var39 = var10003;
               Object var40 = var10002;
               byte var41 = var10001;
               byte var42 = var76;
               var77 = new ErrorState$WithCTA(var42, var41, (Integer)var40, var39, var38, var37, (DefaultConstructorMarker)var36);
            } else {
               var77 = new ErrorState$WithoutCTA(0, 0, null, 7, null);
            }

            ErrorState var28 = var77;
            var2.endReplaceGroup();
            ErrorState var31 = var28;
            var75 = var29;
            recoveredAppendLoading = var30;
            var86 = var31;
         }

         String var43 = var56;
         ErrorState var44 = var86;
         boolean var45 = recoveredAppendLoading;
         LazyPagingItems var46 = var75;
         TicketsScreenUiState$Content var60 = new TicketsScreenUiState$Content(var46, var45, var44, var43);
         var2.endReplaceGroup();
         var78 = var60;
      } else if (var0.getLoadState().getRefresh() instanceof Error) {
         var2.startReplaceGroup(1279316105);
         ComposerKt.sourceInformation(var2, "40@1865L13");
         LoadState var79 = var0.getLoadState().getRefresh();
         Intrinsics.checkNotNull(var79, "null cannot be cast to non-null type androidx.paging.LoadState.Error");
         boolean var63 = ((Error)var79).getError() instanceof IOException;
         if (!var63) {
            var78 = new TicketsScreenUiState$Error(new ErrorState$WithoutCTA(0, 0, string.intercom_failed_to_load_tickets, 3, null), var56);
         } else {
            byte var80 = 0;
            byte var84 = 0;
            Integer var87 = string.intercom_failed_to_load_tickets;
            byte var88 = 0;
            var2.startReplaceGroup(-374363926);
            ComposerKt.sourceInformation(var2, "CC(remember):TicketsListReducer.kt#9igjgp");
            Composer var67 = var2;
            boolean var68 = (var3 & 14 ^ 6) > 4 && var2.changedInstance(var0) || (var3 & 6) == 4;
            int var69 = 0;
            Object var70 = var67.rememberedValue();
            int var71 = 0;
            Object var89;
            if (!var68 && var70 != Composer.Companion.getEmpty()) {
               var89 = var70;
            } else {
               byte var34 = 0;
               Integer var33 = var87;
               byte var32 = 0;
               byte var74 = 0;
               int var72 = 0;
               final androidx.paging.compose.LazyPagingItems recoveredCapture_1601_0 = var0;
               Function0 var35 = ((kotlin.jvm.functions.Function0) () -> TicketsListReducerKt.reduceToTicketsScreenUiState$lambda$6$lambda$5(recoveredCapture_1601_0));
               var80 = var74;
               var84 = var32;
               var87 = var33;
               var88 = var34;
               Object var73 = var35;
               var67.updateRememberedValue(var73);
               var89 = var73;
            }

            Function0 var65 = (Function0)var89;
            var2.endReplaceGroup();
            Object var47 = null;
            byte var48 = 11;
            Function0 var49 = var65;
            byte var50 = var88;
            Integer var51 = var87;
            byte var52 = var84;
            byte var53 = var80;
            ErrorState var81 = new ErrorState$WithCTA(var53, var52, var51, var50, var49, var48, (DefaultConstructorMarker)var47);
            String var54 = var56;
            ErrorState var55 = var81;
            var78 = new TicketsScreenUiState$Error(var55, var54);
         }

         TicketsScreenUiState var61 = var78;
         var2.endReplaceGroup();
         var78 = var61;
      } else if (var0.getLoadState().getRefresh() instanceof Loading) {
         var2.startReplaceGroup(-374348650);
         var2.endReplaceGroup();
         var78 = new TicketsScreenUiState$Initial(var56);
      } else {
         var2.startReplaceGroup(-374344942);
         ComposerKt.sourceInformation(var2, "60@2525L64,61@2618L63");
         TicketsScreenUiState$Empty var62 = new TicketsScreenUiState$Empty(
            new EmptyState(
               StringResources_androidKt.stringResource(string.intercom_tickets_empty_state_title, var2, 0),
               StringResources_androidKt.stringResource(string.intercom_tickets_empty_state_text, var2, 0),
               null,
               4,
               null
            ),
            var56
         );
         var2.endReplaceGroup();
         var78 = var62;
      }

      TicketsScreenUiState var58 = var78;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var58;
   }

   private static final AppConfig reduceToTicketsScreenUiState$lambda$1$lambda$0() {
      return (AppConfig)Injector.get().getAppConfigProvider().get();
   }

   private static final Unit reduceToTicketsScreenUiState$lambda$4$lambda$3$lambda$2(LazyPagingItems var0) {
      var0.retry();
      return Unit.INSTANCE;
   }

   private static final Unit reduceToTicketsScreenUiState$lambda$6$lambda$5(LazyPagingItems var0) {
      var0.refresh();
      return Unit.INSTANCE;
   }
}
