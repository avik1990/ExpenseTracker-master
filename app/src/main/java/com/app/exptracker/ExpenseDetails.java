package com.app.exptracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.BaseActivity;
import com.app.exptracker.utility.CircularTextView;
import com.app.exptracker.utility.ImagePickerActivity;
import com.app.exptracker.utility.Utils;
import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.jsibbold.zoomage.ZoomageView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.content.FileProvider.getUriForFile;

public class ExpenseDetails extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ExpenseDetails";
    Context context;
    DatabaseHelper db;
    List<ExcelDataModel> list_transtype = new ArrayList<>();
    String cat_id = "";
    ImageView iv_dropdown;
    TextView sp_month;
    ImageView btn_back, btn_menu;

    TextView tv_category;
    TextView tv_exp_type;
    TextView tv_amt;
    TextView tv_date;
    TextView tv_memo;
    FloatingActionButton fab_edit;
    String trans_id;
    ImageView img_delete, iv_catimg;
    TextView tv_payment_mode;
    ImageView iv_payment;
    TextView tv_users;
    CircularTextView cv_text;
    CircleImageView iv_user;
    ProgressDialog progressDialog;
    ImageView iv_bill;
    TextView tv_fileSize, tv_fileName;
    String m_Text = "";
    ImageView img_camera;
    public static final int REQUEST_IMAGE = 100;
    Bitmap bitmap;
    String fileName = "", qualityType = "High";
    ProgressDialog pImageDialog;
    LinearLayout llContainer;
    Set<String> listFiles = new HashSet<>();
    Splitter splitter;
    Joiner joiner;
    private static final int RESULT_UPDATED = 9999;
    List<ExcelDataModel> listTags = new ArrayList<>();
    FlexboxLayout tag_container;

    @Override
    protected void InitListner() {
        ImagePickerActivity.INTENT_SHOW_SHADE = 0;
        context = this;
        db = new DatabaseHelper(context);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

        pImageDialog = new ProgressDialog(context);
        pImageDialog.setMessage("Processing Image...");
        pImageDialog.setCancelable(false);
        pImageDialog.setCanceledOnTouchOutside(false);
        splitter = Splitter.on(',').omitEmptyStrings().trimResults();
        joiner = Joiner.on(',').skipNulls();
        new AsyncTaskDetails().execute();
    }

    public String cleanUpCommas(String string) {
        return joiner.join(splitter.split(string));
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setData() {
        try {
            if (list_transtype.get(0).getFileName().isEmpty()) {
                llContainer.removeAllViews();
                iv_bill.setVisibility(View.GONE);
                tv_fileName.setVisibility(View.GONE);
                tv_fileSize.setVisibility(View.GONE);
            } else {
                iv_bill.setVisibility(View.VISIBLE);
                tv_fileName.setVisibility(View.VISIBLE);
                tv_fileSize.setVisibility(View.VISIBLE);

                generateImageViews();
            }
        } catch (Exception e) {
            iv_bill.setVisibility(View.GONE);
            e.printStackTrace();
        }

        if (listTags.size() > 0) {
            inflateTagsLayout();
        }

        tv_category.setText(list_transtype.get(0).getCategory());
        tv_exp_type.setText(list_transtype.get(0).getIncome_Expenses());
        tv_amt.setText(String.valueOf(list_transtype.get(0).getAmount_()));

        if (!list_transtype.get(0).getTime().isEmpty()) {
            tv_date.setText(Utils.getFormattedDateTime1(list_transtype.get(0).getTime()));
        } else {
            tv_date.setText(Utils.getFormattedDate(list_transtype.get(0).getDate()));
        }

        tv_memo.setText(list_transtype.get(0).getMemo());
        tv_users.setText(list_transtype.get(0).getUserName());

        try {
            if (!list_transtype.get(0).getUser_relation().isEmpty()
                    || list_transtype.get(0).getUser_relation() != null) {
                iv_user.setVisibility(View.VISIBLE);
                byte[] decodedString = Base64.decode(list_transtype.get(0).getUser_relation(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                iv_user.setImageBitmap(decodedByte);
            } else {
                iv_user.setImageResource(R.drawable.ic_avatar);
            }
        } catch (Exception e) {
            iv_user.setImageResource(R.drawable.ic_avatar);
        }

        try {
            if (!list_transtype.get(0).getPayment_mode().isEmpty()) {
                tv_payment_mode.setText(list_transtype.get(0).getPayment_mode());
            }
        } catch (Exception e) {
        }

        try {
            if (list_transtype.get(0).getIcon_name() != null && !list_transtype.get(0).getIcon_name().isEmpty()) {
                cv_text.setVisibility(View.GONE);
                iv_catimg.setVisibility(View.VISIBLE);
                int imageid = getResources().getIdentifier(list_transtype.get(0).getIcon_name(), "drawable", getPackageName());
                iv_catimg.setImageResource(imageid);
            } else {
                cv_text.setVisibility(View.VISIBLE);
                iv_catimg.setVisibility(View.GONE);
                cv_text.setText(list_transtype.get(0).getCategory().substring(0, 2).toUpperCase());
            }
        } catch (Exception e) {
            cv_text.setVisibility(View.VISIBLE);
            iv_catimg.setVisibility(View.GONE);
            cv_text.setText(list_transtype.get(0).getCategory().substring(0, 2).toUpperCase());
        }

        if (list_transtype.get(0).getPayment_mode().equalsIgnoreCase("Cash")) {
            iv_payment.setImageResource(R.drawable.ic_rupee_icon);
        } else if (list_transtype.get(0).getPayment_mode().equalsIgnoreCase("Credit Card")) {
            iv_payment.setImageResource(R.drawable.ic_visa_icon);
        } else if (list_transtype.get(0).getPayment_mode().equalsIgnoreCase("Cheque/Debit Card")) {
            iv_payment.setImageResource(R.drawable.ic_cheque_icon);
        }

        progressDialog.dismiss();

    }

    private void inflateTagsLayout() {
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(5, 5, 5, 5);
        tag_container.removeAllViews();
        for (int i = 0; i < listTags.size(); i++) {
            final TextView tv = new TextView(getApplicationContext());
            tv.setText(listTags.get(i).getTagName());
            //tv.setHeight(30);
            tv.setTextSize(13.0f);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(Color.parseColor("#000000"));
            tv.setBackground(getResources().getDrawable(R.drawable.rounded_corner));
            tv.setId(i + 1);
            tv.setLayoutParams(buttonLayoutParams);
            tv.setTag(i);
            tv.setPadding(20, 10, 20, 10);
            tag_container.addView(tv);
        }

    }

    private void generateImageViews() {
        //listFiles.clear();
        if (!list_transtype.get(0).getFileName().isEmpty()) {
            listFiles.clear();
            String list_transtype_img[] = list_transtype.get(0).getFileName().split(",");
            for (int i = 0; i < list_transtype_img.length; i++) {
                listFiles.add(list_transtype_img[i]);
            }
        } else {
            llContainer.removeAllViews();
        }

        LayoutInflater layoutInflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        llContainer.removeAllViews();
        int i = -1;

        List<String> stringsList = new ArrayList<>(listFiles);

        if (listFiles.size() == 0) {
            llContainer.removeAllViews();
        }

        for (String s : listFiles) {
            i++;
            View mainlayout = layoutInflator.inflate(R.layout.row_image_inflater, null);
            ImageView iv_bill = mainlayout.findViewById(R.id.iv_bill);
            ImageView iv_delete = mainlayout.findViewById(R.id.iv_delete);
            RelativeLayout rlDelete = mainlayout.findViewById(R.id.rlDelete);
            TextView tv_fileName = mainlayout.findViewById(R.id.tv_fileName);
            TextView tv_fileSize = mainlayout.findViewById(R.id.tv_fileSize);
            mainlayout.setId(i);

            try {
                File image = new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + Utils.FOLDER_PATH, s);
                Uri uri = getUriForFile(ExpenseDetails.this, getPackageName() + ".provider", image);
                Bitmap b = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                iv_bill.setImageBitmap(b);
                tv_fileName.setText(s);
                long fileSize = image.length() / 1024;
                tv_fileSize.setText(String.valueOf(fileSize) + " KB");


                tv_fileName.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Edit Filename");

                        final EditText input = new EditText(context);
                        input.setText(stringsList.get(mainlayout.getId()).substring(0, stringsList.get(mainlayout.getId()).length() - 4));
                        input.setInputType(InputType.TYPE_CLASS_TEXT);

                        input.setSelection(stringsList.get(mainlayout.getId()).substring(0, stringsList.get(mainlayout.getId()).length() - 4).length());
                        builder.setView(input);

                        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                m_Text = input.getText().toString().replaceAll(",", "");

                                try {
                                    File dir = new File(Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + Utils.FOLDER_PATH);
                                    if (dir.exists()) {
                                        File from = new File(dir, stringsList.get(mainlayout.getId()));
                                        File to = new File(dir, m_Text + ".jpg");
                                        to.delete();

                                        if (from.exists()) {
                                            boolean success = from.renameTo(to);
                                            if (success) {
                                                stringsList.set(mainlayout.getId(), m_Text + ".jpg");
                                                String fileName = stringsList.toString();
                                                if (db.updateFileName(trans_id, cleanUpCommas(fileName.replace("[", "").replaceAll("]", ""))) > 0) {
                                                    Utils.ShowToast(context, "Data Updated Successfully");
                                                    new AsyncTaskDetails().execute();
                                                }
                                            }

                                        } else {
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();
                    }
                });

                mainlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(context, R.style.Theme_FullScreen);
                        dialog.setContentView(R.layout.fullscreen_dialog);

                        final ZoomageView iv_image = dialog.findViewById(R.id.iv_image);
                        ImageView iv_share = dialog.findViewById(R.id.iv_share);
                        Button dialog_close = dialog.findViewById(R.id.dialog_close);

                        Glide.with(context)
                                .load(image)
                                .into(iv_image);

                        dialog_close.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });

                        iv_share.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                // Utils.ShowToast(context, "" + mainlayout.getId());
                                File image = new File(Environment.getExternalStorageDirectory()
                                        .getAbsolutePath() + Utils.FOLDER_PATH, stringsList.get(mainlayout.getId()));
                                Uri uri = getUriForFile(ExpenseDetails.this, getPackageName() + ".provider", image);
                                Intent share = new Intent(Intent.ACTION_SEND);
                                share.setType("image/jpeg");
                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                startActivity(Intent.createChooser(share, "Select"));
                            }
                        });

                        rlDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setMessage("Are you sure to delete?");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                String s = listFiles.toString();
                                                listFiles.remove(stringsList.get(mainlayout.getId()));
                                                s = listFiles.toString();
                                                if (db.updateFileName(trans_id, cleanUpCommas(s.replace("[", "").replaceAll("]", ""))) > 0) {
                                                    File image = new File(Environment.getExternalStorageDirectory()
                                                            .getAbsolutePath() + Utils.FOLDER_PATH, stringsList.get(mainlayout.getId()));
                                                    if (image.exists()) {
                                                        image.delete();
                                                    }
                                                    new AsyncTaskDetails().execute();
                                                }

                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        });

                        iv_delete.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setMessage("Are you sure to delete?");
                                builder1.setCancelable(true);

                                builder1.setPositiveButton(
                                        "Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                                String s = listFiles.toString();
                                                listFiles.remove(stringsList.get(mainlayout.getId()));
                                                s = listFiles.toString();
                                                db.updateFileName(trans_id, cleanUpCommas(s.replace("[", "").replaceAll("]", "")));
                                                new AsyncTaskDetails().execute();
                                            }
                                        });

                                builder1.setNegativeButton(
                                        "No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        });
                        dialog.show();
                    }
                });


            } catch (Exception e) {
            }
            llContainer.addView(mainlayout);
        }


       /* for (int i = 0; i < listFiles.size(); i++) {

        }*/
    }


    @Override
    protected void InitResources() {
        tag_container = findViewById(R.id.tag_container);
        llContainer = findViewById(R.id.llContainer);
        img_camera = findViewById(R.id.img_camera);
        img_camera.setVisibility(View.VISIBLE);
        tv_fileName = findViewById(R.id.tv_fileName);
        tv_fileName.setOnClickListener(this);
        tv_fileSize = findViewById(R.id.tv_fileSize);
        iv_bill = findViewById(R.id.iv_bill);
        iv_user = findViewById(R.id.iv_user);
        trans_id = getIntent().getStringExtra("trans_id");
        tv_users = findViewById(R.id.tv_users);
        cv_text = findViewById(R.id.cv_text);
        cv_text.setSolidColor("#D3D3D3");
        iv_payment = findViewById(R.id.iv_payment);
        iv_catimg = findViewById(R.id.iv_catimg);
        sp_month = findViewById(R.id.sp_month);
        sp_month.setText("Details");
        fab_edit = findViewById(R.id.fab_edit);
        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        iv_dropdown = findViewById(R.id.iv_dropdown);
        btn_back = findViewById(R.id.btn_back);
        btn_menu = findViewById(R.id.btn_menu);
        btn_menu.setVisibility(View.GONE);
        btn_back.setVisibility(View.VISIBLE);
        btn_back.setOnClickListener(this);
        img_camera.setOnClickListener(this);
        iv_dropdown.setVisibility(View.GONE);
        fab_edit = findViewById(R.id.fab_edit);
        tv_category = findViewById(R.id.tv_category);
        tv_exp_type = findViewById(R.id.tv_exp_type);
        tv_amt = findViewById(R.id.tv_amt);
        tv_date = findViewById(R.id.tv_date);
        tv_memo = findViewById(R.id.tv_memo);
        img_delete = findViewById(R.id.img_delete);
        img_delete.setVisibility(View.VISIBLE);
        fab_edit.setOnClickListener(this);
        img_delete.setOnClickListener(this);
    }


    @Override
    protected void InitPermission() {
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_details;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == img_delete) {
            // String msg = "Are you sure to delete?";
            if (!list_transtype.get(0).getFileName().isEmpty()) {
                OpenDeleteDialog();
            } else {
                openAlert();
            }

        } else if (v == fab_edit) {
            Intent i = new Intent(context, AddExpenseIncome.class);
            i.putExtra("from", TAG);
            i.putExtra("trans_id", trans_id);
            startActivityForResult(i, RESULT_UPDATED);
        } else if (v == tv_fileName) {

        } else if (v == img_camera) {
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


    }

    public void openAlert() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Are you sure to delete?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Utils.setIsUpdated(context, true);
                        db.deleteTrasactionDataRow(trans_id);
                        db.deleteTrasactionDataRowfromTags(trans_id);
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }


    private void OpenDeleteDialog() {
        final Dialog dialogDelete = new Dialog(context);
        dialogDelete.setContentView(R.layout.custom_delete_dialog);
        dialogDelete.setTitle("");
        TextView tv_both = dialogDelete.findViewById(R.id.tv_both);
        TextView tv_delcat = dialogDelete.findViewById(R.id.tv_delcat);
        TextView tv_cancel = dialogDelete.findViewById(R.id.tv_cancel);
        // TextView tv_ok = dialogDelete.findViewById(R.id.tv_ok);


        tv_delcat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete.cancel();
                Utils.setIsUpdated(context, true);
                db.deleteTrasactionDataRow(trans_id);
                db.deleteTrasactionDataRowfromTags(trans_id);
                finish();
            }
        });

        tv_both.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDelete.cancel();
                db.deleteTrasactionDataRow(trans_id);
                db.deleteTrasactionDataRowfromTags(trans_id);
                for (String s : listFiles) {
                    File image = new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + Utils.FOLDER_PATH, s);
                    // Uri uri = getUriForFile(ExpenseDetails.this, getPackageName() + ".provider", image);
                    if (image.exists()) {
                        image.delete();
                    }
                }
                finish();
            }
        });


        tv_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogDelete.dismiss();
            }
        });

        dialogDelete.show();
    }


    private class AsyncTaskDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            list_transtype.clear();
            list_transtype = db.GetTransactionById(trans_id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (list_transtype.size() > 0) {
                new AsyncfetchTags().execute();
            }
        }
    }

    private class AsyncfetchTags extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            listTags.clear();
            listTags = db.GetTagsByTransId(trans_id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setData();
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
        Intent intent = new Intent(ExpenseDetails.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 0); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 0);
        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(ExpenseDetails.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 0); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 0);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseDetails.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    fileName = Utils.getCurrentDateTime() + ".jpg";
                    qualityType = "High";
                    new AsyncSaveAndShowImage().execute();
                } catch (IOException e) {
                    bitmap = null;
                    e.printStackTrace();
                }
            }
        } else if (requestCode == RESULT_UPDATED) {
            new AsyncTaskDetails().execute();
        }
    }

    private class AsyncSaveAndShowImage extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            storeImage(bitmap, fileName);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pImageDialog.dismiss();
            showImageInDialog();
        }
    }

    public boolean storeImage(Bitmap imageData, String filename) {
        File sdIconStorageDir = null;

        sdIconStorageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Utils.FOLDER_PATH);
        if (!sdIconStorageDir.exists()) {
            sdIconStorageDir.mkdirs();
        }
        try {
            String filePath = sdIconStorageDir.toString() + File.separator + filename;
            FileOutputStream fileOutputStream = new FileOutputStream(filePath, false);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            if (qualityType.equalsIgnoreCase("High")) {
                imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            } else if (qualityType.equalsIgnoreCase("Medium")) {
                imageData.compress(Bitmap.CompressFormat.JPEG, 30, bos);
            } else if (qualityType.equalsIgnoreCase("Low")) {
                imageData.compress(Bitmap.CompressFormat.JPEG, 10, bos);
            }
            bos.flush();
            bos.close();

            listFiles.add(filename);

            if (listFiles.size() > 0) {
                String s = listFiles.toString();
                Log.e("FILESSSSS", cleanUpCommas(s.replace("[", "").replaceAll("]", "")));
                db.updateFileName(trans_id, cleanUpCommas(s.replace("[", "").replaceAll("]", "")));
            }
        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void showImageInDialog() {
        final Dialog dialog = new Dialog(context, R.style.Theme_FullScreen);
        dialog.setContentView(R.layout.fullscreen_dialog_with_radios);

        try {
            File image = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + Utils.FOLDER_PATH, fileName);
            final ZoomageView iv_image = dialog.findViewById(R.id.iv_image);
            ImageView iv_cross = dialog.findViewById(R.id.iv_cross);
            Button dialog_close = dialog.findViewById(R.id.dialog_close);
            RadioGroup rgQuality = dialog.findViewById(R.id.rgQuality);
            Button dialog_save = dialog.findViewById(R.id.dialog_save);


            Glide.with(context)
                    .load(image)
                    .into(iv_image);


            dialog_save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    storeImage(bitmap, fileName);
                    new AsyncTaskDetails().execute();
                    dialog.cancel();
                }
            });


            rgQuality.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                switch (checkedId) {
                    case R.id.rbHighQuality:
                        qualityType = "High";
                        break;
                    case R.id.rbMediumQuality:
                        qualityType = "Medium";
                        break;
                    case R.id.rbLowQuality:
                        qualityType = "Low";
                        break;
                }
            });

            dialog_close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            iv_cross.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

        } catch (Exception e) {
        }
        dialog.show();
    }
}
