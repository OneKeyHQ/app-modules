//
//  KeychainModuleCore.swift
//  OneKeyWallet
//
//  Created by OneKey on 2025-01-27.
//  Core implementation layer for Keychain operations (React Native independent)
//

import Foundation
import Security
import ReactNativeNativeLogger

// MARK: - Constants

struct KeychainConstants {
  // DO NOT Change this value, otherwise user cannot access the keychain items
  static let serviceIdentifier = Bundle.main.bundleIdentifier
}

// MARK: - Error Types

enum KeychainModuleError: Error {
  case invalidParameters(String)
  case encodingFailed
  case operationFailed(OSStatus)
  case itemNotFound
}

// MARK: - Keychain Module Core

class KeychainModuleCore {

  // MARK: - Set Item

  func setItem(params: SetItemParams) throws {
    guard let valueData = params.value.data(using: .utf8) else {
      OneKeyLog.error("Keychain", "setItem: failed to encode value")
      throw KeychainModuleError.encodingFailed
    }

    let enableSync = params.enableSync ?? true  // Default to disabled; callers must explicitly opt in to iCloud sync

    var query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
      kSecAttrAccount as String: params.key,
      kSecValueData as String: valueData,
      kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlocked,
      kSecAttrSynchronizable as String: enableSync  // Use parameter value
    ]

    // Optional: Set label for friendly display in Keychain Access app
    if let label = params.label {
      query[kSecAttrLabel as String] = label
    }

    // Optional: Set description for additional context
    if let description = params.description {
      query[kSecAttrDescription as String] = description
    }

    // Try to add new item first; if it already exists, update it
    let status = SecItemAdd(query as CFDictionary, nil)

    if status == errSecDuplicateItem {
      // Item exists - update it instead of delete+add (avoids race condition window)
      let searchQuery: [String: Any] = [
        kSecClass as String: kSecClassGenericPassword,
        kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
        kSecAttrAccount as String: params.key,
        kSecAttrSynchronizable as String: kSecAttrSynchronizableAny
      ]
      var updateAttrs: [String: Any] = [
        kSecValueData as String: valueData,
        kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlocked,
        kSecAttrSynchronizable as String: enableSync
      ]
      if let label = params.label {
        updateAttrs[kSecAttrLabel as String] = label
      }
      if let description = params.description {
        updateAttrs[kSecAttrDescription as String] = description
      }
      let updateStatus = SecItemUpdate(searchQuery as CFDictionary, updateAttrs as CFDictionary)
      guard updateStatus == errSecSuccess else {
        OneKeyLog.error("Keychain", "setItem update: failed, OSStatus: \(updateStatus)")
        throw KeychainModuleError.operationFailed(updateStatus)
      }
      OneKeyLog.info("Keychain", "setItem: updated existing")
    } else {
      guard status == errSecSuccess else {
        OneKeyLog.error("Keychain", "setItem: failed, OSStatus: \(status)")
        throw KeychainModuleError.operationFailed(status)
      }
      OneKeyLog.info("Keychain", "setItem: success")
    }
  }

  // MARK: - Get Item

  func getItem(params: GetItemParams) throws -> GetItemResult? {
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
      kSecAttrAccount as String: params.key,
      kSecReturnData as String: true,
      kSecMatchLimit as String: kSecMatchLimitOne,
      kSecAttrSynchronizable as String: kSecAttrSynchronizableAny  // Query both local and synced items
    ]

    var result: AnyObject?
    let status = SecItemCopyMatching(query as CFDictionary, &result)

    if status == errSecSuccess {
      if let valueData = result as? Data,
         let value = String(data: valueData, encoding: .utf8) {
        OneKeyLog.debug("Keychain", "getItem: found")
        return GetItemResult(key: params.key, value: value)
      }
      return nil
    } else if status == errSecItemNotFound {
      OneKeyLog.debug("Keychain", "getItem: not found")
      return nil
    } else {
      OneKeyLog.error("Keychain", "getItem: failed, OSStatus: \(status)")
      throw KeychainModuleError.operationFailed(status)
    }
  }

  // MARK: - Remove Item

  func removeItem(params: RemoveItemParams) throws {
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
      kSecAttrAccount as String: params.key,
      kSecAttrSynchronizable as String: kSecAttrSynchronizableAny  // Delete both local and synced items
    ]

    let status = SecItemDelete(query as CFDictionary)

    // Both success and item not found are acceptable for delete
    guard status == errSecSuccess || status == errSecItemNotFound else {
      OneKeyLog.error("Keychain", "removeItem: failed, OSStatus: \(status)")
      throw KeychainModuleError.operationFailed(status)
    }
    OneKeyLog.info("Keychain", "removeItem: success")
  }

  // MARK: - Check Item Existence

  func hasItem(params: HasItemParams) throws -> Bool {
    let query: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
      kSecAttrAccount as String: params.key,
      kSecReturnData as String: false,
      kSecAttrSynchronizable as String: kSecAttrSynchronizableAny  // Check both local and synced items
    ]

    let status = SecItemCopyMatching(query as CFDictionary, nil)
    OneKeyLog.debug("Keychain", "hasItem: \(status == errSecSuccess)")
    return status == errSecSuccess
  }

  // MARK: - Check iCloud Keychain Sync Status

  func isICloudSyncEnabled() throws -> Bool {
    // Create a test key with synchronizable attribute
    let testKey = "__onekey_icloud_sync_test__"
    let testValue = "test"

    guard let valueData = testValue.data(using: .utf8) else {
      throw KeychainModuleError.encodingFailed
    }

    // First, clean up any existing test key
    let deleteQuery: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
      kSecAttrAccount as String: testKey,
      kSecAttrSynchronizable as String: kSecAttrSynchronizableAny
    ]
    SecItemDelete(deleteQuery as CFDictionary)

    // Try to add a synchronizable item
    let addQuery: [String: Any] = [
      kSecClass as String: kSecClassGenericPassword,
      kSecAttrService as String: KeychainConstants.serviceIdentifier ?? "",
      kSecAttrAccount as String: testKey,
      kSecValueData as String: valueData,
      kSecAttrAccessible as String: kSecAttrAccessibleWhenUnlocked,
      kSecAttrSynchronizable as String: true  // Try to enable sync
    ]

    let addStatus = SecItemAdd(addQuery as CFDictionary, nil)

    // Clean up test item
    SecItemDelete(deleteQuery as CFDictionary)

    // Successfully added synchronizable item - iCloud Keychain is available
    // Failed to add synchronizable item - iCloud Keychain may be disabled
    // Common error codes:
    // errSecMissingEntitlement (-34018): Missing iCloud Keychain entitlement
    // errSecNotAvailable (-25291): iCloud Keychain not available/signed out
    let enabled = addStatus == errSecSuccess
    OneKeyLog.info("Keychain", "iCloud sync check result: \(enabled)")
    return enabled
  }
}
