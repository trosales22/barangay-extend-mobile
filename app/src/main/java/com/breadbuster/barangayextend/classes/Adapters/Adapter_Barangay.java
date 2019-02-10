package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.Beans.BarangayBean;
import com.breadbuster.barangayextend.classes.BottomSheetDialog.Barangay_BottomSheetDialogFragment;
import com.breadbuster.barangayextend.classes.DataObjects.BarangayDataObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Barangay extends RecyclerView.Adapter<Adapter_Barangay.ViewHolder> {
    private List<BarangayDataObject> listOfBarangay;
    private Context context;
    private View view;
    private FragmentManager fragmentManager;

    public static BarangayBean barangayBean = new BarangayBean();

    public Adapter_Barangay(List<BarangayDataObject> listOfBarangay, Context context, FragmentManager fragmentManager) {
        this.listOfBarangay = listOfBarangay;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.barangay, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try{
            final BarangayDataObject barangayDataObject = listOfBarangay.get(position);

            holder.txtBarangayName.setText(barangayDataObject.getBarangayName());
            holder.txtBarangayDescription.setText(barangayDataObject.getBarangayDescription());

            Picasso.with(context)
                    .load(barangayDataObject.getBarangayLogo())
                    .error(R.drawable.ic_broken_image_black)
                    .placeholder(R.drawable.ic_broken_image_black)
                    .into(holder.barangayLogo);

            holder.cardView_barangay.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    barangayBean.setBarangayID(barangayDataObject.getBarangayID());
                    barangayBean.setBarangayLogo(barangayDataObject.getBarangayLogo());
                    barangayBean.setBarangayName(barangayDataObject.getBarangayName());
                    barangayBean.setBarangayDescription(barangayDataObject.getBarangayDescription());

                    Barangay_BottomSheetDialogFragment barangay_bottomSheetDialogFragment = new Barangay_BottomSheetDialogFragment();
                    barangay_bottomSheetDialogFragment.show(fragmentManager,barangay_bottomSheetDialogFragment.getTag());
                }
            });
        }catch(Exception ex){
            Snackbar snackbar = Snackbar
                    .make(view, ex.toString(), Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    @Override
    public int getItemCount() {
        return listOfBarangay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        @InjectView(R.id.txtBarangayName) TextView txtBarangayName;
        @InjectView(R.id.txtBarangayDescription) TextView txtBarangayDescription;
        @InjectView(R.id.barangayLogo) CircleImageView barangayLogo;
        @InjectView(R.id.cardView_barangay) CardView cardView_barangay;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }

    public void setFilter(List<BarangayDataObject> barangayModels) {
        listOfBarangay = new ArrayList<>();
        listOfBarangay.addAll(barangayModels);
        notifyDataSetChanged();
    }
}
