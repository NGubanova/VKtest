package com.example.vktest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1;
    private static boolean EXTERNAL_STORAGE_GRANTED = false;
    private RecyclerView recyclerView;
    private Button sortDate, sortExt, sortSize;
    private DB_File dbFile;
    SharedPreferences dir;
    private static final String DIR = "Dir";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.RV);
        sortDate = findViewById(R.id.sortDate);
        sortSize = findViewById(R.id.sortSize);
        sortExt = findViewById(R.id.sortExt);
        dir = getSharedPreferences("dir", MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = dir.edit();
        prefEditor.putString(DIR, Arrays.toString(Environment.getExternalStorageDirectory().listFiles()));
        prefEditor.apply();

        try {
            hashFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        RecyclerViewAdapter adapterFile = new RecyclerViewAdapter(this, getDirectory());

        int hasReadContactPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            EXTERNAL_STORAGE_GRANTED = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
        if (EXTERNAL_STORAGE_GRANTED) {
            adapterFile = new RecyclerViewAdapter(this, getDirectory());
            recyclerView.setAdapter(adapterFile);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EXTERNAL_STORAGE_GRANTED = true;
            }
        }
        if (EXTERNAL_STORAGE_GRANTED) {
            getDirectory();
        } else {
            Toast.makeText(this, "Need permissions", Toast.LENGTH_SHORT).show();
        }

    }

    private List<File> getDirectory() {
        List<File> DirFiles = new ArrayList<>();
        File directoryPath =  Environment.getExternalStorageDirectory();
        File[] files = directoryPath.listFiles();
        if (files != null) {
                DirFiles.addAll(Arrays.asList(files));
        } else {
            Toast.makeText(this, "Packages empty", Toast.LENGTH_SHORT).show();
        }
        return DirFiles;
    }

    private void hashFile() throws Exception {
        File directoryPath =  Environment.getExternalStorageDirectory();
        File[] files = directoryPath.listFiles();
        byte[] hash;
        for (File file:files){
            hash = FileHasher.createHash(file);
            Boolean checkInsertData = dbFile.insert(file.getName(), hash);
            if (checkInsertData) {
                Toast.makeText(getApplicationContext(),
                        "успешно", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "ошибка", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
