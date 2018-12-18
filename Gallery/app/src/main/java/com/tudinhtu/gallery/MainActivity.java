package com.tudinhtu.gallery;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_PERMISSION_KEY = 1;
    LoadAlbum loadAlbumTask;
    static String appFirstTime="first-time";
    boolean firstTimeMain;
    GridView galleryGridView;
    RadioGroup radioGroup;
    Dialog dialog;
    Button buttonOK;
    RadioButton rdLight, rdDark, rdAuto;
    int theme = 2;   // default

    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        galleryGridView = (GridView) findViewById(R.id.galleryGridView);

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels ;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if(dp < 360)
        {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }

        setBackground(theme); //auto

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.Add:
                Intent intent = new Intent(MainActivity.this, FavoriteAlbum.class);
                startActivityForResult(intent,0);
                break;
<<<<<<< HEAD
            }
            case R.id.Guide:
            {
                Intent intent = new Intent(MainActivity.this, Guide.class);
                intent.putExtra("choose","main" );
                startActivity(intent);
                break;
=======
            case R.id.delete:
                break;
            case R.id.background:
                showRadioButtonDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRadioButtonDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        buttonOK = (Button) dialog.findViewById(R.id.btnOK);
        rdLight = (RadioButton)dialog.findViewById(R.id.radio_light);
        rdDark = (RadioButton)dialog.findViewById(R.id.radio_dark);
        rdAuto = (RadioButton) dialog.findViewById(R.id.radio_auto);
        dialog.show();

        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdLight.isChecked()) {
                    theme = 0;
                    setBackground(theme);
                    Toast.makeText(MainActivity.this, "Theme: " + rdLight.getText(), Toast.LENGTH_SHORT).show();
                }
                if (rdDark.isChecked()) {
                    theme = 1;
                    setBackground(theme);
                    Toast.makeText(MainActivity.this, "Theme: " + rdDark.getText(), Toast.LENGTH_SHORT).show();
                }
                if (rdAuto.isChecked()) {
                    theme = 2;
                    setBackground(theme);
                    Toast.makeText(MainActivity.this, "Theme: " + rdAuto.getText(), Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
>>>>>>> 225025fcbde1a5aeecc6304ad86023236bbb26db
            }
        });

    }

    private void setBackground(int theme) {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        switch (theme) {
            case 0:
                galleryGridView.setBackgroundColor(Color.WHITE);
                break;
            case 1:
                galleryGridView.setBackgroundColor(Color.BLACK);
                break;
            case 2:
                if (hour >= 6 && hour < 18) {
                    galleryGridView.setBackgroundColor(Color.WHITE);
                }
                else {
                    galleryGridView.setBackgroundColor(Color.BLACK);
                }
                break;
        }

        savingPreferences();
    }

    public void savingPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.putInt("key", theme);
        editor.apply();
    }
    public void savingPreferencesMain() {
        SharedPreferences pre=getSharedPreferences(appFirstTime,MODE_PRIVATE);
        SharedPreferences.Editor editor=pre.edit();
        if(pre.getBoolean("main",true)==true) {
            //editor.remove("main");
            editor.putBoolean("main", false);
        }
        editor.commit();
    }
    public void restoringPreferencesMain() {
        SharedPreferences pre=getSharedPreferences(appFirstTime,MODE_PRIVATE);
        firstTimeMain=pre.getBoolean("main",true);
    }
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    class LoadAlbum extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";

            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
            Uri uriExternal = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI;


            String[] projection = { MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
            Cursor cursorExternal = getContentResolver().query(uriExternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursorInternal = getContentResolver().query(uriInternal, projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});

            while (cursor.moveToNext()) {

                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = Function.getCount(getApplicationContext(), album);

                albumList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), countPhoto));
            }
            cursor.close();
            Collections.sort(albumList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            AlbumAdapter adapter = new AlbumAdapter(MainActivity.this, albumList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                    intent.putExtra("name", albumList.get(+position).get(Function.KEY_ALBUM));
                    startActivity(intent);
                }
            });
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                   // loadAlbumTask = new LoadAlbum();
                    //loadAlbumTask.execute();
                } else
                {
                    Toast.makeText(MainActivity.this, "You must accept permissions.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }




    @Override
    protected void onResume() {
        super.onResume();
        restoringPreferencesMain();

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Function.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
            if(firstTimeMain==true)
            {
                Intent intent = new Intent(MainActivity.this, Guide.class);
                intent.putExtra("choose","main" );
                startActivity(intent);
            }
        }else{

            loadAlbumTask = new LoadAlbum();
            loadAlbumTask.execute();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        savingPreferencesMain();
    }
}




class AlbumAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap< String, String >> data;
    public AlbumAdapter(Activity a, ArrayList < HashMap < String, String >> d) {
        activity = a;
        data = d;
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return position;
    }
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder holder = null;
        if (convertView == null) {
            holder = new AlbumViewHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.album_row, parent, false);

            holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
            holder.gallery_count = (TextView) convertView.findViewById(R.id.gallery_count);
            holder.gallery_title = (TextView) convertView.findViewById(R.id.gallery_title);

            convertView.setTag(holder);
        } else {
            holder = (AlbumViewHolder) convertView.getTag();
        }
        holder.galleryImage.setId(position);
        holder.gallery_count.setId(position);
        holder.gallery_title.setId(position);

        HashMap < String, String > song = new HashMap < String, String > ();
        song = data.get(position);
        try {
            holder.gallery_title.setText(song.get(Function.KEY_ALBUM));
            holder.gallery_count.setText(song.get(Function.KEY_COUNT));

            Glide.with(activity)
                    .load(new File(song.get(Function.KEY_PATH))) // Uri of the picture
                    .into(holder.galleryImage);


        } catch (Exception e) {}
        return convertView;
    }
}

class AlbumViewHolder {
    ImageView galleryImage;
    TextView gallery_count, gallery_title;
}
