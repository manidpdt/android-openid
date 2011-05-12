/*
 * http://geekswithblogs.net/bosuch/archive/2011/01/31/android---create-a-custom-multi-line-listview-bound-to-an.aspx
 */

package android.bluebox.model;

import java.util.ArrayList;

import android.bluebox.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MatchingCustomBaseAdapter extends BaseAdapter {

	private static ArrayList<MatchingItem> matchingList;
	
	private LayoutInflater mInflater;
	
	public MatchingCustomBaseAdapter(Context context, ArrayList<MatchingItem> list) {
		matchingList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return matchingList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return matchingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder;
		
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.matching_custom_row_view, null);
			holder = new ViewHolder();
			
			holder.txtName = (TextView) convertView.findViewById(R.id.matching_name);
			holder.txtIdentity = (TextView) convertView.findViewById(R.id.matching_identity);
			holder.txtValue = (TextView) convertView.findViewById(R.id.matching_value);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtName.setText(matchingList.get(position).getName());
		holder.txtIdentity.setText(matchingList.get(position).getIdentity());
		holder.txtValue.setText(matchingList.get(position).getValue());
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtIdentity;
		TextView txtValue;
	}
	
	public void setArraylist(ArrayList<MatchingItem> list) {
		matchingList = list;
	}
}
