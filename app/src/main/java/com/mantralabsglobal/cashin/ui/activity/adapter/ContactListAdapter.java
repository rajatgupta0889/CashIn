package com.mantralabsglobal.cashin.ui.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.view.SelectedContactStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by hello on 7/22/2015.
 */
public class ContactListAdapter extends BaseAdapter implements Filterable
{
    public Context getContext() {
        return context;
    }

    private Context context;

    private SelectedContactStatus selectionStatus;

    private ContactList gcl;

    private ContactList fcl;

    public ContactList getContacts() {
        return gcl;
    }

    public void setGcl(ContactList gcl) {
        this.gcl = gcl;
    }

    public ContactListAdapter(Context context)
    {
        super();
        this.context = context;
        this.gcl=new ContactList();
        this.fcl = new ContactList();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ContactList filteredResult = getFilteredResults(constraint);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.getCount();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                fcl.contacts.clear();
                fcl.contacts.addAll(((ContactList)results.values).contacts);
                ContactListAdapter.this.notifyDataSetChanged();
            }

            private ContactList getFilteredResults(CharSequence constraint){
                ContactList temp  = new ContactList();
                if (constraint.length() == 0){
                    temp =  gcl.clone();
                }
                else {
                    for (Contact obj : gcl.getContacts()) {
                        if (obj.name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            temp.addContact(obj);
                        }
                    }
                }
                return temp;
            }
        };
    }

    public ContactList getFcl() {
        return fcl;
    }

    public void setFcl(ContactList fcl) {
        this.fcl = fcl;
    }

    public void setSelectionStatus(SelectedContactStatus selectionStatus) {
        this.selectionStatus = selectionStatus;
    }

    static class ViewHolderItem
    {
        CheckBox checkBox;
        TextView textView;
    }
    /*Custom View Generation(You may modify this to include other Views) */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolderItem viewHolder;

        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.contact_list_layout, parent, false);

            CheckBox chk_contact = (CheckBox) convertView.findViewById(R.id.chkbxContact);

            TextView tvContactName = (TextView) convertView.findViewById(R.id.text_contact_name);
            //Text to display near checkbox [Here, Contact_Name (Number Label : Phone Number)]
           //+ " ( " + gcl.getContacts().get(position).label + " : " + gcl.getContacts().get(position).phone.toString() + ")");

            viewHolder = new ViewHolderItem();
            viewHolder.checkBox = chk_contact;
            viewHolder.textView = tvContactName;

            chk_contact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                    Contact t = (Contact) arg0.getTag(); //ContactListAdapter.this.gcl.getContact(arg0.getId());
                    if (t != null && arg1) {
                        if (!t.isSelected) {
                            t.isSelected = true;
                            if (selectionStatus != null)
                                selectionStatus.setCurrentSelectionCount(selectionStatus.getCurrentSelectionCount() + 1);
                        }
                    } else if (!arg1 && t != null && t.isSelected) {
                        t.isSelected = false;
                        selectionStatus.setCurrentSelectionCount(selectionStatus.getCurrentSelectionCount() - 1);
                    }
                }

            });

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }
        //Code to get Selected Contacts.
        viewHolder.checkBox.setTag(fcl.getContacts().get(position));

        if(((Contact)viewHolder.checkBox.getTag()).isSelected)
            viewHolder.checkBox.setChecked(true);
        else
            viewHolder.checkBox.setChecked(false);
        viewHolder.textView.setText(fcl.getContacts().get(position).name.toString());

        return convertView;
    }

    @Override
    public int getCount() {

        return fcl.getCount();
    }

    @Override
    public Contact getItem(int arg0) {
        // TODO Auto-generated method stub
        return fcl.getContacts().get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return      Long.parseLong(fcl.getContacts().get(arg0).id);
    }

    public static class ContactList
    {

        private ArrayList<Contact> contacts = new ArrayList<Contact>();

        public ContactList clone(){
            ContactList clone = new ContactList();
            clone.contacts.addAll(this.contacts);
            return clone;
        }

        public int getCount()
        {
            return this.contacts.size();
        }
        public void addContact(Contact c)
        {
            this.contacts.add(c);
        }
        public void removeContact(Contact c)
        {
            this.contacts.remove(c);
        }
        public void removeContact(int id)
        {
            Contact c = getContact(id);
            if(c != null)
                this.contacts.remove(c);
        }
        public Contact getContact(int id)
        {
            Contact tmp=null;
            for(Contact c : contacts)
            {
                if(id==Integer.parseInt(c.id))
                {
                    return c;
                }
            }
            return null;
        }
        public ArrayList<Contact> getContacts()
        {
            return contacts;
        }
        public void setContacts(ArrayList<Contact> c)
        {
            this.contacts=c;
        }

    }

    public static class Contact
    {

        public String id,name,phone,label;
        public boolean isSelected = false;

        public Contact(String tid, String tname,String tphone,String tlabel)
        {
            this.id=tid;
            this.name=tname;
            this.phone=tphone;
            this.label=tlabel;
        }
    }

}
