//
// ChatLayout
// ChatLayoutPositionSnapshot.swift
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

/// Represents content offset position expressed by the specific item and it offset from the top or bottom edge.
internal struct ChatLayoutPositionSnapshot: Hashable {
    /// Represents the edge.
    internal enum Edge: Hashable {
        /// Top edge of the `UICollectionView`
        case top

        /// Bottom edge of the `UICollectionView`
        case bottom
    }

    /// Item's `IndexPath`
    internal var indexPath: IndexPath

    /// Kind of item at the `indexPath`
    internal var kind: ItemKind

    /// The edge of the offset.
    internal var edge: Edge

    /// The offset from the `edge`.
    internal var offset: CGFloat

    /// Constructor
    /// - Parameters:
    ///   - indexPath: Item's `IndexPath`
    ///   - edge: The edge of the offset.
    ///   - offset: The offset from the `edge`.
    ///   - kind: Kind of item at the `indexPath`
    internal init(indexPath: IndexPath,
                kind: ItemKind,
                edge: Edge,
                offset: CGFloat = 0) {
        self.indexPath = indexPath
        self.edge = edge
        self.offset = offset
        self.kind = kind
    }
}
