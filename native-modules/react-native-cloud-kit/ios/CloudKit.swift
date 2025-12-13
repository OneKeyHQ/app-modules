import Foundation
import NitroModules

// MARK: - Constants

struct CloudKitConstants {
  static let recordDataField = "data"
  static let recordMetaField = "meta"
}

// MARK: - Parameter Models

struct SaveRecordParams: Codable {
  let recordType: String
  let recordID: String
  let data: String
  let meta: String
}

struct FetchRecordParams: Codable {
  let recordType: String
  let recordID: String
}

struct DeleteRecordParams: Codable {
  let recordType: String
  let recordID: String
}

struct RecordExistsParams: Codable {
  let recordType: String
  let recordID: String
}

struct QueryRecordsParams: Codable {
  let recordType: String
}

// MARK: - Result Models

struct SaveRecordResult: Codable {
  let recordID: String
  let createdAt: Int64
}

struct RecordResult: Codable {
  let recordID: String
  let recordType: String
  let data: String
  let meta: String
  let createdAt: Int64
  let modifiedAt: Int64
}

struct QueryRecordsResult: Codable {
  let records: [RecordResult]
}

struct AccountInfoResult: Codable {
  let status: Int
  let statusName: String
  let containerUserId: String?
}

// MARK: - Error Types

enum CloudKitModuleError: Error {
  case invalidParameters(String)
  case operationFailed(String)
  case recordNotFound
  case noRecordReturned
}

class CloudKit: HybridCloudKitSpec {
    
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
  
  public func getAccountInfo() throws -> Promise<[String: Any]> {
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
      let result = AccountInfoResult(status: status.rawValue, statusName: name, containerUserId: userId)
      return [
        "status": result.status,
        "statusName": result.statusName,
        "containerUserId": result.containerUserId ?? NSNull()
      ]
    }
  }

  // MARK: - Save Record
  
  public func saveRecord(params: [String: Any]) throws -> Promise<[String: Any]> {
    guard let recordType = params["recordType"] as? String,
          let recordID = params["recordID"] as? String,
          let data = params["data"] as? String,
          let meta = params["meta"] as? String else {
      throw CloudKitModuleError.invalidParameters("recordType, recordID, data and meta are required")
    }
    
    let saveParams = SaveRecordParams(recordType: recordType, recordID: recordID, data: data, meta: meta)
    
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: saveParams.recordID)
      let recordToSave: CKRecord
      do {
        // Update existing record if found
        let record = try await self.database.record(for: ckRecordID)
        recordToSave = record
      } catch let error as CKError where error.code == .unknownItem {
        // Create new record when not found
        let record = CKRecord(recordType: saveParams.recordType, recordID: ckRecordID)
        recordToSave = record
      }
      recordToSave[CloudKitConstants.recordDataField] = saveParams.data as CKRecordValue
      recordToSave[CloudKitConstants.recordMetaField] = saveParams.meta as CKRecordValue
      let savedRecord = try await self.database.save(recordToSave)
      let createdAt = Int64((savedRecord.creationDate?.timeIntervalSince1970 ?? 0) * 1000)
      let result = SaveRecordResult(
        recordID: savedRecord.recordID.recordName,
        createdAt: createdAt
      )
      return [
        "recordID": result.recordID,
        "createdAt": result.createdAt
      ]
    }
  }

  // MARK: - Fetch Record
  
  public func fetchRecord(params: [String: Any]) throws -> Promise<[String: Any]?> {
    guard let recordType = params["recordType"] as? String,
          let recordID = params["recordID"] as? String else {
      throw CloudKitModuleError.invalidParameters("recordType and recordID are required")
    }
    
    let fetchParams = FetchRecordParams(recordType: recordType, recordID: recordID)
    
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: fetchParams.recordID)
      
      do {
        let record = try await self.database.record(for: ckRecordID)
        
        let data = record[CloudKitConstants.recordDataField] as? String ?? ""
        let meta = record[CloudKitConstants.recordMetaField] as? String ?? ""
        let createdAt = Int64((record.creationDate?.timeIntervalSince1970 ?? 0) * 1000)
        let modifiedAt = Int64((record.modificationDate?.timeIntervalSince1970 ?? 0) * 1000)
        
        let result = RecordResult(
          recordID: record.recordID.recordName,
          recordType: record.recordType,
          data: data,
          meta: meta,
          createdAt: createdAt,
          modifiedAt: modifiedAt
        )
        return [
          "recordID": result.recordID,
          "recordType": result.recordType,
          "data": result.data,
          "meta": result.meta,
          "createdAt": result.createdAt,
          "modifiedAt": result.modifiedAt
        ]
      } catch let error as CKError where error.code == .unknownItem {
        return nil
      }
    }
  }

  // MARK: - Delete Record
  
  public func deleteRecord(params: [String: Any]) throws -> Promise<Bool> {
    guard let recordType = params["recordType"] as? String,
          let recordID = params["recordID"] as? String else {
      throw CloudKitModuleError.invalidParameters("recordType and recordID are required")
    }
    
    let deleteParams = DeleteRecordParams(recordType: recordType, recordID: recordID)
    
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: deleteParams.recordID)
      
      do {
        _ = try await self.database.deleteRecord(withID: ckRecordID)
        return true
      } catch let error as CKError where error.code == .unknownItem {
        // Item not found is considered success for delete
        return true
      }
    }
  }

  // MARK: - Record Exists
  
  public func recordExists(params: [String: Any]) throws -> Promise<Bool> {
    guard let recordType = params["recordType"] as? String,
          let recordID = params["recordID"] as? String else {
      throw CloudKitModuleError.invalidParameters("recordType and recordID are required")
    }
    
    let existsParams = RecordExistsParams(recordType: recordType, recordID: recordID)
    
    return Promise.async {
      let ckRecordID = CKRecord.ID(recordName: existsParams.recordID)
      
      do {
        _ = try await self.database.record(for: ckRecordID)
        return true
      } catch let error as CKError where error.code == .unknownItem {
        return false
      }
    }
  }

  // MARK: - Query Records
  
  public func queryRecords(params: [String: Any]) throws -> Promise<[String: Any]> {
    guard let recordType = params["recordType"] as? String else {
      throw CloudKitModuleError.invalidParameters("recordType is required")
    }
    
    let queryParams = QueryRecordsParams(recordType: recordType)
    
    return Promise.async {
      let predicate = NSPredicate(value: true)
      let query = CKQuery(recordType: queryParams.recordType, predicate: predicate)

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
              createdAt: createdAt,
              modifiedAt: modifiedAt
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
            let dictResults = queryResult.records.map { record in
              [
                "recordID": record.recordID,
                "recordType": record.recordType,
                "data": record.data,
                "meta": record.meta,
                "createdAt": record.createdAt,
                "modifiedAt": record.modifiedAt
              ]
            }
            continuation.resume(returning: ["records": dictResults])
          case .failure(let error):
            continuation.resume(throwing: error)
          }
        }

        self.database.add(operation)
      }
    }
  }
}
