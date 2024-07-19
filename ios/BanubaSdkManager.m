#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(BanubaSdkManager, RCTEventEmitter)

RCT_EXTERN_METHOD(initialize:(NSArray<NSString*>*) resourcePath clientTokenString:(NSString*))
RCT_EXTERN_METHOD(deinitialize)
RCT_EXTERN_METHOD(openCamera)
RCT_EXTERN_METHOD(closeCamera)
RCT_EXTERN_METHOD(setCameraFacing:(BOOL)) 
RCT_EXTERN_METHOD(enableFlashlight:(BOOL))
RCT_EXTERN_METHOD(attachView:(nonnull NSNumber*))
RCT_EXTERN_METHOD(loadEffect:(NSString*))
RCT_EXTERN_METHOD(startPlayer)
RCT_EXTERN_METHOD(stopPlayer)
RCT_EXTERN_METHOD(evalJs:(NSString*))

RCT_EXTERN_METHOD(startVideoRecording:(NSString*) path mirrorFrontCamera:(BOOL))
RCT_EXTERN_METHOD(stopVideoRecording)
RCT_EXTERN_METHOD(pauseVideoRecording)
RCT_EXTERN_METHOD(resumeVideoRecording)

RCT_EXTERN_METHOD(takeScreenshot:(NSString*))

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
