#import <Foundation/Foundation.h>
#import <BanubaSdkSpec/BanubaSdkSpec.h>

#import <AVFoundation/AVFoundation.h>
#import <BNBSdkCore/BNBSdkCore.h>
#import <BNBSdkApi/BNBSdkApi-Swift.h>

NS_ASSUME_NONNULL_BEGIN

@interface BNBReactBanubaSdkManager : NativeBanubaSdkManagerSpecBase <
  NativeBanubaSdkManagerSpec, VideoRecorderDelegate>

@end

NS_ASSUME_NONNULL_END
