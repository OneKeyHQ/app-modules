package io.intercom.android.sdk.identity;

import android.os.SystemClock;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReferenceImpl;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class UserJwtRefresher$1 extends FunctionReferenceImpl implements Function0 {
   public static final UserJwtRefresher$1 INSTANCE = new UserJwtRefresher$1();

   UserJwtRefresher$1() {
      super(0, SystemClock.class, "elapsedRealtime", "elapsedRealtime()J", 0);
   }

   public final Long invoke() {
      return SystemClock.elapsedRealtime();
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
