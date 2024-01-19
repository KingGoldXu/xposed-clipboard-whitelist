package sh.tablet.bgclipboard.util;

import android.util.Log;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String TAG = FileUtils.class.getCanonicalName();
    private static final String path = "/data/local/tmp";
    private static final String filename = "bgclipboard_allowed_packages.txt";

    public static void createFile(File file) {
        FileOutputStream fos = null;
        try {
            boolean success = file.createNewFile();
            if (success) {
                fos = new FileOutputStream(file, false);
                String defaults = "com.fooview.android.fooview\ncom.baidu.input_oppo\n";
                fos.write(defaults.getBytes());
                fos.flush();
            }
        } catch (IOException e) {
            Log.e(TAG, "can not create " + file, e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static List<String> readPackageNamesFromFile() {
        List<String> packageNames = new ArrayList<>();
        File myDir = new File(path);
        if (!myDir.exists()) {
            Log.e(TAG, path + "do not exists!");
            return packageNames;
        }
        File file = new File(myDir, filename);
        if (!file.exists()) {
            createFile(file);
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!(line.startsWith("#") || line.trim().isEmpty())) {
                    packageNames.add(line.trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packageNames;
    }
}
