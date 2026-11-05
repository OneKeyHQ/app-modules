import UIKit

extension UIColor {
    convenience init?(hex: String) {
        let value = hex.hasPrefix("#") ? String(hex.dropFirst()) : hex
        guard value.count == 6 || value.count == 8 else { return nil }
        var number: UInt64 = 0
        guard Scanner(string: value).scanHexInt64(&number) else { return nil }
        let eightDigits = value.count != 6
        let red = CGFloat((number >> (eightDigits ? 24 : 16)) & 0xff) / 255
        let green = CGFloat((number >> (eightDigits ? 16 : 8)) & 0xff) / 255
        let blue = CGFloat((number >> (eightDigits ? 8 : 0)) & 0xff) / 255
        let alpha = eightDigits ? CGFloat(number & 0xff) / 255 : 1
        self.init(red: red, green: green, blue: blue, alpha: alpha)
    }
}
