package io.intercom.android.sdk.helpcenter.articles;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.articles.ArticleWebViewClient;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Content;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Error;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Initial;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState;
import io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.m5.components.IntercomErrorScreenKt;
import io.intercom.android.sdk.m5.components.LoadingScreenKt;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleActivity.kt\nio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1$3\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,290:1\n87#2:291\n84#2,9:292\n94#2:356\n81#3,6:301\n88#3,6:316\n96#3:355\n402#4,9:307\n411#4:322\n412#4,2:353\n1047#5,6:323\n1047#5,6:329\n1047#5,6:335\n1047#5,6:341\n1047#5,6:347\n1047#5,6:357\n*S KotlinDebug\n*F\n+ 1 IntercomArticleActivity.kt\nio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1$3\n*L\n111#1:291\n111#1:292,9\n111#1:356\n111#1:301,6\n111#1:316,6\n111#1:355\n111#1:307,9\n111#1:322\n111#1:353,2\n125#1:323,6\n188#1:329,6\n200#1:335,6\n203#1:341,6\n206#1:347,6\n228#1:357,6\n*E\n"
)
final class IntercomArticleActivity$onCreate$1$1$3 implements Function3 {
   // $VF: synthetic field
   final IntercomArticleActivity this$0;

   IntercomArticleActivity$onCreate$1$1$3(IntercomArticleActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(PaddingValues var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "paddingValues");
      ComposerKt.sourceInformation(var2, "C100@4578L16:IntercomArticleActivity.kt#1wdz33");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -494666138,
               var4,
               -1,
               "io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.onCreate.<anonymous>.<anonymous>.<anonymous> (IntercomArticleActivity.kt:100)"
            );
         }

         ArticleViewState var5 = (ArticleViewState)SnapshotStateKt.collectAsState(
               IntercomArticleActivity.access$getViewModel(this.this$0).getState(), null, var2, 0, 1
            )
            .getValue();
         if (var5 instanceof io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Initial) {
            var2.startReplaceGroup(2087909018);
            ComposerKt.sourceInformation(var2, "102@4689L221");
            LoadingScreenKt.LoadingScreen(PaddingKt.padding(Modifier.Companion, var1), drawable.intercom_article_webview_loading_state, false, var2, 0, 4);
            var2.endReplaceGroup();
         } else if (var5 instanceof io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Content) {
            var2.startReplaceGroup(2088452727);
            ComposerKt.sourceInformation(var2, "109@5041L21,115@5389L6,110@5091L7004");
            ScrollState var6 = ScrollKt.rememberScrollState(0, var2, 0, 1);
            Modifier var7 = BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.fillMaxSize$default(
                  ScrollKt.verticalScroll$default(PaddingKt.padding(Modifier.Companion, var1), var6, false, null, false, 14, null), 0.0F, 1, null
               ),
               IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
               null,
               2,
               null
            );
            IntercomArticleActivity var10 = this.this$0;
            int var11 = 0;
            int var12 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            Vertical var8 = Arrangement.INSTANCE.getTop();
            Horizontal var9 = Alignment.Companion.getStart();
            MeasurePolicy var13 = ColumnKt.columnMeasurePolicy(var8, var9, var2, 14 & var11 >> 3 | 112 & var11 >> 3);
            Modifier var14 = var7;
            MeasurePolicy var15 = var13;
            Composer var16 = var2;
            int var17 = 112 & var11 << 3;
            int var18 = 0;
            ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
            CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
            Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
            Function0 var22 = ComposeUiNode.Companion.getConstructor();
            Composer var23 = var16;
            int var24 = 6 | 896 & var17 << 6;
            int var25 = 0;
            ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var23.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var23.startReusableNode();
            if (var23.getInserting()) {
               var23.createNode(var22);
            } else {
               var23.useNode();
            }

            Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
            int var27 = 0;
            Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
            int var28 = 14 & var24 >> 6;
            Composer var29 = var23;
            int var30 = 0;
            ComposerKt.sourceInformationMarkerStart(var29, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var31 = 6 | 112 & var11 >> 6;
            Composer var32 = var29;
            ColumnScope var33 = ColumnScopeInstance.INSTANCE;
            int var34 = 0;
            ComposerKt.sourceInformationMarkerStart(var32, -292781008, "C124@5946L4437,187@10430L39,123@5887L4617:IntercomArticleActivity.kt#1wdz33");
            String var35 = ((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Content)var5).getArticleUrl();
            Pair[] var36 = new Pair[]{
               TuplesKt.to("MobileClientDisplayType", "AndroidIntercomHeaderless"),
               TuplesKt.to("MobileClient", "AndroidIntercomWebView"),
               TuplesKt.to("MobileClientReactionsHidden", "true")
            };
            Map var37 = MapsKt.mapOf(var36);
            var32.startReplaceGroup(-286526201);
            ComposerKt.sourceInformation(var32, "CC(remember):IntercomArticleActivity.kt#9igjgp");
            Composer var38 = var32;
            boolean var39 = var32.changedInstance(var10) | var32.changed(var35);
            int var40 = 0;
            Object var41 = var38.rememberedValue();
            int var42 = 0;
            Object var10000;
            if (!var39 && var41 != Composer.Companion.getEmpty()) {
               var10000 = var41;
            } else {
               int var43 = 0;
               final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_88_0 = var10;
               final java.lang.String recoveredExpressionCapture_88_1 = var35;
               final java.util.Map recoveredExpressionCapture_88_2 = var37;
               Object var44 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_88_0) -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3.invoke$lambda$11$lambda$2$lambda$1(recoveredExpressionCapture_88_0, recoveredExpressionCapture_88_1, recoveredExpressionCapture_88_2, (android.content.Context) ((android.content.Context)recoveredExpressionArgument_88_0)));
               var38.updateRememberedValue(var44);
               var10000 = var44;
            }

            Function1 var76 = (Function1)var10000;
            var32.endReplaceGroup();
            Function1 var120 = var76;
            Object var10001 = null;
            var32.startReplaceGroup(-286387111);
            ComposerKt.sourceInformation(var32, "CC(remember):IntercomArticleActivity.kt#9igjgp");
            Composer var79 = var32;
            boolean var81 = false;
            int $i$f$cachex = 0;
            Object it$ivx = var79.rememberedValue();
            int var94/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10002;
            if (it$ivx == Composer.Companion.getEmpty()) {
               Object var45 = null;
               Function1 var46 = var76;
               int var98 = 0;
               Function1 var47 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_89_0) -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3.invoke$lambda$11$lambda$4$lambda$3((android.webkit.WebView) ((android.webkit.WebView)recoveredExpressionArgument_89_0)));
               var120 = var46;
               var10001 = var45;
               Object var99 = var47;
               var79.updateRememberedValue(var99);
               var10002 = var99;
            } else {
               var10002 = it$ivx;
            }

            Function1 var77 = (Function1)var10002;
            var32.endReplaceGroup();
            AndroidView_androidKt.AndroidView(var120, (Modifier)var10001, var77, var32, 384, 2);
            io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState var78 = ((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Content)var5).getReactionState();
            boolean var80 = var78.getReactionComponentVisibility() == 0;
            var32.startReplaceGroup(-286373239);
            ComposerKt.sourceInformation(var32, "199@11124L117,202@11307L121,205@11492L119,196@10883L767,213@11953L40");
            if (var80) {
               Modifier var121 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState var124 = var78;
               var32.startReplaceGroup(-286364825);
               ComposerKt.sourceInformation(var32, "CC(remember):IntercomArticleActivity.kt#9igjgp");
               Composer var87 = var32;
               boolean var91 = var32.changedInstance(var10);
               int $i$f$cachexx = 0;
               Object it$ivxx = var87.rememberedValue();
               int var103/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
               Object var126;
               if (!var91 && it$ivxx != Composer.Companion.getEmpty()) {
                  var126 = it$ivxx;
               } else {
                  io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState var106 = var78;
                  Modifier var109 = var121;
                  int var48 = 0;
                  final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_90_0 = var10;
                  Function0 var112 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3.invoke$lambda$11$lambda$6$lambda$5(recoveredExpressionCapture_90_0));
                  var121 = var109;
                  var124 = var106;
                  Object var49 = var112;
                  var87.updateRememberedValue(var49);
                  var126 = var49;
               }

               Function0 var82 = (Function0)var126;
               var32.endReplaceGroup();
               Function0 var127 = var82;
               var32.startReplaceGroup(-286358965);
               ComposerKt.sourceInformation(var32, "CC(remember):IntercomArticleActivity.kt#9igjgp");
               Composer var88 = var32;
               boolean var92 = var32.changedInstance(var10);
               int $i$f$cachexxx = 0;
               Object it$ivxxx = var88.rememberedValue();
               int var104/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
               Object var10003;
               if (!var92 && it$ivxxx != Composer.Companion.getEmpty()) {
                  var10003 = it$ivxxx;
               } else {
                  Function0 var113 = var82;
                  io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState var107 = var124;
                  Modifier var110 = var121;
                  int var115 = 0;
                  final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_91_0 = var10;
                  Function0 var50 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3.invoke$lambda$11$lambda$8$lambda$7(recoveredExpressionCapture_91_0));
                  var121 = var110;
                  var124 = var107;
                  var127 = var113;
                  Object var117 = var50;
                  var88.updateRememberedValue(var117);
                  var10003 = var117;
               }

               Function0 var83 = (Function0)var10003;
               var32.endReplaceGroup();
               Function0 var129 = var83;
               var32.startReplaceGroup(-286353047);
               ComposerKt.sourceInformation(var32, "CC(remember):IntercomArticleActivity.kt#9igjgp");
               Composer var89 = var32;
               boolean var93 = var32.changedInstance(var10);
               int $i$f$cachexxxx = 0;
               Object it$ivxxxx = var89.rememberedValue();
               int var105/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
               Object var10004;
               if (!var93 && it$ivxxxx != Composer.Companion.getEmpty()) {
                  var10004 = it$ivxxxx;
               } else {
                  Function0 var119 = var83;
                  Function0 var114 = var127;
                  io.intercom.android.sdk.helpcenter.articles.ArticleViewState$ReactionState var108 = var124;
                  Modifier var111 = var121;
                  int var116 = 0;
                  final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_92_0 = var10;
                  Function0 var51 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3.invoke$lambda$11$lambda$10$lambda$9(recoveredExpressionCapture_92_0));
                  var121 = var111;
                  var124 = var108;
                  var127 = var114;
                  var129 = var119;
                  Object var118 = var51;
                  var89.updateRememberedValue(var118);
                  var10004 = var118;
               }

               Function0 var84 = (Function0)var10004;
               var32.endReplaceGroup();
               ReactionsComponentKt.ReactionsComponent(var121, var124, var127, var129, var84, var32, 6, 0);
               if (((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Content)var5).getReactionState().getTeamHelpVisibility() == 0) {
                  io.intercom.android.sdk.helpcenter.articles.ArticleViewState$TeamPresenceState var85 = ((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Content)var5).getTeamPresenceState();
                  TeamPresenceComponentKt.TeamPresenceComponent(var85, false, null, var32, 0, 6);
               }
            }

            var32.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var32);
            ComposerKt.sourceInformationMarkerEnd(var29);
            var23.endNode();
            ComposerKt.sourceInformationMarkerEnd(var23);
            ComposerKt.sourceInformationMarkerEnd(var16);
            ComposerKt.sourceInformationMarkerEnd(var2);
            var2.endReplaceGroup();
         } else {
            if (!(var5 instanceof io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Error)) {
               var2.startReplaceGroup(344451014);
               var2.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var2.startReplaceGroup(2095391085);
            ComposerKt.sourceInformation(var2, "222@12341L899");
            boolean var64 = ((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Error)var5).getRetryButtonVisibility() == 0;
            Modifier var65 = PaddingKt.padding(Modifier.Companion, var1);
            var2.startReplaceGroup(344697230);
            ComposerKt.sourceInformation(var2, "227@12689L228");
            ErrorState var123;
            if (!var64) {
               var123 = new io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA(0, ((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Error)var5).getMessage(), null, 5, null);
            } else {
               byte var122 = 0;
               int var125 = ((io.intercom.android.sdk.helpcenter.articles.ArticleViewState$Error)var5).getMessage();
               Object var128 = null;
               byte var130 = 0;
               var2.startReplaceGroup(344703479);
               ComposerKt.sourceInformation(var2, "CC(remember):IntercomArticleActivity.kt#9igjgp");
               Composer var68 = var2;
               boolean var69 = var2.changedInstance(this.this$0);
               IntercomArticleActivity var70 = this.this$0;
               int var71 = 0;
               Object var72 = var68.rememberedValue();
               int var73 = 0;
               Object var131;
               if (!var69 && var72 != Composer.Companion.getEmpty()) {
                  var131 = var72;
               } else {
                  byte var55 = 0;
                  Object var54 = null;
                  int var53 = var125;
                  byte var52 = 0;
                  int var74 = 0;
                  final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredExpressionCapture_87_0 = var70;
                  Function0 var56 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$onCreate$1$1$3.invoke$lambda$13$lambda$12(recoveredExpressionCapture_87_0));
                  var122 = var52;
                  var125 = var53;
                  var128 = var54;
                  var130 = var55;
                  Object var75 = var56;
                  var68.updateRememberedValue(var75);
                  var131 = var75;
               }

               Function0 var67 = (Function0)var131;
               var2.endReplaceGroup();
               Object var57 = null;
               byte var58 = 13;
               Function0 var59 = var67;
               byte var60 = var130;
               Object var61 = var128;
               int var62 = var125;
               byte var63 = var122;
               var123 = new io.intercom.android.sdk.m5.components.ErrorState$WithCTA(var63, var62, (Integer)var61, var60, var59, var58, (DefaultConstructorMarker)var57);
            }

            ErrorState var66 = var123;
            var2.endReplaceGroup();
            IntercomErrorScreenKt.IntercomErrorScreen(var66, var65, var2, 0, 0);
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final WebView invoke$lambda$11$lambda$2$lambda$1(IntercomArticleActivity var0, String var1, Map var2, Context var3) {
      Intrinsics.checkNotNullParameter(var3, "it");

      WebView var5;
      try {
         var5 = new WebView(var3);
      } catch (Exception var8) {
         var5 = new WebView(var0.getApplicationContext());
      }

      WebView var4 = var5;
      WebView var9 = var4;
      WebView var6 = var9;
      int var7 = 0;
      var6.setLayoutParams(new LayoutParams(-1, -2));
      var6.getSettings().setJavaScriptEnabled(true);
      var6.getSettings().setDomStorageEnabled(true);
      var6.getSettings().setMediaPlaybackRequiresUserGesture(true);
      var6.getSettings().setMixedContentMode(2);
      var6.getSettings().setAllowFileAccess(false);
      var6.getSettings().setAllowContentAccess(false);
      var6.getSettings().setGeolocationEnabled(false);
      var6.setWebChromeClient(new WebChromeClient());
      var6.setWebViewClient(new ArticleWebViewClient(var1, new IntercomArticleActivity$onCreate$1$1$3$1$1$1$1$1(var0, var6)));
      IntercomArticleActivity.access$setCookies(var0);
      var6.loadUrl(var1, var2);
      return var9;
   }

   private static final Unit invoke$lambda$11$lambda$4$lambda$3(WebView var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$6$lambda$5(IntercomArticleActivity var0) {
      IntercomArticleActivity.access$getViewModel(var0).sadReactionTapped();
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$8$lambda$7(IntercomArticleActivity var0) {
      IntercomArticleActivity.access$getViewModel(var0).neutralReactionTapped();
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10$lambda$9(IntercomArticleActivity var0) {
      IntercomArticleActivity.access$getViewModel(var0).happyReactionTapped();
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$13$lambda$12(IntercomArticleActivity var0) {
      IntercomArticleActivity.access$getViewModel(var0).fragmentLoaded(IntercomArticleActivity.access$getArguments(var0).getArticleId());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((PaddingValues)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
