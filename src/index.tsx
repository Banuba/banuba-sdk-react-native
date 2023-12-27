import {
  requireNativeComponent,
  Platform,
  type ViewStyle,
  NativeModules,
  UIManager,
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

  startPlayer(): void;

  stopPlayer(): void;

  loadEffect(path: string): void;

  /**
   * Evaluate JS code in context of the current effect.
   * @param script code to evalute
   */
  evalJs(script: string): void;
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

export default BanubaSdkManager as BanubaSdkManagerI;
