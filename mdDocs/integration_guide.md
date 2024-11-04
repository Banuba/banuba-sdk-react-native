# Integration Guide

## Configuration

### Android

1. Define Banuba SDK version in the android [build.gradle](./example/android/build.gradle#L9):

```groovy
    buildscript {
        ext {
            ...
            bnb_sdk_version = '1.16.+'
        }
        ...
    }
```

2. Add maven repository to android [build.gradle](./example/android/build.gradle#L14):

```groovy
    maven {
        name "GitHubPackages"
        url "https://maven.pkg.github.com/sdk-banuba/banuba-sdk-android"
        credentials {
            username = "sdk-banuba"
            password = "\u0067\u0068\u0070\u005f\u004a\u0067\u0044\u0052\u0079\u0049\u0032\u006d\u0032\u004e\u0055\u0059\u006f\u0033\u0033\u006b\u0072\u0034\u0049\u0069\u0039\u0049\u006f\u006d\u0077\u0034\u0052\u0057\u0043\u0064\u0030\u0052\u0078\u006d\u0045\u0069"
        }
    }
```

### IOS

1. Add the source and version of the Banuba SDK in the IOS [Podfile](./example/ios/Podfile#L14-15):

```
source 'https://github.com/sdk-banuba/banuba-sdk-podspecs.git'
$bnb_sdk_version = '~> 1.16.0'
```

2. Add NSCameraUsageDescription in the [Info.plist](example/ios/ReactNativeExample/Info.plist#L34):

```
 <key>NSCameraUsageDescription</key>
 <string>We use camera to render AR effects</string>
```

### Usage

1. Init `BanubaSdkManager`:

```typescript
BanubaSdkManager.initialize([], 'Client token');
```

2. Attach `EffectPlayerView` to `BanubaSdkManager`:

```typescript
    constructor(props: {} | Readonly<{}>) {
        super(props);
        ...
        this.ep = React.createRef<typeof EffectPlayerView>();

        this.eventEmitter = new NativeEventEmitter(BanubaSdkManager);
        this.eventEmitter.addListener('onVideoRecordingStatus', (started) => {
            console.log('onVideoRecordingStatus', started);
        });
        this.eventEmitter.addListener('onVideoRecordingFinished', (success) => {
            console.log('onVideoRecordingFinished', success);
        });
        this.eventEmitter.addListener('onScreenshotReady', (success) => {
            console.log('onScreenshotReady', success);
        });
    }

    ...

componentDidMount() {
    BanubaSdkManager.attachView(this.ep.current._nativeTag);
}

    ...

    render(): React.ReactNode {
        return (
            <EffectPlayerView  ref={this.ep} />
        );
    }
```

3. Start player: 

```typescript
    BanubaSdkManager.openCamera();
    BanubaSdkManager.startPlayer();
```

4. Load and apply Effect: 

```typescript
    BanubaSdkManager.loadEffect('path to the effect');
```

### Add AR effects

[Banuba Face AR SDK](https://www.banuba.com/facear-sdk/face-filters) product is used on camera for applying various AR effects while making a content:

1. Android - Add [the folder with your effects](./example/effects/) to your project and setup it in the android [build.gradle](example/android/app/build.gradle#L137) app module:

```groovy
    task copyEffects {
        copy {
            from '../../effects'
            into 'src/main/assets/bnb-resources/effects'
        }
    }
```

2. IOS - just link effects folder into XCode project (`File` -> `Add Files to ...`).

### Additional methods

* Change camera facing:

```typescript
    setCameraFacing(argFront: boolean): void;
```

* Enables flashlight:

> [!NOTE]
> Available only for back camera facing

```typescript
    enableFlashlight(argEnabled: boolean): void;
```

* [Evaluate JS](https://docs.banuba.com/far-sdk/effects/makeup_deprecated/face_beauty) code in context of the current effect:

```typescript
    // Script Example:
    const script = `Background = require('bnb_js/background')`

    evalJs(script: string): void;
```

* Reload current effect config from the string provided:

```typescript
    const script = `
        {
            "camera" : {},
                "background" : {
                // ...
            }
        }
    `

    reloadConfig(script: string): void;
```

* Start screen frames capture.
`path` file to place the recording (will be in MP4 format).
`mirrorFrontCamera` if to mirror video in final file for front camera. This parameter affects iOS only. `false` is recommended to much Android behaviour. `true` will behave similar to default iOS Camera app.

```typescript
    startVideoRecording(path: string, mirrorFrontCamera: boolean): void;
```

* Stop screen frames capture. You will recieve `onVideoRecordingFinished` native event once ready.

```typescript
    stopVideoRecording(): void;
```

* Pause screen recording:

```typescript
    pauseVideoRecording(): void;
```

* Resume screen recording after it was paused:

```typescript
    resumeVideoRecoding(): void;
```

* Capture one screen frame. When ready, you will recive `onScreenshotReady` native event. See sample code. @param path Where to store the screenshot. Output format is defined by path extension (`.jpeg` or .`png`):

```typescript
    takeScreenshot(path: string): void;
```