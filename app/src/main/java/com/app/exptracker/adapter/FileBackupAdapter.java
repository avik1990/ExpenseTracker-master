package com.app.exptracker.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.exptracker.R;
import com.app.exptracker.utility.Utils;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;

public class FileBackupAdapter extends RecyclerView.Adapter<FileBackupAdapter.MyViewHolder> {

    private List<File> expenseList;
    Context mContext;
    GetFileNames getFileNameFromAdapter;

    public interface GetFileNames {
        void returnedFileName(String month, String sort);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_fileName, tv_filePath;
        LinearLayout ll_cat;
        ImageView iv_share;

        public MyViewHolder(View view) {
            super(view);
            tv_fileName = view.findViewById(R.id.tv_fileName);
            tv_filePath = view.findViewById(R.id.tv_filePath);
            ll_cat = view.findViewById(R.id.ll_cat);
            iv_share = view.findViewById(R.id.iv_share);
        }
    }

    public FileBackupAdapter(Context mContext, List<File> expenseList, GetFileNames getFileNameFromAdapter) {
        this.mContext = mContext;
        this.getFileNameFromAdapter = getFileNameFromAdapter;
        this.expenseList = expenseList;
        Collections.reverse(this.expenseList);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final File c = expenseList.get(position);
        holder.tv_fileName.setText(c.getName());
        holder.tv_filePath.setText(c.getAbsolutePath());
        holder.ll_cat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File image = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Utils.BACKUP_FOLDER_PATH, holder.tv_fileName.getText().toString());
                try {
                    Utils.openFile(mContext, image);
                } catch (Exception e) {
                }
            }
        });

        holder.iv_share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File image = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Utils.BACKUP_FOLDER_PATH, holder.tv_fileName.getText().toString());
                Uri uri = getUriForFile(mContext, mContext.getPackageName() + ".provider", image);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("*/*");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                mContext.startActivity(Intent.createChooser(share, "Select"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_filebackup, parent, false);
        return new MyViewHolder(v);
    }


}
