package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.InputTypeState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$ConversationBottomBarKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,297:1\n1047#2,6:298\n1047#2,6:304\n1047#2,6:310\n1047#2,6:316\n1047#2,6:322\n1047#2,6:328\n1047#2,6:334\n*S KotlinDebug\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$ConversationBottomBarKt$lambda-1$1\n*L\n249#1:298,6\n250#1:304,6\n251#1:310,6\n252#1:316,6\n253#1:322,6\n254#1:328,6\n255#1:334,6\n*E\n"
)
final class ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(
         var1, "C248@12114L11,249@12159L2,250@12200L2,251@12247L2,252@12290L2,253@12342L2,254@12389L2,236@11505L901:ConversationBottomBar.kt#yb6g9"
      );
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2128348862,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$ConversationBottomBarKt.lambda-1.<anonymous> (ConversationBottomBar.kt:236)"
            );
         }

         Object var10000 = null;
         ComposerState var10003 = new ComposerState$TextInput(
            "Wubba Lubba Dub Dub!", new StringRes(string.intercom_reply_to_conversation, null, 2, null), false, null, null, null, 60, null
         );
         Avatar var10008 = Avatar.create("", "PR");
         Intrinsics.checkNotNullExpressionValue(var10008, "create(...)");
         BottomBarUiState var10001 = new BottomBarUiState(
            var10003,
            new CurrentlyTypingState(new AvatarWrapper(var10008, false, 2, null), false, null, TypingIndicatorType.ADMIN, 6, null),
            InputTypeState.Companion.getDEFAULT(),
            null,
            8,
            null
         );
         var1.startReplaceGroup(289198509);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            BottomBarUiState var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function2 var12 = ((kotlin.jvm.functions.Function2) (recoveredArgument_519_0, recoveredArgument_519_1) -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_519_0, (java.util.List)recoveredArgument_519_1));
            var10000 = var10;
            var10001 = var11;
            Object var57 = var12;
            var4.updateRememberedValue(var57);
            var10002 = (Function2)var57;
         } else {
            var10002 = (Function2)var7;
         }

         Function2 var3 = (kotlin.jvm.functions.Function2) var10002;
         var1.endReplaceGroup();
         var10002 = var3;
         var1.startReplaceGroup(289199940);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var106;
         if (var7 == Composer.Companion.getEmpty()) {
            Function2 var82 = var3;
            BottomBarUiState var76 = var10001;
            Object var70 = var10000;
            int var58 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_520_0) -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposerInputType)recoveredArgument_520_0));
            var10000 = var70;
            var10001 = var76;
            var10002 = var82;
            Object var59 = var13;
            var4.updateRememberedValue(var59);
            var106 = (Function1)var59;
         } else {
            var106 = (Function1)var7;
         }

         Function1 var21 = (kotlin.jvm.functions.Function1) var106;
         var1.endReplaceGroup();
         var106 = var21;
         var1.startReplaceGroup(289201252);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var88 = var21;
            Function2 var83 = (kotlin.jvm.functions.Function2) var10002;
            BottomBarUiState var77 = var10001;
            Object var71 = var10000;
            int var60 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$5$lambda$4());
            var10000 = var71;
            var10001 = var77;
            var10002 = var83;
            var106 = var88;
            Object var61 = var14;
            var4.updateRememberedValue(var61);
            var10004 = (Function0)var61;
         } else {
            var10004 = (Function0)var7;
         }

         Function0 var22 = (kotlin.jvm.functions.Function0) var10004;
         var1.endReplaceGroup();
         var10004 = var22;
         var1.startReplaceGroup(289202756);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10005;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var93 = var22;
            Function1 var89 = (kotlin.jvm.functions.Function1) var106;
            Function2 var84 = (kotlin.jvm.functions.Function2) var10002;
            BottomBarUiState var78 = var10001;
            Object var72 = var10000;
            int var62 = 0;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$7$lambda$6());
            var10000 = var72;
            var10001 = var78;
            var10002 = var84;
            var106 = var89;
            var10004 = var93;
            Object var63 = var15;
            var4.updateRememberedValue(var63);
            var10005 = (Function0)var63;
         } else {
            var10005 = (Function0)var7;
         }

         Function0 var23 = (kotlin.jvm.functions.Function0) var10005;
         var1.endReplaceGroup();
         var10005 = var23;
         var1.startReplaceGroup(289204132);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10006;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var97 = var23;
            Function0 var94 = (kotlin.jvm.functions.Function0) var10004;
            Function1 var90 = (kotlin.jvm.functions.Function1) var106;
            Function2 var85 = (kotlin.jvm.functions.Function2) var10002;
            BottomBarUiState var79 = var10001;
            Object var73 = var10000;
            int var64 = 0;
            Function0 var16 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$9$lambda$8());
            var10000 = var73;
            var10001 = var79;
            var10002 = var85;
            var106 = var90;
            var10004 = var94;
            var10005 = var97;
            Object var65 = var16;
            var4.updateRememberedValue(var65);
            var10006 = (Function0)var65;
         } else {
            var10006 = (Function0)var7;
         }

         Function0 var24 = (kotlin.jvm.functions.Function0) var10006;
         var1.endReplaceGroup();
         var10006 = var24;
         Object var10007 = null;
         float var111 = 0.0F;
         var1.startReplaceGroup(289205796);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10009;
         if (var7 == Composer.Companion.getEmpty()) {
            float var18 = 0.0F;
            Object var17 = null;
            Function0 var100 = var24;
            Function0 var98 = (kotlin.jvm.functions.Function0) var10005;
            Function0 var95 = (kotlin.jvm.functions.Function0) var10004;
            Function1 var91 = (kotlin.jvm.functions.Function1) var106;
            Function2 var86 = (kotlin.jvm.functions.Function2) var10002;
            BottomBarUiState var80 = var10001;
            Object var74 = var10000;
            int var66 = 0;
            Function1 var19 = ((kotlin.jvm.functions.Function1) (recoveredArgument_524_0) -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$11$lambda$10((java.lang.String)recoveredArgument_524_0));
            var10000 = var74;
            var10001 = var80;
            var10002 = var86;
            var106 = var91;
            var10004 = var95;
            var10005 = var98;
            var10006 = var100;
            var10007 = var17;
            var111 = var18;
            Object var67 = var19;
            var4.updateRememberedValue(var67);
            var10009 = (Function1)var67;
         } else {
            var10009 = (Function1)var7;
         }

         Function1 var25 = (kotlin.jvm.functions.Function1) var10009;
         var1.endReplaceGroup();
         var10009 = var25;
         var1.startReplaceGroup(289207300);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationBottomBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10010;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var104 = var25;
            float var103 = var111;
            Object var102 = var10007;
            Function0 var101 = (kotlin.jvm.functions.Function0) var10006;
            Function0 var99 = (kotlin.jvm.functions.Function0) var10005;
            Function0 var96 = (kotlin.jvm.functions.Function0) var10004;
            Function1 var92 = (kotlin.jvm.functions.Function1) var106;
            Function2 var87 = (kotlin.jvm.functions.Function2) var10002;
            BottomBarUiState var81 = var10001;
            Object var75 = var10000;
            int var68 = 0;
            Function0 var20 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationBottomBarKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$13$lambda$12());
            var10000 = var75;
            var10001 = var81;
            var10002 = var87;
            var106 = var92;
            var10004 = var96;
            var10005 = var99;
            var10006 = var101;
            var10007 = var102;
            var111 = var103;
            var10009 = var104;
            Object var69 = var20;
            var4.updateRememberedValue(var69);
            var10010 = var69;
         } else {
            var10010 = var7;
         }

         Function0 var26 = (Function0)var10010;
         var1.endReplaceGroup();
         ConversationBottomBarKt.recovered$3e50179ac1c3f351(
            (Modifier)var10000,
            var10001,
            (kotlin.jvm.functions.Function2) var10002,
            (kotlin.jvm.functions.Function1) var106,
            (kotlin.jvm.functions.Function0) var10004,
            (kotlin.jvm.functions.Function0) var10005,
            (kotlin.jvm.functions.Function0) var10006,
            (Function1)var10007,
            var111,
            (kotlin.jvm.functions.Function1) var10009,
            var26,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var1,
            807103872,
            6,
            0,
            4192641
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0, List var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(ComposerInputType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$9$lambda$8() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$13$lambda$12() {
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
