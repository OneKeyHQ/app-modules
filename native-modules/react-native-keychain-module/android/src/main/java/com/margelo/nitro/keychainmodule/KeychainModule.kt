package com.margelo.nitro.keychainmodule

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.NullType
import com.margelo.nitro.core.Promise

@DoNotStrip
class KeychainModule : HybridKeychainModuleSpec() {
  override fun setItem(params: SetItemParams): Promise<Boolean> {
    return Promise.resolved(false)
  }

  override fun getItem(params: GetItemParams): Promise<Variant_NullType_GetItemResult> {
    return Promise.resolved(Variant_NullType_GetItemResult.First(NullType.NULL))
  }

  override fun removeItem(params: RemoveItemParams): Promise<Boolean> {
    return Promise.resolved(false)
  }

  override fun hasItem(params: HasItemParams): Promise<Boolean> {
    return Promise.resolved(false)
  }

  override fun isICloudSyncEnabled(): Promise<Boolean> {
    return Promise.resolved(false)
  }
}
