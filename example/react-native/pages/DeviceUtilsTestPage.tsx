import React, { useState, useEffect } from 'react';
import { View, Text, StyleSheet, Alert } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { NitroModules } from 'react-native-nitro-modules';

interface DeviceUtilsTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function DeviceUtilsTestPage({ onGoHome, safeAreaInsets }: DeviceUtilsTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [backgroundColor, setBackgroundColor] = useState('#ffffff');
  const [isSpanning, setIsSpanning] = useState(false);
  const [spanningCallbackActive, setSpanningCallbackActive] = useState(false);

  const deviceUtils = NitroModules.createHybridObject('ReactNativeDeviceUtils');

  // Clear previous results
  const clearResults = () => {
    setResult(null);
    setError(null);
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
        deviceUtils.onSpanningChanged((spanning: boolean) => {
          setIsSpanning(spanning);
          setResult({ 
            spanningCallbackTriggered: true, 
            isSpanning: spanning,
            timestamp: new Date().toLocaleTimeString()
          });
        });
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
      deviceUtils.changeBackgroundColor(backgroundColor);
      setResult({ 
        backgroundColorChanged: true, 
        color: backgroundColor 
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // Test with predefined colors
  const testPredefinedColor = (color: string) => {
    clearResults();
    try {
      deviceUtils.changeBackgroundColor(color);
      setResult({ 
        backgroundColorChanged: true, 
        color: color 
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
        
        <TestInput
          placeholder="Enter color (hex like #FF0000 or name like 'red')"
          value={backgroundColor}
          onChangeText={setBackgroundColor}
        />
        
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
