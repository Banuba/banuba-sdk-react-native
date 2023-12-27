import BNBSdkApi

@objc(BanubaSdkManager)
class BanubaSdkManager: NSObject {
    @objc
    func initialize(_ resourcePath: [String], clientTokenString: String) {
        var reactResourcePath = resourcePath
        reactResourcePath.append(Bundle.main.bundlePath + "/bnb-resources")
        reactResourcePath.append(Bundle.main.bundlePath) // for "effects"
        BNBSdkApi.BanubaSdkManager.initialize(
            resourcePath: reactResourcePath,
            clientTokenString: clientTokenString
        )
    }
    
    @objc
    func deinitialize() {
        BNBSdkApi.BanubaSdkManager.deinitialize()
    }
    
    @objc
    func attachView(_ tag: NSNumber) {
        banubaSdkManager.setup(configuration: EffectPlayerConfiguration())
        
        var view: BNBSdkApi.EffectPlayerView? = nil
        RCTUnsafeExecuteOnMainQueueSync {
            let uiManager = self.bridge.module(for: RCTUIManager.self) as! RCTUIManager
            view = uiManager.view(forReactTag: tag) as? BNBSdkApi.EffectPlayerView
        }

        banubaSdkManager.setRenderTarget(
            view: view!,
            playerConfiguration: nil
        )
        
    }
    
    @objc
    func openCamera() {
        banubaSdkManager.input.startCamera()
    }
    
    @objc
    func closeCamera() {
        banubaSdkManager.input.stopCamera()
    }
    
    @objc 
    func loadEffect(_ path: String) {
        banubaSdkManager.loadEffect(path, synchronous: true)
    }
    
    @objc
    func startPlayer() {
        banubaSdkManager.startEffectPlayer()
    }
    
    @objc
    func stopPlayer() {
        banubaSdkManager.stopEffectPlayer()
    }
    
    @objc
    func evalJs(_ script: String) {
        banubaSdkManager.effectManager()?.current()?.evalJs(script, resultCallback: nil)
    }
    
    @objc
    var bridge: RCTBridge!
    
    private var banubaSdkManager = BNBSdkApi.BanubaSdkManager()
}
