/**
 * @jest-environment jsdom
 */
import ImageCropPicker, { ImageCropPickerError } from '../index.web';

function lastFileInput(): HTMLInputElement {
  const input = document.querySelector<HTMLInputElement>('input[type=file]');
  if (!input) {
    throw new Error('No file input');
  }
  return input;
}

beforeEach(() => {
  jest.spyOn(HTMLInputElement.prototype, 'click').mockImplementation(() => {});
});

afterEach(() => {
  jest.restoreAllMocks();
  document.body.innerHTML = '';
});

it('rejects E_PICKER_CANCELLED when the file chooser is dismissed', async () => {
  const result = ImageCropPicker.openPicker({ cropping: true });
  const input = lastFileInput();
  expect(input.accept).toBe('image/*');
  input.dispatchEvent(new Event('cancel'));
  await expect(result).rejects.toMatchObject({
    code: 'E_PICKER_CANCELLED',
    message: 'User cancelled image selection',
  });
  expect(document.querySelector('input[type=file]')).toBeNull();
});

it('allows one picker at a time', async () => {
  const first = ImageCropPicker.openPicker();
  await expect(ImageCropPicker.openPicker()).rejects.toMatchObject({
    code: 'E_PICKER_IN_PROGRESS',
  });
  lastFileInput().dispatchEvent(new Event('cancel'));
  await expect(first).rejects.toBeInstanceOf(ImageCropPickerError);
  // The lock is released after the first call settles.
  const next = ImageCropPicker.openPicker();
  lastFileInput().dispatchEvent(new Event('cancel'));
  await expect(next).rejects.toMatchObject({ code: 'E_PICKER_CANCELLED' });
});

it('rejects files over the 64 MiB input limit', async () => {
  const result = ImageCropPicker.openPicker({ cropping: true });
  const input = lastFileInput();
  const file = new File(['x'], 'huge.jpg', { type: 'image/jpeg' });
  Object.defineProperty(file, 'size', { value: 64 * 1024 * 1024 + 1 });
  Object.defineProperty(input, 'files', { value: [file] });
  input.dispatchEvent(new Event('change'));
  await expect(result).rejects.toMatchObject({
    code: 'E_NO_IMAGE_DATA_FOUND',
  });
});

it('rejects E_CROPPER_IMAGE_NOT_FOUND without a path', async () => {
  await expect(
    ImageCropPicker.openCropper({ path: '', width: 100, height: 100 })
  ).rejects.toMatchObject({ code: 'E_CROPPER_IMAGE_NOT_FOUND' });
});

it('cleanSingle only revokes URLs it created', async () => {
  const revoke = jest.fn();
  URL.revokeObjectURL = revoke;
  await ImageCropPicker.cleanSingle('blob:not-ours');
  await ImageCropPicker.clean();
  expect(revoke).not.toHaveBeenCalled();
});
