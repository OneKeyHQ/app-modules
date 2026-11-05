package io.intercom.android.sdk.m5.data;

import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.utilities.extensions.ConversationExtensionsKt;
import java.util.Comparator;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nComparisons.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Comparisons.kt\nkotlin/comparisons/ComparisonsKt__ComparisonsKt$compareByDescending$1\n+ 2 IntercomDataLayer.kt\nio/intercom/android/sdk/m5/data/IntercomDataLayer\n*L\n1#1,121:1\n101#2:122\n*E\n"
)
public final class IntercomDataLayer$addConversations$lambda$2$RecoveredInnere3b0c44298fc1c14$inlined$sortedByDescending$1 implements Comparator {
   @Override
   public final int compare(Object var1, Object var2) {
      Conversation var3 = (Conversation)var2;
      int var4 = 0;
      Comparable var10000 = ConversationExtensionsKt.lastActionCreatedAt(var3);
      var3 = (Conversation)var1;
      Comparable var5 = var10000;
      var4 = 0;
      return ComparisonsKt.compareValues(var5, ConversationExtensionsKt.lastActionCreatedAt(var3));
   }
}
