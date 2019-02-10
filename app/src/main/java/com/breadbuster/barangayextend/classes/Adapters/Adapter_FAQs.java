package com.breadbuster.barangayextend.classes.Adapters;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.breadbuster.barangayextend.R;
import com.breadbuster.barangayextend.classes.DataObjects.FAQs_DataObject;

import java.util.List;

public class Adapter_FAQs extends RecyclerView.Adapter<Adapter_FAQs.ViewHolder> {
    private List<FAQs_DataObject> FAQS_list;
    private Context context;
    private View view;

    public Adapter_FAQs(List<FAQs_DataObject> FAQS_list, Context context) {
        this.FAQS_list = FAQS_list;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.frequently_asked_questions, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            final FAQs_DataObject FAQsDataObject = FAQS_list.get(position);

            holder.txtQuestion.setText(FAQsDataObject.getQuestion());
            holder.txtAnswer.setText(FAQsDataObject.getAnswer());
        }catch(Exception ex){
            Snackbar snackbar = Snackbar
                    .make(view, ex.toString(), Snackbar.LENGTH_LONG);

            snackbar.show();
        }
    }

    @Override
    public int getItemCount() {
        return FAQS_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtQuestion,txtAnswer;
        public LinearLayout linearLayout_FAQs;

        public ViewHolder(View itemView) {
            super(itemView);

            txtQuestion = itemView.findViewById(R.id.txtQuestion);
            txtAnswer = itemView.findViewById(R.id.txtAnswer);
            linearLayout_FAQs = itemView.findViewById(R.id.linearLayout_faqs);
        }
    }
}
