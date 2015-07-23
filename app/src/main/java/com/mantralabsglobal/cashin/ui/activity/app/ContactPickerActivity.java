package com.mantralabsglobal.cashin.ui.activity.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.businessobjects.ContactResult;
import com.mantralabsglobal.cashin.ui.fragment.utils.ContactListFragment;

import java.util.ArrayList;

/**
 * Created by hello on 7/22/2015.
 */
public class ContactPickerActivity extends BaseActivity {

    public static final String PICKER_TYPE = "type";
    public static final String PICKER_TYPE_PHONE = "phone";
    public static final String CONTACT_PICKER_RESULT = "contacts";
    int selectionCount = 0;
    int maxSelection = 3;
    String title;

    public static final int RESULT_ERROR = RESULT_FIRST_USER;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getAction() != null && !Intent.ACTION_PICK.equals(getIntent().getAction())) {
            Intent ret = new Intent();
            ret.putExtra("error", "Unsupported action type");
            setResult(RESULT_ERROR, ret);
            return;
        }

        if (getIntent().getExtras() == null || PICKER_TYPE_PHONE.equals(getIntent().getExtras().getString(PICKER_TYPE))) {
            setContentView(R.layout.activity_contact_picker);
        } else {
            Intent ret = new Intent();
            ret.putExtra("error", "Unsupported picker type");
            setResult(RESULT_ERROR, ret);
            return;
        }
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        this.title = getTitle().toString();
        updateTitle();

    }

    public void setSelectionCount(int count){
        this.selectionCount = count;
        updateTitle();
    }

    private void updateTitle() {
        this.setTitle(title + "(" + selectionCount  + "/" + maxSelection + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reference_picker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.contacts_done) {
            returnResults();
            return true;
        } else if (item.getItemId() == R.id.contacts_cancel) {
            cancel();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void returnResults() {
        ContactListFragment fragment = (ContactListFragment) getSupportFragmentManager().getFragments().get(0);
        ArrayList<ContactResult> resultList = new ArrayList<ContactResult>( fragment.getResults().values() );
        Intent retIntent = new Intent();
        retIntent.putExtra(CONTACT_PICKER_RESULT, resultList);

        setResult(RESULT_OK, retIntent);
        finish();
    }

    private void cancel() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onBackPressed() {
        cancel();
        super.onBackPressed();
    }

}