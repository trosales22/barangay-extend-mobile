package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.DataObjects.Feedbacks_DataObject;
import com.breadbuster.barangayextend.classes.urls;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

public class Adapter_Feedbacks extends RecyclerView.Adapter<Adapter_Feedbacks.ViewHolder> {
    private List<Feedbacks_DataObject> feedbacksDataObjectList;
    private Context context;
    private View view;

    public Adapter_Feedbacks(List<Feedbacks_DataObject> feedbacksDataObjectList, Context context) {
        this.feedbacksDataObjectList = feedbacksDataObjectList;
        this.context = context;
    }

    @Override
    public Adapter_Feedbacks.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_feedbacks, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_Feedbacks.ViewHolder holder, int position) {
        final Feedbacks_DataObject feedbacksDataObject = feedbacksDataObjectList.get(position);

        holder.lblFeedbackBy.setText(feedbacksDataObject.getFeedBackBy());
        holder.lblFeedbackBy_dateAndTimeSubmitted.setText(feedbacksDataObject.getFeedbackBy_dateAndTimeSubmitted());
        holder.lblFeedback.setText(feedbacksDataObject.getFeedback());

        if(feedbacksDataObject.getFeedbackBy_profilePicture().isEmpty()){
            holder.imgfeedbackBy_profilePicture.setImageResource(R.mipmap.ic_launcher_round);
        }else {
            Picasso.with(context)
                    .load(feedbacksDataObject.getFeedbackBy_profilePicture())
                    .error(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .into(holder.imgfeedbackBy_profilePicture);
        }

    }

    @Override
    public int getItemCount() {
        return feedbacksDataObjectList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.cardView_feedbacks) CardView cardView_feedbacks;
        @InjectView(R.id.imgfeedbackBy_profilePicture) CircleImageView imgfeedbackBy_profilePicture;
        @InjectView(R.id.lblFeedbackBy) TextView lblFeedbackBy;
        @InjectView(R.id.lblFeedbackBy_dateAndTimeSubmitted) TextView lblFeedbackBy_dateAndTimeSubmitted;
        @InjectView(R.id.lblFeedback) TextView lblFeedback;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this,itemView);
        }
    }
}
