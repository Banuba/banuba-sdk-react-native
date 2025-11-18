/* eslint-disable react-native/no-inline-styles */
import React, {useEffect, useMemo, useRef, useState} from 'react';
import {Button, NativeEventEmitter, Text, View} from 'react-native';
import BanubaSdkManager, {EffectPlayerView} from '@banuba/react-native';
import * as RNFS from 'react-native-fs';

// Optional: keep the token out of the component body
const BANUBA_TOKEN = 'Client token';
const EFFECT_PATH = 'effects/TrollGrandma';

export default function App() {
  // You can refine this type if you need strict typing:
  // const ep = useRef<React.ElementRef<typeof EffectPlayerView> | null>(null);
  const ep = useRef<any>(null);

  const [recording, setRecording] = useState(false);
  const recordButtonTitle = recording ? 'Stop recording' : 'Tap to record';

  // Create one emitter instance
  const eventEmitter = useMemo(
    () => new NativeEventEmitter(BanubaSdkManager),
    [],
  );

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
    const subStatus = eventEmitter.addListener(
      'onVideoRecordingStatus',
      started => {
        console.log('onVideoRecordingStatus', started);
      },
    );
    const subFinished = eventEmitter.addListener(
      'onVideoRecordingFinished',
      success => {
        console.log('onVideoRecordingFinished', success);
      },
    );
    const subScreenshot = eventEmitter.addListener(
      'onScreenshotReady',
      payload => {
        console.log('onScreenshotReady', payload);
      },
    );

    return () => {
      subStatus.remove();
      subFinished.remove();
      subScreenshot.remove();
    };
  }, [eventEmitter]);

  // 3) Attach view and start player once the native view is mounted
  useEffect(() => {
    // In RN, the ref gets populated after the first render
    const nativeTag = ep.current?._nativeTag;
    console.log('🚀 ~ BanubaScreen ~ nativeTag:', ep);

    if (nativeTag) {
      BanubaSdkManager.attachView(nativeTag);
      BanubaSdkManager.openCamera();
      BanubaSdkManager.startPlayer();
      BanubaSdkManager.loadEffect(EFFECT_PATH);
    }
    // Cleanup player on unmount
    return () => {
      BanubaSdkManager.stopPlayer();
    };
  }, [ep.current?._nativeTag]); // empty deps => run once after mount

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
      <EffectPlayerView style={{flex: 1}} ref={ep} />
      <View style={{marginTop: 0, marginBottom: 50}}>
        <Button onPress={onPressVideoRecording} title={recordButtonTitle} />
      </View>
    </View>
  );
}
