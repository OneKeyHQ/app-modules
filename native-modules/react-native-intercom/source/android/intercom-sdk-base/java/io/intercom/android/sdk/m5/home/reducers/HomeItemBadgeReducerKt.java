package io.intercom.android.sdk.m5.home.reducers;

import io.intercom.android.sdk.m5.home.data.Badge;
import io.intercom.android.sdk.m5.home.data.BadgeType;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$IconWithIndicator;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$IndicatorWithCount;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge$None;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u0000¨\u0006\u0004",
   d2 = {
         "reduceHomeItemBadge",
         "Lio/intercom/android/sdk/m5/home/states/HomeItemBadge;",
         "badge",
         "Lio/intercom/android/sdk/m5/home/data/Badge;",
         "intercom-sdk-base_release"
   }
)
public final class HomeItemBadgeReducerKt {
   @NotNull
   public static final HomeItemBadge reduceHomeItemBadge(@Nullable Badge var0) {
      return var0 == null
         ? HomeItemBadge$None.INSTANCE
         : (
            var0.getBadgeType() == BadgeType.PASSIVE
               ? HomeItemBadge$IconWithIndicator.INSTANCE
               : (
                  var0.getBadgeType() == BadgeType.UNREAD && var0.getLabel() != null
                     ? new HomeItemBadge$IndicatorWithCount(Integer.parseInt(var0.getLabel()))
                     : HomeItemBadge$None.INSTANCE
               )
         );
   }
}
