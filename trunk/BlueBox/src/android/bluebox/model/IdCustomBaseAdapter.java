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

public class IdCustomBaseAdapter extends BaseAdapter {

	private static ArrayList<IdentityItem> idList;
	
	private LayoutInflater mInflater;
	
	public IdCustomBaseAdapter(Context context, ArrayList<IdentityItem> list) {
		idList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return idList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return idList.get(position);
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
			convertView = mInflater.inflate(R.layout.id_custom_row_view, null);
			holder = new ViewHolder();
			
			holder.txtName = (TextView) convertView.findViewById(R.id.id_name);
			holder.txtTagList = (TextView) convertView.findViewById(R.id.id_tag);
			holder.txtWorkspaceList = (TextView) convertView.findViewById(R.id.id_workspace);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtName.setText(idList.get(position).getName());
		holder.txtTagList.setText(idList.get(position).getTagList());
		holder.txtWorkspaceList.setText(idList.get(position).getWorkspaceList());
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtTagList;
		TextView txtWorkspaceList;
	}
	
	public void setArraylist(ArrayList<IdentityItem> list) {
		idList = list;
	}
}
