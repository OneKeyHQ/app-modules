import React from 'react';
import { renderToStaticMarkup } from 'react-dom/server';
import { View } from 'react-native';

import { NativeList } from '../NativeList.web';
import type { NativeListSnapshot } from '../models';

jest.mock('react-native', () => ({
  View: require('react-native-web').View,
}));
jest.mock('../web/NativeListWebAvatarCache', () => ({
  acquireNativeListAvatar: jest.fn(),
  canonicalNativeListAvatarUri: jest.fn(),
}));

const snapshot: NativeListSnapshot = {
  schemaVersion: 1,
  generation: 1,
  layout: { kind: 'linear' },
  rows: [],
};

it('marks the React footer slot in the rendered Web DOM', () => {
  const markup = renderToStaticMarkup(
    <NativeList snapshot={snapshot} listFooter={<span>Show more</span>} />
  );
  expect(markup).toContain('data-testid="native-list-footer-slot"');
  expect(markup).toContain('Show more');
});

it('keeps native disclosure slots out of the Web footer and DOM props', () => {
  const markup = renderToStaticMarkup(
    <NativeList
      snapshot={snapshot}
      listFooter={<span>Web footer</span>}
      listExpandedContent={<span>Native expanded content</span>}
      listStructureToggle={<span>Native toggle</span>}
      onStructuralTransitionSettled={() => {}}
    />
  );
  expect(markup).toContain('Web footer');
  expect(markup).not.toContain('Native expanded content');
  expect(markup).not.toContain('Native toggle');
  expect(markup).not.toContain('onStructuralTransitionSettled');
});

it('keeps the sidecar and left footer in one scroll content tree', () => {
  const markup = renderToStaticMarkup(
    <NativeList
      snapshot={snapshot}
      listFooter={<span>Left footer</span>}
      listSidecar={<span>Recent activity</span>}
      sidecarLayout={{ width: 400, gap: 24, maxContentWidth: 1140 }}
    />
  );
  expect(markup).toContain('Left footer');
  expect(markup).toContain('Recent activity');
  expect(markup.match(/Recent activity/g)).toHaveLength(1);
});

it('uses logical sidecar insets so an RTL host mirrors both columns', () => {
  const WebView = View as React.ComponentType<
    React.ComponentProps<typeof View> & { dir: 'rtl' }
  >;
  const markup = renderToStaticMarkup(
    <WebView dir="rtl">
      <NativeList
        snapshot={snapshot}
        listSidecar={<span>Recent activity</span>}
        sidecarLayout={{ width: 400, gap: 24, maxContentWidth: 1140 }}
      />
    </WebView>
  );
  expect(markup).toContain('margin-right:0px');
  expect(markup).toContain('left:0px;top:0px;width:400px');
});
