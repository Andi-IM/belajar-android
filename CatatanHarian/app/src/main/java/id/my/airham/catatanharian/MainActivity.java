package id.my.airham.catatanharian;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_STORAGE = 100;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Aplikasi Catatan Proyek 1");
        listView = findViewById(R.id.list_view);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, InsertAndViewActivity.class);
            Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);
            intent.putExtra("filename", data.get("nama").toString());
            Toast.makeText(this, "You clicked ", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            Map<String, Object> data = (Map<String, Object>) parent.getAdapter().getItem(position);

            tampilkanDialogKonfirmasiHapusCatatan(data.get("nama").toString());
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (periksaIzinPenyimpanan()) {
                mengambilListFilePadaFolder();
            }
        } else {
            mengambilListFilePadaFolder();
        }
    }

    private void mengambilListFilePadaFolder() {
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File directory = new File(path);

        if (directory.exists()) {
            File[] files = directory.listFiles();
            String[] fileNames = new String[files.length];
            String[] dateCreated = new String[files.length];
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
            ArrayList<Map<String, Object>> itemDataList = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
                Date lastModFile = new Date(files[i].lastModified());
                dateCreated[i] = simpleDateFormat.format(lastModFile);
                Map<String, Object> listItemMap = new HashMap<>();
                listItemMap.put("name", fileNames[i]);
                listItemMap.put("date", dateCreated[i]);
                itemDataList.add(listItemMap);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    this,
                    itemDataList,
                    android.R.layout.simple_list_item_2,
                    new String[]{"name", "date"},
                    new int[]{android.R.id.text1, android.R.id.text2}
            );
            listView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    private boolean periksaIzinPenyimpanan() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE);
                return false;
            }
        }
        return true;
    }

    private void tampilkanDialogKonfirmasiHapusCatatan(final String fileName) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Catatan Ini")
                .setMessage("Apakah Anda yakin ingin menghapus Catatan " + fileName + "?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> hapusFile(fileName))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void hapusFile(String fileName) {
        String path = Environment.getExternalStorageDirectory().toString() + "/kominfo.proyek1";
        File file = new File(path, fileName);
        if (file.exists()){
            file.delete();
        }
        mengambilListFilePadaFolder();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mengambilListFilePadaFolder();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_tambah) {
            Intent intent = new Intent(this, InsertAndViewActivity.class);
            startActivity(intent);
        }
        return true;
    }
}