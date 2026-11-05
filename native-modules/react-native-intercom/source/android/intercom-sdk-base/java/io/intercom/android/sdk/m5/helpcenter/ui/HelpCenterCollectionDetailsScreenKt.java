package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.OverscrollEffect;
import androidx.compose.foundation.gestures.FlingBehavior;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.foundation.lazy.LazyDslKt;
import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$CollectionRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$FullHelpCenterRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$SendMessageRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Error;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Initial;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Loading;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001aG\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\t\u001a<\u0010\n\u001a\u00020\u0001*\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\u0007H\u0002¨\u0006\u000e",
   d2 = {
         "HelpCenterCollectionDetailsScreen",
         "",
         "viewModel",
         "Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;",
         "collectionId",
         "",
         "onArticleClicked",
         "Lkotlin/Function1;",
         "onCollectionClicked",
         "(Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "helpCenterSectionItems",
         "Landroidx/compose/foundation/lazy/LazyListScope;",
         "state",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionDetailsUiState$Content;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterCollectionDetailsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterCollectionDetailsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionDetailsScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,96:1\n1047#2,6:97\n1047#2,6:103\n1047#2,6:109\n1878#3,3:115\n*S KotlinDebug\n*F\n+ 1 HelpCenterCollectionDetailsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionDetailsScreenKt\n*L\n29#1:97,6\n34#1:103,6\n42#1:109,6\n73#1:115,3\n*E\n"
)
public final class HelpCenterCollectionDetailsScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterCollectionDetailsScreen(
      @NotNull HelpCenterViewModel var0, @NotNull String var1, @Nullable Function1 var2, @NotNull Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "viewModel");
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      Intrinsics.checkNotNullParameter(var3, "onCollectionClicked");
      Composer var29 = var4.startRestartGroup(-1331499807);
      ComposerKt.sourceInformation(
         var29,
         "C(HelpCenterCollectionDetailsScreen)P(3)28@1457L2,33@1563L70,31@1512L128,37@1690L16,41@1832L853,38@1711L974:HelpCenterCollectionDetailsScreen.kt#ln9mxm"
      );
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var29.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var29.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var29.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var29.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var29.getSkipping()) {
         var29.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var29.startReplaceGroup(-1710804376);
            ComposerKt.sourceInformation(var29, "CC(remember):HelpCenterCollectionDetailsScreen.kt#9igjgp");
            Composer var9 = var29;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var41 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_817_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionDetailsScreenKt.HelpCenterCollectionDetailsScreen$lambda$1$lambda$0((java.lang.String) ((java.lang.String)recoveredExpressionArgument_817_0)));
               var9.updateRememberedValue(var41);
               var10000 = var41;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var29.endReplaceGroup();
            var2 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1331499807, var7, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionDetailsScreen (HelpCenterCollectionDetailsScreen.kt:30)"
            );
         }

         String var46 = "";
         var29.startReplaceGroup(-1710800916);
         ComposerKt.sourceInformation(var29, "CC(remember):HelpCenterCollectionDetailsScreen.kt#9igjgp");
         Composer var32 = var29;
         boolean var34 = var29.changedInstance(var0) | (var7 & 112) == 32;
         int var35 = 0;
         Object var37 = var32.rememberedValue();
         int var39 = 0;
         Object var49;
         if (!var34 && var37 != Composer.Companion.getEmpty()) {
            var49 = var37;
         } else {
            String var19 = "";
            int var42 = 0;
            Function2 var10001 = new HelpCenterCollectionDetailsScreenKt$HelpCenterCollectionDetailsScreen$2$1(var0, var1, null);
            var46 = var19;
            Object var15 = var10001;
            var32.updateRememberedValue(var15);
            var49 = var15;
         }

         Function2 var30 = (Function2)var49;
         var29.endReplaceGroup();
         EffectsKt.LaunchedEffect(var46, var30, var29, 6);
         State var31 = SnapshotStateKt.collectAsState(var0.getCollectionDetailsState(), null, var29, 0, 1);
         Horizontal var33 = Alignment.Companion.getCenterHorizontally();
         Modifier var47 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         Object var50 = null;
         Object var10002 = null;
         boolean var10003 = false;
         Object var10004 = null;
         Horizontal var10005 = var33;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var29.startReplaceGroup(-1710791525);
         ComposerKt.sourceInformation(var29, "CC(remember):HelpCenterCollectionDetailsScreen.kt#9igjgp");
         Composer var38 = var29;
         boolean var40 = var29.changed(var31) | (var7 & 896) == 256 | (var7 & 7168) == 2048;
         int $i$f$cachex = 0;
         Object it$ivx = var38.rememberedValue();
         int var16/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10009;
         if (!var40 && it$ivx != Composer.Companion.getEmpty()) {
            var10009 = it$ivx;
         } else {
            Object var27 = null;
            boolean var26 = false;
            Object var25 = null;
            Horizontal var24 = var33;
            Object var23 = null;
            boolean var22 = false;
            Object var21 = null;
            Object var20 = null;
            Modifier var45 = var47;
            int var17 = 0;
            final androidx.compose.runtime.State recoveredExpressionCapture_818_0 = var31;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_818_1 = var2;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_818_2 = var3;
            Function1 var28 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_818_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionDetailsScreenKt.HelpCenterCollectionDetailsScreen$lambda$4$lambda$3(recoveredExpressionCapture_818_0, recoveredExpressionCapture_818_1, recoveredExpressionCapture_818_2, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_818_0)));
            var47 = var45;
            var50 = var20;
            var10002 = var21;
            var10003 = var22;
            var10004 = var23;
            var10005 = var24;
            var10006 = var25;
            var10007 = var26;
            var10008 = var27;
            Object var18 = var28;
            var38.updateRememberedValue(var18);
            var10009 = var18;
         }

         Function1 var36 = (Function1)var10009;
         var29.endReplaceGroup();
         LazyDslKt.LazyColumn(
            var47,
            (LazyListState)var50,
            (PaddingValues)var10002,
            var10003,
            (Vertical)var10004,
            var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var36,
            var29,
            196614,
            478
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var48 = var29.endRestartGroup();
      if (var48 != null) {
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_819_0 = var0;
         final java.lang.String recoveredExpressionCapture_819_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_819_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_819_3 = var3;
         final int recoveredExpressionCapture_819_4 = var5;
         final int recoveredExpressionCapture_819_5 = var6;
         var48.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_819_0, recoveredExpressionArgument_819_1) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionDetailsScreenKt.HelpCenterCollectionDetailsScreen$lambda$5(recoveredExpressionCapture_819_0, recoveredExpressionCapture_819_1, recoveredExpressionCapture_819_2, recoveredExpressionCapture_819_3, recoveredExpressionCapture_819_4, recoveredExpressionCapture_819_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_819_0), ((java.lang.Integer)recoveredExpressionArgument_819_1).intValue())));
      }
   }

   private static final void helpCenterSectionItems(LazyListScope var0, io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content var1, Function1 var2, Function1 var3) {
      LazyListScope.item$default(
         var0,
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(1491252145, true, new HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$1(var1)),
         3,
         null
      );
      List var4 = var1.getCollectionDetailsRows();
      Iterable var5 = var4;
      int var6 = 0;
      int var7 = 0;

      for (Object var9 : var5) {
         int var10 = var7++;
         if (var10 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         CollectionDetailsRow var11 = (CollectionDetailsRow)var9;
         int var12 = var10;
         int var13 = 0;
         CollectionDetailsRow var14 = var11;
         if (var14 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$ArticleRow) {
            LazyListScope.item$default(
               var0,
               null,
               null,
               ComposableLambdaKt.composableLambdaInstance(
                  -103698696, true, new HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$1(var12, var11, var2, var4)
               ),
               3,
               null
            );
         } else if (Intrinsics.areEqual(var14, io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$FullHelpCenterRow.INSTANCE)) {
            LazyListScope.item$default(
               var0,
               null,
               null,
               io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterCollectionDetailsScreenKt.INSTANCE.recovered$030ea852b673257f(),
               3,
               null
            );
         } else if (var14 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$CollectionRow) {
            LazyListScope.item$default(
               var0,
               null,
               null,
               ComposableLambdaKt.composableLambdaInstance(1175818224, true, new HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$2(var11, var3)),
               3,
               null
            );
         } else {
            if (!(var14 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsRow$SendMessageRow)) {
               throw new NoWhenBranchMatchedException();
            }

            LazyListScope.item$default(
               var0,
               null,
               null,
               ComposableLambdaKt.composableLambdaInstance(1352146481, true, new HelpCenterCollectionDetailsScreenKt$helpCenterSectionItems$2$3(var11)),
               3,
               null
            );
         }
      }
   }

   private static final Unit HelpCenterCollectionDetailsScreen$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterCollectionDetailsScreen$lambda$4$lambda$3(State var0, Function1 var1, Function1 var2, LazyListScope var3) {
      Intrinsics.checkNotNullParameter(var3, "$this$LazyColumn");
      CollectionDetailsUiState var4 = (CollectionDetailsUiState)var0.getValue();
      if (Intrinsics.areEqual(var4, io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Initial.INSTANCE) || Intrinsics.areEqual(var4, io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Loading.INSTANCE)) {
         LazyListScope.item$default(
            var3,
            null,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterCollectionDetailsScreenKt.INSTANCE.recovered$b26556cd5c386b55(),
            3,
            null
         );
      } else if (var4 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Error) {
         LazyListScope.item$default(
            var3,
            null,
            null,
            ComposableLambdaKt.composableLambdaInstance(211733483, true, new HelpCenterCollectionDetailsScreenKt$HelpCenterCollectionDetailsScreen$3$1$1(var4)),
            3,
            null
         );
      } else {
         if (!(var4 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content)) {
            throw new NoWhenBranchMatchedException();
         }

         if (((io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content)var4).getCollectionDetailsRows().isEmpty()) {
            LazyListScope.item$default(
               var3,
               null,
               null,
               io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterCollectionDetailsScreenKt.INSTANCE.recovered$6532409fab43c00f(),
               3,
               null
            );
         } else {
            helpCenterSectionItems(var3, (io.intercom.android.sdk.m5.helpcenter.states.CollectionDetailsUiState$Content)var4, var1, var2);
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterCollectionDetailsScreen$lambda$5(
      HelpCenterViewModel var0, String var1, Function1 var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      HelpCenterCollectionDetailsScreen(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }
}
