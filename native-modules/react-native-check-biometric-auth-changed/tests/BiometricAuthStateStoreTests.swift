import Foundation
import Security


private final class StateFixture {
  var current: Data? = Data([2])
  var readStatus: OSStatus = errSecSuccess
  var result: AnyObject? = Data([1]) as NSData
  var updateStatus: OSStatus = errSecSuccess
  var addStatus: OSStatus = errSecSuccess
  var legacy: Data? = Data([1])
  var stored: Data?
  var events: [String] = []

  func check() throws -> Bool {
    try BiometricAuthStateStore.check(
      currentState: { self.events.append("current"); return self.current },
      read: { self.events.append("read"); return (self.readStatus, self.result) },
      update: { value in
        self.events.append("update")
        if self.updateStatus == errSecSuccess { self.stored = value }
        return self.updateStatus
      },
      add: { value in
        self.events.append("add")
        if self.addStatus == errSecSuccess { self.stored = value }
        return self.addStatus
      },
      legacyState: { self.events.append("legacy"); return self.legacy },
      removeLegacyState: { self.events.append("removeLegacy"); self.legacy = nil }
    )
  }
}

final class BiometricAuthStateStoreTests {
  func testUnavailableCurrentStateRejectsBeforeReading() {
    let fixture = StateFixture(); fixture.current = nil
    assertFailure(fixture, status: 1, operation: "Current biometric")
    assertEqual(fixture.events, ["current"])
  }

  func testReadErrorsDoNotUseOrDeleteLegacy() {
    for status in [errSecInteractionNotAllowed, errSecAuthFailed, errSecNotAvailable] {
      let fixture = StateFixture(); fixture.readStatus = status
      assertFailure(fixture, status: status, operation: "read")
      assertEqual(fixture.events, ["current", "read"])
    }
  }

  func testSuccessfulReadRequiresData() {
    for result: AnyObject? in [nil, "invalid" as NSString] {
      let fixture = StateFixture(); fixture.result = result
      assertFailure(fixture, status: errSecDecode, operation: "invalid data")
      assertEqual(fixture.events, ["current", "read"])
    }
  }

  func testUpdateFailureDoesNotAddOrDeleteLegacy() {
    let fixture = StateFixture(); fixture.readStatus = errSecItemNotFound
    fixture.updateStatus = errSecInteractionNotAllowed
    assertFailure(fixture, status: errSecInteractionNotAllowed, operation: "update")
    assertEqual(fixture.events, ["current", "read", "legacy", "update"])
  }

  func testAddFailurePreservesLegacy() {
    let fixture = StateFixture(); fixture.readStatus = errSecItemNotFound
    fixture.updateStatus = errSecItemNotFound; fixture.addStatus = errSecDuplicateItem
    assertFailure(fixture, status: errSecDuplicateItem, operation: "add")
    assertEqual(fixture.events, ["current", "read", "legacy", "update", "add"])
  }

  func testInitialBaselineResolvesOnlyAfterSuccessfulAdd() throws {
    let fixture = StateFixture(); fixture.readStatus = errSecItemNotFound
    fixture.updateStatus = errSecItemNotFound; fixture.legacy = nil
    try assertFalse(fixture.check())
    assertEqual(fixture.stored, fixture.current)
    assertEqual(fixture.events, ["current", "read", "legacy", "update", "add"])
  }

  func testChangedAndUnchangedChecksPersistBeforeReturning() throws {
    for previous in [Data([1]), Data([2])] {
      let fixture = StateFixture(); fixture.result = previous as NSData
      try assertEqual(fixture.check(), previous != fixture.current)
      assertEqual(fixture.stored, fixture.current)
      assertEqual(fixture.events, ["current", "read", "update"])
      assertNotNil(fixture.legacy, "Existing Keychain state must not migrate legacy data")
    }
  }

  func testMigrationDeletesLegacyOnlyAfterSuccessfulPersistence() throws {
    for previous in [Data([1]), Data([2])] {
      let fixture = StateFixture(); fixture.readStatus = errSecItemNotFound
      fixture.updateStatus = errSecItemNotFound; fixture.legacy = previous
      try assertEqual(fixture.check(), previous != fixture.current)
      assertEqual(fixture.stored, fixture.current)
      assertNil(fixture.legacy)
      assertEqual(fixture.events, ["current", "read", "legacy", "update", "add", "removeLegacy"])
    }
  }

  func testFailureReleasesLockForTheNextTransaction() throws {
    let fixture = StateFixture(); fixture.readStatus = errSecInteractionNotAllowed
    assertFailure(fixture, status: errSecInteractionNotAllowed, operation: "read")
    fixture.readStatus = errSecSuccess
    try assertTrue(fixture.check())
  }

  func testConcurrentTransactionsAreSerialized() {
    var active = 0
    var maximumActive = 0
    var writes = 0
    let errorLock = NSLock()
    var failures: [Error] = []
    DispatchQueue.concurrentPerform(iterations: 20) { _ in
      do {
        _ = try BiometricAuthStateStore.check(
          currentState: {
            active += 1; maximumActive = max(maximumActive, active)
            Thread.sleep(forTimeInterval: 0.001)
            return Data([2])
          },
          read: { (errSecSuccess, Data([2]) as NSData) },
          update: { _ in writes += 1; active -= 1; return errSecSuccess },
          add: { _ in fail("Must not add after a successful update") },
          legacyState: { fail("Must not read legacy after a successful read") },
          removeLegacyState: { fail("Must not remove legacy") }
        )
      } catch {
        errorLock.lock(); failures.append(error); errorLock.unlock()
      }
    }
    assertTrue(failures.isEmpty)
    assertEqual(maximumActive, 1)
    assertEqual(writes, 20)
    assertEqual(active, 0)
  }

  func testRepeatedChecksAdvanceTheSameBaseline() throws {
    var stored = Data([1])
    func check() throws -> Bool {
      try BiometricAuthStateStore.check(
        currentState: { Data([2]) },
        read: { (errSecSuccess, stored as NSData) },
        update: { stored = $0; return errSecSuccess },
        add: { _ in fail("Unexpected add") },
        legacyState: { nil },
        removeLegacyState: {}
      )
    }
    try assertTrue(check())
    try assertFalse(check())
  }

  private func assertFailure(_ fixture: StateFixture, status: OSStatus, operation: String,
    file: StaticString = #filePath, line: UInt = #line) {
    assertThrowsError(try fixture.check(), file: file, line: line) { error in
      let error = error as NSError
      assertEqual(error.domain, "BiometricAuthChanged", file: file, line: line)
      assertEqual(error.code, Int(status), file: file, line: line)
      assertTrue(error.localizedDescription.contains(operation), file: file, line: line)
    }
    assertEqual(fixture.legacy, Data([1]), file: file, line: line)
    assertNil(fixture.stored, file: file, line: line)
  }

}

private func fail(_ message: String, file: StaticString = #filePath, line: UInt = #line) -> Never {
  fatalError(message, file: (file), line: line)
}
private func assertTrue(_ value: @autoclosure () throws -> Bool, _ message: String = "Expected true", file: StaticString = #filePath, line: UInt = #line) rethrows {
  if try !value() { fail(message, file: file, line: line) }
}
private func assertFalse(_ value: @autoclosure () throws -> Bool, file: StaticString = #filePath, line: UInt = #line) rethrows {
  if try value() { fail("Expected false", file: file, line: line) }
}
private func assertEqual<T: Equatable>(_ actual: @autoclosure () throws -> T, _ expected: T, file: StaticString = #filePath, line: UInt = #line) rethrows {
  let value = try actual()
  if value != expected { fail("Expected \(expected), got \(value)", file: file, line: line) }
}
private func assertNil<T>(_ value: T?, file: StaticString = #filePath, line: UInt = #line) {
  if value != nil { fail("Expected nil", file: file, line: line) }
}
private func assertNotNil<T>(_ value: T?, _ message: String, file: StaticString = #filePath, line: UInt = #line) {
  if value == nil { fail(message, file: file, line: line) }
}
private func assertThrowsError<T>(_ expression: @autoclosure () throws -> T, file: StaticString = #filePath, line: UInt = #line, inspect: (Error) -> Void) {
  do { _ = try expression() } catch { inspect(error); return }
  fail("Expected an error", file: file, line: line)
}

@main enum BiometricStateTestRunner {
  static func main() throws {
    let tests = BiometricAuthStateStoreTests()
    tests.testUnavailableCurrentStateRejectsBeforeReading()
    tests.testReadErrorsDoNotUseOrDeleteLegacy()
    tests.testSuccessfulReadRequiresData()
    tests.testUpdateFailureDoesNotAddOrDeleteLegacy()
    tests.testAddFailurePreservesLegacy()
    try tests.testInitialBaselineResolvesOnlyAfterSuccessfulAdd()
    try tests.testChangedAndUnchangedChecksPersistBeforeReturning()
    try tests.testMigrationDeletesLegacyOnlyAfterSuccessfulPersistence()
    try tests.testFailureReleasesLockForTheNextTransaction()
    tests.testConcurrentTransactionsAreSerialized()
    try tests.testRepeatedChecksAdvanceTheSameBaseline()
    print("11 biometric state core tests passed (including 20 concurrent transactions)")
  }
}
