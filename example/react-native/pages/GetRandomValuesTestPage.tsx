import React, { useState } from 'react';
import { View, Text, Alert, ScrollView } from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeGetRandomValues } from '@onekeyfe/react-native-get-random-values';

interface GetRandomValuesTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function GetRandomValuesTestPage({ onGoHome, safeAreaInsets }: GetRandomValuesTestPageProps) {
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  
  // Form inputs
  const [byteLength, setByteLength] = useState('32');
  const [generatedValues, setGeneratedValues] = useState<string[]>([]);

  const executeRandomValueOperation = async (operation: () => Promise<any> | any, operationName: string) => {
    setIsLoading(true);
    setError(null);
    setResult(null);

    try {
      const res = await Promise.resolve(operation());
      setResult(res);
      Alert.alert('Success', `${operationName} completed successfully`);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `${operationName} failed: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const getRandomBase64 = () => {
    const length = parseInt(byteLength, 10);
    
    if (isNaN(length) || length <= 0) {
      Alert.alert('Error', 'Byte length must be a positive number');
      return;
    }

    if (length > 1024) {
      Alert.alert('Warning', 'Large byte lengths may affect performance');
    }

    executeRandomValueOperation(
      () => ReactNativeGetRandomValues.getRandomBase64(length),
      'Get Random Base64'
    );
  };

  const generateMultipleValues = () => {
    const length = parseInt(byteLength, 10);
    
    if (isNaN(length) || length <= 0) {
      Alert.alert('Error', 'Byte length must be a positive number');
      return;
    }

    setIsLoading(true);
    setError(null);
    
    try {
      const values: string[] = [];
      for (let i = 0; i < 5; i++) {
        const randomValue = ReactNativeGetRandomValues.getRandomBase64(length);
        values.push(randomValue);
      }
      
      setGeneratedValues(values);
      setResult({ 
        count: values.length, 
        values: values,
        averageLength: Math.round(values.reduce((sum, val) => sum + val.length, 0) / values.length)
      });
      Alert.alert('Success', `Generated ${values.length} random values`);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `Generate multiple values failed: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const testDifferentLengths = () => {
    setIsLoading(true);
    setError(null);
    
    try {
      const lengths = [8, 16, 32, 64, 128];
      const results: Record<number, string> = {};
      
      lengths.forEach(length => {
        const randomValue = ReactNativeGetRandomValues.getRandomBase64(length);
        results[length] = randomValue;
      });
      
      setResult({
        message: 'Generated random values with different byte lengths',
        results: results,
        lengthMapping: Object.fromEntries(
          Object.entries(results).map(([length, value]) => [
            `${length} bytes`, `${value.length} base64 chars`
          ])
        )
      });
      Alert.alert('Success', 'Generated values with different lengths');
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `Test different lengths failed: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const testRandomnessQuality = () => {
    setIsLoading(true);
    setError(null);
    
    try {
      const testCount = 10;
      const length = 16;
      const values: string[] = [];
      const uniqueValues = new Set<string>();
      
      for (let i = 0; i < testCount; i++) {
        const randomValue = ReactNativeGetRandomValues.getRandomBase64(length);
        values.push(randomValue);
        uniqueValues.add(randomValue);
      }
      
      const uniqueCount = uniqueValues.size;
      const uniquePercentage = (uniqueCount / testCount) * 100;
      
      setResult({
        totalGenerated: testCount,
        uniqueValues: uniqueCount,
        uniquePercentage: `${uniquePercentage.toFixed(1)}%`,
        duplicates: testCount - uniqueCount,
        quality: uniquePercentage === 100 ? 'Excellent' : uniquePercentage >= 90 ? 'Good' : 'Poor',
        samples: values.slice(0, 3)
      });
      
      Alert.alert('Success', `Randomness test completed. ${uniquePercentage.toFixed(1)}% unique values`);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : String(err);
      setError(errorMessage);
      Alert.alert('Error', `Randomness test failed: ${errorMessage}`);
    } finally {
      setIsLoading(false);
    }
  };

  const clearResults = () => {
    setResult(null);
    setError(null);
    setGeneratedValues([]);
  };

  const resetForm = () => {
    setByteLength('32');
    clearResults();
  };

  return (
    <TestPageBase title="Get Random Values Test" onGoHome={onGoHome} safeAreaInsets={safeAreaInsets}>
      {/* Basic Random Value Generation */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Random Value Generation
        </Text>
        
        <TestInput
          placeholder="Byte length (e.g., 32)"
          value={byteLength}
          onChangeText={setByteLength}
        />
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10, marginTop: 10 }}>
          <TestButton
            title="Get Random Base64"
            onPress={getRandomBase64}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140 }}
          />
          <TestButton
            title="Generate 5 Values"
            onPress={generateMultipleValues}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140 }}
          />
        </View>
      </View>

      {/* Advanced Testing */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Advanced Testing
        </Text>
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
          <TestButton
            title="Test Different Lengths"
            onPress={testDifferentLengths}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140, backgroundColor: '#FF9500' }}
          />
          <TestButton
            title="Test Randomness Quality"
            onPress={testRandomnessQuality}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 140, backgroundColor: '#34C759' }}
          />
        </View>
      </View>

      {/* Utility Buttons */}
      <View>
        <Text style={{ fontSize: 16, fontWeight: '600', marginBottom: 10, color: '#333' }}>
          Utilities
        </Text>
        
        <View style={{ flexDirection: 'row', flexWrap: 'wrap', gap: 10 }}>
          <TestButton
            title="Clear Results"
            onPress={clearResults}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120, backgroundColor: '#8E8E93' }}
          />
          <TestButton
            title="Reset Form"
            onPress={resetForm}
            disabled={isLoading}
            style={{ flex: 1, minWidth: 120, backgroundColor: '#FF3B30' }}
          />
        </View>
      </View>

      <TestResult result={result} error={error} />

      {/* Generated Values History */}
      {generatedValues.length > 0 && (
        <View style={{ backgroundColor: '#fff', padding: 15, borderRadius: 8 }}>
          <Text style={{ fontSize: 14, fontWeight: '600', color: '#333', marginBottom: 10 }}>
            Last Generated Values:
          </Text>
          <ScrollView style={{ maxHeight: 120 }}>
            {generatedValues.map((value, index) => (
              <Text key={index} style={{ 
                fontSize: 12, 
                color: '#666', 
                fontFamily: 'Courier New',
                marginBottom: 4,
                backgroundColor: '#f8f8f8',
                padding: 4,
                borderRadius: 4
              }}>
                {index + 1}: {value}
              </Text>
            ))}
          </ScrollView>
        </View>
      )}

      {/* Documentation */}
      <View style={{ marginTop: 20, padding: 15, backgroundColor: '#fff', borderRadius: 8 }}>
        <Text style={{ fontSize: 14, color: '#666', lineHeight: 20 }}>
          <Text style={{ fontWeight: '600' }}>Random Values Module:</Text>{'\n'}
          • <Text style={{ fontWeight: '500' }}>getRandomBase64:</Text> Generate cryptographically secure random values{'\n'}
          • <Text style={{ fontWeight: '500' }}>Byte Length:</Text> Specify the number of random bytes to generate{'\n'}
          • <Text style={{ fontWeight: '500' }}>Base64 Output:</Text> Returns base64-encoded random string{'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Use Cases:</Text>{'\n'}
          • Generating secure tokens and keys{'\n'}
          • Creating unique identifiers{'\n'}
          • Cryptographic nonces and salts{'\n'}
          • Session IDs and API keys{'\n'}
          {'\n'}
          <Text style={{ fontWeight: '600' }}>Note:</Text> Base64 encoding increases output length by ~33% compared to input bytes.
        </Text>
      </View>
    </TestPageBase>
  );
}
