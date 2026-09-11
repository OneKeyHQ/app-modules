const path = require('node:path');
const { HtmlRspackPlugin } = require('@rspack/core');

module.exports = {
  mode: process.env.NODE_ENV === 'production' ? 'production' : 'development',
  entry: path.resolve(__dirname, 'index.tsx'),
  output: {
    clean: true,
    filename: 'assets/[name].[contenthash:8].js',
    path: path.resolve(__dirname, '../web-build'),
    publicPath: '/',
  },
  devtool: 'source-map',
  devServer: {
    host: '127.0.0.1',
    port: 8090,
    historyApiFallback: true,
    hot: true,
    proxy: [
      {
        context: ['/onekey-market-api'],
        target: 'https://utility.onekeycn.com',
        router: req => req.url.startsWith('/onekey-market-api/swap/')
          ? 'https://swap.onekeycn.com'
          : /^\/onekey-market-api\/utility\/v1\/(stocks|market\/asset\/list)(\?|$)/.test(req.url)
          ? 'https://utility.onekeytest.com' : 'https://utility.onekeycn.com',
        changeOrigin: true,
        pathRewrite: (requestPath, req) => {
          const url = new URL(requestPath, 'http://localhost');
          req.marketLocale = url.searchParams.get('locale');
          url.searchParams.delete('locale');
          return url.pathname.replace(/^\/onekey-market-api/, '') + url.search;
        },
        on: {
          proxyReq: (proxyReq, req) => {
            proxyReq.setHeader(
              'X-Onekey-Request-Locale',
              req.marketLocale === 'en-US' ? 'en-US' : 'zh-CN',
            );
          },
        },
        headers: {
          Accept: 'application/json',
          'User-Agent': 'OneKeyWallet/6.15.0',
          'X-Onekey-Request-Version': '6.15.0',
          'X-Onekey-Request-Platform': 'web',
          'X-Onekey-Request-Locale': 'zh-cn',
          'X-Onekey-Request-Currency': 'usd',
        },
      },
    ],
  },
  resolve: {
    alias: {
      '@onekeyfe/react-native-native-list$': path.resolve(
        __dirname,
        '../../../native-views/react-native-native-list/src',
      ),
      '@onekeyfe/react-native-pager-view$': path.resolve(
        __dirname,
        '../../../native-views/react-native-pager-view/src',
      ),
      'react-native$': 'react-native-web',
    },
    extensions: [
      '.web.tsx',
      '.web.ts',
      '.web.jsx',
      '.web.js',
      '.tsx',
      '.ts',
      '.jsx',
      '.js',
      '.json',
    ],
  },
  module: {
    rules: [
      {
        test: /\.m?js$/,
        resolve: { fullySpecified: false },
      },
      {
        test: /\.[jt]sx?$/,
        exclude: /node_modules/,
        use: {
          loader: 'builtin:swc-loader',
          options: {
            jsc: {
              parser: { syntax: 'typescript', tsx: true },
              transform: { react: { runtime: 'automatic' } },
            },
          },
        },
      },
      {
        test: /\.css$/,
        type: 'css',
      },
      {
        test: /\.(ttf|svg)$/,
        type: 'asset/resource',
        generator: { filename: 'assets/[name].[contenthash:8][ext]' },
      },
    ],
  },
  plugins: [
    new HtmlRspackPlugin({
      title: 'OneKey Native Examples',
      templateContent:
        '<!doctype html><html lang="zh-CN"><head><meta charset="utf-8"></head><body><div id="root"></div></body></html>',
      meta: {
        viewport:
          'width=device-width, initial-scale=1, maximum-scale=1, viewport-fit=cover',
        'theme-color': '#080808',
      },
    }),
  ],
};
