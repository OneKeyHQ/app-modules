package io.intercom.android.sdk.m5.navigation;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BI\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\u0006\u0010\u0014\u001a\u00020\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÂ\u0003J\t\u0010\u0019\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\nHÆ\u0003JK\u0010\u001d\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\t\u001a\u00020\nHÇ\u0001J\b\u0010\u001e\u001a\u00020\u001fH\u0007J\u0013\u0010 \u001a\u00020\u00062\b\u0010!\u001a\u0004\u0018\u00010\"H×\u0003J\t\u0010#\u001a\u00020\u001fH×\u0001J\t\u0010$\u001a\u00020\u0003H×\u0001J\u0018\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u001fH\u0007R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u000fR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0015\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u000e¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$ConversationScreenArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "conversationId",
         "",
         "initialMessage",
         "isLaunchedProgrammatically",
         "",
         "articleId",
         "articleTitle",
         "transitionArgs",
         "Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "()Z",
         "getArticleId",
         "getArticleTitle",
         "getTransitionArgs",
         "()Lio/intercom/android/sdk/m5/navigation/transitions/TransitionArgs;",
         "getEncodedInitialMessage",
         "route",
         "getRoute",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "describeContents",
         "",
         "equals",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomRootActivityArgs$ConversationScreenArgs extends IntercomRootActivityArgs {
   @Nullable
   private final String conversationId;
   @NotNull
   private final String initialMessage;
   private final boolean isLaunchedProgrammatically;
   @Nullable
   private final String articleId;
   @Nullable
   private final String articleTitle;
   @NotNull
   private final TransitionArgs transitionArgs;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new IntercomRootActivityArgs$ConversationScreenArgs$Creator();

   public IntercomRootActivityArgs$ConversationScreenArgs(
      @Nullable String var1, @NotNull String var2, boolean var3, @Nullable String var4, @Nullable String var5, @NotNull TransitionArgs var6
   ) {
      Intrinsics.checkNotNullParameter(var2, "initialMessage");
      Intrinsics.checkNotNullParameter(var6, "transitionArgs");
      super(null);
      this.conversationId = var1;
      this.initialMessage = var2;
      this.isLaunchedProgrammatically = var3;
      this.articleId = var4;
      this.articleTitle = var5;
      this.transitionArgs = var6;
   }

   // $VF: synthetic method
   public IntercomRootActivityArgs$ConversationScreenArgs(
      String var1, String var2, boolean var3, String var4, String var5, TransitionArgs var6, int var7, DefaultConstructorMarker var8
   ) {
      this((((var7 & 1) != 0) ? (String) (null) : var1), (((var7 & 2) != 0) ? (String) ("") : var2), (((var7 & 4) != 0) ? (boolean) (false) : var3), (((var7 & 8) != 0) ? (String) (null) : var4), (((var7 & 16) != 0) ? (String) (null) : var5), (((var7 & 32) != 0) ? (TransitionArgs) (new TransitionArgs(null, null, null, null, 15, null)) : var6));
   }

   @Nullable
   public final String getConversationId() {
      return this.conversationId;
   }

   public final boolean isLaunchedProgrammatically() {
      return this.isLaunchedProgrammatically;
   }

   @Nullable
   public final String getArticleId() {
      return this.articleId;
   }

   @Nullable
   public final String getArticleTitle() {
      return this.articleTitle;
   }

   @NotNull
   public final TransitionArgs getTransitionArgs() {
      return this.transitionArgs;
   }

   @NotNull
   public final String getEncodedInitialMessage() {
      String var10000 = Uri.encode(this.initialMessage);
      Intrinsics.checkNotNullExpressionValue(var10000, "encode(...)");
      return var10000;
   }

   @NotNull
   @Override
   public String getRoute() {
      return "CONVERSATION?conversationId="
         + this.conversationId
         + "&initialMessage="
         + this.getEncodedInitialMessage()
         + "&articleId="
         + this.articleId
         + "&articleTitle="
         + this.articleTitle
         + "&isLaunchedProgrammatically="
         + this.isLaunchedProgrammatically
         + "&transitionArgs="
         + this.transitionArgs;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.conversationId);
      var1.writeString(this.initialMessage);
      var1.writeInt(this.isLaunchedProgrammatically ? 1 : 0);
      var1.writeString(this.articleId);
      var1.writeString(this.articleTitle);
      this.transitionArgs.writeToParcel(var1, var2);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @Nullable
   public final String component1() {
      return this.conversationId;
   }

   private final String component2() {
      return this.initialMessage;
   }

   public final boolean component3() {
      return this.isLaunchedProgrammatically;
   }

   @Nullable
   public final String component4() {
      return this.articleId;
   }

   @Nullable
   public final String component5() {
      return this.articleTitle;
   }

   @NotNull
   public final TransitionArgs component6() {
      return this.transitionArgs;
   }

   @NotNull
   public final IntercomRootActivityArgs$ConversationScreenArgs copy(
      @Nullable String var1, @NotNull String var2, boolean var3, @Nullable String var4, @Nullable String var5, @NotNull TransitionArgs var6
   ) {
      Intrinsics.checkNotNullParameter(var2, "initialMessage");
      Intrinsics.checkNotNullParameter(var6, "transitionArgs");
      return new IntercomRootActivityArgs$ConversationScreenArgs(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static IntercomRootActivityArgs$ConversationScreenArgs copy$default(
      IntercomRootActivityArgs$ConversationScreenArgs var0,
      String var1,
      String var2,
      boolean var3,
      String var4,
      String var5,
      TransitionArgs var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.conversationId;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.initialMessage;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.isLaunchedProgrammatically;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.articleId;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.articleTitle;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.transitionArgs;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationScreenArgs(conversationId="
         + this.conversationId
         + ", initialMessage="
         + this.initialMessage
         + ", isLaunchedProgrammatically="
         + this.isLaunchedProgrammatically
         + ", articleId="
         + this.articleId
         + ", articleTitle="
         + this.articleTitle
         + ", transitionArgs="
         + this.transitionArgs
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.conversationId == null ? 0 : this.conversationId.hashCode();
      var1 = var1 * 31 + this.initialMessage.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isLaunchedProgrammatically);
      var1 = var1 * 31 + (this.articleId == null ? 0 : this.articleId.hashCode());
      var1 = var1 * 31 + (this.articleTitle == null ? 0 : this.articleTitle.hashCode());
      return var1 * 31 + this.transitionArgs.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof IntercomRootActivityArgs$ConversationScreenArgs)) {
         return false;
      } else {
         IntercomRootActivityArgs$ConversationScreenArgs var2 = (IntercomRootActivityArgs$ConversationScreenArgs)var1;
         if (!Intrinsics.areEqual(this.conversationId, var2.conversationId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.initialMessage, var2.initialMessage)) {
            return false;
         } else if (this.isLaunchedProgrammatically != var2.isLaunchedProgrammatically) {
            return false;
         } else if (!Intrinsics.areEqual(this.articleId, var2.articleId)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.articleTitle, var2.articleTitle) ? false : Intrinsics.areEqual(this.transitionArgs, var2.transitionArgs);
         }
      }
   }

   public IntercomRootActivityArgs$ConversationScreenArgs() {
      this(null, null, false, null, null, null, 63, null);
   }
}
