package com.app.exptracker.user;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.ImagePickerActivity;
import com.app.exptracker.utility.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUserActivity extends BaseActivity implements View.OnClickListener, UserAdapter.GetUserIDAdapter {

    private static final String TAG = "AddUserActivity";
    FloatingActionButton fab_add;
    Context context;
    DatabaseHelper db;
    ProgressDialog p;
    TextView tv_expenses, tv_income;
    TextView sp_month, tv_currentyear;
    ImageView iv_dropdown, btn_menu, btn_back;
    EditText et_category;
    long returnedId;
    List<ExcelDataModel> listUser = new ArrayList<>();
    UserAdapter userAdapter;
    RecyclerView rl_recyclerview;
    private final int requestCode = 20;
    CircleImageView iv_imgview1;
    String userID = "";
    public static final int REQUEST_IMAGE = 100;


    @Override
    protected void InitListner() {
        context = this;
        db = new DatabaseHelper(context);
        fab_add.setOnClickListener(this);

        p = new ProgressDialog(context);
        p.setMessage("Please wait...");
        p.setIndeterminate(false);
        p.setCancelable(false);
        fetchUserData();
    }

    @Override
    protected void InitResources() {
        ImagePickerActivity.INTENT_SHOW_SHADE = 1;
        rl_recyclerview = findViewById(R.id.rl_recyclerview);
        et_category = findViewById(R.id.et_category);
        tv_currentyear = findViewById(R.id.tv_currentyear);
        tv_expenses = findViewById(R.id.tv_expenses);
        tv_income = findViewById(R.id.tv_income);
        fab_add = findViewById(R.id.fab_add);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        iv_dropdown.setVisibility(View.GONE);
        sp_month = findViewById(R.id.sp_month);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        //sp_month.setVisibility(View.GONE);
        sp_month.setText("Add Users");
    }

    @Override
    protected void InitPermission() {

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_adduser;
    }

    @Override
    public void onClick(View v) {
        if (v == fab_add) {
            if (et_category.getText().toString().trim().isEmpty()) {
                Utils.ShowToast(context, "Add User Name");
                return;
            }

            ExcelDataModel excelDataModel = new ExcelDataModel();
            excelDataModel.setUserName(et_category.getText().toString().trim());

            returnedId = db.addUser(excelDataModel);

            if (returnedId < 0) {
                Utils.ShowToast(context, "User Already Exists");
            } else {
                // finish();
                et_category.setText("");
                fetchUserData();
                Utils.ShowToast(context, "User Added");
            }

        } else if (v == btn_back) {
            finish();
        }
    }

    public void fetchUserData() {
        listUser.clear();
        listUser = db.getAllUsers();
        if (listUser.size() > 0) {
            userAdapter = new UserAdapter(context, listUser, this);
            rl_recyclerview.setAdapter(userAdapter);

            rl_recyclerview.setOnTouchListener((view, event) -> {
                int eventaction = event.getAction();

                switch (eventaction) {
                    case MotionEvent.ACTION_DOWN:
                        fab_add.hide();
                        //Toast.makeText(context, "ACTIO", Toast.LENGTH_SHORT).show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        //Toast.makeText(context, "ACTIOMove", Toast.LENGTH_SHORT).show();
                        fab_add.hide();
                        break;

                    case MotionEvent.ACTION_UP:
                        //Toast.makeText(context, "ACTIOUp", Toast.LENGTH_SHORT).show();
                        fab_add.show();
                        break;
                }
                return false;
            });

        }
    }

    @Override
    public void returnPosition(int pos) {
    }

    @Override
    public void returnPositionImageView(int pos, CircleImageView iv_imgview) {
        iv_imgview1 = iv_imgview;
        userID = listUser.get(pos).getUserId();
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    //loading profile image from local cache
                    //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    iv_imgview1.setImageBitmap(bitmap);
                    db.updateUserImage(userID, Utils.convert(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }


    private void launchCameraIntent() {
        Intent intent = new Intent(AddUserActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(AddUserActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddUserActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}