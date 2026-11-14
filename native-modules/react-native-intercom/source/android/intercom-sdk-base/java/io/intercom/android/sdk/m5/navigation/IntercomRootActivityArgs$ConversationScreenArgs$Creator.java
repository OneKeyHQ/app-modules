package io.intercom.android.sdk.m5.navigation;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomRootActivityArgs$ConversationScreenArgs$Creator implements Creator {
   public final IntercomRootActivityArgs$ConversationScreenArgs[] newArray(int var1) {
      return new IntercomRootActivityArgs$ConversationScreenArgs[var1];
   }

   public final IntercomRootActivityArgs$ConversationScreenArgs createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new IntercomRootActivityArgs$ConversationScreenArgs(
         var1.readString(),
         var1.readString(),
         var1.readInt() != 0,
         var1.readString(),
         var1.readString(),
         (TransitionArgs)TransitionArgs.CREATOR.createFromParcel(var1)
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
