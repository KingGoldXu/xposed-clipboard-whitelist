package sh.tablet.bgclipboard;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class BGClipboard implements IXposedHookLoadPackage {
    private static final String logTag = "===tablettttttt===";
    private static final String clipboardService = "com.android.server.clipboard.ClipboardService";
    private static XSharedPreferences getPref(String path) {
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, path);
        return pref.getFile().canRead() ? pref : null;
    }
    private static List<String> allowedPackages = new ArrayList<>(Arrays.asList("com.fooview.android.fooview", "com.baidu.input_oppo"));

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XSharedPreferences pref;
        if(lpparam.packageName.equals("android")) {
            pref = getPref("packages");
            if (pref != null) {
                Gson gson = new Gson();
                String json = pref.getString("allow_list", null);
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                if (json != null) {
                    allowedPackages = gson.fromJson(json, type);
                }
                XposedBridge.log("allowedPackages: " + allowedPackages.toString());
            } else {
                XposedBridge.log("------------- can not getPref -------------");
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
            Class<?> clazz = XposedHelpers.findClassIfExists("com.android.server.clipboard.ClipboardServiceExtImpl",
                    lpparam.classLoader);
            if(clazz != null) {
                XposedHelpers.findAndHookMethod(clazz,
                        "hookGetPrimaryClipResult",
                        "android.content.Context",
                        "android.content.ClipData",
                        "android.app.AppOpsManager",
                        String.class,
                        int.class,
                        int.class,
                        int.class,
                        int.class,
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                try {
                                    String packageName = (String) param.args[3];
                                    if (allowedPackages.contains(packageName)) {
                                        param.setResult(param.args[1]);
                                    }
                                } catch (Throwable t) {
                                    Log.e(logTag, "Something wrong when hooking hookGetPrimaryClipResult", t);
                                }
                            }
                        });
            }
        }
    }
}
