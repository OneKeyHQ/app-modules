# react-native-bundle-crypto

react-native-bundle-crypto

## Installation

```sh
npm install @onekeyfe/react-native-bundle-crypto react-native-nitro-modules

> `react-native-nitro-modules` is required as this library relies on [Nitro Modules](https://nitro.margelo.com/).
```

## Usage

```ts
import { ReactNativeBundleCrypto } from '@onekeyfe/react-native-bundle-crypto';

// ...

const hash = await ReactNativeBundleCrypto.sha256OfFile('/absolute/path/bundle.zip');
if (hash.sha256) {
  console.log('sha256:', hash.sha256);
}

const isSame = ReactNativeBundleCrypto.secureEqualHex(
  '0123456789abcdef',
  '0123456789abcdef'
);
console.log(isSame);
```

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
