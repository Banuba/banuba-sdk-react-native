/* eslint-disable react-native/no-inline-styles */
import React, {useEffect, useRef, useState} from 'react';
import {Button, type EventSubscription, View} from 'react-native';
import BanubaSdkManager, {EffectPlayerView} from '@banuba/react-native';
import * as RNFS from 'react-native-fs';

// Optional: keep the token out of the component body
const BANUBA_TOKEN = 'Client token';
const EFFECT_PATH = 'effects/TrollGrandma';

export default function App() {
  const [recording, setRecording] = useState(false);
  const recordButtonTitle = recording ? 'Stop recording' : 'Tap to record';

  const listenerStatus = React.useRef<null | EventSubscription>(null);
  const listenerFinish = React.useRef<null | EventSubscription>(null);
  const listenerScreenshot  = React.useRef<null | EventSubscription>(null);

  // Guard to avoid re-initializing the SDK when Fast Refresh remounts the component
  const initializedRef = useRef(false);

  // 1) Initialize SDK once
  useEffect(() => {
    if (!initializedRef.current) {
      BanubaSdkManager.initialize([], BANUBA_TOKEN);
      initializedRef.current = true;
    }
  }, []);

  // 2) Subscribe to events once
  useEffect(() => {
    listenerStatus.current = BanubaSdkManager.onVideoRecordingStatus(
      started => {
        console.log('onVideoRecordingStatus', started);
      },
    );
    listenerFinish.current = BanubaSdkManager.onVideoRecordingFinished(
      success => {
        console.log('onVideoRecordingFinished', success);
      },
    );
    listenerScreenshot.current = BanubaSdkManager.onScreenshotReady(
      payload => {
        console.log('onScreenshotReady', payload);
      },
    );

    return () => {
      listenerStatus.current?.remove();
      listenerStatus.current = null;
      listenerFinish.current?.remove();
      listenerFinish.current = null;
      listenerScreenshot.current?.remove();
      listenerScreenshot.current = null;
    };
  }, []);

  // 3) Attach view and start player once the native view is mounted
  useEffect(() => {
    BanubaSdkManager.attachView();
    BanubaSdkManager.openCamera();
    BanubaSdkManager.startPlayer();
    BanubaSdkManager.loadEffect(EFFECT_PATH);
    
    // Cleanup player on unmount
    return () => {
      BanubaSdkManager.stopPlayer();
    };
  }, []); // empty deps => run once after mount

  const onPressVideoRecording = () => {
    if (!recording) {
      BanubaSdkManager.startVideoRecording(
        `${RNFS.DocumentDirectoryPath}/video.mp4`,
        false,
      );
      setRecording(true);
    } else {
      BanubaSdkManager.stopVideoRecording();
      setRecording(false);
    }
  };

  return (
    <View style={{flex: 1}}>
      <EffectPlayerView style={{flex: 1}} />
      <View style={{marginTop: 0, marginBottom: 50}}>
        <Button onPress={onPressVideoRecording} title={recordButtonTitle} />
      </View>
    </View>
  );
}
