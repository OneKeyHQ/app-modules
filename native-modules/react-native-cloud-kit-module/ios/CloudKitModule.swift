import Foundation
import CloudKit
import NitroModules

class CloudKitModule: HybridCloudKitModuleSpec {

  // MARK: - Properties
  private let container = CKContainer.default()
  private lazy var database = container.privateCloudDatabase

  // MARK: - Check Availability

  public func isAvailable() throws -> Promise<Bool> {
    return Promise.async {
      let status = try await self.container.accountStatus()
      return status == .available
    }
  }

  // MARK: - Get Account Info

  public func getAccountInfo() throws -> Promise<AccountInfoResult> {
    return Promise.async {
      let status = try await self.container.accountStatus()
      var userId: String?
      if status == .available {
        do {
          let userRecordID = try await self.container.userRecordID()
          userId = userRecordID.recordName
        } catch {
          userId = nil
        }
      }
      let name: String
      switch status {
      case .available:
        name = "available"
      case .noAccount:
        name = "noAccount"
      case .restricted:
        name = "restricted"
      default:
        name = "couldNotDetermine"
      }
      let result = AccountInfoResult(status: Double(status.rawValue), statusName: name, containerUserId: userId)
      return result
    }
  }

  // MARK: - Save Record

  public func saveRecord(params: SaveRecordParams) throws -> Promise<SaveRecordResult> {
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: params.recordID)
      let recordToSave: CKRecord
      do {
        // Update existing record if found
        let record = try await self.database.record(for: ckRecordID)
        recordToSave = record
      } catch let error as CKError where error.code == .unknownItem {
        // Create new record when not found
        let record = CKRecord(recordType: params.recordType, recordID: ckRecordID)
        recordToSave = record
      }
      recordToSave[CloudKitConstants.recordDataField] = params.data as CKRecordValue
      recordToSave[CloudKitConstants.recordMetaField] = params.meta as CKRecordValue
      let savedRecord = try await self.database.save(recordToSave)
      let createdAt = Int64((savedRecord.creationDate?.timeIntervalSince1970 ?? 0) * 1000)
      let result = SaveRecordResult(
        recordID: savedRecord.recordID.recordName,
        createdAt: Double(createdAt)
      )
      return result
    }
  }

  // MARK: - Fetch Record

  public func fetchRecord(params: FetchRecordParams) throws -> Promise<RecordResult?> {
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: params.recordID)

      do {
        let record = try await self.database.record(for: ckRecordID)

        let data = record[CloudKitConstants.recordDataField] as? String ?? ""
        let meta = record[CloudKitConstants.recordMetaField] as? String ?? ""
        let createdAt = Int64((record.creationDate?.timeIntervalSince1970 ?? 0) * 1000)
        let modifiedAt = Int64((record.modificationDate?.timeIntervalSince1970 ?? 0) * 1000)

        return RecordResult(
          recordID: record.recordID.recordName,
          recordType: record.recordType,
          data: data,
          meta: meta,
          createdAt: Double(createdAt),
          modifiedAt: Double(modifiedAt)
        )
      } catch let error as CKError where error.code == .unknownItem {
        return nil
      }
    }
  }

  // MARK: - Delete Record

  public func deleteRecord(params: DeleteRecordParams) throws -> Promise<Void> {
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: params.recordID)

      do {
        _ = try await self.database.deleteRecord(withID: ckRecordID)
        return Void()
      } catch let error as CKError where error.code == .unknownItem {
        // Item not found is considered success for delete
        return Void()
      }
    }
  }

  // MARK: - Record Exists

  public func recordExists(params: RecordExistsParams) throws -> Promise<Bool> {
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: params.recordID)

      do {
        _ = try await self.database.record(for: ckRecordID)
        return true
      } catch let error as CKError where error.code == .unknownItem {
        return false
      }
    }
  }

  // MARK: - Query Records

  public func queryRecords(params: QueryRecordsParams) throws -> Promise<QueryRecordsResult> {
    return Promise.async {
      let predicate = NSPredicate(value: true)
      let query = CKQuery(recordType: params.recordType, predicate: predicate)

      // Use CKQueryOperation with desiredKeys to fetch only meta (exclude large data field)
      return try await withCheckedThrowingContinuation { continuation in
        var results: [RecordResult] = []
        let operation = CKQueryOperation(query: query)
        operation.desiredKeys = [CloudKitConstants.recordMetaField]
        // operation.resultsLimit = 500 // Optional: tune as needed

        operation.recordMatchedBlock = { _, result in
          switch result {
          case .success(let record):
            let meta = record[CloudKitConstants.recordMetaField] as? String ?? ""
            let createdAt = Int64((record.creationDate?.timeIntervalSince1970 ?? 0) * 1000)
            let modifiedAt = Int64((record.modificationDate?.timeIntervalSince1970 ?? 0) * 1000)
            let rr = RecordResult(
              recordID: record.recordID.recordName,
              recordType: record.recordType,
              data: "",
              meta: meta,
              createdAt: Double(createdAt),
              modifiedAt: Double(modifiedAt)
            )
            results.append(rr)
          case .failure:
            break
          }
        }

        operation.queryResultBlock = { result in
          switch result {
          case .success:
            // Sort by modification time descending to return latest first
            let sorted = results.sorted { $0.modifiedAt > $1.modifiedAt }
            let queryResult = QueryRecordsResult(records: sorted)
            continuation.resume(returning: queryResult)
          case .failure(let error):
            continuation.resume(throwing: error)
          }
        }

        self.database.add(operation)
      }
    }
  }
}
