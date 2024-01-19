package sh.tablet.bgclipboard.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class DataUtils {

    public static void saveData(ArrayList<String> data, Context context) {
        SharedPreferences pref = context.getSharedPreferences("packages", Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(data);
        editor.putString("allow_list", json);
        editor.apply();
    }

    public static ArrayList<String> loadData(Context context) {
        SharedPreferences pref = context.getSharedPreferences("packages", Context.MODE_WORLD_READABLE);
        Gson gson = new Gson();
        String json = pref.getString("allow_list", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
