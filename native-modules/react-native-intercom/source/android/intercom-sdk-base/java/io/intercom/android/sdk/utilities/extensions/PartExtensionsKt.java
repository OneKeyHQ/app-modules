package io.intercom.android.sdk.utilities.extensions;

import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.Part;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0000\u001a\f\u0010\u0004\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0005\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0006\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0007\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\u0014\u0010\b\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\t\u001a\u00020\nH\u0002\u001a\u0018\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0002H\u0002\u001a\u001a\u0010\r\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0000\u001a\u001a\u0010\u0011\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0000\u001a\f\u0010\u0012\u001a\u00020\u0013*\u00020\u0002H\u0000\u001a\f\u0010\u0014\u001a\u00020\u0013*\u00020\u0002H\u0000\u001a\f\u0010\u0015\u001a\u00020\u0013*\u00020\u0002H\u0000\u001a\f\u0010\u0016\u001a\u00020\u0017*\u00020\u0002H\u0000\u001a\f\u0010\u0018\u001a\u00020\u0019*\u00020\u0002H\u0000¨\u0006\u001a",
   d2 = {
         "nextPartFromSameParticipant",
         "",
         "Lio/intercom/android/sdk/models/Part;",
         "nextPart",
         "isTypingPart",
         "isAttributeCollector",
         "isLinkCard",
         "isQuickReplyOnly",
         "isSingleBlockPartOfType",
         "type",
         "Lio/intercom/android/sdk/blocks/lib/BlockType;",
         "shouldConcatenate",
         "part",
         "hasPreviousConcatPartNewStyle",
         "",
         "index",
         "",
         "hasNextConcatPartNewStyle",
         "resolvedDisplayName",
         "",
         "resolvedDisplayTitle",
         "resolvedDisplaySuffix",
         "resolvedAvatarWrapper",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "resolvedAvatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nPartExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PartExtensions.kt\nio/intercom/android/sdk/utilities/extensions/PartExtensionsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,85:1\n1#2:86\n*E\n"
)
public final class PartExtensionsKt {
   public static final boolean nextPartFromSameParticipant(@NotNull Part var0, @NotNull Part var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "nextPart");
      return Intrinsics.areEqual(var0.getParticipantId(), var1.getParticipantId());
   }

   public static final boolean isTypingPart(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return var0.getMessageStyle() == MessageStyle.ADMIN_IS_TYPING_STYLE;
   }

   public static final boolean isAttributeCollector(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return var0.getMessageStyle() == MessageStyle.ATTRIBUTE_COLLECTOR && !var0.getForm().getAttributes().isEmpty();
   }

   public static final boolean isLinkCard(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return isSingleBlockPartOfType(var0, BlockType.LINK) && MessageStyle.CHAT == var0.getMessageStyle();
   }

   public static final boolean isQuickReplyOnly(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      List var10000 = var0.getReplyOptions();
      Intrinsics.checkNotNullExpressionValue(var10000, "getReplyOptions(...)");
      return !var10000.isEmpty() && var0.getBlocks().isEmpty();
   }

   private static final boolean isSingleBlockPartOfType(Part var0, BlockType var1) {
      return var0.getBlocks().size() == 1 && ((Block)var0.getBlocks().get(0)).getType() == var1;
   }

   private static final boolean shouldConcatenate(Part var0, Part var1) {
      return nextPartFromSameParticipant(var0, var1)
         && (var0.isAdmin() || Math.abs(var1.getCreatedAt() - var0.getCreatedAt()) < TimeUnit.HOURS.toSeconds(1L))
         && !isTypingPart(var0)
         && !isTypingPart(var1)
         && !isLinkCard(var0)
         && !isLinkCard(var1)
         && !var0.isEvent()
         && !var1.isEvent()
         && !isAttributeCollector(var0)
         && !isAttributeCollector(var1)
         && !isQuickReplyOnly(var0)
         && !isQuickReplyOnly(var1)
         && var0.getMessageStyle() != MessageStyle.POST
         && var1.getMessageStyle() != MessageStyle.POST
         && var0.getMessageStyle() != MessageStyle.NOTE
         && var1.getMessageStyle() != MessageStyle.NOTE;
   }

   public static final boolean hasPreviousConcatPartNewStyle(@NotNull List var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return (1 <= var1 ? var1 <= CollectionsKt.getLastIndex(var0) : false) && shouldConcatenate((Part)var0.get(var1 - 1), (Part)var0.get(var1));
   }

   public static final boolean hasNextConcatPartNewStyle(@NotNull List var0, int var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return (0 <= var1 ? var1 < CollectionsKt.getLastIndex(var0) : false) && shouldConcatenate((Part)var0.get(var1), (Part)var0.get(var1 + 1));
   }

   @NotNull
   public static final String resolvedDisplayName(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      io.intercom.android.sdk.models.Metadata var10000 = var0.getMetadata();
      if (var10000 != null) {
         io.intercom.android.sdk.models.Metadata var1 = var10000;
         io.intercom.android.sdk.models.Metadata var2 = var1;
         int var3 = 0;
         var10000 = !StringsKt.isBlank(var2.getTitle()) ? var1 : null;
         if (var10000 != null) {
            io.intercom.android.sdk.models.Metadata var10 = var10000;
            int var4 = 0;
            StringBuilder var5 = new StringBuilder();
            StringBuilder var6 = var5;
            int var7 = 0;
            var6.append(var10.getTitle());
            String var12 = var10.getSuffix();
            if (var12 != null) {
               String itx = var12;
               int var9 = 0;
               var6.append(" • " + itx);
            }

            String var13 = var5.toString();
            if (var13 != null) {
               return var13;
            }
         }
      }

      String var14 = var0.getParticipant().getForename();
      Intrinsics.checkNotNullExpressionValue(var14, "getForename(...)");
      return var14;
   }

   @NotNull
   public static final String resolvedDisplayTitle(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      io.intercom.android.sdk.models.Metadata var10000 = var0.getMetadata();
      if (var10000 != null) {
         io.intercom.android.sdk.models.Metadata var1 = var10000;
         io.intercom.android.sdk.models.Metadata var2 = var1;
         int var3 = 0;
         var10000 = !StringsKt.isBlank(var2.getTitle()) ? var1 : null;
         if (var10000 != null) {
            String var5 = var10000.getTitle();
            if (var5 != null) {
               return var5;
            }
         }
      }

      String var6 = var0.getParticipant().getForename();
      Intrinsics.checkNotNullExpressionValue(var6, "getForename(...)");
      return var6;
   }

   @NotNull
   public static final String resolvedDisplaySuffix(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      io.intercom.android.sdk.models.Metadata var10000 = var0.getMetadata();
      if (var10000 != null) {
         io.intercom.android.sdk.models.Metadata var1 = var10000;
         io.intercom.android.sdk.models.Metadata var2 = var1;
         int var3 = 0;
         var10000 = !StringsKt.isBlank(var2.getTitle()) ? var1 : null;
         if (var10000 != null) {
            String var5 = var10000.getSuffix();
            if (var5 != null) {
               return var5;
            }
         }
      }

      return "";
   }

   @NotNull
   public static final AvatarWrapper resolvedAvatarWrapper(@NotNull Part var0) {
      Avatar var3;
      label17: {
         Intrinsics.checkNotNullParameter(var0, "<this>");
         io.intercom.android.sdk.models.Metadata var10000 = var0.getMetadata();
         if (var10000 != null) {
            List var2 = var10000.getAvatars();
            if (var2 != null) {
               var3 = (Avatar)CollectionsKt.firstOrNull(var2);
               break label17;
            }
         }

         var3 = null;
      }

      Avatar var1 = var3;
      AvatarWrapper var4;
      if (var1 != null) {
         var4 = new AvatarWrapper(var1, false, 2, null);
      } else {
         Avatar var10002 = var0.getParticipant().getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
         Boolean var10003 = var0.getParticipant().isBot();
         Intrinsics.checkNotNullExpressionValue(var10003, "isBot(...)");
         var4 = new AvatarWrapper(var10002, var10003);
      }

      return var4;
   }

   @NotNull
   public static final Avatar resolvedAvatar(@NotNull Part var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      io.intercom.android.sdk.models.Metadata var10000 = var0.getMetadata();
      if (var10000 != null) {
         List var1 = var10000.getAvatars();
         if (var1 != null) {
            Avatar var2 = (Avatar)CollectionsKt.firstOrNull(var1);
            if (var2 != null) {
               return var2;
            }
         }
      }

      Avatar var3 = var0.getParticipant().getAvatar();
      Intrinsics.checkNotNullExpressionValue(var3, "getAvatar(...)");
      return var3;
   }
}
