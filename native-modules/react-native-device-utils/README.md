# react-native-device-utils

react-native-device-utils

## Installation

```sh
npm install react-native-device-utils react-native-nitro-modules

> `react-native-nitro-modules` is required as this library relies on [Nitro Modules](https://nitro.margelo.com/).
```

## Usage

```js
import { ReactNativeDeviceUtils } from 'react-native-device-utils';

// ...

const result = await ReactNativeDeviceUtils.hello({ message: 'World' });
console.log(result); // { success: true, data: 'Hello, World!' }
```

## Contributing

- [Development workflow](CONTRIBUTING.md#development-workflow)
- [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
- [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
