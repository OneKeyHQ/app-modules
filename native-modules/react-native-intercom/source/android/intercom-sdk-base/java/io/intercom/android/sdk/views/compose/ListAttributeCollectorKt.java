package io.intercom.android.sdk.views.compose;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.ExposedDropdownMenuKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000e\u001aI\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r¨\u0006\u0011²\u0006\n\u0010\u0012\u001a\u00020\u0007X\u008a\u008e\u0002²\u0006\n\u0010\u0013\u001a\u00020\u0014X\u008a\u008e\u0002",
   d2 = {
         "ListAttributeCollector",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "attributeData",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "hasErrors",
         "",
         "loading",
         "onSubmitAttribute",
         "Lkotlin/Function1;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/AttributeData;ZZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "ListAttributePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SubmittedListAttributePreview",
         "DisabledListAttributePreview",
         "SubmittedAndDisabledListAttributePreview",
         "intercom-sdk-base_release",
         "expanded",
         "value",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nListAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,224:1\n1047#2,6:225\n1047#2,6:231\n1047#2,6:237\n1047#2,6:243\n85#3:249\n117#3,2:250\n85#3:252\n117#3,2:253\n*S KotlinDebug\n*F\n+ 1 ListAttributeCollector.kt\nio/intercom/android/sdk/views/compose/ListAttributeCollectorKt\n*L\n41#1:225,6\n46#1:231,6\n47#1:237,6\n53#1:243,6\n46#1:249\n46#1:250,2\n47#1:252\n47#1:253,2\n*E\n"
)
public final class ListAttributeCollectorKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ListAttributeCollector(
      @Nullable Modifier var0, @NotNull AttributeData var1, boolean var2, boolean var3, @Nullable Function1 var4, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "attributeData");
      Composer var26 = var5.startRestartGroup(1993212876);
      ComposerKt.sourceInformation(
         var26, "C(ListAttributeCollector)P(3)40@1752L2,45@1883L34,46@1952L55,46@1935L72,52@2219L37,55@2264L2776,49@2095L2945:ListAttributeCollector.kt#h0e4f2"
      );
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var26.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var26.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var26.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var26.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var26.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var26.getSkipping()) {
         var26.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var7 & 4) != 0) {
            var2 = false;
         }

         if ((var7 & 8) != 0) {
            var3 = false;
         }

         if ((var7 & 16) != 0) {
            var26.startReplaceGroup(497280001);
            ComposerKt.sourceInformation(var26, "CC(remember):ListAttributeCollector.kt#9igjgp");
            Composer var10 = var26;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var38 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1645_0) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.ListAttributeCollector$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.AttributeData) ((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredExpressionArgument_1645_0)));
               var10.updateRememberedValue(var38);
               var10000 = var38;
            } else {
               var10000 = var13;
            }

            Function1 var9 = (Function1)var10000;
            var26.endReplaceGroup();
            var4 = var9;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1993212876, var8, -1, "io.intercom.android.sdk.views.compose.ListAttributeCollector (ListAttributeCollector.kt:41)");
         }

         boolean var27 = var1.isFormDisabled();
         boolean var28 = var1.getAttribute().getSubmitted();
         var26.startReplaceGroup(497284225);
         ComposerKt.sourceInformation(var26, "CC(remember):ListAttributeCollector.kt#9igjgp");
         Composer var32 = var26;
         boolean var35 = false;
         int var39 = 0;
         Object var16 = var32.rememberedValue();
         int var17 = 0;
         Object var51;
         if (var16 == Composer.Companion.getEmpty()) {
            int var18 = 0;
            Object var45 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
            var32.updateRememberedValue(var45);
            var51 = var45;
         } else {
            var51 = var16;
         }

         MutableState var30 = (MutableState)var51;
         var26.endReplaceGroup();
         MutableState var29 = var30;
         Object[] var52 = new Object[0];
         var26.startReplaceGroup(497286454);
         ComposerKt.sourceInformation(var26, "CC(remember):ListAttributeCollector.kt#9igjgp");
         Composer var36 = var26;
         boolean var40 = var26.changedInstance(var1);
         int $i$f$cachex = 0;
         Object it$ivx = var36.rememberedValue();
         int var46/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var55;
         if (!var40 && it$ivx != Composer.Companion.getEmpty()) {
            var55 = it$ivx;
         } else {
            Object[] var24 = var52;
            int var19 = 0;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredExpressionCapture_1646_0 = var1;
            Function0 var10001 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.ListAttributeCollector$lambda$6$lambda$5(recoveredExpressionCapture_1646_0));
            var52 = var24;
            Object var20 = var10001;
            var36.updateRememberedValue(var20);
            var55 = var20;
         }

         Function0 var33 = (Function0)var55;
         var26.endReplaceGroup();
         MutableState var31 = (MutableState)RememberSaveableKt.rememberSaveable(var52, var33, var26, 0);
         boolean var34 = var2 ? true : !var27 && !var28;
         Modifier var37 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         boolean var53 = ListAttributeCollector$lambda$3(var29);
         var26.startReplaceGroup(497294980);
         ComposerKt.sourceInformation(var26, "CC(remember):ListAttributeCollector.kt#9igjgp");
         Composer var44 = var26;
         boolean var47 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var44.rememberedValue();
         int var21/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var56;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            boolean var50 = var53;
            int var22 = 0;
            final androidx.compose.runtime.MutableState recoveredExpressionCapture_1647_0 = var29;
            Function1 var25 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1647_0) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.ListAttributeCollector$lambda$10$lambda$9(recoveredExpressionCapture_1647_0, ((java.lang.Boolean)recoveredExpressionArgument_1647_0).booleanValue()));
            var53 = var50;
            Object var23 = var25;
            var44.updateRememberedValue(var23);
            var56 = var23;
         } else {
            var56 = it$ivxx;
         }

         Function1 var42 = (Function1)var56;
         var26.endReplaceGroup();
         ExposedDropdownMenuKt.ExposedDropdownMenuBox(
            var53,
            var42,
            var37,
            ComposableLambdaKt.rememberComposableLambda(
               1992435426, true, new ListAttributeCollectorKt$ListAttributeCollector$3(var27, var34, var1, var31, var28, var3, var29, var4), var26, 54
            ),
            var26,
            3120,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var54 = var26.endRestartGroup();
      if (var54 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_1648_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredExpressionCapture_1648_1 = var1;
         final boolean recoveredExpressionCapture_1648_2 = var2;
         final boolean recoveredExpressionCapture_1648_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_1648_4 = var4;
         final int recoveredExpressionCapture_1648_5 = var6;
         final int recoveredExpressionCapture_1648_6 = var7;
         var54.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1648_0, recoveredExpressionArgument_1648_1) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.ListAttributeCollector$lambda$11(recoveredExpressionCapture_1648_0, recoveredExpressionCapture_1648_1, recoveredExpressionCapture_1648_2, recoveredExpressionCapture_1648_3, recoveredExpressionCapture_1648_4, recoveredExpressionCapture_1648_5, recoveredExpressionCapture_1648_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1648_0), ((java.lang.Integer)recoveredExpressionArgument_1648_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ListAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1324269915);
      ComposerKt.sourceInformation(var2, "C(ListAttributePreview)133@5116L574:ListAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1324269915, var1, -1, "io.intercom.android.sdk.views.compose.ListAttributePreview (ListAttributeCollector.kt:132)");
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$ListAttributeCollectorKt.INSTANCE.recovered$fd838d44303763da(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1649_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1649_0, recoveredExpressionArgument_1649_1) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.ListAttributePreview$lambda$12(recoveredExpressionCapture_1649_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1649_0), ((java.lang.Integer)recoveredExpressionArgument_1649_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedListAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-899805828);
      ComposerKt.sourceInformation(var2, "C(SubmittedListAttributePreview)156@5766L616:ListAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -899805828, var1, -1, "io.intercom.android.sdk.views.compose.SubmittedListAttributePreview (ListAttributeCollector.kt:155)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$ListAttributeCollectorKt.INSTANCE.recovered$265b6f46b79d310a(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1651_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1651_0, recoveredExpressionArgument_1651_1) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.SubmittedListAttributePreview$lambda$13(recoveredExpressionCapture_1651_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1651_0), ((java.lang.Integer)recoveredExpressionArgument_1651_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DisabledListAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(865192767);
      ComposerKt.sourceInformation(var2, "C(DisabledListAttributePreview)180@6457L573:ListAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               865192767, var1, -1, "io.intercom.android.sdk.views.compose.DisabledListAttributePreview (ListAttributeCollector.kt:179)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$ListAttributeCollectorKt.INSTANCE.recovered$3faf0e22fa74c6a7(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1644_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1644_0, recoveredExpressionArgument_1644_1) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.DisabledListAttributePreview$lambda$14(recoveredExpressionCapture_1644_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1644_0), ((java.lang.Integer)recoveredExpressionArgument_1644_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedAndDisabledListAttributePreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1340154819);
      ComposerKt.sourceInformation(var2, "C(SubmittedAndDisabledListAttributePreview)203@7117L615:ListAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1340154819, var1, -1, "io.intercom.android.sdk.views.compose.SubmittedAndDisabledListAttributePreview (ListAttributeCollector.kt:202)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.views.compose.ComposableSingletons$ListAttributeCollectorKt.INSTANCE.recovered$db063fabb945fca0(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_1650_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_1650_0, recoveredExpressionArgument_1650_1) -> io.intercom.android.sdk.views.compose.ListAttributeCollectorKt.SubmittedAndDisabledListAttributePreview$lambda$15(recoveredExpressionCapture_1650_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_1650_0), ((java.lang.Integer)recoveredExpressionArgument_1650_1).intValue())));
      }
   }

   private static final Unit ListAttributeCollector$lambda$1$lambda$0(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final boolean ListAttributeCollector$lambda$3(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void ListAttributeCollector$lambda$4(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final MutableState ListAttributeCollector$lambda$6$lambda$5(AttributeData var0) {
      String var10000 = var0.getAttribute().getValue();
      if (var10000 == null) {
         var10000 = "";
      }

      return SnapshotStateKt.mutableStateOf$default(var10000, null, 2, null);
   }

   private static final String ListAttributeCollector$lambda$7(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (String)var1.getValue();
   }

   private static final void ListAttributeCollector$lambda$8(MutableState var0, String var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit ListAttributeCollector$lambda$10$lambda$9(MutableState var0, boolean var1) {
      ListAttributeCollector$lambda$4(var0, var1);
      return Unit.INSTANCE;
   }

   private static final Unit ListAttributeCollector$lambda$11(
      Modifier var0, AttributeData var1, boolean var2, boolean var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      ListAttributeCollector(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit ListAttributePreview$lambda$12(int var0, Composer var1, int var2) {
      ListAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedListAttributePreview$lambda$13(int var0, Composer var1, int var2) {
      SubmittedListAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DisabledListAttributePreview$lambda$14(int var0, Composer var1, int var2) {
      DisabledListAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedAndDisabledListAttributePreview$lambda$15(int var0, Composer var1, int var2) {
      SubmittedAndDisabledListAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final String access$ListAttributeCollector$lambda$7(MutableState var0) {
      return ListAttributeCollector$lambda$7(var0);
   }

   // $VF: synthetic method
   public static final boolean access$ListAttributeCollector$lambda$3(MutableState var0) {
      return ListAttributeCollector$lambda$3(var0);
   }

   // $VF: synthetic method
   public static final void access$ListAttributeCollector$lambda$8(MutableState var0, String var1) {
      ListAttributeCollector$lambda$8(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$ListAttributeCollector$lambda$4(MutableState var0, boolean var1) {
      ListAttributeCollector$lambda$4(var0, var1);
   }
}
