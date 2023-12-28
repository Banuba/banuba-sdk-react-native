/* eslint-disable react-native/no-inline-styles */
import BanubaSdkManager, { EffectPlayerView } from '@banuba/react-native';
import React from 'react';
import { Component } from 'react';
import { Button, NativeEventEmitter, View } from 'react-native';
import * as RNFS from 'react-native-fs';

export default class App extends Component {
  ep: any;
  eventEmitter: NativeEventEmitter;
  recording = false;

  constructor(props: {} | Readonly<{}>) {
    super(props);
    BanubaSdkManager.initialize([], 'Client token');
    this.ep = React.createRef<typeof EffectPlayerView>();

    this.eventEmitter = new NativeEventEmitter(BanubaSdkManager);
    this.eventEmitter.addListener('onVideoRecordingStatus', (started) => {
      console.log('onVideoRecordingStatus', started);
    });
    this.eventEmitter.addListener('onVideoRecordingFinished', (success) => {
      console.log('onVideoRecordingFinished', success);
    });
  }

  render(): React.ReactNode {
    return (
      <View style={{ flex: 1 }}>
        <EffectPlayerView style={{ flex: 1 }} ref={this.ep} />
        <View>
          <Button onPress={this.onPressVideoRecording} title="Tap to record" />
          <Button
            onPress={this.onPressTakeScreenshot}
            title="Take screenshot"
          />
        </View>
      </View>
    );
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

  onPressVideoRecording(): void {
    if (!this.recording) {
      BanubaSdkManager.startVideoRecording(
        RNFS.DocumentDirectoryPath + '/video.mp4'
      );
    } else {
      BanubaSdkManager.stopVideoRecording();
    }
    this.recording = !this.recording;
  }

  onPressTakeScreenshot(): void {}
}
