import {
  requireNativeComponent,
  Platform,
  type ViewStyle,
  NativeModules,
  UIManager,
  type NativeModule,
} from 'react-native';

interface BanubaSdkManagerI {
  /**
   * SDK initialization.
   * @param resourcePaths extra path to find effects
   * @param clientToken
   */
  initialize(resourcePaths: string[], clientToken: string): void;

  deinitialize(): void;

  attachView(tag: number): void;

  openCamera(): void;

  closeCamera(): void;

  /**
   * Change camera facing.
   */
  setCameraFacing(argFront: boolean): void;

  /**
   * Enables flashlight. Available only for back camera facing.
   */
  enableFlashlight(argEnabled: boolean): void;

  startPlayer(): void;

  stopPlayer(): void;

  loadEffect(path: string): void;

  /**
   * Evaluate JS code in context of the current effect.
   * @param script code to evalute
   */
  evalJs(script: string): void;

  /**
   * Start screen frames capture.
   * @param path file to place the recording (will be in MP4 format)
   * @param mirrorFrontCamera if to mirror video in final file for front camera.
   * This parameter affects iOS only. `false` is recommended to much Android
   * behaviour. `true` will behave similar to default iOS Camera app.
   * @see stopVideoRecording, pauseVideoRecording
   */
  startVideoRecording(path: string, mirrorFrontCamera: boolean): void;

  /**
   * Stop screen frames capture. You will recieve
   * `onVideoRecordingFinished` native event once ready.
   * See sample code.
   * @see startVideoRecording
   */
  stopVideoRecording(): void;

  /**
   * Pause screen recording.
   * @see resumeVideoRecoding, startVideoRecording
   */
  pauseVideoRecording(): void;

  /**
   * Resume screen recording after it was paused.
   * @see pauseVideoRecording, startVideoRecording
   */
  resumeVideoRecoding(): void;

  /**
   * Capture one screen frame. When ready, you will recive
   * `onScreenshotReady` native event. See sample code.
   * @param path Where to store the screenshot. Output
   * format is defined by path extension (`.jpeg` or .`png`)
   */
  takeScreenshot(path: string): void;
}

const LINKING_ERROR =
  `The package 'banuba-sdk-react-native' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type EffectPlayerViewProps = {
  style: ViewStyle;
  ref: number;
};

const ComponentName = 'EffectPlayerView';

export const EffectPlayerView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<EffectPlayerViewProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

const BanubaSdkManager = NativeModules.BanubaSdkManager
  ? NativeModules.BanubaSdkManager
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export default BanubaSdkManager as BanubaSdkManagerI & NativeModule;
