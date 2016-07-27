package ivanrudyk.com.open_weather_api.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import ivanrudyk.com.open_weather_api.R;
import ivanrudyk.com.open_weather_api.fragment.NavigationDraverFragment;
import ivanrudyk.com.open_weather_api.model_user.Users;

public class RegisterUserActivity extends AppCompatActivity {

    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = database.child("user");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    EditText etLogin, etPassword, etUserName, etCity, etConfirmPassword;
    ImageView ivCamera, ivGalary, ivOkRegister, ivCancelRegister;
    ProgressBar progressBarRegister;

    Users userPhoto = new Users();
    Users user = new Users();
    CameraPhoto cameraPhoto;
    private final int CAMERA_REQUEST = 13323;
    private Bitmap photoLoad;
    GalleryPhoto galleryPhoto;
    private final int GALLERY_REQEST = 34623;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_user);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        initializeComponentView();

        ivOkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addData(etLogin.getText().toString(),
//                        etPassword.getText().toString(),
//                        etUserName.getText().toString(),
//                        etCity.getText().toString(),
//                        etConfirmPassword.getText().toString());
                RetiveLocationPhoto ret = new RetiveLocationPhoto();
                ret.execute();
                Toast.makeText(RegisterUserActivity.this, "User "+user.getUserName().toString()+" registered", Toast.LENGTH_SHORT).show();
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                    cameraPhoto.addToGallery();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Camera opens", Toast.LENGTH_SHORT);
                }
            }
        });
        ivGalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(galleryPhoto.openGalleryIntent(), GALLERY_REQEST);
            }
        });
        ivCancelRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(RegisterUserActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                String photoPath = cameraPhoto.getPhotoPath();
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(50, 80).getBitmap();
                    photoLoad = bitmap;
                    userPhoto.setPhoto(photoLoad);

                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Photo Camera", Toast.LENGTH_SHORT);
                }

            } else if (requestCode == GALLERY_REQEST) {
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(50, 80).getBitmap();
                    photoLoad = bitmap;
                    userPhoto.setPhoto(photoLoad);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Photo Gallary", Toast.LENGTH_SHORT);
                }

            }

        }
    }

    private void loadPhotoStorage(String userName, Bitmap photo) {

        StorageReference storageRef = storage.getReferenceFromUrl("gs://justweather-92b19.appspot.com/");
        StorageReference userRef = storageRef.child(userName + "/");
        StorageReference userImagesRef = userRef.child("photo.jpg");

        Bitmap bitmap = photo;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    private void addData(String login, String password, String userName, String city, String confirmPassword) {
        user.setLogin(login);
        user.setPassword(password);
        user.setUserName(userName);

        ArrayList<String> locationStart = new ArrayList();
        locationStart.add(city);
        user.setLocation(locationStart);


        if (login.length() > 0 && login != null) {
            if (userName.length() > 0 && userName != null) {
                if (city.length() > 0 && city != null) {
                    if (password.length() > 0 && password != null) {
                        if (confirmPassword.length() > 0 && confirmPassword != null) {
                            if (password.equals(confirmPassword)) {

                                DatabaseReference nameRef = ref.child(userName);
                                nameRef.setValue(user);
                                DatabaseReference nameRefLocation = nameRef.child("location");
                                nameRefLocation.setValue(locationStart);

                                etLogin.setText("");
                                etPassword.setText("");
                                etUserName.setText("");


                                user.setPhoto(photoLoad);
                                if (photoLoad != null) {
                                    loadPhotoStorage(user.getUserName(), photoLoad);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Passwords is not equals", Toast.LENGTH_SHORT);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Confirm password", Toast.LENGTH_SHORT);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Insert password", Toast.LENGTH_SHORT);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Insert your sity", Toast.LENGTH_SHORT);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Insert user name", Toast.LENGTH_SHORT);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Insert login or email", Toast.LENGTH_SHORT);
        }
    }

    private void initializeComponentView() {
        etLogin = (EditText) findViewById(R.id.et_register_login);
        etPassword = (EditText) findViewById(R.id.et_register_password);
        etUserName = (EditText) findViewById(R.id.et_register_user_name);
        etCity = (EditText) findViewById(R.id.et_city_register);
        etConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        ivCamera = (ImageView) findViewById(R.id.iv_camera);
        ivGalary = (ImageView) findViewById(R.id.iv_galery);
        ivOkRegister = (ImageView) findViewById(R.id.iv_ok_register);
        ivCancelRegister = (ImageView) findViewById(R.id.iv_cancel_register);
        progressBarRegister = (ProgressBar) findViewById(R.id.progressBarRegister);

        cameraPhoto = new CameraPhoto(getApplicationContext());
        galleryPhoto = new GalleryPhoto(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }


    class RetiveLocationPhoto extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            addData(etLogin.getText().toString(),
                    etPassword.getText().toString(),
                    etUserName.getText().toString(),
                    etCity.getText().toString(),
                    etConfirmPassword.getText().toString());
            NavigationDraverFragment.users.setUserName(user.getUserName());
            NavigationDraverFragment.users.setPhoto(userPhoto.getPhoto());
            progressBarRegister.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBarRegister.setVisibility(View.INVISIBLE);
        }
    }
}
