// Renders every row of scenarios.json with the production Web slider. Bundled
// and screenshotted by render-web.mjs; not part of the published package.
import { createRoot } from 'react-dom/client';

import spec from './scenarios.json';
import { SegmentSlider } from '@onekeyfe/react-native-segment-slider';

type IPalette = Record<string, string>;

const palettes = spec.palettes as Record<string, IPalette>;
const width = spec.sliderWidth + spec.sideInset * 2;

function Gallery() {
  return (
    <div style={{ width }}>
      {spec.rows.map((row, index) => {
        const { background, ...colors } = palettes[row.palette]!;
        return (
          <div
            key={index}
            data-row={index}
            style={{
              position: 'relative',
              width,
              height: spec.rowHeight,
              background,
            }}
          >
            <div
              style={{
                position: 'absolute',
                left: spec.sideInset,
                top: spec.sliderTop,
                width: spec.sliderWidth,
              }}
            >
              <SegmentSlider
                value={row.value}
                segments={row.segments}
                onChange={() => {}}
                {...colors}
              />
            </div>
          </div>
        );
      })}
    </div>
  );
}

createRoot(document.getElementById('root')!).render(<Gallery />);
