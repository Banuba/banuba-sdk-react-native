import BNBSdkApi

let recordingStatusEvent = "onVideoRecordingStatus"
let recordingFinishedEvent = "onVideoRecordingFinished"

@objc(BanubaSdkManager)
class BanubaSdkManager: RCTEventEmitter {

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
    func startVideoRecording(_ path: String) {
        banubaSdkManager.output?.startRecordingWithURL(URL(fileURLWithPath: path), delegate: self)
    }
    @objc
    func stopVideoRecording() {
        banubaSdkManager.output?.stopRecording()
    }
    @objc
    func pauseVideoRecording() {
        banubaSdkManager.output?.pauseRecording()
    }
    @objc
    func resumeVideoRecording() {
        banubaSdkManager.output?.resumeRecording()
    }
    
    override func startObserving() {
        hasListeners = true
    }
    
    override func stopObserving() {
        hasListeners = false
    }
    
    override func supportedEvents() -> [String]! {
        return [recordingStatusEvent, recordingFinishedEvent]
    }
    
    private var hasListeners = false
    
    private var banubaSdkManager = BNBSdkApi.BanubaSdkManager()
}

extension BanubaSdkManager : VideoRecorderDelegate
{
    func onRecorderStateChanged(_ state: BNBSdkApi.VideoRecordingState) {
        print("onRecorderStateChanged(\(state))")
        if hasListeners {
            let status: Bool
            switch state {
                case .recording, .processing:
                    status = true
                case .stopped, .paused:
                    status = false
                @unknown default:
                    fatalError()
            }
            
            self.sendEvent(withName: recordingStatusEvent, body: status)
        }
    }
    
    func onRecordingFinished(success: Bool, error: (Error)?) {
        print("onRecordingFinished(success: \(success), error: \(error?.localizedDescription ?? "nil"))")
        if hasListeners {
            self.sendEvent(withName: recordingFinishedEvent, body: success)
        }
    }
    
    func onRecordingProgress(duration: TimeInterval) {
    }
}
