#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(BanubaSdkManager, NSObject)

RCT_EXTERN_METHOD(initialize:(NSArray<NSString*>*) resourcePath clientTokenString:(NSString*))
RCT_EXTERN_METHOD(deinitialize)
RCT_EXTERN_METHOD(openCamera)
RCT_EXTERN_METHOD(closeCamera)
RCT_EXTERN_METHOD(attachView:(nonnull NSNumber*))
RCT_EXTERN_METHOD(loadEffect:(NSString*))
RCT_EXTERN_METHOD(startPlayer)
RCT_EXTERN_METHOD(stopPlayer)
RCT_EXTERN_METHOD(evalJs:(NSString*))

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
