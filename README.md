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

### Android

* Android OS 6.0
* API level 23
* OpenGL ES3.0 and higher

### IOS

* IOS 13.0

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

1. Clone the repository, ensure that you have setted up [React Native CLI development environment](https://reactnative.dev/docs/environment-setup).
2. Copy and Paste your Client Token into appropriate section of [example/src/App.tsx](example/src/App.tsx#L18).
3. Run `yarn` command. This will install all required dependences.
4. (iOS only) Go to `example/ios` directory and run `pod install`. Return back to root. This will install all required iOS dependencies.
5. Connect a device and run `yarn example ios` or `yarn example android`. Alternatively you may may open XCode or Android Studio projects from `example/ios` and `example/android` directories.

You may also use `npm` to run the sample. See [example/README.md](example/README.md).

### Useful Docs

* List of [Banuba SDK packages](https://docs.banuba.com/far-sdk/tutorials/development/installation)
