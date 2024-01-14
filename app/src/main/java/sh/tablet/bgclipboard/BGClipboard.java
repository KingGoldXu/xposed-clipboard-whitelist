package sh.tablet.bgclipboard;

import android.util.Log;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BGClipboard implements IXposedHookLoadPackage {
    private static final String logTag = "===tablettttttt===";
    private static final List<String> allowedPackages = Arrays.asList(
            "com.fooview.android.fooview",
            "com.baidu.input_oppo",
            "com.baidu.input",
            "com.iflytek.inputmethod",
            "com.google.android.inputmethod.pinyin",
            "com.sohu.inputmethod.sogou");
    private static final String clipboardService = "com.android.server.clipboard.ClipboardService";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(!lpparam.packageName.equals("android")) {
            return;
        }

        XposedHelpers.findAndHookMethod(clipboardService,
                lpparam.classLoader,
                "isDefaultIme",
                int.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            Log.d(logTag, "hook before isDefaultIme(int, String)");
                            String packageName = (String) param.args[1];
                            if (allowedPackages.contains(packageName)) {
                                param.setResult(true);
                            } else {
                                param.setResult(false);
                            }
                        } catch (Throwable t) {
                            Log.e(logTag, "Failed to override isDefaultIme(int, String)", t);
                        }
                    }
                });
    }
}
