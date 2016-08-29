package com.example.shreyaprabhu.oswar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shreya Prabhu on 8/28/2016.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {
    private Context mContext;
    private List<ContactModel> contactList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView contact_name, contact_phone;

        public MyViewHolder(View view) {
            super(view);
            contact_name = (TextView) view.findViewById(R.id.contact_name);
            contact_phone = (TextView) view.findViewById(R.id.contact_phone);

        }
    }


    public ContactsAdapter(Context mContext, List<ContactModel> contactModelList) {
        this.mContext = mContext;
        this.contactList = contactModelList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ContactModel contactModel = contactList.get(position);
        holder.contact_phone.setText(contactModel.getPhone());
        holder.contact_name.setText(contactModel.getName());

    }


    @Override
    public int getItemCount() {
        return contactList.size();

    }
}
