import * as React from 'react';
import BanubaSdkManager, { EffectPlayerView } from '@banuba/react-native';
import { NativeEventEmitter } from 'react-native';

export default class App extends React.Component {
  ep: any;
  eventEmitter: NativeEventEmitter;

  constructor(props: {} | Readonly<{}>) {
    super(props);
    BanubaSdkManager.initialize([], 'Client token');
    this.ep = React.createRef<typeof EffectPlayerView>();

    this.eventEmitter = new NativeEventEmitter(BanubaSdkManager);
    this.eventEmitter.addListener('onVideoRecordingStatus', (started) => {
      console.log('onVideoRecordingStatus', started);
    });
    this.eventEmitter.addListener('onVideoRecordingFinished', (duration) => {
      console.log('onVideoRecordingFinished', duration);
    });
  }

  render(): React.ReactNode {
    // eslint-disable-next-line react-native/no-inline-styles
    return <EffectPlayerView style={{ flex: 1 }} ref={this.ep} />;
  }

  componentDidMount(): void {
    BanubaSdkManager.attachView(this.ep.current._nativeTag);
    BanubaSdkManager.openCamera();
    BanubaSdkManager.startPlayer();
    BanubaSdkManager.loadEffect('effects/TrollGrandma');
  }

  componentWillUnmount(): void {
    BanubaSdkManager.stopPlayer();
  }
}
