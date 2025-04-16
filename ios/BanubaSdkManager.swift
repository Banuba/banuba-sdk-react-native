import BNBSdkApi
import AVFoundation

let recordingStatusEvent = "onVideoRecordingStatus"
let recordingFinishedEvent = "onVideoRecordingFinished"
let screenshotReadyEvent = "onScreenshotReady"
let processImageEvent = "processImageEvent"

@objc(BanubaSdkManager)
class BanubaSdkManager: RCTEventEmitter {

    private var banubaSdkManager: BNBSdkApi.BanubaSdkManager = BNBSdkApi.BanubaSdkManager()
    private let configuration: EffectPlayerConfiguration = .init()
    private var recordingVideoCompletion: ((Result<Void, Error>) -> Void)?

    @objc
    func initialize(_ resourcePath: [String], clientTokenString: String) {
        var reactResourcePath = resourcePath
        reactResourcePath.append(Bundle.main.bundlePath + "/bnb-resources")
        reactResourcePath.append(Bundle.main.bundlePath) // for "effects"
        BNBSdkApi.BanubaSdkManager.initialize(
            resourcePath: reactResourcePath,
            clientTokenString: clientTokenString
        )
        banubaSdkManager.setup(configuration: configuration)
    }

    deinit {
        deinitialize()
    }

    @objc
    func deinitialize() {
        banubaSdkManager.stopEffectPlayer()
        banubaSdkManager.removeRenderTarget()
        banubaSdkManager.destroyEffectPlayer()
        BNBSdkApi.BanubaSdkManager.deinitialize()
    }

    @objc
    func attachView(_ tag: NSNumber) {
        banubaSdkManager.destroyEffectPlayer()

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
    func setCameraFacing(_ front: Bool) {
        let cameraSessionType: CameraSessionType = front ? .FrontCameraSession : .BackCameraSession
        banubaSdkManager.input.switchCamera(to: cameraSessionType, completion: {})
    }

    @objc
    func setCameraZoom(_ factor: Float) {
        _ = banubaSdkManager.input.setZoomFactor(factor)
    }

    @objc
    func enableFlashlight(_ enabled: Bool) {
        _ = banubaSdkManager.input.setTorch(
            mode: enabled ? AVCaptureDevice.TorchMode.on : AVCaptureDevice.TorchMode.off)
    }

    @objc
    func loadEffect(_ path: String) {
        banubaSdkManager.loadEffect(path, synchronous: true)
    }

    @objc
    func startPlayer() {
        banubaSdkManager.startEffectPlayer()
        if banubaSdkManager.renderTarget == nil {
            banubaSdkManager.setRenderTarget(layer: CAMetalLayer(), playerConfiguration: configuration)
        }
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
    func reloadConfig(_ script: String) {
        banubaSdkManager.effectManager()?.reloadConfig(script)
    }

    @objc
    func startVideoRecording(_ path: String, mirrorFrontCamera: Bool) {
        let outputConfig = OutputConfiguration(
            applyWatermark: true,
            adjustDeviceOrientation: false,
            mirrorFrontCamera: mirrorFrontCamera
        )
        banubaSdkManager.output?.startRecordingWithURL(
            URL(fileURLWithPath: path),
            configuration: outputConfig,
            progressTimeInterval: 0,
            delegate: self
        )
        banubaSdkManager.input.startAudioCapturing()
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

    @objc
    func takeScreenshot(_ path: String) {
        banubaSdkManager.output?.takeSnapshot(handler: { image in
            var success = false;
            if let image = image {
                var data: Data?
                if path.hasSuffix(".jpeg") || path.hasSuffix(".jpg") {
                    data = image.jpegData(compressionQuality: 0.7)
                } else {
                    data = image.pngData()
                }
                do {
                    if data != nil {
                        try data?.write(to: URL(fileURLWithPath: path))
                        success = true
                    }
                } catch {
                    print("Error writing screenshot \(error)")
                }
            }
            self.sendEvent(withName: screenshotReadyEvent, body: success)
        })
    }

    @objc
    func processImage(_ path: String) {
        let start = CACurrentMediaTime()

        guard let sourceUrl = URL(string: path) else {
            self.sendEvent(withName: processImageEvent, body: "Error while getting image from the path")
            return
        }

        guard let imageData = try? Data(contentsOf: sourceUrl),
              let image = UIImage(data: imageData, scale: 1.0)
        else {
            self.sendEvent(withName: processImageEvent, body: "Error while processing image")
            return
        }
        banubaSdkManager.startEditingImage(image) { [weak banubaSdkManager] _, _ in
            banubaSdkManager?.captureEditedImage { resultImage in
                defer { banubaSdkManager?.stopEditingImage() }
                guard let resultImage else {
                    self.sendEvent(withName: processImageEvent, body: "Unable to apply effect to image")
                    return
                }

                do {
                    let manager = FileManager.default
                    let photoFileName = "tmp.png"
                    let destinationUrl = manager.temporaryDirectory.appendingPathComponent(photoFileName)
                    if manager.fileExists(atPath: destinationUrl.path) {
                        try? manager.removeItem(at: destinationUrl)
                    }

                    try resultImage.pngData()?.write(to: destinationUrl)

                    self.sendEvent(withName: processImageEvent, body: destinationUrl.path)
                    print("Time to save image: \n path = \(destinationUrl.absoluteString), \n time = \(CACurrentMediaTime() - start) ms")
                } catch {
                    self.sendEvent(withName: processImageEvent, body: "Error while saving image")
                    return
                }
            }
        }
    }

    override func startObserving() {
        hasListeners = true
    }

    override func stopObserving() {
        hasListeners = false
    }

    override func supportedEvents() -> [String]! {
        return [recordingStatusEvent, recordingFinishedEvent, screenshotReadyEvent, processImageEvent]
    }

    private var hasListeners = false
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
        banubaSdkManager.input.stopAudioCapturing()
    }

    func onRecordingProgress(duration: TimeInterval) {
    }
}
