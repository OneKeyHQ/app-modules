package io.intercom.android.sdk.helpcenter.search;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.TextFieldColors;
import androidx.compose.material3.TextFieldDefaults;
import androidx.compose.material3.TextFieldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.focus.FocusRequester;
import androidx.compose.ui.focus.FocusRequesterModifierKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterSearchTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterSearchTopBar.kt\nio/intercom/android/sdk/helpcenter/search/HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,163:1\n118#2:164\n1047#3,6:165\n1047#3,6:171\n*S KotlinDebug\n*F\n+ 1 HelpCenterSearchTopBar.kt\nio/intercom/android/sdk/helpcenter/search/HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4\n*L\n93#1:164\n109#1:165,6\n87#1:171,6\n*E\n"
)
final class HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4 implements Function2 {
   // $VF: synthetic field
   final FocusRequester $focusRequester;
   // $VF: synthetic field
   final Function1 $onSearchAction;
   // $VF: synthetic field
   final FocusManager $focusManager;
   // $VF: synthetic field
   final MutableStateFlow $textFlow;
   // $VF: synthetic field
   final MutableState $searchText$delegate;

   HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4(FocusRequester var1, Function1 var2, FocusManager var3, MutableStateFlow var4, MutableState var5) {
      this.$focusRequester = var1;
      this.$onSearchAction = var2;
      this.$focusManager = var3;
      this.$textFlow = var4;
      this.$searchText$delegate = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(
         var1,
         "C101@4158L10,108@4468L213,116@4806L6,117@4882L6,118@4957L6,125@5410L6,115@4745L703,86@3522L116,127@5481L686,84@3443L2739:HelpCenterSearchTopBar.kt#fpck24"
      );
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -227105272, var2, -1, "io.intercom.android.sdk.helpcenter.search.HelpCenterSearchTopBar.<anonymous> (HelpCenterSearchTopBar.kt:84)"
            );
         }

         String var3 = HelpCenterSearchTopBarKt.access$HelpCenterSearchTopBar$lambda$2(this.$searchText$delegate);
         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var5 = 56;
         int var6 = 0;
         Modifier var4 = FocusRequesterModifierKt.focusRequester(
            SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var5)), this.$focusRequester
         );
         TextStyle var44 = IntercomTheme.INSTANCE.getTypography(var1, IntercomTheme.$stable).getType04();
         KeyboardOptions var45 = new KeyboardOptions(
            0, null, KeyboardType.Companion.recovered$29477a01a04c8fe0(), ImeAction.Companion.recovered$99e7e34e4aa177ff(), null, null, null, 115, null
         );
         String var63 = null;
         Object var10001 = null;
         Object var10002 = null;
         Object var10003 = null;
         var1.startReplaceGroup(1000454130);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var9 = var1;
         boolean var10 = var1.changed(this.$onSearchAction) | var1.changedInstance(this.$focusManager);
         Function1 var11 = this.$onSearchAction;
         FocusManager var12 = this.$focusManager;
         MutableState var13 = this.$searchText$delegate;
         int var14 = 0;
         Object var15 = var9.rememberedValue();
         int var16 = 0;
         Object var10004;
         if (!var10 && var15 != Composer.Companion.getEmpty()) {
            var10004 = var15;
         } else {
            Object var34 = null;
            Object var33 = null;
            Object var32 = null;
            Object var31 = null;
            int var17 = 0;
            final kotlin.jvm.functions.Function1 recoveredCapture_103_0 = var11;
            final androidx.compose.ui.focus.FocusManager recoveredCapture_103_1 = var12;
            final androidx.compose.runtime.MutableState recoveredCapture_103_2 = var13;
            Function1 var35 = ((kotlin.jvm.functions.Function1) (recoveredArgument_103_0) -> HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4.invoke$lambda$1$lambda$0(recoveredCapture_103_0, recoveredCapture_103_1, recoveredCapture_103_2, (androidx.compose.foundation.text.KeyboardActionScope)recoveredArgument_103_0));
            var63 = (String)var31;
            var10001 = var32;
            var10002 = var33;
            var10003 = var34;
            Object var18 = var35;
            var9.updateRememberedValue(var18);
            var10004 = var18;
         }

         Function1 var8 = (Function1)var10004;
         var1.endReplaceGroup();
         Object var36 = null;
         byte var37 = 47;
         Object var38 = null;
         Function1 var39 = var8;
         Object var40 = var10003;
         Object var41 = var10002;
         Object var42 = var10001;
         Object var43 = var63;
         KeyboardActions var7 = new KeyboardActions(
            (Function1)var43, (Function1)var42, (Function1)var41, (Function1)var40, var39, (Function1)var38, var37, (DefaultConstructorMarker)var36
         );
         TextFieldDefaults var47 = TextFieldDefaults.INSTANCE;
         long var49 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         long var52 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         long var55 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         long var58 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var61 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var20 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var22 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var24 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var26 = Color.Companion.recovered$562f21ab17d9b4c7();
         long var28 = IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         TextFieldColors var46 = var47.recovered$8136159443990665(
            var49,
            var52,
            var55,
            0L,
            var58,
            var61,
            var20,
            0L,
            var28,
            0L,
            null,
            var22,
            var24,
            var26,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            0L,
            var1,
            1794048,
            3504,
            0,
            0,
            3072,
            2147468936,
            4095
         );
         var63 = var3;
         var1.startReplaceGroup(1000423761);
         ComposerKt.sourceInformation(var1, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var50 = var1;
         boolean var51 = var1.changedInstance(this.$textFlow);
         MutableStateFlow var53 = this.$textFlow;
         var13 = this.$searchText$delegate;
         var14 = 0;
         var15 = var50.rememberedValue();
         var16 = 0;
         if (!var51 && var15 != Composer.Companion.getEmpty()) {
            var10001 = var15;
         } else {
            String var30 = var3;
            int var60 = 0;
            final kotlinx.coroutines.flow.MutableStateFlow recoveredCapture_104_0 = var53;
            final androidx.compose.runtime.MutableState recoveredCapture_104_1 = var13;
            var10001 = ((kotlin.jvm.functions.Function1) (recoveredArgument_104_0) -> HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4.invoke$lambda$3$lambda$2(recoveredCapture_104_0, recoveredCapture_104_1, (java.lang.String)recoveredArgument_104_0));
            var63 = var30;
            Object var62 = var10001;
            var50.updateRememberedValue(var62);
            var10001 = var62;
         }

         Function1 var48 = (Function1)var10001;
         var1.endReplaceGroup();
         TextFieldKt.TextField(
            var63,
            var48,
            var4,
            false,
            false,
            var44,
            null,
            ComposableSingletons$HelpCenterSearchTopBarKt.INSTANCE.recovered$eac478aa65045e5d(),
            null,
            ComposableLambdaKt.rememberComposableLambda(
               1908343233, true, new HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4$3(this.$textFlow, this.$searchText$delegate), var1, 54
            ),
            null,
            null,
            null,
            false,
            null,
            var45,
            var7,
            true,
            0,
            0,
            null,
            null,
            var46,
            var1,
            817889280,
            12779520,
            0,
            3964248
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function1 var0, FocusManager var1, MutableState var2, KeyboardActionScope var3) {
      Intrinsics.checkNotNullParameter(var3, "$this$KeyboardActions");
      if (HelpCenterSearchTopBarKt.access$HelpCenterSearchTopBar$lambda$2(var2).length() > 0) {
         var0.invoke(HelpCenterSearchTopBarKt.access$HelpCenterSearchTopBar$lambda$2(var2));
         FocusManager.clearFocus$default(var1, false, 1, null);
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(MutableStateFlow var0, MutableState var1, String var2) {
      Intrinsics.checkNotNullParameter(var2, "newText");
      HelpCenterSearchTopBarKt.access$HelpCenterSearchTopBar$lambda$3(var1, var2);
      var0.setValue(var2);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
