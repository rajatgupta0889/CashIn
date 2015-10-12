package com.mantralabsglobal.cashin.ui.activity.camera;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.fragment.camera.CwacCameraFragment;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by pk on 7/9/2015.
 */
public class CwacCameraActivity extends AppCompatActivity implements
   /* ActionBar.OnNavigationListener,*/ CwacCameraFragment.Contract {
    private static final String STATE_SINGLE_SHOT = "single_shot";
    private static final String STATE_LOCK_TO_LANDSCAPE =
            "lock_to_landscape";
    private static final int CONTENT_REQUEST = 1337;
    private CwacCameraFragment std = null;
    private CwacCameraFragment ffc = null;
    private CwacCameraFragment current = null;
    private boolean hasTwoCameras = (Camera.getNumberOfCameras() > 1);
    private boolean singleShot = true;
    private boolean isLockedToLandscape = false;

    public static final String FFC = "FFC";
    public static final String STANDARD = "STANDARD";
    private static ImageButton flashBtn, flipCameraBtn;
    public static final String DEFAULT_CAMERA = "DEFAULT_CAMERA";
    public static final String SHOW_CAMERA_SWITCH = "SHOW_CAMERA_SWITCH";
    public static final String FLIP_CAMERA = "FLIP_CAMERA";
    public static final String FLASH = "FLASH";
    boolean showCameraSwitch = false;
    boolean useFFCByDefault = false;
    public static boolean flashOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //requestWindowFeature(Window.);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cwac_main_activity);
        ButterKnife.inject(this);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        //setSupportActionBar(toolbar);

        //  getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();

        String defaultCamera = intent.getStringExtra(DEFAULT_CAMERA);
        useFFCByDefault = FFC.equals(defaultCamera) ? true : false;

        boolean flipCamera = intent.getBooleanExtra(FLIP_CAMERA, false);
        boolean useFlash = intent.getBooleanExtra(FLASH, false);

        flashBtn = (ImageButton) findViewById(R.id.flash_button);
        flipCameraBtn = (ImageButton) findViewById(R.id.flip_camera_button);

        if(!flipCamera)
            flipCameraBtn.setVisibility(View.INVISIBLE);

        if(!useFlash)
            flashBtn.setVisibility(View.INVISIBLE);

        if(defaultCamera.equals( CwacCameraActivity.STANDARD ) && flipCamera)
            flipCameraBtn.setImageResource(R.drawable.ic_back_camera_icon);
        else
            flipCameraBtn.setImageResource(R.drawable.ic_front_camera_icon);

      /*  showCameraSwitch = intent.getBooleanExtra(SHOW_CAMERA_SWITCH, false);

        if (hasTwoCameras && showCameraSwitch) {
            final ActionBar actionBar=getSupportActionBar();

            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

            ArrayAdapter<CharSequence> adapter=
                    ArrayAdapter.createFromResource(actionBar.getThemedContext(),
                            R.array.cwac_nav,
                            android.R.layout.simple_list_item_1);

            actionBar.setListNavigationCallbacks(adapter, this);
        }
        else {*/
        if (useFFCByDefault) {
            ffc = CwacCameraFragment.newInstance(useFFCByDefault);
            current = ffc;
        } else {
            std = CwacCameraFragment.newInstance(useFFCByDefault);
            current = std;
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.container, current).commit();
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        //     if (useFFCByDefault)
        //       getSupportActionBar().setSelectedNavigationItem(1);
    }

    /* @Override
     public boolean onNavigationItemSelected(int position, long id) {
         if (position == 0) {
             if (std == null) {
                 std= CwacCameraFragment.newInstance(false);
             }

             current=std;
         }
         else {
             if (ffc == null) {
                 ffc= CwacCameraFragment.newInstance(true);
             }

             current=ffc;
         }

         getFragmentManager().beginTransaction()
                 .replace(R.id.container, current).commit();

         return(true);
     }
 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     //   new MenuInflater(this).inflate(R.menu.cwac_main, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.content) {
            Intent i=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File dir=
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File output=new File(dir, "CameraContentDemo.jpeg");

            i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(output));

            startActivityForResult(i, CONTENT_REQUEST);
        }
        else if (item.getItemId() == R.id.landscape) {
            item.setChecked(!item.isChecked());
            current.lockToLandscape(item.isChecked());
            isLockedToLandscape=item.isChecked();
        }*/
        return (super.onOptionsItemSelected(item));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CONTENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // do nothing
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_CAMERA && current != null
                && !current.isSingleShotProcessing()) {
            current.takePicture();

            return (true);
        }

        return (super.onKeyDown(keyCode, event));
    }

    @Override
    public boolean isSingleShotMode() {
        return (singleShot);
    }

    @Override
    public void setSingleShotMode(boolean mode) {
        singleShot = mode;
    }

    @OnClick(R.id.take_picture_button)
    public void takePicture() {
        if (current != null)
            current.takeSimplePicture();
    }

    @OnClick(R.id.flash_button)
    public void flashCamera() {
        if (current != null) {
            if (!useFFCByDefault) {
                flashOn = !flashOn;
                if (flashOn) {
                    flashBtn.setImageResource(R.drawable.ic_flash_icon);
                } else {
                    flashBtn.setImageResource(R.drawable.ic_flash_off_icon);
                }
                // current.flashOnOff();
            } else {
                Toast.makeText(getApplicationContext(), "No flash for front screen!", Toast.LENGTH_SHORT).show();
                flashOn = false;
                flashBtn.setImageResource(R.drawable.ic_flash_off_icon);
            }
        }
    }

    @OnClick(R.id.flip_camera_button)
    public void flipCamera() {
        if (current != null) {
            useFFCByDefault = !useFFCByDefault;
            if(useFFCByDefault){
                if(ffc == null)
                    ffc = CwacCameraFragment.newInstance(true);
                current = ffc;
                flipCameraBtn.setImageResource(R.drawable.ic_front_camera_icon);
            }
            else {
                if(std == null)
                    std = CwacCameraFragment.newInstance(false);
                current = std;
                flipCameraBtn.setImageResource(R.drawable.ic_back_camera_icon);
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, current).commit();
        }
    }
}