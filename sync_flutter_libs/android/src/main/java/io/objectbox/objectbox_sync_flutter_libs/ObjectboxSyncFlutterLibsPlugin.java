package io.objectbox.objectbox_sync_flutter_libs;

import android.os.Build;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/** ObjectboxSyncFlutterLibsPlugin */
public class ObjectboxSyncFlutterLibsPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
    private MethodChannel channel;

    @Override
    public void onAttachedToEngine(FlutterPlugin.FlutterPluginBinding flutterPluginBinding) {
        channel =
                new MethodChannel(
                        flutterPluginBinding.getBinaryMessenger(), "objectbox_sync_flutter_libs");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if ("loadObjectBoxLibrary".equals(call.method)) {
            // Loading the JNI library through Dart is broken on Android 6 (and maybe earlier).
            // Try to fix by loading it first via Java API, then again in Dart.
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                result.success(null);
                return;
            }
            try {
                System.loadLibrary("objectbox-jni");
                System.out.println("[ObjectBox] Loaded JNI library via workaround.");
                result.success(null);
            } catch (Throwable e) {
                result.error("OBX_SO_LOAD_FAILED", e.getMessage(), null);
            }
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(FlutterPlugin.FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
