/**
 * @format
 */

import { AppRegistry } from 'react-native';
import { Buffer } from 'buffer';
import { name as appName } from './app.json';

global.Buffer ??= Buffer;

AppRegistry.registerComponent(appName, () => require('./App').default);
