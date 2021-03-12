package com.app.exptracker.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.exptracker.R;
import com.app.exptracker.database.DatabaseHelper;
import com.app.exptracker.model.ExcelDataModel;
import com.app.exptracker.utility.Utils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<ExcelDataModel> countryList;
    Context mContext;
    GetUserIDAdapter getMonthFromAdapter;
    DatabaseHelper db;
    String m_Text = "";

    public interface GetUserIDAdapter {
        public void returnPosition(int pos);

        public void returnPositionImageView(int pos, CircleImageView iv_imgview);
    }

    /**
     * View holder class
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_users;
        ImageView iv_delete, iv_edit;
        CircleImageView iv_img;

        public MyViewHolder(View view) {
            super(view);
            tv_users = view.findViewById(R.id.tv_users);
            iv_delete = view.findViewById(R.id.iv_delete);
            iv_edit = view.findViewById(R.id.iv_edit);
            iv_img = view.findViewById(R.id.iv_img);
        }
    }

    public UserAdapter(Context mContext, List<ExcelDataModel> countryList, GetUserIDAdapter getMonthFromAdapter) {
        this.mContext = mContext;
        this.getMonthFromAdapter = getMonthFromAdapter;
        this.countryList = countryList;
        db = new DatabaseHelper(mContext);
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
        final ExcelDataModel c = countryList.get(position);
        holder.tv_users.setText(c.getUserName());
        if (position == 0) {
            holder.iv_delete.setVisibility(View.GONE);
            holder.iv_edit.setVisibility(View.GONE);
        } else {
            holder.iv_delete.setVisibility(View.VISIBLE);
            holder.iv_edit.setVisibility(View.VISIBLE);
        }

        try {
            if (!c.getUser_relation().isEmpty() || c.getUser_relation() != null) {
                byte[] decodedString = Base64.decode(c.getUser_relation(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.iv_img.setImageBitmap(decodedByte);
            }
        } catch (Exception e) {

        }


        holder.iv_img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getMonthFromAdapter.returnPosition(position);
                getMonthFromAdapter.returnPositionImageView(position, holder.iv_img);
            }
        });

       /* holder.tv_users.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit User");

                final EditText input = new EditText(mContext);
                input.setText(c.getUserName());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        if (db.updateNote(c.getUserId(), m_Text) > 0) {
                            ((AddUserActivity) mContext).fetchUserData();
                            Utils.ShowToast(mContext, "Data Updated Successfully");
                        } else {
                            Utils.ShowToast(mContext, "Please try again.Data may already exists");
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
                return false;
            }
        });*/

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (db.CheckUserIdCount(c.getUserId()) <= 0) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Delete User")
                            .setMessage("Are you sure you want to delete this user?")

                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    db.deleteUserDataRow(c.getUserId());
                                    ((AddUserActivity) mContext).fetchUserData();
                                }
                            })

                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Utils.ShowToast(mContext, "Data is already associated with. User can't be deleted");
                }
            }
        });
        holder.iv_edit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit User");

                //LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               // buttonLayoutParams.setMargins(20, 0, 20, 0);

                final EditText input = new EditText(mContext);
                input.setText(c.getUserName());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setSelection(c.getUserName().length());
                builder.setView(input);
                //input.setLayoutParams(buttonLayoutParams);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        if (db.updateNote(c.getUserId(), m_Text) > 0) {
                            ((AddUserActivity) mContext).fetchUserData();
                            Utils.ShowToast(mContext, "Data Updated Successfully");
                        } else {
                            Utils.ShowToast(mContext, "Please try again.Data may already exists");
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
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_users, parent, false);
        return new MyViewHolder(v);
    }
}
