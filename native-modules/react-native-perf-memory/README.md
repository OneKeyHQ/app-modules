# react-native-perf-memory

react-native-perf-memory

## Installation

```sh
npm install react-native-perf-memory react-native-nitro-modules

> `react-native-nitro-modules` is required as this library relies on [Nitro Modules](https://nitro.margelo.com/).
```

## Usage

```js
import { ReactNativePerfMemory } from 'react-native-perf-memory';

// ...

const result = await ReactNativePerfMemory.hello({ message: 'World' });
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
