#import "BNBReactEffectPlayerView.h"

#import <react/renderer/components/BanubaSdkSpec/ComponentDescriptors.h>

using namespace facebook::react;

static EffectPlayerView* _view = nil;

@implementation BNBReactEffectPlayerView

-(instancetype)init
{
  if(self = [super init]) {
   _view = [EffectPlayerView new];
   _view.contentMode = UIViewContentModeScaleAspectFill;
   [self addSubview:_view];
  }
  return self;
}

-(void)layoutSubviews
{
  [super layoutSubviews];
  _view.frame = self.bounds;
}

+(EffectPlayerView* _Nullable) playerView {
  return _view;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<EffectPlayerViewComponentDescriptor>();
}

@end
