package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Beans.UserBean;
import com.breadbuster.barangayextend.classes.BottomSheetDialog.Users_BottomSheetDialogFragment;
import com.breadbuster.barangayextend.classes.DataObjects.UserDataObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Users extends RecyclerView.Adapter<Adapter_Users.ViewHolder>{
    private List<UserDataObject> listUsers;
    private Context context;
    private View view;
    private FragmentManager fragmentManager;

    public static UserBean userBean = new UserBean();

    public Adapter_Users(List<UserDataObject> listUsers, Context context, FragmentManager fragmentManager) {
        this.listUsers = listUsers;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_users, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            final UserDataObject userItem = listUsers.get(position);

            holder.txtFullname.setText(userItem.getFirstname() + " " + userItem.getLastname());
            holder.txtUsername.setText(userItem.getUsername());

            if(userItem.getImgProfilePicture().isEmpty()){
                holder.imgProfilePicture.setImageResource(R.mipmap.ic_launcher_round);
            }else {
                Picasso.with(context)
                        .load(userItem.getImgProfilePicture())
                        .error(R.mipmap.ic_launcher_round)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(holder.imgProfilePicture);
            }

            holder.cardView_users.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    userBean.setProfilePicture(userItem.getImgProfilePicture());
                    userBean.setFullname(userItem.getFirstname() + " " + userItem.getLastname());
                    userBean.setEmailAddress(userItem.getEmailAddress());
                    userBean.setUsername(userItem.getUsername());
                    userBean.setPhoneNumber(userItem.getPhoneNumber());
                    userBean.setGender(userItem.getGender());
                    userBean.setUserType(userItem.getUserType());
                    userBean.setBarangay(userItem.getBarangay());

                    Users_BottomSheetDialogFragment users_bottomSheetDialogFragment = new Users_BottomSheetDialogFragment();
                    users_bottomSheetDialogFragment.show(fragmentManager,users_bottomSheetDialogFragment.getTag());
                }
            });
        }catch(Exception ex){
            Log.e("Adapter_Users",ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.txtFullname) TextView txtFullname;
        @InjectView(R.id.txtUsername) TextView txtUsername;

        @InjectView(R.id.imgProfilePicture) CircleImageView imgProfilePicture;
        @InjectView(R.id.cardView_users) CardView cardView_users;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    public void setFilter(List<UserDataObject> userModels) {
        listUsers = new ArrayList<>();
        listUsers.addAll(userModels);
        notifyDataSetChanged();
    }
}
