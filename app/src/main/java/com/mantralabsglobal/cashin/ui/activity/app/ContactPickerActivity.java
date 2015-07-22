package com.mantralabsglobal.cashin.ui.activity.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.ui.activity.adapter.ContactListAdapter;
import com.mantralabsglobal.cashin.ui.view.SelectedContactStatus;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by hello on 7/22/2015.
 */
public class ContactPickerActivity extends BaseActivity implements SearchView.OnQueryTextListener{

    private static final String TAG = "ContactPickerActivity";
    ContactListAdapter adapter;
    SelectedContactStatus selectedContactStatus;

    @InjectView(R.id.list_contacts)
    ListView listViewContact;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_picker);
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.inject(this);
        adapter = new ContactListAdapter(this);
        listViewContact.setAdapter(adapter);

        try
        {
            new AsyncContactLoader(adapter).execute("%");
        }
        catch(Exception e)
        {
           Log.e(TAG, "failed to load contacts", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.reference_picker, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(this);

        selectedContactStatus = (SelectedContactStatus) menu.findItem(R.id.menu_select_count).getActionView();
        selectedContactStatus.setMaxSelectionount(3);
        this.adapter.setSelectionStatus(selectedContactStatus);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        finish();
        return true;
    }

    public void onOkClick()
    {
        Intent intent = new Intent();
        /*if(adapter.getSelectedContacts().getCount()>0)
        {

            String[][] sel_cons = new String[adapter.getSelectedContacts().getCount()][4];
            for(int i=0;i< adapter.getSelectedContacts().getCount();i++)
            {
                sel_cons[i][0] = adapter.getSelectedContacts().getContacts().get(i).id;
                sel_cons[i][1] = adapter.getSelectedContacts().getContacts().get(i).name;
                sel_cons[i][2] = adapter.getSelectedContacts().getContacts().get(i).phone;
                sel_cons[i][3] = adapter.getSelectedContacts().getContacts().get(i).label;
            }



            //Bundling up the contacts to pass
            Bundle data_to_pass = new Bundle();

            data_to_pass.putSerializable("selectedContacts", sel_cons);

            intent.putExtras(data_to_pass);
            setResult(RESULT_OK,intent);
            Log.v("Result", "ok");
        }*/
        finish();
    }


    public void onCancelClick()
    {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);

        return true;
    }

    class AsyncContactLoader extends AsyncTask<String, ContactListAdapter.ContactList, ContactListAdapter.ContactList>
    {


        ContactListAdapter cla;
        ProgressDialog pgdlg;


        AsyncContactLoader(ContactListAdapter adap)
        {
            cla = adap;
        }

        protected void onPreExecute()
        {
            pgdlg = ProgressDialog.show(ContactPickerActivity.this, getString(R.string.title_please_wait), getString(R.string.loading_contacts),true);
        }


        @Override
        protected ContactListAdapter.ContactList doInBackground(String... filters )
        {
            ContactListAdapter.ContactList glst=null;

            String filter = filters[0];
            ContentResolver cr = getContentResolver();
            int count=0;

            Uri uri = ContactsContract.Contacts.CONTENT_URI;

            String[] projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.HAS_PHONE_NUMBER
            };

            Cursor cursor = cr.query(uri, projection,  ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?", new  String[] {filter.toString()},  ContactsContract.Contacts.DISPLAY_NAME+ " ASC");

            if(cursor.getCount()>0)
            {

                glst=new ContactListAdapter.ContactList();

                while(cursor.moveToNext())
                {

                    if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0)
                    {
                        String id =  cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                        //Phone numbers lies in a separate table. Querying that table with Contact ID
                        Cursor ph_cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +"=?", new String[] {id}, null);
                        while(ph_cur.moveToNext())
                        {

                            String phId = ph_cur.getString(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

                            //Label eg : home, office etc. They are stored as int values

                            String customLabel = ph_cur.getString(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                            String label = (String)ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(),ph_cur.getInt(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)),customLabel);
                            String ph_no = ph_cur.getString(ph_cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            ContactListAdapter.Contact tmp = new ContactListAdapter.Contact(phId,name,ph_no,label);
                            glst.addContact(tmp);
                            count++;

                            if(count==100)
                            {
                                publishProgress(glst);
                                count=0;
                            }

                        }
                        ph_cur.close();
                    }


                }
                cursor.close();

            }


            return glst;
        }

        @Override
        protected void onProgressUpdate(ContactListAdapter.ContactList... glsts )
        {
            if(pgdlg.isShowing())
                pgdlg.dismiss();
            cla.setGcl(glsts[0]);
            cla.setFcl(glsts[0].clone());
            cla.notifyDataSetChanged();
//Log.v("Progress", cla.getCount()+" loaded");
        }


//Loading contacts finished, refresh list view to load any missed out contacts
        @Override
        protected void onPostExecute(ContactListAdapter.ContactList result)
        {
            if(pgdlg.isShowing())
                pgdlg.dismiss();
            cla.setGcl(result);
            cla.notifyDataSetChanged();

        }


    }

}