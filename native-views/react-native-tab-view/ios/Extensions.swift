import UIKit

extension Collection {
  subscript(safe index: Index) -> Element? {
    indices.contains(index) ? self[index] : nil
  }
}

extension Collection where Element == TabInfo {
  func findByKey(_ key: String?) -> Element? {
    guard let key else { return nil }
    guard !isEmpty else { return nil }
    return first { $0.key == key }
  }
}

extension UIView {
  func pinEdges(to other: UIView) {
    NSLayoutConstraint.activate([
      leadingAnchor.constraint(equalTo: other.leadingAnchor),
      trailingAnchor.constraint(equalTo: other.trailingAnchor),
      topAnchor.constraint(equalTo: other.topAnchor),
      bottomAnchor.constraint(equalTo: other.bottomAnchor)
    ])
  }
}

extension UIImage {
  func resizeImageTo(size: CGSize) -> UIImage? {
    let renderer = UIGraphicsImageRenderer(size: size)
    return renderer.image { _ in
      self.draw(in: CGRect(origin: .zero, size: size))
    }
  }
}
