package io.intercom.android.sdk.tickets;

import android.content.Context;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.models.FileType;
import io.intercom.android.sdk.models.Ticket$TicketAttribute$FilesAttribute$File;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.BoxedTextLayoutKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u001a%\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0001¢\u0006\u0002\u0010\u0007\u001a\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0000\u001a\u0081\u0001\u0010\f\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\t2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00102 \b\u0002\u0010\u0012\u001a\u001a\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0013¢\u0006\u0002\b\u0015¢\u0006\u0002\b\u00162 \b\u0002\u0010\u0017\u001a\u001a\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0013¢\u0006\u0002\b\u0015¢\u0006\u0002\b\u0016H\u0001¢\u0006\u0004\b\u0018\u0010\u0019\u001a'\u0010\u001a\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\tH\u0001¢\u0006\u0002\u0010\u001b\u001a\r\u0010\u001c\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u001d¨\u0006\u001e",
   d2 = {
         "FileAttachmentList",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "files",
         "",
         "Lio/intercom/android/sdk/models/Ticket$TicketAttribute$FilesAttribute$File;",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Landroidx/compose/runtime/Composer;II)V",
         "getFileType",
         "Lio/intercom/android/sdk/models/FileType;",
         "mimeType",
         "",
         "FileAttachment",
         "fileName",
         "fileType",
         "borderColor",
         "Landroidx/compose/ui/graphics/Color;",
         "textColor",
         "trialingIcon",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/RowScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "leadingIcon",
         "FileAttachment-vRFhKjU",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Lio/intercom/android/sdk/models/FileType;JJLkotlin/jvm/functions/Function3;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "FailedFileAttached",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Lio/intercom/android/sdk/models/FileType;Landroidx/compose/runtime/Composer;II)V",
         "FileAttachmentListPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nFIleAttachmentList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FIleAttachmentList.kt\nio/intercom/android/sdk/tickets/FIleAttachmentListKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 8 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,146:1\n75#2:147\n118#3:148\n87#4:149\n85#4,8:150\n94#4:191\n81#5,6:158\n88#5,6:173\n96#5:190\n402#6,9:164\n411#6:179\n412#6,2:188\n1869#7:180\n1870#7:187\n1047#8,6:181\n*S KotlinDebug\n*F\n+ 1 FIleAttachmentList.kt\nio/intercom/android/sdk/tickets/FIleAttachmentListKt\n*L\n30#1:147\n33#1:148\n31#1:149\n31#1:150,8\n31#1:191\n31#1:158,6\n31#1:173,6\n31#1:190\n31#1:164,9\n31#1:179\n31#1:188,2\n35#1:180\n35#1:187\n39#1:181,6\n*E\n"
)
public final class FIleAttachmentListKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FileAttachmentList(@Nullable Modifier var0, @NotNull List var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "files");
      var2 = var2.startRestartGroup(580044030);
      ComposerKt.sourceInformation(var2, "C(FileAttachmentList)P(1)29@1177L7,30@1189L514:FIleAttachmentList.kt#3ofz5f");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(580044030, var5, -1, "io.intercom.android.sdk.tickets.FileAttachmentList (FIleAttachmentList.kt:28)");
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Context var6 = (Context)var10;
         int var56 = 6;
         var8 = 0;
         Vertical var57 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var56));
         var9 = 48 | 14 & var5;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var59 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var57, var59, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var0;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -1748932289, "C:FIleAttachmentList.kt#3ofz5f");
         var30.startReplaceGroup(-1441890306);
         ComposerKt.sourceInformation(var30, "*38@1478L194,35@1336L351");
         Iterable var33 = var1;
         int var34 = 0;

         for (Object var36 : var33) {
            Ticket$TicketAttribute$FilesAttribute$File var37 = (Ticket$TicketAttribute$FilesAttribute$File)var36;
            int var38 = 0;
            String var39 = var37.getName();
            FileType var40 = var37.getFileType();
            Modifier var10000 = Modifier.Companion;
            boolean var10001 = false;
            Object var10002 = null;
            Object var10003 = null;
            Object var10004 = null;
            var30.startReplaceGroup(-1188185032);
            ComposerKt.sourceInformation(var30, "CC(remember):FIleAttachmentList.kt#9igjgp");
            Composer var41 = var30;
            boolean var42 = var30.changed(var37) | var30.changedInstance(var6);
            int var43 = 0;
            Object var44 = var41.rememberedValue();
            int var45 = 0;
            Object var10005;
            if (!var42 && var44 != Composer.Companion.getEmpty()) {
               var10005 = var44;
            } else {
               Object var46 = null;
               Object var47 = null;
               Object var48 = null;
               boolean var49 = false;
               Modifier var50 = var10000;
               int var51 = 0;
               final io.intercom.android.sdk.models.Ticket$TicketAttribute$FilesAttribute$File recoveredCapture_1522_0 = var37;
               final android.content.Context recoveredCapture_1522_1 = var6;
               Function0 var52 = ((kotlin.jvm.functions.Function0) () -> FIleAttachmentListKt.FileAttachmentList$lambda$3$lambda$2$lambda$1$lambda$0(recoveredCapture_1522_0, recoveredCapture_1522_1));
               var10000 = var50;
               var10001 = var49;
               var10002 = var48;
               var10003 = var47;
               var10004 = var46;
               Object var53 = var52;
               var41.updateRememberedValue(var53);
               var10005 = var53;
            }

            Function0 var54 = (Function0)var10005;
            var30.endReplaceGroup();
            recovered$b6c145e31a447e64(
               ClickableKt.recovered$1c9660c41fd850dc(var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var54, 15, null),
               var39,
               var40,
               0L,
               0L,
               null,
               null,
               var30,
               0,
               120
            );
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var62 = var2.endRestartGroup();
      if (var62 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1523_0 = var0;
         final java.util.List recoveredCapture_1523_1 = var1;
         final int recoveredCapture_1523_2 = var3;
         final int recoveredCapture_1523_3 = var4;
         var62.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1523_0, recoveredArgument_1523_1) -> FIleAttachmentListKt.FileAttachmentList$lambda$4(recoveredCapture_1523_0, recoveredCapture_1523_1, recoveredCapture_1523_2, recoveredCapture_1523_3, (androidx.compose.runtime.Composer)recoveredArgument_1523_0, ((java.lang.Integer)recoveredArgument_1523_1).intValue())));
      }
   }

   @NotNull
   public static final FileType getFileType(@NotNull String var0) {
      Intrinsics.checkNotNullParameter(var0, "mimeType");
      return StringsKt.contains$default(var0, "image", false, 2, null)
         ? FileType.IMAGE
         : (StringsKt.contains$default(var0, "video", false, 2, null) ? FileType.VIDEO : FileType.ATTACHMENT);
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable][androidx.compose.ui.UiComposable]]")
   public static final void recovered$b6c145e31a447e64(
      @Nullable Modifier var0,
      @NotNull String var1,
      @NotNull FileType var2,
      long var3,
      long var5,
      @Nullable Function3 var7,
      @Nullable Function3 var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "fileName");
      Intrinsics.checkNotNullParameter(var2, "fileType");
      var9 = var9.startRestartGroup(1921960973);
      ComposerKt.sourceInformation(
         var9,
         "C(FileAttachment)P(4,1,2,0:c#ui.graphics.Color,5:c#ui.graphics.Color,6)61@2063L6,62@2140L6,71@2450L612,66@2290L814:FIleAttachmentList.kt#3ofz5f"
      );
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var11 & 2) != 0) {
         var12 |= 48;
      } else if ((var10 & 48) == 0) {
         var12 |= var9.changed(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var9.changed(var2) ? 256 : 128;
      }

      if ((var10 & 3072) == 0) {
         var12 |= (var11 & 8) == 0 && var9.changed(var3) ? 2048 : 1024;
      }

      if ((var10 & 24576) == 0) {
         var12 |= (var11 & 16) == 0 && var9.changed(var5) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var9.changedInstance(var7) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var9.changedInstance(var8) ? 1048576 : 524288;
      }

      if ((var12 & 599187) == 599186 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         var9.startDefaults();
         if ((var10 & 1) != 0 && !var9.getDefaultsInvalid()) {
            var9.skipToGroupEnd();
            if ((var11 & 8) != 0) {
               var12 &= -7169;
            }

            if ((var11 & 16) != 0) {
               var12 &= -57345;
            }
         } else {
            if ((var11 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var11 & 8) != 0) {
               var3 = Color.recovered$41aa0ee03c2282fb(
                  IntercomTheme.INSTANCE.getColors(var9, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(), 0.1F, 0.0F, 0.0F, 0.0F, 14, null
               );
               var12 &= -7169;
            }

            if ((var11 & 16) != 0) {
               var5 = IntercomTheme.INSTANCE.getColors(var9, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               var12 &= -57345;
            }

            if ((var11 & 32) != 0) {
               var7 = null;
            }

            if ((var11 & 64) != 0) {
               var8 = null;
            }
         }

         var9.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1921960973, var12, -1, "io.intercom.android.sdk.tickets.FileAttachment (FIleAttachmentList.kt:65)");
         }

         BoxedTextLayoutKt.recovered$73ea9cf1e7267681(
            var0,
            var1,
            var3,
            var5,
            ComposableLambdaKt.rememberComposableLambda(-344274505, true, new FIleAttachmentListKt$FileAttachment$1(var7, var2), var9, 54),
            var8,
            var9,
            24576 | 14 & var12 | 112 & var12 | 896 & var12 >> 3 | 7168 & var12 >> 3 | 458752 & var12 >> 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var9.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1525_0 = var0;
         final java.lang.String recoveredCapture_1525_1 = var1;
         final io.intercom.android.sdk.models.FileType recoveredCapture_1525_2 = var2;
         final long recoveredCapture_1525_3 = var3;
         final long recoveredCapture_1525_4 = var5;
         final kotlin.jvm.functions.Function3 recoveredCapture_1525_5 = var7;
         final kotlin.jvm.functions.Function3 recoveredCapture_1525_6 = var8;
         final int recoveredCapture_1525_7 = var10;
         final int recoveredCapture_1525_8 = var11;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1525_0, recoveredArgument_1525_1) -> FIleAttachmentListKt.FileAttachment_vRFhKjU$lambda$5(recoveredCapture_1525_0, recoveredCapture_1525_1, recoveredCapture_1525_2, recoveredCapture_1525_3, recoveredCapture_1525_4, recoveredCapture_1525_5, recoveredCapture_1525_6, recoveredCapture_1525_7, recoveredCapture_1525_8, (androidx.compose.runtime.Composer)recoveredArgument_1525_0, ((java.lang.Integer)recoveredArgument_1525_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FailedFileAttached(
      @Nullable Modifier var0, @NotNull String var1, @NotNull FileType var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "fileName");
      Intrinsics.checkNotNullParameter(var2, "fileType");
      var3 = var3.startRestartGroup(912363521);
      ComposerKt.sourceInformation(var3, "C(FailedFileAttached)P(2)103@3379L6,104@3429L6,99@3242L531:FIleAttachmentList.kt#3ofz5f");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(912363521, var6, -1, "io.intercom.android.sdk.tickets.FailedFileAttached (FIleAttachmentList.kt:98)");
         }

         long var7 = IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).recovered$2bcfe565407cdfb8();
         long var9 = IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).recovered$2bcfe565407cdfb8();
         recovered$b6c145e31a447e64(
            var0,
            var1,
            var2,
            var9,
            var7,
            ComposableSingletons$FIleAttachmentListKt.INSTANCE.recovered$b26556cd5c386b55(),
            null,
            var3,
            196608 | 14 & var6 | 112 & var6 | 896 & var6,
            64
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1521_0 = var0;
         final java.lang.String recoveredCapture_1521_1 = var1;
         final io.intercom.android.sdk.models.FileType recoveredCapture_1521_2 = var2;
         final int recoveredCapture_1521_3 = var4;
         final int recoveredCapture_1521_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1521_0, recoveredArgument_1521_1) -> FIleAttachmentListKt.FailedFileAttached$lambda$6(recoveredCapture_1521_0, recoveredCapture_1521_1, recoveredCapture_1521_2, recoveredCapture_1521_3, recoveredCapture_1521_4, (androidx.compose.runtime.Composer)recoveredArgument_1521_0, ((java.lang.Integer)recoveredArgument_1521_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void FileAttachmentListPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-414644973);
      ComposerKt.sourceInformation(var0, "C(FileAttachmentListPreview)120@3853L733:FIleAttachmentList.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-414644973, var1, -1, "io.intercom.android.sdk.tickets.FileAttachmentListPreview (FIleAttachmentList.kt:119)");
         }

         SurfaceKt.recovered$ecd79a99e04a9534(
            null, null, 0L, 0L, 0.0F, 0.0F, null, ComposableSingletons$FIleAttachmentListKt.INSTANCE.recovered$1400af5e34386102(), var0, 12582912, 127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1524_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1524_0, recoveredArgument_1524_1) -> FIleAttachmentListKt.FileAttachmentListPreview$lambda$7(recoveredCapture_1524_0, (androidx.compose.runtime.Composer)recoveredArgument_1524_0, ((java.lang.Integer)recoveredArgument_1524_1).intValue())));
      }
   }

   private static final Unit FileAttachmentList$lambda$3$lambda$2$lambda$1$lambda$0(Ticket$TicketAttribute$FilesAttribute$File var0, Context var1) {
      LinkOpener.handleUrl(var0.getUrl(), var1, Injector.get().getApi());
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachmentList$lambda$4(Modifier var0, List var1, int var2, int var3, Composer var4, int var5) {
      FileAttachmentList(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachment_vRFhKjU$lambda$5(
      Modifier var0, String var1, FileType var2, long var3, long var5, Function3 var7, Function3 var8, int var9, int var10, Composer var11, int var12
   ) {
      recovered$b6c145e31a447e64(var0, var1, var2, var3, var5, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit FailedFileAttached$lambda$6(Modifier var0, String var1, FileType var2, int var3, int var4, Composer var5, int var6) {
      FailedFileAttached(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit FileAttachmentListPreview$lambda$7(int var0, Composer var1, int var2) {
      FileAttachmentListPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
