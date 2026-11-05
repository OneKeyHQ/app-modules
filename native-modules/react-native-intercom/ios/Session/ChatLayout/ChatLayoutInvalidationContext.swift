//
// ChatLayout
// ChatLayoutInvalidationContext.swift
// https://github.com/ekazaev/ChatLayout
//
// Created by Eugene Kazaev in 2020-2024.
// Distributed under the MIT license.
//
// Become a sponsor:
// https://github.com/sponsors/ekazaev
//

import Foundation
import UIKit

/// Custom implementation of `UICollectionViewLayoutInvalidationContext`
@objc(_TtC18IntercomSDKPrivate29ChatLayoutInvalidationContext)
internal final class ChatLayoutInvalidationContext: UICollectionViewLayoutInvalidationContext {
    /// Indicates whether to recompute the positions and sizes of the items based on the current
    /// collection view and delegate layout metrics.
    internal var invalidateLayoutMetrics = true
}
