package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.FlowLayoutKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.EventLabelSegment;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a-\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b¨\u0006\f",
   d2 = {
         "EventRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "labelSegments",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/EventLabelSegment;",
         "avatar",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;Landroidx/compose/runtime/Composer;II)V",
         "ParticipantAddedRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nEventRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/EventRowKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,136:1\n118#2:137\n118#2:138\n118#2:145\n118#2:146\n1047#3,6:139\n*S KotlinDebug\n*F\n+ 1 EventRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/EventRowKt\n*L\n38#1:137\n39#1:138\n48#1:145\n49#1:146\n42#1:139,6\n*E\n"
)
public final class EventRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EventRow(@Nullable Modifier var0, @NotNull List var1, @NotNull AvatarWrapper var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "labelSegments");
      Intrinsics.checkNotNullParameter(var2, "avatar");
      var3 = var3.startRestartGroup(150762908);
      ComposerKt.sourceInformation(var3, "C(EventRow)P(2,1)36@1558L10,41@1766L11,49@2086L1681,43@1785L1982:EventRow.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(150762908, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.EventRow (EventRow.kt:35)");
         }

         TextStyle var7 = IntercomTheme.INSTANCE.getTypography(var3, IntercomTheme.$stable).getType04Point5();
         int var9 = 20;
         int var10 = 0;
         float var8 = Dp.recovered$95b3d103663bf060(var9);
         var10 = 16;
         int $i$f$getDpx = 0;
         float var27 = Dp.recovered$95b3d103663bf060(var10);
         StringBuilder var10000 = new StringBuilder().append("Event: ");
         Iterable var10001 = var1;
         CharSequence var10002 = "";
         Object var10003 = null;
         Object var10004 = null;
         byte var10005 = 0;
         Object var10006 = null;
         var3.startReplaceGroup(1628134550);
         ComposerKt.sourceInformation(var3, "CC(remember):EventRow.kt#9igjgp");
         Composer var12 = var3;
         boolean var13 = false;
         int var14 = 0;
         Object var15 = var12.rememberedValue();
         int var16 = 0;
         Object var10007;
         if (var15 == Composer.Companion.getEmpty()) {
            Object var24 = null;
            byte var23 = 0;
            Object var22 = null;
            Object var21 = null;
            CharSequence var20 = var10002;
            Iterable var19 = var10001;
            StringBuilder var18 = var10000;
            int var17 = 0;
            Function1 var25 = ((kotlin.jvm.functions.Function1) (recoveredArgument_701_0) -> EventRowKt.EventRow$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.EventLabelSegment)recoveredArgument_701_0));
            var10000 = var18;
            var10001 = var19;
            var10002 = var20;
            var10003 = var21;
            var10004 = var22;
            var10005 = var23;
            var10006 = var24;
            Object var35 = var25;
            var12.updateRememberedValue(var35);
            var10007 = var35;
         } else {
            var10007 = var15;
         }

         Function1 var30 = (Function1)var10007;
         var3.endReplaceGroup();
         String var29 = var10000.append(
               CollectionsKt.joinToString$default(
                  var10001, var10002, (CharSequence)var10003, (CharSequence)var10004, var10005, (CharSequence)var10006, var30, 30, null
               )
            )
            .toString();
         Modifier var36 = PaddingKt.recovered$f3b9f2e94c07c806(SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), var27, 0.0F, 2, null);
         $i$f$getDpx = 6;
         int $i$f$getDpxx = 0;
         Horizontal var38 = Arrangement.INSTANCE
            .recovered$49b0bf381d0b9a78(Dp.recovered$95b3d103663bf060($i$f$getDpx), Alignment.Companion.getCenterHorizontally());
         $i$f$getDpx = 6;
         int $i$f$getDpxxx = 0;
         FlowLayoutKt.FlowRow(
            var36,
            var38,
            Arrangement.INSTANCE.recovered$6fba629a41b87d9d(Dp.recovered$95b3d103663bf060($i$f$getDpx), Alignment.Companion.getCenterVertically()),
            null,
            0,
            0,
            ComposableLambdaKt.rememberComposableLambda(639828375, true, new EventRowKt$EventRow$1(var8, var2, var29, var7, var1), var3, 54),
            var3,
            1573296,
            56
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var37 = var3.endRestartGroup();
      if (var37 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_702_0 = var0;
         final java.util.List recoveredCapture_702_1 = var1;
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredCapture_702_2 = var2;
         final int recoveredCapture_702_3 = var4;
         final int recoveredCapture_702_4 = var5;
         var37.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_702_0, recoveredArgument_702_1) -> EventRowKt.EventRow$lambda$2(recoveredCapture_702_0, recoveredCapture_702_1, recoveredCapture_702_2, recoveredCapture_702_3, recoveredCapture_702_4, (androidx.compose.runtime.Composer)recoveredArgument_702_0, ((java.lang.Integer)recoveredArgument_702_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ParticipantAddedRowPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(524974868);
      ComposerKt.sourceInformation(var0, "C(ParticipantAddedRowPreview)102@3848L1488:EventRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               524974868, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.ParticipantAddedRowPreview (EventRow.kt:101)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$EventRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_703_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_703_0, recoveredArgument_703_1) -> EventRowKt.ParticipantAddedRowPreview$lambda$3(recoveredCapture_703_0, (androidx.compose.runtime.Composer)recoveredArgument_703_0, ((java.lang.Integer)recoveredArgument_703_1).intValue())));
      }
   }

   private static final CharSequence EventRow$lambda$1$lambda$0(EventLabelSegment var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return var0.getText();
   }

   private static final Unit EventRow$lambda$2(Modifier var0, List var1, AvatarWrapper var2, int var3, int var4, Composer var5, int var6) {
      EventRow(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ParticipantAddedRowPreview$lambda$3(int var0, Composer var1, int var2) {
      ParticipantAddedRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
