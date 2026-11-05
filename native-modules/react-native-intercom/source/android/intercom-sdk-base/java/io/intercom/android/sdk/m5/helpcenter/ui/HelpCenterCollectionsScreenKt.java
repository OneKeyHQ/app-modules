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
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Error;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Initial;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Loading;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
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
   d1 = "\u0000,\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001aK\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\bH\u0001¢\u0006\u0002\u0010\n\u001a(\u0010\u000b\u001a\u00020\u0001*\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\bH\u0002¨\u0006\u000f",
   d2 = {
         "HelpCenterCollectionsScreen",
         "",
         "viewModel",
         "Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;",
         "collectionIds",
         "",
         "",
         "onCollectionClick",
         "Lkotlin/Function1;",
         "onAutoNavigateToCollection",
         "(Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;Ljava/util/List;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "helpCenterCollectionItems",
         "Landroidx/compose/foundation/lazy/LazyListScope;",
         "state",
         "Lio/intercom/android/sdk/m5/helpcenter/states/CollectionsUiState$Content;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterCollectionsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterCollectionsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionsScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt\n*L\n1#1,131:1\n1047#2,6:132\n1047#2,6:138\n1047#2,6:144\n204#3,13:150\n*S KotlinDebug\n*F\n+ 1 HelpCenterCollectionsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionsScreenKt\n*L\n40#1:132,6\n46#1:138,6\n58#1:144,6\n114#1:150,13\n*E\n"
)
public final class HelpCenterCollectionsScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterCollectionsScreen(
      @NotNull HelpCenterViewModel var0, @NotNull List var1, @NotNull Function1 var2, @NotNull Function1 var3, @Nullable Composer var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "viewModel");
      Intrinsics.checkNotNullParameter(var1, "collectionIds");
      Intrinsics.checkNotNullParameter(var2, "onCollectionClick");
      Intrinsics.checkNotNullParameter(var3, "onAutoNavigateToCollection");
      Composer var28 = var4.startRestartGroup(-1836627764);
      ComposerKt.sourceInformation(
         var28,
         "C(HelpCenterCollectionsScreen)P(3!1,2)39@1855L73,37@1804L131,45@1991L208,43@1940L266,53@2250L16,57@2392L945,54@2271L1066:HelpCenterCollectionsScreen.kt#ln9mxm"
      );
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var28.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var28.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var28.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var28.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var28.getSkipping()) {
         var28.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1836627764, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionsScreen (HelpCenterCollectionsScreen.kt:36)"
            );
         }

         String var10000 = "";
         var28.startReplaceGroup(2138885056);
         ComposerKt.sourceInformation(var28, "CC(remember):HelpCenterCollectionsScreen.kt#9igjgp");
         Composer var8 = var28;
         boolean var9 = var28.changedInstance(var0) | var28.changedInstance(var1);
         int var10 = 0;
         Object var11 = var8.rememberedValue();
         int var12 = 0;
         Object var49;
         if (!var9 && var11 != Composer.Companion.getEmpty()) {
            var49 = var11;
         } else {
            String var18 = "";
            int var13 = 0;
            Function2 var10001 = new HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$1$1(var0, var1, null);
            var10000 = var18;
            Object var14 = var10001;
            var8.updateRememberedValue(var14);
            var49 = var14;
         }

         Function2 var7 = (Function2)var49;
         var28.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var7, var28, 6);
         String var46 = "";
         var28.startReplaceGroup(2138889543);
         ComposerKt.sourceInformation(var28, "CC(remember):HelpCenterCollectionsScreen.kt#9igjgp");
         Composer var31 = var28;
         boolean var33 = var28.changedInstance(var0) | (var6 & 7168) == 2048;
         int $i$f$cachex = 0;
         Object it$ivx = var31.rememberedValue();
         int var38/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var51;
         if (!var33 && it$ivx != Composer.Companion.getEmpty()) {
            var51 = it$ivx;
         } else {
            String var44 = "";
            int var40 = 0;
            Function2 var50 = new HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$2$1(var0, var3, null);
            var46 = var44;
            Object var42 = var50;
            var31.updateRememberedValue(var42);
            var51 = var42;
         }

         Function2 var29 = (Function2)var51;
         var28.endReplaceGroup();
         EffectsKt.LaunchedEffect(var46, var29, var28, 6);
         State var30 = SnapshotStateKt.collectAsState(var0.getCollectionsState(), null, var28, 0, 1);
         Horizontal var32 = Alignment.Companion.getCenterHorizontally();
         Modifier var47 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         Object var52 = null;
         Object var10002 = null;
         boolean var10003 = false;
         Object var10004 = null;
         Horizontal var10005 = var32;
         Object var10006 = null;
         boolean var10007 = false;
         Object var10008 = null;
         var28.startReplaceGroup(2138903112);
         ComposerKt.sourceInformation(var28, "CC(remember):HelpCenterCollectionsScreen.kt#9igjgp");
         Composer var37 = var28;
         boolean var39 = var28.changed(var30) | (var6 & 896) == 256;
         int $i$f$cachexx = 0;
         Object it$ivxx = var37.rememberedValue();
         int var15/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var10009;
         if (!var39 && it$ivxx != Composer.Companion.getEmpty()) {
            var10009 = it$ivxx;
         } else {
            Object var26 = null;
            boolean var25 = false;
            Object var24 = null;
            Horizontal var23 = var32;
            Object var22 = null;
            boolean var21 = false;
            Object var20 = null;
            Object var19 = null;
            Modifier var45 = var47;
            int var16 = 0;
            final androidx.compose.runtime.State recoveredExpressionCapture_820_0 = var30;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_820_1 = var2;
            Function1 var27 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_820_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionsScreenKt.HelpCenterCollectionsScreen$lambda$3$lambda$2(recoveredExpressionCapture_820_0, recoveredExpressionCapture_820_1, (androidx.compose.foundation.lazy.LazyListScope) ((androidx.compose.foundation.lazy.LazyListScope)recoveredExpressionArgument_820_0)));
            var47 = var45;
            var52 = var19;
            var10002 = var20;
            var10003 = var21;
            var10004 = var22;
            var10005 = var23;
            var10006 = var24;
            var10007 = var25;
            var10008 = var26;
            Object var17 = var27;
            var37.updateRememberedValue(var17);
            var10009 = var17;
         }

         Function1 var35 = (Function1)var10009;
         var28.endReplaceGroup();
         LazyDslKt.LazyColumn(
            var47,
            (LazyListState)var52,
            (PaddingValues)var10002,
            var10003,
            (Vertical)var10004,
            var10005,
            (FlingBehavior)var10006,
            var10007,
            (OverscrollEffect)var10008,
            var35,
            var28,
            196614,
            478
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var48 = var28.endRestartGroup();
      if (var48 != null) {
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_821_0 = var0;
         final java.util.List recoveredExpressionCapture_821_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_821_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_821_3 = var3;
         final int recoveredExpressionCapture_821_4 = var5;
         var48.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_821_0, recoveredExpressionArgument_821_1) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterCollectionsScreenKt.HelpCenterCollectionsScreen$lambda$4(recoveredExpressionCapture_821_0, recoveredExpressionCapture_821_1, recoveredExpressionCapture_821_2, recoveredExpressionCapture_821_3, recoveredExpressionCapture_821_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_821_0), ((java.lang.Integer)recoveredExpressionArgument_821_1).intValue())));
      }
   }

   private static final void helpCenterCollectionItems(LazyListScope var0, io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content var1, Function1 var2) {
      LazyListScope.item$default(
         var0,
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(-464708772, true, new HelpCenterCollectionsScreenKt$helpCenterCollectionItems$1(var1)),
         3,
         null
      );
      LazyListScope var3 = var0;
      List var4 = var1.getCollections();
      Function2 var5 = null;
      int var6 = 0;
      var3.items(
         var4.size(),
         null,
         new HelpCenterCollectionsScreenKt$helpCenterCollectionItems$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$2(var4),
         ComposableLambdaKt.composableLambdaInstance(
            2039820996,
            true,
            new HelpCenterCollectionsScreenKt$helpCenterCollectionItems$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
               var4, var2
            )
         )
      );
   }

   private static final Unit HelpCenterCollectionsScreen$lambda$3$lambda$2(State var0, Function1 var1, LazyListScope var2) {
      Intrinsics.checkNotNullParameter(var2, "$this$LazyColumn");
      CollectionsUiState var3 = (CollectionsUiState)var0.getValue();
      if (Intrinsics.areEqual(var3, io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Initial.INSTANCE) || Intrinsics.areEqual(var3, io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Loading.INSTANCE)) {
         LazyListScope.item$default(
            var2,
            null,
            null,
            io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterCollectionsScreenKt.INSTANCE.recovered$b26556cd5c386b55(),
            3,
            null
         );
      } else if (var3 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Error) {
         LazyListScope.item$default(
            var2,
            null,
            null,
            ComposableLambdaKt.composableLambdaInstance(-2086280874, true, new HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$3$1$1(var3)),
            3,
            null
         );
      } else {
         if (!(var3 instanceof io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content)) {
            throw new NoWhenBranchMatchedException();
         }

         if (((io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content)var3).getCollections().isEmpty()) {
            LazyListScope.item$default(
               var2,
               null,
               null,
               io.intercom.android.sdk.m5.helpcenter.ui.ComposableSingletons$HelpCenterCollectionsScreenKt.INSTANCE.recovered$6532409fab43c00f(),
               3,
               null
            );
         } else {
            helpCenterCollectionItems(var2, (io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content)var3, var1);
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterCollectionsScreen$lambda$4(
      HelpCenterViewModel var0, List var1, Function1 var2, Function1 var3, int var4, Composer var5, int var6
   ) {
      HelpCenterCollectionsScreen(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }
}
