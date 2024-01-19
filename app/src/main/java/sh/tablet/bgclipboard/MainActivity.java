package sh.tablet.bgclipboard;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;

import sh.tablet.bgclipboard.ui.ListAdapter;
import sh.tablet.bgclipboard.util.DataUtils;

public class MainActivity extends AppCompatActivity {
    private ListAdapter adapter;
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("已允许app"); // 设置标题

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        data = DataUtils.loadData(this);
        if (data == null || data.isEmpty()) {
            data = new ArrayList<>(Arrays.asList("com.fooview.android.fooview", "com.baidu.input_oppo"));
            DataUtils.saveData(data, this);
        }
        adapter = new ListAdapter(data);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(view -> {
            // 弹出对话框以添加新项
            showAddItemDialog();
        });
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加新项");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("添加", (dialog, which) -> {
            String newItem = input.getText().toString();
            if (!newItem.isEmpty()) {
                data.add(newItem);
                DataUtils.saveData(data, this);
                adapter.notifyItemInserted(data.size() - 1);
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
