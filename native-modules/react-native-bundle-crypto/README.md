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

## Apple Gopenpgp framework

The Mac Catalyst slice in the vendored `Gopenpgp.xcframework` uses Gopenpgp
`v3.4.1`. It is a static `arm64-apple-ios15.5-macabi` build produced with Go
`1.26.2` and `golang.org/x/mobile`
`v0.0.0-20260821190718-4776eadac327`:

```sh
gomobile bind \
  -tags=mobile,ios \
  -target=ios,iossimulator,maccatalyst/arm64 \
  -iosversion=15.5 \
  -ldflags='-s -w' \
  -o Gopenpgp.xcframework \
  github.com/ProtonMail/gopenpgp/v3/crypto \
  github.com/ProtonMail/gopenpgp/v3/armor \
  github.com/ProtonMail/gopenpgp/v3/constants \
  github.com/ProtonMail/gopenpgp/v3/mime \
  github.com/ProtonMail/gopenpgp/v3/mobile \
  github.com/ProtonMail/gopenpgp/v3/profile
```

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
