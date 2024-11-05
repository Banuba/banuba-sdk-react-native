# Banuba React Native Plugin

## Overview

[Banuba Face AR Plugin](https://www.banuba.com/facear-sdk/face-filters) is intended to provide a suite of augmented reality features applicable on photos and live video feed (live streaming, video conferencing, etc.). The package includes:

* Face and hand tracking
* 3D face filters
* Virtual try-on of jewelry, cosmetics, headwear, glasses, contacts, and more
* Color filters (LUTs)
* Face touch-up
* Virtual backgrounds
* Screen recording, take screenshot
* etc.

## Requirements

* Android: API level 23(Android 6) or higher
* IOS: 13.0 or higher

## Usage

### License

Before you commit to a license, you are free to test all the features of the SDK for free. The trial period lasts 14 days. Send us a message to start the [Face AR trial](https://www.banuba.com/facear-sdk/face-filters#form).

Feel free to [contact us](https://www.banuba.com/support) if you have any questions.

### Installation

Run in Terminal to install Banuba Face AR React Native plugin:

```
npm install @banuba/react-native
```

or

```
yarn add @banuba/react-native
```

### Integration guide

Please follow our [Integration Guide](./mdDocs/integration_guide.md) to complete full integration.

### Launch

1. Set up [React Native CLI development environment](https://reactnative.dev/docs/environment-setup).
2. Clone the repository.
3. Copy and Paste your Client Token into appropriate section of [example/src/App.tsx](example/src/App.tsx#L18).
4. Run `yarn` command in the root repository. This will install all required dependencies.

#### IOS Specific

* Go to `example/ios` directory and run `pod install`.This will install all required iOS dependencies.
* Return back to root.

#### Run the Example

* Connect a device and run `yarn example ios` or `yarn example android`.
* Alternatively, open XCode or Android Studio projects from the respective directories.

You may also use `npm` to run the sample. See [example/README.md](example/README.md).

### Useful Docs

* List of [Banuba SDK packages](https://docs.banuba.com/far-sdk/tutorials/development/installation)

### Dependencies

||Version|
|:-:|:-:|
|Yarn|3.6.1|
|React Native|0.75.4|
|Android|6.0+|
|IOS|13.0|
