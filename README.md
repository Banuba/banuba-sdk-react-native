[React Native Face AR SDK](https://www.banuba.com/facear-sdk/face-filters) is 
a version of Banuba Face AR SDK. It is intended to provide a suite of augmented 
reality features applicable on photos and live video feed (live streaming, 
video conferencing, etc.). The package includes:

* Face and hand tracking
* 3D face filters
* Virtual try-on of jewelry, cosmetics, headwear, glasses, contacts, and more
* Color filters (LUTs)
* Face touch-up
* Virtual backgrounds
* Screen recording, take screenshot
* etc.

## [Requirements](https://docs.banuba.com/face-ar-sdk-v1/overview/system_requirements)

## Usage

### Token
Before you commit to a license, you are free to test all the features of the SDK for free. To start it, [send us a message](https://www.banuba.com/facear-sdk/face-filters#form).  


Feel free to [contact us](https://docs.banuba.com/face-ar-sdk-v1/support) if you have any questions.

### Getting Started

0. Clone the repository, ensure that you have setted up [React Native CLI development environment](https://reactnative.dev/docs/environment-setup).
1. Copy and Paste your Client Token into appropriate section of [`example/src/App.tsx`](example/src/App.tsx#L18).
2. Run `yarn` command. This will install all required dependences.
3. (iOS only) Go to `example/ios` directory and run `pod install`. Return back to root. This will install all required iOS dependencies.
4. Connect a device and run `yarn example ios` or `yarn example android`. Alternatively you may may open XCode or Android Studio projects from
`example/ios` and `example/android` directories.

You may also use `npm` to run the sample. See [example/README.md](example/README.md).

### Integration steps

This is how to integrate Banuba SDK into existing React Native app. You still need a Client Token for this.

1. Add `@banuba/react-native` [dependency](https://www.npmjs.com/package/@banuba/react-native): `yarn add @banuba/react-native`.
2. *For iOS*: add a link to the native Banuba SDK into [`ios/Podfile`](example/ios/Podfile#L13): `source 'https://github.com/sdk-banuba/banuba-sdk-podspecs.git'`,
add desired `$bnb_sdk_version = '~> 1.13.0`, list Banuba SDK [packages you need](https://docs.banuba.com/face-ar-sdk-v1/core/tutorials/using_packages/).
*For Android*: add our [maven repository](example/android/build.gradle#L13), define `ext.bnb_sdk_version`. 
In [`example/android/app/build.gradle`](example/android/app/build.gradle) list [the packages you need](https://docs.banuba.com/face-ar-sdk-v1/core/tutorials/using_packages/).
3. Add code from [`example/src/App.tsx`](example/src/App.tsx) into your app.
4. Add `effects` folder into your project. Link it with your app
    1. iOS: just link effects folder into XCode project (`File` -> `Add Files to ...`).
    2. Android: add [the following](example/android/app/build.gradle#L132) code into app `build.gradle`. 
5. *For iOS*: add [`NSCameraUsageDescription`](example/ios/ReactNativeExample/Info.plist#L34).

### Docs
Refer [this file](src/index.tsx) about availabl API. You can find more info about Banuba [Face AR SDK here](https://docs.banuba.com/). 

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
