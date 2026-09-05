import Foundation
import UIKit

final class HybridNativeList: HybridNativeListSpec {
  private let hostView = NativeListView(frame: .zero)

  var view: UIView { hostView }

  var snapshotJson: String = "" {
    didSet {
      guard snapshotJson != oldValue else { return }
      runOnMain { [weak self] in
        guard let self else { return }
        self.hostView.applySnapshotJson(self.snapshotJson)
      }
    }
  }

  var onRowAction: ((_ payloadJson: String) -> Void)?
  var onSelectionDelta: ((_ payloadJson: String) -> Void)?
  var onReorder: ((_ payloadJson: String) -> Void)?
  var onEndReached: ((_ payloadJson: String) -> Void)?
  var onVisibleRangeChanged: ((_ payloadJson: String) -> Void)?

  override init() {
    super.init()
    hostView.onRowAction = { [weak self] in self?.onRowAction?($0) }
    hostView.onSelectionDelta = { [weak self] in self?.onSelectionDelta?($0) }
    hostView.onReorder = { [weak self] in self?.onReorder?($0) }
    hostView.onEndReached = { [weak self] in self?.onEndReached?($0) }
    hostView.onVisibleRangeChanged = { [weak self] in self?.onVisibleRangeChanged?($0) }
  }

  func applySnapshot(snapshotJson: String) throws {
    runOnMain { [weak self] in self?.hostView.applySnapshotJson(snapshotJson) }
  }

  func applyPatches(patchesJson: String) throws {
    runOnMain { [weak self] in self?.hostView.applyPatchesJson(patchesJson) }
  }

  func reconcileSelection(selectedKeysJson: String) throws {
    runOnMain { [weak self] in self?.hostView.reconcileSelectionJson(selectedKeysJson) }
  }

  func scrollToKey(
    key: String,
    animated: Bool,
    alignment: NativeListScrollAlignment,
    viewPosition: Double,
    viewOffset: Double
  ) throws {
    runOnMain { [weak self] in
      self?.hostView.scrollToKey(
        key,
        animated: animated,
        alignment: alignment.stringValue,
        viewPosition: viewPosition,
        viewOffset: viewOffset
      )
    }
  }

  func scrollToIndex(
    index: Double,
    animated: Bool,
    alignment: NativeListScrollAlignment,
    viewPosition: Double,
    viewOffset: Double
  ) throws {
    guard index.isFinite,
          index >= 0,
          index.rounded(.towardZero) == index,
          index <= Double(Int.max) else { return }
    runOnMain { [weak self] in
      self?.hostView.scrollToIndex(
        Int(index),
        animated: animated,
        alignment: alignment.stringValue,
        viewPosition: viewPosition,
        viewOffset: viewOffset
      )
    }
  }

  func scrollToOffset(offset: Double, animated: Bool) throws {
    guard offset.isFinite, offset >= 0 else { return }
    runOnMain { [weak self] in
      self?.hostView.scrollToOffset(offset, animated: animated)
    }
  }

  func scrollToEnd(animated: Bool) throws {
    runOnMain { [weak self] in self?.hostView.scrollToEnd(animated: animated) }
  }

  func setRefreshing(refreshing: Bool) throws {
    runOnMain { [weak self] in self?.hostView.setRefreshing(refreshing) }
  }

  private func runOnMain(_ work: @escaping () -> Void) {
    if Thread.isMainThread {
      work()
    } else {
      DispatchQueue.main.async(execute: work)
    }
  }
}
