#import "BNBReactBanubaSdkManager.h"


@implementation BNBReactBanubaSdkManager {
    BanubaSdkManager* banubaSdkManager;
    EffectPlayerConfiguration* configuration;
}

+ (NSString *)moduleName { 
  return @"BanubaSdkManager";
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:(
    const facebook::react::ObjCTurboModule::InitParams &)params { 
  return std::make_shared<facebook::react::NativeBanubaSdkManagerSpecJSI>(params);
}

- (void)closeCamera {
  [banubaSdkManager.input stopCamera];
}

- (void)deinitialize { 
  [banubaSdkManager stopEffectPlayer];
  [banubaSdkManager removeRenderTarget];
  [banubaSdkManager destroyEffectPlayer];
  [BanubaSdkManager deinitialize];
}

- (void)enableFlashlight:(BOOL)enabled {
  auto _ = [banubaSdkManager.input setTorchWithMode:
    enabled? AVCaptureTorchModeOn: AVCaptureTorchModeOff];
}

- (void)evalJs:(nonnull NSString *)script { 
  [[[banubaSdkManager effectManager] current] evalJs:script resultCallback: nil];
}

- (void)initialize:(nonnull NSArray *)resourcePaths
    clientToken:(nonnull NSString *)clientToken {
  banubaSdkManager = [BanubaSdkManager new];
  configuration = [EffectPlayerConfiguration new];

  auto  reactResourcePath = resourcePaths;
  reactResourcePath = [reactResourcePath arrayByAddingObject:
    [NSBundle.mainBundle.bundlePath stringByAppendingString: @"/bnb-resources"]];
  reactResourcePath = [reactResourcePath arrayByAddingObject:
    NSBundle.mainBundle.bundlePath]; // for "effects"
  [BanubaSdkManager
    initializeWithResourcePath:reactResourcePath
    clientTokenString:clientToken logLevel:BNBSeverityLevelInfo];
     
  [banubaSdkManager setupWithConfiguration: configuration];
}

- (void)loadEffect:(nonnull NSString *)path { 
  [banubaSdkManager startEffectPlayer];
  if (banubaSdkManager.renderTarget == nil) {
      [banubaSdkManager setRenderTargetWithLayer: [CAMetalLayer new]
        playerConfiguration: configuration];
  }
}

- (void)openCamera { 
   [banubaSdkManager.input startCamera];
}

- (void)pauseVideoRecording { 
  [banubaSdkManager.output pauseRecording];
}

- (void)processImage:(nonnull NSString *)path {
  NSURL* sourceUrl;
  
  if(!(sourceUrl = [NSURL URLWithString:path])) {
    [self emitProcessImageEvent:@"Error while getting image from the path"];
    return;
  }
  
  NSData* imageData;
  if (!(imageData = [[NSData alloc] initWithContentsOfURL: sourceUrl])) {
     [self emitProcessImageEvent:@"Error while processing image"];
     return;
  }
  
  const auto image = [UIImage imageWithData:imageData];

  [banubaSdkManager
      startEditingImage: image
      recognizerIterations: nil
      imageOrientation: BNBCameraOrientationDeg0
      requireMirroring: false
      faceOrientation: 0
      fieldOfView: 60
      resetEffect: false
      completion: ^(NSInteger, CGRect) {
    [self->banubaSdkManager
        captureEditedImageWithImageOrientation: BNBCameraOrientationDeg0
        resetEffect: false
        completion: ^(UIImage* _Nullable resultImage) {

      if (resultImage) {
          auto manager = NSFileManager.defaultManager;
          auto photoFileName = @"processed_image.png";
          auto destinationUrl = [manager.temporaryDirectory
            URLByAppendingPathComponent:photoFileName];
          if ([manager fileExistsAtPath: destinationUrl.path]) {
              [manager removeItemAtURL:destinationUrl error:nil];
          }
          NSError* e = nil;
          if ([UIImagePNGRepresentation(resultImage)
            writeToURL:destinationUrl options:0 error:&e]) {
            [self emitProcessImageEvent:destinationUrl.path];
          } else {
             [self emitProcessImageEvent:e.localizedDescription];
          }
      } else {
         [self emitProcessImageEvent:@"Unable to apply effect to image"];
      }
      [self->banubaSdkManager stopEditingImageWithStartCameraInput:false];
    }];
  }];
  

}

- (void)reloadConfig:(nonnull NSString *)script { 
  [banubaSdkManager.effectManager reloadConfig: script];
}

- (void)resumeVideoRecoding { 
  [banubaSdkManager.output resumeRecording];
}

- (void)setCameraFacing:(BOOL)front {
  auto cameraSessionType = front ?
    CameraSessionTypeFrontCameraSession : CameraSessionTypeFrontCameraSession;
  [banubaSdkManager.input
    switchCameraTo: cameraSessionType completion: ^(void){}];
}

- (void)setCameraZoom:(double)factor { 
  auto _ = [banubaSdkManager.input setZoomFactor: factor];
}

- (void)startPlayer { 
  [banubaSdkManager startEffectPlayer];
  if (banubaSdkManager.renderTarget == nil) {
    [banubaSdkManager setRenderTargetWithLayer: [CAMetalLayer new]
      playerConfiguration: configuration];
  }
}

- (void)startVideoRecording:(nonnull NSString *)path
    mirrorFrontCamera:(BOOL)mirrorFrontCamera {
  auto outputConfig = [[OutputConfiguration alloc]
    initWithApplyWatermark: true
    adjustDeviceOrientation:false
    mirrorFrontCamera:mirrorFrontCamera];
  
  [banubaSdkManager.output startRecordingWithURL:
      [[NSURL alloc] initFileURLWithPath:path]
      configuration: outputConfig
      progressTimeInterval: 0
      delegate: self
  ];
  [banubaSdkManager.input startAudioCapturing];
}

- (void)stopPlayer { 
  [banubaSdkManager stopEffectPlayer];
}

- (void)stopVideoRecording { 
  [banubaSdkManager.output stopRecording];
}

- (void)takeScreenshot:(nonnull NSString *)path { 
  [banubaSdkManager.output takeSnapshotWithHandler:^(UIImage* _Nullable image) {
      auto success = false;
      if (image) {
          NSData* data;
          if ([path  hasSuffix:@".jpeg"] || [path hasSuffix: @".jpg"]) {
              data = UIImageJPEGRepresentation(image, 0.7);
          } else {
              data = UIImagePNGRepresentation(image);
          }
          if (data) {
            NSError* e = NULL;
            if(![data writeToURL:[NSURL fileURLWithPath:path] options:0 error:&e]) {
              NSLog(@"Error writing screenshot: %@", e.localizedDescription);
            } else {
              success = true;
            }
          }
        
      }
      [self emitOnScreenshotReady:success];
  }];
}

- (void)onRecorderStateChanged:(enum VideoRecordingState)state { 
  NSLog(@"onRecorderStateChanged(%ld)", long(state));

  bool status;
  switch (state) {
      case VideoRecordingStateRecording:
      case VideoRecordingStateProcessing:
          status = true;
      break;
      case VideoRecordingStateStopped:
      case VideoRecordingStatePaused:
          status = false;
      break;
      default:
          throw std::logic_error("Unreachable branch");
  }

  [self emitOnVideoRecordingStatus: status];
}

- (void)onRecordingFinishedWithSuccess:(BOOL)success error:(NSError * _Nullable)error { 
  NSLog(@"onRecordingFinished(success: %d, error: %@)",
    success, error ? error.localizedDescription : @"nil");

  [self emitOnVideoRecordingFinished: success];
  [banubaSdkManager.input stopAudioCapturing];
}

- (void)onRecordingProgressWithDuration:(NSTimeInterval)duration {
}

@end
