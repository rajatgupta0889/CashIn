package com.mantralabsglobal.cashin.ui.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mantralabsglobal.cashin.R;
import com.mantralabsglobal.cashin.service.ReferenceService;
import com.mantralabsglobal.cashin.ui.view.CustomEditText;
import com.mantralabsglobal.cashin.ui.view.CustomSpinner;

import java.util.List;

/**
 * Created by hello on 7/24/2015.
 */
public class ReferenceListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    List<ReferenceService.Reference> referenceList;
    private ArrayAdapter<CharSequence> relationshipAdapter;

    public ReferenceListAdapter(Context context, List<ReferenceService.Reference> referenceList)
    {
        this.context = context;
        this.referenceList = referenceList;
    }

    @Override
    public int getGroupCount() {
        return referenceList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return referenceList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return referenceList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*10+ childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ReferenceService.Reference reference = (ReferenceService.Reference)getGroup(groupPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.reference_list_group_header, null);
        }
        TextView tvReferenceName = (TextView) convertView.findViewById(R.id.tv_reference_name);
        TextView tvReferencePhone = (TextView) convertView.findViewById(R.id.tv_reference_phone);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_expand_state);
        if (isExpanded) {
            imageView.setImageResource(R.drawable.ic_chevron_down);
        } else {
            imageView.setImageResource(R.drawable.ic_chevron_up);
        }

        tvReferenceName.setText(reference.getName());
        tvReferencePhone.setText(reference.getNumber());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ReferenceService.Reference reference = (ReferenceService.Reference)getGroup(groupPosition);

        if(convertView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.reference_list_item, null);
            CustomSpinner tvReferenceRelationship = (CustomSpinner) convertView.findViewById(R.id.cc_relationship);
            tvReferenceRelationship.setAdapter(getRelationshipAdapter());
        }
        final CustomSpinner tvReferenceRelationship = (CustomSpinner) convertView.findViewById(R.id.cc_relationship);
        ReferenceService.Reference ref1 = (ReferenceService.Reference)tvReferenceRelationship.getTag();
        if(ref1 != null)
        {
            ref1.setRelationship(tvReferenceRelationship.getSpinner().getSelectedItem().toString());
        }

        tvReferenceRelationship.setSelection(getRelationshipAdapter().getPosition(reference.getRelationship()));

        tvReferenceRelationship.setTag(reference);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setReferenceData(List<ReferenceService.Reference> value) {
        this.referenceList = value;
    }

    public List<ReferenceService.Reference> getReferenceList() {
        return referenceList;
    }

    protected ArrayAdapter<CharSequence> getRelationshipAdapter()
    {
        if(relationshipAdapter == null)
        {
            relationshipAdapter = ArrayAdapter.createFromResource(context, R.array.reference_relationship_array, android.R.layout.simple_spinner_item);
            relationshipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }
        return relationshipAdapter;
    }
}
