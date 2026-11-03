package cn.jpush.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.ActionHelper;

public class TagAliasReceiver extends BroadcastReceiver {
   private static final String TAG = "TagAliasReceiver";
   public static final String ACTION_TAG_ALIAS_TIMEOUT = "cn.jpush.android.intent.TAG_ALIAS_TIMEOUT";
   public static final String ACTION_TAG_ALIAS_CALLBACK = "cn.jpush.android.intent.TAG_ALIAS_CALLBACK";
   public static final String KEY_TAGALIASOPERATOR_SEQID = "tagalias_seqid";
   public static final String KEY_TAGALIASOPERATOR_CALLBACKCODE = "tagalias_errorcode";

   @Override
   public void onReceive(Context var1, Intent var2) {
      if (var2 == null) {
         Logger.recoverMethod_ww_e925fa09("TagAliasReceiver", "TagAliasOperator onReceive intent is null");
      } else {
         long var3 = var2.getLongExtra("tagalias_seqid", -1L);
         int var5 = var2.getIntExtra("tagalias_errorcode", 0);
         if (var3 == -1L) {
            Logger.recoverMethod_w_e925fa09("TagAliasReceiver", "TagAliasOperator onReceive rid is invalide");
         } else {
            ActionHelper.getInstance().onTagAliasResponse(var1, var3, var5, var2);
         }
      }
   }
}
