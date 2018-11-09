package org.bytedeco.javacv.android.recognize.example.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacv.android.recognize.example.preferences.SharedPrefManager;
import org.bytedeco.javacv.android.recognize.example.recognizeHelpers.CvCameraPreview;
import org.bytedeco.javacv.android.recognize.example.R;
import org.bytedeco.javacv.android.recognize.example.recognizeHelpers.TrainHelper;
import org.bytedeco.javacv.android.recognize.example.retrofit.RetrofitClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_PLAIN;
import static org.bytedeco.javacpp.opencv_core.LINE_8;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.putText;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import static org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import static org.bytedeco.javacv.android.recognize.example.recognizeHelpers.TrainHelper.ACCEPT_LEVEL;

public class OpenCvRecognizeActivity extends Activity implements CvCameraPreview.CvCameraViewListener {
    public static final String TAG = "OpenCvRecognizeActivity";

    private CascadeClassifier faceDetector;
    private String[] nomes = {"", "I know you"};
    private int absoluteFaceSize = 0;
    private CvCameraPreview cameraView;
    boolean takePhoto;
    opencv_face.FaceRecognizer faceRecognizer = createEigenFaceRecognizer();
    boolean trained;

    // customized
    LinearLayout adminBtnLinearLayout;
    private int count = 0;

    // String ... means get arbitrary number of parameters, in this case permissions will be the arguments
    private boolean hasPermissions(Context context, String... permissions) {

        // Check permission status
        // Build.VERSION.SDK_INT  is the SDK version of the software currently running on this hardware device
        // Build.VERSION_CODES.M is getting the version code of the Marshmallow.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            // Check for each permission using a loop.
            for (String permission : permissions) {
                //ActivityCompat.checkSelfPermission is Determine whether you have been granted a particular permission.
                // PERMISSION_GRANTED if you have the permission, or PERMISSION_DENIED if not.
                // PackageManager is Class for retrieving various kinds of information related to the application packages that are currently installed on the device.
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;  // Return false if user doesn't have permission.
                }
            }
        }
        return true;  // Return true if user pass permission check.
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv);

        ButterKnife.bind(this);

        // The application support only, for SDK version >= 23
        if (Build.VERSION.SDK_INT >= 23) {

            // Create string array to keep permissions in manifest file
            String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            // Call hasPermissions method, It will return boolean value.
            if (!hasPermissions(this, PERMISSIONS)) {
                // ActivityCompat is Helper for accessing features in Activity.
                ActivityCompat.requestPermissions(this, PERMISSIONS, 1 );
            }
        }

        // LinearLayout that contains all the buttons
        adminBtnLinearLayout = findViewById(R.id.admin_buttons_liner_layout);
        // Get the value from intent and show visible LinearLayout that contains the camera buttons
        int valuePassedInIntent = getIntent().getIntExtra("KEY", 0);
        // Show and hide buttons according to value passed by intend.
        if( valuePassedInIntent == 1){
            // Show LinearLayout that contains camera control buttons.
            adminBtnLinearLayout.setVisibility(View.VISIBLE);
        }

        // Log.i(TAG, getIntent().getExtras().getString("studentName"));
        nomes[1] = getIntent().getExtras().getString("studentName");

        // Refer to the CvCameraPreview.
        // CvCameraPreview is the graphical object used to display a real-time preview of the Camera.
        cameraView = (CvCameraPreview) findViewById(R.id.camera_view);
        // Set this activity
        cameraView.setCvCameraViewListener(this);


        //AsyncTask enables proper and easy use of the UI thread.
        // The AsyncTask class can be thought of as a very convenient threading mechanism.
        // It gives you a few tools that you can use that simple Java threads simply don't have
        // such as on cancel cleanup operations. You don't have to do any UI in the background.
        // You could simply execute one by writing one as an anonymous class like this
        //It will execute whatever you put in doInBackground on a background thread with the given parameters.
        // Likewise, you can simply use Void and execute with no parameters.
        // This AsyncTask execute in onCreate and it do,
        // 1. Load EIGEN_FACES_CLASSIFIER yml file into OpenCV face recognizer from TRAIN_FOLDER folder
        // 2. Response to three click events, 'take photo', 'train', 'reset'
        new AsyncTask<Void,Void,Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    // Load frontface.xml file to face detecting in background.
                    faceDetector = TrainHelper.loadClassifierCascade(OpenCvRecognizeActivity.this, R.raw.frontalface);
                    if(TrainHelper.isTrained(getBaseContext())) {
                        // getFilesDir() is Returns the absolute path to the directory on the filesystem where files
                        // created with openFileOutput(String, int)are stored.
                        // Refer to folder call, train_folder
                        File folder = new File(getFilesDir(), TrainHelper.TRAIN_FOLDER);

                        // Refer to eigenFacesClassifier.yml file in the trailfolder
                        File f = new File(folder, TrainHelper.EIGEN_FACES_CLASSIFIER);
                        // Load eigenFacesClassifier.yml file
                        faceRecognizer.load(f.getAbsolutePath());
                        // After loading set trained variable to true.
                        trained = true;
                    }
                }catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
                return null;
            }

            //onPostExecute(Result), invoked on the UI thread after the background computation finishes.
            // The result of the background computation is passed to this step as a parameter.
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // After loading eigenFacesClassifier.yml file, those button clicks are done by user.
                // 1. Click take photo button.
                findViewById(R.id.btPhoto).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        takePhoto = true;
                    }
                });

                // Click on train button.
                findViewById(R.id.btTrain).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        train();
                    }
                });

                // Click on reset button.
                findViewById(R.id.btReset).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            TrainHelper.reset(getBaseContext());
                            Toast.makeText(getBaseContext(), "Reseted with sucess.", Toast.LENGTH_SHORT).show();
                            finish();
                        }catch (Exception e) {
                            Log.d(TAG, e.getLocalizedMessage(), e);
                        }
                    }
                });
            }
        }.execute();

        count = 0;
    }

    // Invoke after click on train button.
    void train() {
        // remainigPhotos is number of photos a user has to take from 25 photos.
        // TrainHelper.PHOTOS_TRAIN_QTY  is maximum number of photos, 25.
        // Number of photos a user has taken until now.
        int remainigPhotos = TrainHelper.PHOTOS_TRAIN_QTY - TrainHelper.qtdPhotos(getBaseContext());

        // User has to take 25 number of photos.
        if(remainigPhotos > 0) {
            Toast.makeText(getBaseContext(), "You need more to call train: "+ remainigPhotos, Toast.LENGTH_SHORT).show();
            return;
        }else if(TrainHelper.isTrained(getBaseContext())) {
            // user took 25 photos and trained them.
            Toast.makeText(getBaseContext(), "Already trained", Toast.LENGTH_SHORT).show();
            return;
        }

        // User took 25 photos, but not click on train button until now.
        Toast.makeText(getBaseContext(), "Start train: ", Toast.LENGTH_SHORT).show();

        // This AsyncTask do,
        // 1. Train images.
        // 2. Finish asyncTask after training finish.
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try{
                    // If user noly took photos, but not train yet.
                    // If isTrained return false,
                    if(!TrainHelper.isTrained(getBaseContext())) {
                        // Do training.
                        TrainHelper.train(getBaseContext());
                    }
                }catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                try {
                    // TrainHelper.isTrained(getBaseContext()) return true or false.
                    Toast.makeText(getBaseContext(), "Reseting after train - Sucess : "+ TrainHelper.isTrained(getBaseContext()), Toast.LENGTH_SHORT).show();
                    // Finish AsyncTask operation after training images.
                    finish();
                }catch (Exception e) {
                    Log.d(TAG, e.getLocalizedMessage(), e);
                }
            }
        }.execute();
    }

    // This method call when camera view start.
    @Override
    public void onCameraViewStarted(int width, int height) {
        // Set width and height of the camera view when camera start.
        absoluteFaceSize = (int) (width * 0.32f);
    }

    // This method call when camera view stop.
    @Override
    public void onCameraViewStopped() {

    }

    // This call every time when user click on take photo button.
    private void capturePhoto(Mat rgbaMat) {
        try {
            // Parameters,
            // 1. Context
            // 2. id of the person (in this only one person)
            // 3. Get existing number of images and increase it by 1.
            // 4. Cloned rgba image
            // 5. Face detector ClassifierCascade
            TrainHelper.takePhoto(getBaseContext(), 1, TrainHelper.qtdPhotos(getBaseContext()) + 1, rgbaMat.clone(), faceDetector);
        }catch (Exception e) {
            e.printStackTrace();
        }
        takePhoto = false;
    }

    // If photo are already trained, this method  is called.
    // Parameters
    // 1. Vector objects of camera frames.
    // 2. Gray image
    // 3. Color image
    private void recognize(opencv_core.Rect dadosFace, Mat grayMat, Mat rgbaMat) {
        // create new object of each detected camera frame's gray image.
        Mat detectedFace = new Mat(grayMat, dadosFace);
        // resize gray image
        resize(detectedFace, detectedFace, new Size(TrainHelper.IMG_SIZE,TrainHelper.IMG_SIZE));

        //The peer class to native pointers and arrays of int
        IntPointer label = new IntPointer(1);

        // The peer class to native pointers and arrays of double
        DoublePointer reliability = new DoublePointer(1);

        // Predicts the label and confidence for a given sample.
        faceRecognizer.predict(detectedFace, label, reliability);

        // prediction is -1 or 1
        int prediction = label.get(0); // ex: 3708.155960217258
        double acceptanceLevel = reliability.get(0);
        String name;
        if (prediction == -1 || acceptanceLevel >= ACCEPT_LEVEL) {
            name = getString(R.string.unknown);
        } else {

            name = nomes[prediction] + " - " + acceptanceLevel;

            // Count number of success attempts.
            count++;
            Log.i(TAG, String.valueOf(count));

            if( count == 120 ){
                // Here is the code for update database.
                //Toast.makeText(getApplicationContext(), "Database updated", Toast.LENGTH_LONG).show();
                signForSchol();
               // count = 0;
            }

            // reset count variable
            // count = 0;
        }

        // Draw a text
        // get the position of the text
        int x = Math.max(dadosFace.tl().x() - 10, 0);
        int y = Math.max(dadosFace.tl().y() - 10, 0);

        // Put text on camera view
        // Parameters
        //1. rgba image
        // 2. string name
        // 3. place
        // 4. font type
        // 5. font size
        // 6. font color
        putText(rgbaMat, name, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new opencv_core.Scalar(0,255,0,0));


    }

    // Draw a rectangle around the detected face.
    // Parameters
    // 1. Vector objects for all camera frames
    void showDetectedFace(RectVector faces, Mat rgbaMat) {
        int x = faces.get(0).x();
        int y = faces.get(0).y();
        int w = faces.get(0).width();
        int h = faces.get(0).height();

        // draw the rectangle.
        rectangle(rgbaMat, new Point(x, y), new Point(x + w, y + h), opencv_core.Scalar.GREEN, 2, LINE_8, 0);
    }

    void noTrainedLabel(opencv_core.Rect face, Mat rgbaMat) {
        int x = Math.max(face.tl().x() - 10, 0);
        int y = Math.max(face.tl().y() - 10, 0);
        putText(rgbaMat, "No trained or train unavailable", new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new opencv_core.Scalar(0,255,0,0));
    }

    // This method call in entire camera view
    // This is one of the method of CvCameraPreview.CvCameraViewListener interface.
    // parameter, rgbaMat is color image of the video frame
    @Override
    public Mat onCameraFrame(Mat rgbaMat) {

        // if face detector cascade file load successfully
        if (faceDetector != null) {
            // Create a gray image using row and columns.
            Mat greyMat = new Mat(rgbaMat.rows(), rgbaMat.cols());
            cvtColor(rgbaMat, greyMat, CV_BGR2GRAY);

            // Create new vector objects of camera frames.
            RectVector faces = new RectVector();

            // Apply the detectMultiScale method from the face cascade to locate one or several faces in the image.
            // Parameters
            // 1. grayMat is gray image
            // Vector objects
            // scaleFactor
            // minNeighbors
            // flags
            // Set to face size to default
            faceDetector.detectMultiScale(greyMat, faces, 1.25f, 3, 1,
                    new Size(absoluteFaceSize, absoluteFaceSize),
                    new Size(4 * absoluteFaceSize, 4 * absoluteFaceSize));

            // if there is only one face on camera view
            if (faces.size() == 1) {

                // Call showDetectedFace, to draw a rectangle around the detected face.
                // parameter
                // 1. faces is MatVector object
                // 2. Color image
                showDetectedFace(faces, rgbaMat);
                // take photo button click make takePhoto variable to true
                if(takePhoto) {
                    //  Then, capture photo method call
                    capturePhoto(rgbaMat);

                    // Then, show an alert for remaining images.
                    alertRemainingPhotos();
                }

                // click on take photo button make trained true.
                if(trained) {
                    // Then, recognize method is called.
                    recognize(faces.get(0), greyMat, rgbaMat);
                }else{

                    noTrainedLabel(faces.get(0), rgbaMat);
                }
            }
            greyMat.release();
        }
        return rgbaMat;
    }

    void alertRemainingPhotos() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int remainigPhotos = TrainHelper.PHOTOS_TRAIN_QTY - TrainHelper.qtdPhotos(getBaseContext());
                Toast.makeText(getBaseContext(), "You need more to call train: "+ remainigPhotos, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void signForSchol() {
        // visibleProgressBar();
        String userToken = "Bearer " + SharedPrefManager.getInstance(this).getToken();
        String userId = SharedPrefManager.getInstance(this).getUserId();

        Call<ResponseBody> callSign = RetrofitClient
                .getmInstance()
                .getApi()
                .signForInstallment(userToken, userId);

        callSign.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                Log.i(TAG, String.valueOf(response.body()));

                if (response.isSuccessful()) {

                    try {


                        String json = response.body().string();
                        Log.i(TAG, "onResponse :On OpenCV json : " + json);



                       JSONObject data = null;
                       data = new JSONObject(json);
                       Log.i(TAG, "onResponse OpenCV: data : " + data);

                        // invisibleProgressBar();
                        // Finish the camera activity.
                        OpenCvRecognizeActivity.this.finish();


                    } catch (JSONException e) {
                        // invisibleProgressBar();
                        Toast.makeText(OpenCvRecognizeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse:On OpenCV jsonException: " + e.getMessage());

                    } catch (IOException e) {
                        // invisibleProgressBar();
                        Toast.makeText(OpenCvRecognizeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, "onResponse:On OpenCV jsonException: " + e.getMessage());
                    }
                }else {
                    String json = null;
                    try {
                        json = response.errorBody().string();
                        JSONObject data = null;
                        data = new JSONObject(json);
                        String errorMessage = data.getString("fail");
                        // invisibleProgressBar();
                        Toast.makeText(OpenCvRecognizeActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse:On OPenCV json on error : " + errorMessage);

                        // invisibleProgressBar();
                        // Finish the camera activity.
                        OpenCvRecognizeActivity.this.finish();

                    } catch (IOException e) {
                        // invisibleProgressBar();
                        Toast.makeText(OpenCvRecognizeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse else IOException On OpenCV : " + e.getMessage());
                    } catch (JSONException e) {
                        // invisibleProgressBar();
                        Toast.makeText(OpenCvRecognizeActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(TAG, "onResponse JSONException On OpenCV: " + e.getMessage());
                    }
                }
            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // invisibleProgressBar();
                // Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                Log.i(TAG, "onFailure : " + t.getMessage());
            }
        });
    }
}
