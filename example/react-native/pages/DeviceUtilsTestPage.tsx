import React, { useState, useRef, useEffect } from 'react';
import { View, Text, StyleSheet, Alert } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import  { ReactNativeDeviceUtils } from '@onekeyfe/react-native-device-utils';

interface DeviceUtilsTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}
const deviceUtils = ReactNativeDeviceUtils

deviceUtils.initEventListeners();

export function DeviceUtilsTestPage({ onGoHome, safeAreaInsets }: DeviceUtilsTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [colorR, setColorR] = useState('255');
  const [colorG, setColorG] = useState('255');
  const [colorB, setColorB] = useState('255');
  const [colorA, setColorA] = useState('255');
  const [isSpanning, setIsSpanning] = useState(false);
  const [spanningCallbackActive, setSpanningCallbackActive] = useState(false);
  const spanningListenerIdRef = useRef<number | null>(null);

  useEffect(() => {
    return () => {
      if (spanningListenerIdRef.current !== null) {
        deviceUtils.removeSpanningChangedListener(spanningListenerIdRef.current);
      }
    };
  }, []);


  // Clear previous results
  const clearResults = () => {
    setResult(null);
    setError(null);
  };

  // Helper function to parse color string to RGBA
  const parseColorToRGBA = (color: string): { r: number; g: number; b: number; a: number } => {
    // Handle hex colors
    if (color.startsWith('#')) {
      const hex = color.replace('#', '');
      let r = 0, g = 0, b = 0, a = 255;
      
      if (hex.length === 6) {
        r = parseInt(hex.substring(0, 2), 16);
        g = parseInt(hex.substring(2, 4), 16);
        b = parseInt(hex.substring(4, 6), 16);
      } else if (hex.length === 8) {
        r = parseInt(hex.substring(0, 2), 16);
        g = parseInt(hex.substring(2, 4), 16);
        b = parseInt(hex.substring(4, 6), 16);
        a = parseInt(hex.substring(6, 8), 16);
      } else if (hex.length === 3) {
        r = parseInt(hex[0] + hex[0], 16);
        g = parseInt(hex[1] + hex[1], 16);
        b = parseInt(hex[2] + hex[2], 16);
      }
      
      return { r, g, b, a };
    }
    
    // Handle named colors
    const namedColors: { [key: string]: { r: number; g: number; b: number; a: number } } = {
      'red': { r: 255, g: 0, b: 0, a: 255 },
      'green': { r: 0, g: 255, b: 0, a: 255 },
      'blue': { r: 0, g: 0, b: 255, a: 255 },
      'white': { r: 255, g: 255, b: 255, a: 255 },
      'black': { r: 0, g: 0, b: 0, a: 255 },
    };
    
    return namedColors[color.toLowerCase()] || { r: 255, g: 255, b: 255, a: 255 };
  };

  // Test isDualScreenDevice
  const testIsDualScreenDevice = () => {
    clearResults();
    try {
      const isDual = deviceUtils.isDualScreenDevice();
      setResult({ isDualScreenDevice: isDual });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test isSpanning
  const testIsSpanning = () => {
    clearResults();
    try {
      const spanning = deviceUtils.isSpanning();
      setResult({ isSpanning: spanning });
      setIsSpanning(spanning);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test getWindowRects
  const testGetWindowRects = async () => {
    clearResults();
    try {
      const rects = await deviceUtils.getWindowRects();
      setResult({ windowRects: rects });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test getHingeBounds
  const testGetHingeBounds = async () => {
    clearResults();
    try {
      const hingeBounds = await deviceUtils.getHingeBounds();
      setResult({ hingeBounds });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test spanning callback
  const testSpanningCallback = () => {
    clearResults();
    try {
      if (!spanningCallbackActive) {
        const listenerId = deviceUtils.addSpanningChangedListener((spanning: boolean) => {
          setIsSpanning(spanning);
          setResult({ 
            spanningCallbackTriggered: true, 
            isSpanning: spanning,
            timestamp: new Date().toLocaleTimeString()
          });
        });
        spanningListenerIdRef.current = listenerId;
        setSpanningCallbackActive(true);
        Alert.alert('Success', 'Spanning callback registered! Try rotating your device or connecting/disconnecting external displays.');
      } else {
        Alert.alert('Info', 'Spanning callback is already active');
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test changeBackgroundColor
  const testChangeBackgroundColor = () => {
    clearResults();
    try {
      const r = parseInt(colorR) || 0;
      const g = parseInt(colorG) || 0;
      const b = parseInt(colorB) || 0;
      const a = parseInt(colorA) || 255;
      
      // Validate values are in range 0-255
      if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255 || a < 0 || a > 255) {
        setError('All color values must be between 0 and 255');
        return;
      }
      
      deviceUtils.changeBackgroundColor(r, g, b, a);
      setResult({ 
        backgroundColorChanged: true, 
        rgba: { r, g, b, a }
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test with predefined colors
  const testPredefinedColor = (color: string) => {
    clearResults();
    try {
      const rgba = parseColorToRGBA(color);
      setColorR(rgba.r.toString());
      setColorG(rgba.g.toString());
      setColorB(rgba.b.toString());
      setColorA(rgba.a.toString());
      deviceUtils.changeBackgroundColor(rgba.r, rgba.g, rgba.b, rgba.a);
      setResult({ 
        backgroundColorChanged: true, 
        color: color,
        rgba: rgba
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test setUserInterfaceStyle
  const testSetUserInterfaceStyle = (style: 'light' | 'dark' | 'unspecified') => {
    clearResults();
    try {
      deviceUtils.setUserInterfaceStyle(style);
      setResult({
        userInterfaceStyleChanged: true,
        style: style,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Get all device info at once
  const getAllDeviceInfo = async () => {
    clearResults();
    try {
      const isDual = deviceUtils.isDualScreenDevice();
      const spanning = deviceUtils.isSpanning();
      const windowRects = await deviceUtils.getWindowRects();
      const hingeBounds = await deviceUtils.getHingeBounds();

      setResult({
        isDualScreenDevice: isDual,
        isSpanning: spanning,
        windowRects,
        hingeBounds,
        timestamp: new Date().toLocaleTimeString()
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  return (
    <TestPageBase
      title="Device Utils Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      {/* Device Detection Tests */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Device Detection</Text>
        
        <TestButton
          title="Check if Dual Screen Device"
          onPress={testIsDualScreenDevice}
        />
        
        <TestButton
          title="Check if Spanning"
          onPress={testIsSpanning}
        />
        
        <TestButton
          title="Get All Device Info"
          onPress={getAllDeviceInfo}
        />
      </View>

      {/* Window Information Tests */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Window Information</Text>
        
        <TestButton
          title="Get Window Rectangles"
          onPress={testGetWindowRects}
        />
        
        <TestButton
          title="Get Hinge Bounds"
          onPress={testGetHingeBounds}
        />
      </View>

      {/* Spanning Callback Test */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Spanning Callback</Text>
        
        <TestButton
          title={spanningCallbackActive ? "Callback Active" : "Register Spanning Callback"}
          onPress={testSpanningCallback}
          disabled={spanningCallbackActive}
        />
        
        {spanningCallbackActive && (
          <View style={styles.statusContainer}>
            <Text style={styles.statusText}>
              Current Spanning State: {isSpanning ? 'Yes' : 'No'}
            </Text>
          </View>
        )}
      </View>

      {/* Background Color Tests */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Background Color</Text>
        
        <View style={styles.colorInputsContainer}>
          <View style={styles.colorInputWrapper}>
            <Text style={styles.colorInputLabel}>R (0-255):</Text>
            <TestInput
              placeholder="R"
              value={colorR}
              onChangeText={setColorR}
              keyboardType="numeric"
              style={styles.colorInput}
            />
          </View>
          <View style={styles.colorInputWrapper}>
            <Text style={styles.colorInputLabel}>G (0-255):</Text>
            <TestInput
              placeholder="G"
              value={colorG}
              onChangeText={setColorG}
              keyboardType="numeric"
              style={styles.colorInput}
            />
          </View>
          <View style={styles.colorInputWrapper}>
            <Text style={styles.colorInputLabel}>B (0-255):</Text>
            <TestInput
              placeholder="B"
              value={colorB}
              onChangeText={setColorB}
              keyboardType="numeric"
              style={styles.colorInput}
            />
          </View>
          <View style={styles.colorInputWrapper}>
            <Text style={styles.colorInputLabel}>A (0-255):</Text>
            <TestInput
              placeholder="A"
              value={colorA}
              onChangeText={setColorA}
              keyboardType="numeric"
              style={styles.colorInput}
            />
          </View>
        </View>
        
        <TestButton
          title="Change Background Color"
          onPress={testChangeBackgroundColor}
        />
        
        <View style={styles.colorButtonsContainer}>
          <TestButton
            title="Red"
            onPress={() => testPredefinedColor('red')}
            style={[styles.colorButton, { backgroundColor: '#FF0000' }]}
          />
          <TestButton
            title="Green"
            onPress={() => testPredefinedColor('green')}
            style={[styles.colorButton, { backgroundColor: '#00FF00' }]}
          />
          <TestButton
            title="Blue"
            onPress={() => testPredefinedColor('blue')}
            style={[styles.colorButton, { backgroundColor: '#0000FF' }]}
          />
          <TestButton
            title="Reset"
            onPress={() => testPredefinedColor('white')}
            style={[styles.colorButton, { backgroundColor: '#FFFFFF', borderWidth: 1, borderColor: '#ccc' }]}
          />
        </View>
      </View>

      {/* User Interface Style Tests */}
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>User Interface Style</Text>

        <View style={styles.colorButtonsContainer}>
          <TestButton
            title="Light"
            onPress={() => testSetUserInterfaceStyle('light')}
            style={styles.colorButton}
          />
          <TestButton
            title="Dark"
            onPress={() => testSetUserInterfaceStyle('dark')}
            style={styles.colorButton}
          />
          <TestButton
            title="System"
            onPress={() => testSetUserInterfaceStyle('unspecified')}
            style={styles.colorButton}
          />
        </View>
      </View>

      {/* Results */}
      <TestResult result={result} error={error} />
      
      {/* Instructions */}
      <View style={styles.instructionsContainer}>
        <Text style={styles.instructionsTitle}>Instructions:</Text>
        <Text style={styles.instructionsText}>
          • Dual Screen: Works best on Surface Duo or devices with external displays{'\n'}
          • Spanning: Try rotating device or connecting external display{'\n'}
          • Callback: Register once, then test spanning changes{'\n'}
          • Background: Changes system UI colors (status bar, navigation bar)
        </Text>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  section: {
    marginBottom: 20,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 10,
  },
  statusContainer: {
    backgroundColor: '#e8f4fd',
    padding: 12,
    borderRadius: 8,
    marginTop: 10,
  },
  statusText: {
    fontSize: 14,
    color: '#1976d2',
    fontWeight: '500',
  },
  colorInputsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    marginBottom: 10,
  },
  colorInputWrapper: {
    flex: 1,
    minWidth: 70,
  },
  colorInputLabel: {
    fontSize: 12,
    color: '#666',
    marginBottom: 4,
    fontWeight: '500',
  },
  colorInput: {
    minWidth: 60,
  },
  colorButtonsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 10,
    marginTop: 10,
  },
  colorButton: {
    flex: 1,
    minWidth: 70,
  },
  instructionsContainer: {
    backgroundColor: '#fff3cd',
    padding: 15,
    borderRadius: 8,
    marginTop: 20,
    borderLeftWidth: 4,
    borderLeftColor: '#ffc107',
  },
  instructionsTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#856404',
    marginBottom: 8,
  },
  instructionsText: {
    fontSize: 14,
    color: '#856404',
    lineHeight: 20,
  },
});
