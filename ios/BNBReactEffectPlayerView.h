#import <React/RCTViewComponentView.h>
#import <UIKit/UIKit.h>

#import <AVFoundation/AVFoundation.h>
#import <BNBSdkCore/BNBSdkCore.h>
#import <BNBSdkApi/BNBSdkApi-Swift.h>

NS_ASSUME_NONNULL_BEGIN

@interface BNBReactEffectPlayerView : RCTViewComponentView

+(EffectPlayerView* _Nullable) playerView;

@end

NS_ASSUME_NONNULL_END
