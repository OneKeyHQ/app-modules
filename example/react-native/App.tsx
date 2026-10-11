/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 */

import { configureNativeListFonts } from '@onekeyfe/react-native-native-list';
import { StatusBar, useColorScheme } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import { NavigationContainer } from '@react-navigation/native';
import { AppNavigator } from './route';

// Configure once in this UI runtime. This example ships no custom font assets.
configureNativeListFonts({});

function App() {
  const isDarkMode = useColorScheme() === 'dark';

  return (
    <SafeAreaProvider>
      <NavigationContainer>
        <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
        <AppNavigator />
      </NavigationContainer>
    </SafeAreaProvider>
  );
}

export default App;
