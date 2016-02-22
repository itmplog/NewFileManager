package top.itmp.newfilemanager;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private TextView currentPath;
    private Button back;
    private File currentFile;
    private File[] currentFiles;
    private final int REQUIREPERMISSION_RTN = 0x12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.fileList);
        currentPath = (TextView) findViewById(R.id.currentPath);
        back = (Button) findViewById(R.id.back);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUIREPERMISSION_RTN);
        } else {
            listPath(currentPath, listView);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentFiles[position].isFile())
                    return;
                File[] tmp = currentFiles[position].listFiles();
                if(tmp == null || tmp.length == 0){
                    Toast.makeText(getApplicationContext(), currentFiles[position].getName().toString() + " is empty or cannot be accessed.", Toast.LENGTH_SHORT).show();
                } else {
                    currentFile = currentFiles[position];
                    currentFiles = tmp;
                    inflateListView(currentFiles);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!currentFile.getCanonicalPath().equals(Environment.getExternalStorageDirectory().toString())) {
                        Log.v("sdcard", currentFile.getCanonicalPath() + "\n" + Environment.getExternalStorageDirectory());
                        currentFile = currentFile.getParentFile();
                        currentFiles = currentFile.listFiles();
                        inflateListView(currentFiles);
                    }else{
                        Toast.makeText(getApplicationContext(), "Already Home.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUIREPERMISSION_RTN:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    listPath(currentPath, listView);
                } else {
                    Toast.makeText(getApplicationContext(), "Need Permission to read SDCard.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), "Need Permission to read SDCard.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void listPath(TextView currentPath, ListView listView){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(getApplicationContext(), "Sdcard not mounted.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            File sdcard = new File(Environment.getExternalStorageDirectory().toString());
            if(sdcard.exists()){
                currentFile = sdcard;
                currentFiles = sdcard.listFiles();
                inflateListView(currentFiles);
            }
        }
    }

    private void inflateListView(File[] files){
        List<Map<String, Object>> listItems =
                new ArrayList<>();
        for(int i = 0; i < files.length; i++){
            Map<String, Object> listItem =
                    new HashMap<>();
            if(files[i].isDirectory()){
                listItem.put("icon", R.drawable.folder);
            } else {
                listItem.put("icon", R.drawable.file);
            }
            listItem.put("fileName", files[i].getName());

            listItems.add(listItem);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                listItems, R.layout.line,
                new String[]{"icon", "fileName"},
                new int[]{R.id.icon, R.id.file_name});

        listView.setAdapter(simpleAdapter);

        try{
            currentPath.setText("Currnet Path is: "
            + currentFile.getCanonicalPath());
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    /* get the View(Button) Size and location
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        currentPath.setText(back.getMeasuredWidth() + " " + back.getMeasuredHeight());
        currentPath.append("\t X: " + back.getRight() + " Y: " + back.getTop());
    }
*/
}
