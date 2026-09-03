import { useMemo, useState } from 'react';
import { FlatList, Pressable, StyleSheet, Text, View } from 'react-native';
import {
  OneKeyImage,
  OneKeyImageContentFit,
  OneKeyImageLoadingStrategy,
  OneKeyImageVariant,
} from '@onekeyfe/react-native-image';

const variants = [
  OneKeyImageVariant.GENERIC,
  OneKeyImageVariant.TOKEN,
  OneKeyImageVariant.NETWORK,
  OneKeyImageVariant.AVATAR,
];

export function OneKeyImageTestPage() {
  const [count, setCount] = useState(100);
  const [animatedWebPStatus, setAnimatedWebPStatus] = useState('loading');
  const [loadingStrategy, setLoadingStrategy] = useState(
    OneKeyImageLoadingStrategy.STATIC,
  );
  const items = useMemo(
    () => Array.from({ length: count }, (_, index) => index),
    [count],
  );

  return (
    <View style={styles.page}>
      <FlatList
        data={items}
        keyExtractor={item => String(item)}
        numColumns={5}
        removeClippedSubviews
        windowSize={7}
        initialNumToRender={20}
        maxToRenderPerBatch={20}
        contentContainerStyle={styles.content}
        ListHeaderComponent={
          <>
            <View style={styles.animatedWebPCard}>
              <Text style={styles.animatedWebPTitle}>
                Animated WebP runtime
              </Text>
              <Text testID="animated-webp-status">{animatedWebPStatus}</Text>
              <OneKeyImage
                testID="animated-webp-image"
                source={{ uri: 'https://www.gstatic.com/webp/animated/1.webp' }}
                autoplay
                optimizeTos={false}
                contentFit={OneKeyImageContentFit.CONTAIN}
                onLoadStart={() => setAnimatedWebPStatus('loading')}
                onDisplay={() => setAnimatedWebPStatus('displayed')}
                onError={({ error }) =>
                  setAnimatedWebPStatus(`error: ${error}`)
                }
                style={styles.animatedWebPImage}
              />
            </View>

            <View style={styles.examples}>
              {variants.map((variant, index) => (
                <OneKeyImage
                  key={variant}
                  source={{
                    uri: `https://picsum.photos/seed/onekey-${index}/240/240`,
                  }}
                  variant={variant}
                  contentFit={OneKeyImageContentFit.COVER}
                  loadingStrategy={OneKeyImageLoadingStrategy.SKELETON}
                  style={styles.largeImage}
                />
              ))}
            </View>

            <View style={styles.controls}>
              {[100, 200].map(value => (
                <Pressable
                  key={value}
                  style={[
                    styles.button,
                    count === value && styles.activeButton,
                  ]}
                  onPress={() => setCount(value)}
                >
                  <Text style={styles.buttonText}>{value} images</Text>
                </Pressable>
              ))}
              {[
                OneKeyImageLoadingStrategy.STATIC,
                OneKeyImageLoadingStrategy.SKELETON,
              ].map(value => (
                <Pressable
                  key={value}
                  style={[
                    styles.button,
                    loadingStrategy === value && styles.activeButton,
                  ]}
                  onPress={() => setLoadingStrategy(value)}
                >
                  <Text style={styles.buttonText}>{value}</Text>
                </Pressable>
              ))}
            </View>
          </>
        }
        renderItem={({ item }) => (
          <OneKeyImage
            source={{ uri: `https://picsum.photos/seed/stress-${item}/96/96` }}
            variant={variants[item % variants.length]}
            loadingStrategy={loadingStrategy}
            style={styles.gridImage}
          />
        )}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  page: {
    flex: 1,
    backgroundColor: '#f5f5f5',
  },
  content: {
    padding: 20,
  },
  animatedWebPCard: {
    alignItems: 'center',
    gap: 8,
    marginBottom: 16,
  },
  animatedWebPTitle: {
    fontSize: 16,
    fontWeight: '600',
  },
  animatedWebPImage: {
    width: 240,
    height: 180,
    backgroundColor: '#ddd',
  },
  examples: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
    marginBottom: 16,
  },
  largeImage: {
    width: 72,
    height: 72,
    borderRadius: 16,
    overflow: 'hidden',
  },
  controls: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 12,
    marginBottom: 16,
  },
  button: {
    backgroundColor: '#3a3a3c',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  activeButton: {
    backgroundColor: '#2f65e8',
  },
  buttonText: {
    color: '#fff',
    fontWeight: '600',
  },
  gridImage: {
    width: 58,
    height: 58,
    margin: 3,
    borderRadius: 10,
    overflow: 'hidden',
  },
});
