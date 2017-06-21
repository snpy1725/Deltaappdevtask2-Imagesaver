package com.task.imagesaver;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.R.attr.listChoiceBackgroundIndicator;
import static android.R.attr.onClick;

import static com.task.imagesaver.R.id.parent;


public class MainActivity extends AppCompatActivity {

    public class MyImage {
        private String title, description, path;
        Bitmap b;Uri a;
public void setbitmapforimage(Bitmap b)
{
    this.b=b;
}
public Bitmap getBitmap()
{
    return b;
}

public void setUriforimage(Uri a){
    this.a=a;
}
public Uri getUri()
{
    return a;
}
        public String getTitle() { return title; }

        public String getDescription() { return description; }

        public void setTitle(String title) { this.title = title; }



        public void setDescription(String description) {
            this.description = description;
        }

        public void setPath(String path) { this.path = path; }

        public String getPath() { return path; }

        @Override public String toString() {
            return "Title:" + title + "   "  +
                    "\nCaption:" + description;
        }
    }
    public class ImageAdapter extends ArrayAdapter<MyImage> {


        private  class ViewHolder {
            ImageView imgIcon;
            TextView description;
        }

        public ImageAdapter(Context context, ArrayList<MyImage> images) {
            super(context, 0, images);
        }

        @Override public View getView(int position, View convertView,
                                      ViewGroup parent) {
            // view lookup cache stored in tag
            ViewHolder viewHolder;
            // Check if an existing view is being reused, otherwise inflate the
            // item view
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_image, parent, false);
                viewHolder.description =
                        (TextView) convertView.findViewById(R.id.item_img_infor);
                viewHolder.imgIcon =
                        (ImageView) convertView.findViewById(R.id.item_img_icon);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Get the data item for this position
            MyImage image = getItem(position);
            // set description text
            viewHolder.description.setText(image.toString());
            // set image icon
            final int THUMBSIZE = 96;
            //        viewHolder.imgIcon.setImageURI(Uri.fromFile(new File(image
            // .getPath())));
            viewHolder.imgIcon.setImageBitmap(ThumbnailUtils
                    .extractThumbnail(BitmapFactory.decodeFile(image.getPath()),
                            THUMBSIZE,THUMBSIZE));
            viewHolder.imgIcon.setImageBitmap(image.getBitmap());


            // Return the completed view to render on screen
            return convertView;
        }
    }
    private ArrayList<MyImage> images;
    private ImageAdapter imageAdapter;
    private ListView listView;
    public static int RESULT_LOAD_IMAGE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        images = new ArrayList<>();
        // Create the adapter to convert the array to views
        imageAdapter = new ImageAdapter(this, images);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.main_list_view);
        listView.setAdapter(imageAdapter);
        ConstraintLayout m=(ConstraintLayout)findViewById(R.id.layoutwhole);
        m.setBackgroundColor(Color.rgb(27,208,192));

    }

    public void selectImage(View view){

        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.photo_adder);


        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                   camerapic();
                                }
            else if(items[item].equals("Choose from Library"))
             gallery();
                else if(items[item].equals("Cancel"))
                    dialog.dismiss();
            }}
        );
        builder.show();


    }

    public void camerapic()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(takePictureIntent,0);
    }

    public void gallery() {

            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


            startActivityForResult(i, RESULT_LOAD_IMAGE);

    }
MyImage variable;
    public void croppress(View view)
    {

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Crop Image");
        builder.setMessage("Select the image to crop from the list");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView parent, View v, int position, long id){

                      variable= images.get(position);
                        Uri picUri=  variable.getUri();
                        try {
                            Intent cropIntent = new Intent("com.android.camera.action.CROP");
                            // indicate image type and Uri
                            cropIntent.setDataAndType(picUri, "image/*");
                            // set crop properties here
                            cropIntent.putExtra("crop", true);
                            // indicate aspect of desired crop
                            cropIntent.putExtra("aspectX", 1);
                            cropIntent.putExtra("aspectY", 1);
                            // indicate output X and Y
                            cropIntent.putExtra("outputX", 128);
                            cropIntent.putExtra("outputY", 128);
                            // retrieve data on return
                            cropIntent.putExtra("return-data", true);
                            // start the activity - we handle returning in onActivityResult
                            startActivityForResult(cropIntent, 2);
                        }
                        // respond to users whose devices do not support the crop action
                        catch (ActivityNotFoundException anfe) {
                            // display an error message
                            String errorMessage = "Whoops - your device doesn't support the crop action!";
                            Toast toast = Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT);
                                 toast.show();
                        }


                    }

                } );}});
        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
            }
        });
        builder.show();
        imageAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data)
            { Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver()
                        .query(selectedImage, filePathColumn, null, null,
                                null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();



                final   MyImage image = new MyImage();

                image.setTitle("Image in List");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set caption for this photo");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       image.setDescription( input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        image.setDescription("No caption");
                    }
                });
                builder.show();
                imageAdapter.notifyDataSetChanged();


          image.setPath(picturePath);
                image.setUriforimage(selectedImage);
                images.add(image);





          }
        else  if(requestCode == 0  && null != data)
            {

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
           final      MyImage image = new MyImage();
                image.setTitle("Image in List");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Set caption for this photo");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        image.setDescription( input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        image.setDescription("No caption");
                    }
                });
                builder.show();

                imageAdapter.notifyDataSetChanged();
                image.setbitmapforimage(thumbnail);
                images.add(image);


                }
                else if(requestCode==2 && null!=data)
        {
            // get the returned data
            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap selectedBitmap = extras.getParcelable("data");
           variable.setbitmapforimage(selectedBitmap);
            imageAdapter.notifyDataSetChanged();

        }
            }


public void deletepress(View view)
{ AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle("Delete entry");
     builder.setMessage("Select the image to delete from the list");
     builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

    public void onClick(DialogInterface dialog, int which) {

      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          public void onItemClick(AdapterView parent, View v, int position, long id){

            imageAdapter.remove(images.get(position));
              imageAdapter.notifyDataSetChanged();

          }

      } );}});
builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
    public void onClick(DialogInterface dialog, int which) {
        // do nothing
    }
});
builder.show();
}





    }




