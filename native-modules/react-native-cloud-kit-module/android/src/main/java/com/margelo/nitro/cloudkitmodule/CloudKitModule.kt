package com.margelo.nitro.cloudkitmodule

import com.facebook.proguard.annotations.DoNotStrip
import com.margelo.nitro.core.Promise

@DoNotStrip
class CloudKitModule : HybridCloudKitModuleSpec() {

  override fun isAvailable(): Promise<Boolean> {
    return Promise.resolved(false)
  }

  override fun getAccountInfo(): Promise<AccountInfoResult> {
    return Promise.resolved(
      AccountInfoResult(
        status = 0.0,
        statusName = "",
        containerUserId = null
      )
    )
  }

  override fun saveRecord(params: SaveRecordParams): Promise<SaveRecordResult> {
    return Promise.resolved(
      SaveRecordResult(
        recordID = "",
        createdAt = 0.0,
      )
    )
  }

  override fun fetchRecord(params: FetchRecordParams): Promise<RecordResult?> {
    return Promise.resolved(null)
  }

  override fun deleteRecord(params: DeleteRecordParams): Promise<Boolean> {
    return Promise.resolved(false)
  }

  override fun recordExists(params: RecordExistsParams): Promise<Boolean> {
    return Promise.resolved(false)
  }

  override fun queryRecords(params: QueryRecordsParams): Promise<QueryRecordsResult> {
    return Promise.resolved(
      QueryRecordsResult(
        records = emptyArray()
      )
    )
  }
}
