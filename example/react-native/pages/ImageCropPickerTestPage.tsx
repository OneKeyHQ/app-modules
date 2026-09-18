import { useCallback, useState } from 'react';
import { Image, StyleSheet, Text, View } from 'react-native';
import ImageCropPicker, {
  ImageCropPickerError,
  type Image as PickedImage,
} from '@onekeyfe/react-native-image-crop-picker';
import { TestButton, TestPageBase, TestResult } from './TestPageBase';

// Production consumers import from `react-native-image-crop-picker`, an npm
// alias mapped to @onekeyfe/react-native-image-crop-picker.
export function ImageCropPickerTestPage() {
  const [image, setImage] = useState<PickedImage | null>(null);
  const [result, setResult] = useState<unknown>(null);
  const [error, setError] = useState<string | null>(null);

  const run = useCallback(async (task: () => Promise<PickedImage | void>) => {
    setResult(null);
    setError(null);
    try {
      const picked = await task();
      if (picked) {
        setImage(picked);
        // Keep the output readable: base64 payloads are only summarized.
        setResult({
          ...picked,
          data: picked.data ? `<${picked.data.length} base64 chars>` : undefined,
        });
      } else {
        setResult('done');
      }
    } catch (err) {
      setError(
        err instanceof ImageCropPickerError
          ? `${err.code}: ${err.message}`
          : String(err)
      );
    }
  }, []);

  return (
    <TestPageBase title="Image Crop Picker">
      <Text style={s.hint}>
        The picker needs no photo library permission. To check the OK-48227
        regression, deny Photos access for this app in Settings first; the
        picker must still open.
      </Text>

      <TestButton
        title="openPicker: crop 500×500 + base64"
        onPress={() =>
          run(() =>
            ImageCropPicker.openPicker({
              width: 500,
              height: 500,
              cropping: true,
              includeBase64: true,
              cropperChooseText: 'Confirm',
              cropperCancelText: 'Cancel',
            })
          )
        }
      />
      <TestButton
        title="openPicker: avatar 240×240, circle, quality 0.8"
        onPress={() =>
          run(() =>
            ImageCropPicker.openPicker({
              width: 240,
              height: 240,
              cropping: true,
              cropperCircleOverlay: true,
              compressImageQuality: 0.8,
            })
          )
        }
      />
      <TestButton
        title="openPicker: wallpaper 480×800"
        onPress={() =>
          run(() =>
            ImageCropPicker.openPicker({
              width: 480,
              height: 800,
              cropping: true,
              includeBase64: true,
            })
          )
        }
      />
      <TestButton
        title="openPicker: no crop, max 1024"
        onPress={() =>
          run(() =>
            ImageCropPicker.openPicker({
              compressImageMaxWidth: 1024,
              compressImageMaxHeight: 1024,
            })
          )
        }
      />
      <TestButton
        title="openCropper: last result, 300×200"
        disabled={!image}
        onPress={() =>
          run(() =>
            ImageCropPicker.openCropper({
              path: image?.path ?? '',
              width: 300,
              height: 200,
            })
          )
        }
      />
      <TestButton
        title="openCropper: missing file"
        onPress={() =>
          run(() =>
            ImageCropPicker.openCropper({
              path: 'file:///does/not/exist.jpg',
              width: 100,
              height: 100,
            })
          )
        }
      />
      <TestButton title="clean()" onPress={() => run(ImageCropPicker.clean)} />

      {image ? (
        <View style={s.preview}>
          <Image
            source={{ uri: image.path }}
            style={[s.previewImage, { aspectRatio: image.width / image.height }]}
            resizeMode="contain"
          />
        </View>
      ) : null}

      <TestResult result={result} error={error} />
    </TestPageBase>
  );
}

const s = StyleSheet.create({
  hint: {
    fontSize: 13,
    color: '#636366',
    lineHeight: 18,
  },
  preview: {
    alignItems: 'center',
  },
  previewImage: {
    width: 240,
    backgroundColor: '#e5e5ea',
  },
});
