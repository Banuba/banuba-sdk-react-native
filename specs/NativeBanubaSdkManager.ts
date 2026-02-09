import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';
import type {EventEmitter} from 'react-native/Libraries/Types/CodegenTypes';


export interface Spec extends TurboModule {
  /**
   * SDK initialization.
   * @param resourcePaths extra path to find effects
   * @param clientToken
   */
  initialize(resourcePaths: string[], clientToken: string): void;

  deinitialize(): void;

  // TODO
  //attachView(tag: number): void;

  openCamera(): void;

  closeCamera(): void;

  /**
   * Change camera facing.
   */
  setCameraFacing(argFront: boolean): void;

  /**
   * Set camera zoom.
   * `factor` a multiplier. For example, a value of 2.0 doubles the size of an
   * image. Value 1.0 (minimum alowed) means "no zoom".
   */
  setCameraZoom(factor: number): void;

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
   * Reload current effect config from the string provided.
   */
  reloadConfig(script: string): void;

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

  /**
   * Processes image with applied effect
   */
  processImage(path: string): void;

  readonly onVideoRecordingStatus: EventEmitter<boolean>;
  readonly onVideoRecordingFinished: EventEmitter<boolean>;
  readonly onScreenshotReady: EventEmitter<boolean>;
  readonly processImageEvent: EventEmitter<string>;

}

export default TurboModuleRegistry.getEnforcing<Spec>(
  'BanubaSdkManager',
);