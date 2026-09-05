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
  },
  resolve: {
    alias: {
      '@onekeyfe/react-native-native-list$': path.resolve(
        __dirname,
        '../../../native-views/react-native-native-list/src',
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
        test: /\.ttf$/,
        type: 'asset/resource',
        generator: { filename: 'assets/[name].[contenthash:8][ext]' },
      },
    ],
  },
  plugins: [
    new HtmlRspackPlugin({
      title: 'NativeList Token Selector',
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
