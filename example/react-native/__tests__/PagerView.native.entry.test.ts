import fs from 'node:fs';
import path from 'node:path';

it('resolves the real Native package entry without evaluating Web modules', () => {
  const root = path.resolve(
    __dirname,
    '../../../native-views/react-native-pager-view',
  );
  const manifest = JSON.parse(
    fs.readFileSync(path.join(root, 'package.json'), 'utf8'),
  ) as {
    main: string;
    'react-native'?: string;
    exports: { '.': { 'react-native'?: string } };
  };
  jest.isolateModules(() => {
    jest.doMock(
      path.join(root, 'lib/module/CollapsiblePagerView.web.js'),
      () => {
        throw new Error('Native entry evaluated the Web pager');
      },
    );
    jest.doMock(
      path.join(root, 'lib/module/WebCollapsiblePagerPage.js'),
      () => {
        throw new Error('Native entry evaluated the Web page context');
      },
    );
    const entry = manifest['react-native'] ?? manifest.main;
    const api = require(path.join(root, entry)) as Record<string, unknown>;
    expect(api.default).toBeDefined();
    expect(api.CollapsiblePagerView).toBeDefined();
    expect(api.NativeScroller).toBeDefined();
    expect(api.ScrollEdgeEffect).toBeDefined();
    expect(api.usePagerView).toBeDefined();
    expect(api.WebCollapsiblePagerView).toBeUndefined();
    expect(api.useWebCollapsiblePagerPage).toBeUndefined();
    expect(manifest.exports['.']['react-native']).toBe(entry);
  });
});
