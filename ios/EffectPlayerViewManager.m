#import <React/RCTViewManager.h>
#import <BNBSdkApi/BNBSdkApi.h>

@interface EffectPlayerViewManager : RCTViewManager
@end

@implementation EffectPlayerViewManager

RCT_EXPORT_MODULE(EffectPlayerView)

- (UIView *)view
{
   EffectPlayerView* view = [EffectPlayerView new];
   view.contentMode = UIViewContentModeScaleAspectFill;
   return view;
}

@end
