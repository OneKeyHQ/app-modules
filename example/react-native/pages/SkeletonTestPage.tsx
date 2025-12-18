import React, { useState } from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { TestPageBase, TestButton } from './TestPageBase';
import { SkeletonView } from '@onekeyfe/react-native-skeleton';

interface SkeletonTestPageProps {
  onGoHome: () => void;
  safeAreaInsets: any;
}

export function SkeletonTestPage({ onGoHome, safeAreaInsets }: SkeletonTestPageProps) {
  const [showSkeletons, setShowSkeletons] = useState(true);
  const [currentColor, setCurrentColor] = useState('#E1E9EE');

  const colors = [
    { name: 'Light Gray', value: '#E1E9EE' },
    { name: 'Medium Gray', value: '#C4C4C4' },
    { name: 'Dark Gray', value: '#8E8E93' },
    { name: 'Blue', value: '#007AFF' },
    { name: 'Purple', value: '#5856D6' },
    { name: 'Green', value: '#34C759' },
  ];

  return (
    <TestPageBase
      title="Skeleton Test"
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
    >
      <View style={styles.section}>
        <Text style={styles.sectionTitle}>控制</Text>
        
        <TestButton
          title={showSkeletons ? "隐藏 Skeletons" : "显示 Skeletons"}
          onPress={() => setShowSkeletons(!showSkeletons)}
          style={styles.button}
        />
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>颜色选择</Text>
        <View style={styles.colorGrid}>
          {colors.map((color) => (
            <TestButton
              key={color.value}
              title={color.name}
              onPress={() => setCurrentColor(color.value)}
              style={[
                styles.colorButton,
                { backgroundColor: currentColor === color.value ? '#007AFF' : '#F2F2F7' }
              ]}
            />
          ))}
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Skeleton 演示</Text>
        
        {/* Card List Demo */}
        <View style={styles.demoContainer}>
          <Text style={styles.demoTitle}>卡片列表</Text>
          {[1, 2, 3].map((item) => (
            <View key={item} style={styles.cardDemo}>
              {showSkeletons ? (
                <>
                  <SkeletonView style={styles.avatarSkeleton} color={currentColor} />
                  <View style={styles.cardContent}>
                    <SkeletonView style={styles.titleSkeleton} color={currentColor} />
                    <SkeletonView style={styles.subtitleSkeleton} color={currentColor} />
                  </View>
                </>
              ) : (
                <>
                  <View style={[styles.avatarSkeleton, { backgroundColor: '#007AFF' }]} />
                  <View style={styles.cardContent}>
                    <Text style={styles.realTitle}>用户名 {item}</Text>
                    <Text style={styles.realSubtitle}>这是一个示例描述文本</Text>
                  </View>
                </>
              )}
            </View>
          ))}
        </View>

        {/* Text Lines Demo */}
        <View style={styles.demoContainer}>
          <Text style={styles.demoTitle}>文本行</Text>
          <View style={styles.textDemo}>
            {showSkeletons ? (
              <>
                <SkeletonView style={styles.longLine} color={currentColor} />
                <SkeletonView style={styles.mediumLine} color={currentColor} />
                <SkeletonView style={styles.shortLine} color={currentColor} />
              </>
            ) : (
              <>
                <Text style={styles.realText}>这是一行完整的文本内容，用来展示真实的内容</Text>
                <Text style={styles.realText}>这是中等长度的文本</Text>
                <Text style={styles.realText}>短文本</Text>
              </>
            )}
          </View>
        </View>

        {/* Image Grid Demo */}
        <View style={styles.demoContainer}>
          <Text style={styles.demoTitle}>图片网格</Text>
          <View style={styles.imageGrid}>
            {[1, 2, 3, 4, 5, 6].map((item) => (
              showSkeletons ? (
                <SkeletonView 
                  key={item}
                  style={styles.imageSkeleton} 
                  color={currentColor} 
                />
              ) : (
                <View 
                  key={item}
                  style={[styles.imageSkeleton, { backgroundColor: '#34C759' }]} 
                />
              )
            ))}
          </View>
        </View>

        {/* Custom Shapes Demo */}
        <View style={styles.demoContainer}>
          <Text style={styles.demoTitle}>自定义形状</Text>
          <View style={styles.shapesContainer}>
            {showSkeletons ? (
              <>
                <SkeletonView style={styles.circleSkeleton} color={currentColor} />
                <SkeletonView style={styles.rectangleSkeleton} color={currentColor} />
                <SkeletonView style={styles.pillSkeleton} color={currentColor} />
              </>
            ) : (
              <>
                <View style={[styles.circleSkeleton, { backgroundColor: '#FF3B30' }]} />
                <View style={[styles.rectangleSkeleton, { backgroundColor: '#FF9500' }]} />
                <View style={[styles.pillSkeleton, { backgroundColor: '#5856D6' }]} />
              </>
            )}
          </View>
        </View>
      </View>
    </TestPageBase>
  );
}

const styles = StyleSheet.create({
  section: {
    marginBottom: 25,
  },
  sectionTitle: {
    fontSize: 18,
    fontWeight: '600',
    color: '#333',
    marginBottom: 15,
  },
  button: {
    marginBottom: 10,
  },
  colorGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  colorButton: {
    paddingVertical: 8,
    paddingHorizontal: 12,
    borderRadius: 6,
    minWidth: 80,
  },
  demoContainer: {
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    marginBottom: 15,
  },
  demoTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 12,
  },
  
  // Card Demo Styles
  cardDemo: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 12,
    paddingVertical: 8,
  },
  avatarSkeleton: {
    width: 50,
    height: 50,
    borderRadius: 25,
  },
  cardContent: {
    marginLeft: 12,
    flex: 1,
  },
  titleSkeleton: {
    height: 16,
    borderRadius: 8,
    marginBottom: 8,
    width: '70%',
  },
  subtitleSkeleton: {
    height: 12,
    borderRadius: 6,
    width: '50%',
  },
  realTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#333',
    marginBottom: 4,
  },
  realSubtitle: {
    fontSize: 14,
    color: '#666',
  },

  // Text Demo Styles
  textDemo: {
    gap: 10,
  },
  longLine: {
    height: 14,
    borderRadius: 7,
    width: '100%',
  },
  mediumLine: {
    height: 14,
    borderRadius: 7,
    width: '75%',
  },
  shortLine: {
    height: 14,
    borderRadius: 7,
    width: '40%',
  },
  realText: {
    fontSize: 14,
    color: '#333',
    lineHeight: 20,
  },

  // Image Grid Styles
  imageGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  imageSkeleton: {
    width: 80,
    height: 80,
    borderRadius: 8,
  },

  // Custom Shapes Styles
  shapesContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 15,
  },
  circleSkeleton: {
    width: 60,
    height: 60,
    borderRadius: 30,
  },
  rectangleSkeleton: {
    width: 100,
    height: 40,
    borderRadius: 8,
  },
  pillSkeleton: {
    width: 120,
    height: 30,
    borderRadius: 15,
  },
});
