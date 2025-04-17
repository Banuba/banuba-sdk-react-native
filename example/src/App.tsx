/* eslint-disable react-native/no-inline-styles */
import BanubaSdkManager from '@banuba/react-native';
import React, { Component } from 'react';
import { Button, NativeEventEmitter, View, Image, StyleSheet } from 'react-native';
import * as RNFS from 'react-native-fs';
import { launchImageLibrary } from 'react-native-image-picker';

export default class App extends Component {
  eventEmitter = new NativeEventEmitter(BanubaSdkManager);

  state = {
    effectChosen: false,
    processedPhotoUri: null,
  };

  constructor(props) {
    super(props);
    BanubaSdkManager.initialize([], 'Client Token');

    this.eventEmitter.addListener('processImageEvent', (result) => {
      console.log('processImageEvent', result);
      if (typeof result === 'string' && result.length > 0) {
        const uri = result.startsWith('file://') ? result : `file://${result}`;
        this.setState({ processedPhotoUri: uri });
      }
    });
  }

  componentWillUnmount() {
    this.eventEmitter.removeAllListeners('processImageEvent');
  }

  onPressChooseEffect = () => {
    BanubaSdkManager.startPlayer();
    BanubaSdkManager.loadEffect('effects/TrollGrandma');
    this.setState({ effectChosen: true });
  };

  onPressChoosePhoto = () => {
    if (!this.state.effectChosen) return;
    const options = {
      mediaType: 'photo',
      quality: 1,
    };
    launchImageLibrary(options, (response) => {
      if (response.didCancel) {
        console.log('User cancelled image picker');
      } else if (response.errorCode) {
        console.log('ImagePicker Error: ', response.errorMessage);
      } else {
        if (response.assets && response.assets.length > 0) {
          const photo = response.assets[0];
          console.log('Selected photo: ', photo.uri);
          BanubaSdkManager.processImage(photo.uri);
        }
      }
    });
  };

  render() {
    const { processedPhotoUri, effectChosen } = this.state;
    return (
      <View style={{ flex: 1 }}>
        {processedPhotoUri ? (
          <Image
            source={{ uri: processedPhotoUri }}
            style={styles.fullScreenImage}
            resizeMode="contain"
          />
        ) : (
          <View style={styles.placeholder}>
            <Button title="No image processed yet" disabled={true} />
          </View>
        )}
        <View style={styles.buttonContainer}>
          <Button title="Choose Effect" onPress={this.onPressChooseEffect} />
          <View style={{ width: 16 }} />
          <Button
            title="Choose Photo"
            onPress={this.onPressChoosePhoto}
            disabled={!effectChosen}
          />
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    marginBottom: 32,
    paddingHorizontal: 16,
  },
  fullScreenImage: {
    flex: 1,
    width: '100%',
    height: '100%',
  },
  placeholder: {
    flex: 1,
    backgroundColor: '#fff',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
