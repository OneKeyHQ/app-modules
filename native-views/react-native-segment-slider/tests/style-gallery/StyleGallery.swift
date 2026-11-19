import Foundation
import UIKit

// Only Nitro's generated base is stubbed. Layers, colors and touch handling
// come from the production Swift sources.
class HybridSegmentSliderSpec: NSObject {}

/// Renders every row of `scenarios.json` with the production iOS slider and
/// writes a PNG. Usage: style-gallery <scenarios.json> <out.png>
@main
enum StyleGallery {
  static func main() throws {
    let args = CommandLine.arguments
    guard args.count == 3 else {
      print("usage: style-gallery <scenarios.json> <out.png>")
      exit(2)
    }
    let data = try Data(contentsOf: URL(fileURLWithPath: args[1]))
    let spec = try JSONSerialization.jsonObject(with: data) as! [String: Any]
    let sliderWidth = CGFloat(spec["sliderWidth"] as! Double)
    let sideInset = CGFloat(spec["sideInset"] as! Double)
    let rowHeight = CGFloat(spec["rowHeight"] as! Double)
    let sliderTop = CGFloat(spec["sliderTop"] as! Double)
    let palettes = spec["palettes"] as! [String: [String: String]]
    let rows = spec["rows"] as! [[String: Any]]

    let width = sliderWidth + sideInset * 2
    let canvas = UIView(frame: CGRect(x: 0, y: 0, width: width,
                                      height: rowHeight * CGFloat(rows.count)))
    var retained: [HybridSegmentSlider] = []
    for (index, row) in rows.enumerated() {
      let palette = palettes[row["palette"] as! String]!
      let rowView = UIView(frame: CGRect(x: 0, y: rowHeight * CGFloat(index),
                                         width: width, height: rowHeight))
      rowView.backgroundColor = SegmentSliderColorParser.parse(palette["background"])
      canvas.addSubview(rowView)

      let value = row["value"] as! Double
      let slider = HybridSegmentSlider()
      slider.fillColor = palette["fillColor"]!
      slider.trackColor = palette["trackColor"]!
      slider.thumbColor = palette["thumbColor"]!
      slider.thumbBorderColor = palette["thumbBorderColor"]!
      slider.markActiveColor = palette["markActiveColor"]!
      slider.markInactiveColor = palette["markInactiveColor"]!
      slider.markBorderColor = palette["markBorderColor"]!
      slider.bubbleColor = palette["bubbleColor"]!
      slider.bubbleTextColor = palette["bubbleTextColor"]!
      slider.showBubble = true
      slider.segments = row["segments"] as! Double
      slider.defaultValue = value
      slider.view.frame = CGRect(x: sideInset, y: sliderTop, width: sliderWidth, height: 24)
      rowView.addSubview(slider.view)
      slider.view.layoutIfNeeded()
      if row["pressed"] as? Bool == true {
        // Same 12-pt endpoint inset the production view maps touches with.
        let x = 12 + CGFloat(value / 100) * (sliderWidth - 24)
        (slider.view as! SegmentSliderUIView).onTouchBegan?(x)
      }
      retained.append(slider)
    }

    let format = UIGraphicsImageRendererFormat()
    format.scale = 2
    let image = UIGraphicsImageRenderer(bounds: canvas.bounds, format: format).image { context in
      canvas.layer.render(in: context.cgContext)
    }
    try image.pngData()!.write(to: URL(fileURLWithPath: args[2]))
    withExtendedLifetime(retained) {}
    print("wrote \(rows.count) rows to \(args[2])")
  }
}
