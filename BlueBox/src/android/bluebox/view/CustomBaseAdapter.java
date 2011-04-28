/*
 * http://geekswithblogs.net/bosuch/archive/2011/01/31/android---create-a-custom-multi-line-listview-bound-to-an.aspx
 */

package android.bluebox.view;

import java.util.ArrayList;

import android.bluebox.R;
import android.bluebox.model.WorkspaceItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomBaseAdapter extends BaseAdapter {

	private static ArrayList<WorkspaceItem> workspaceList;
	
	private LayoutInflater mInflater;
	
	public CustomBaseAdapter(Context context, ArrayList<WorkspaceItem> list) {
		workspaceList = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return workspaceList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return workspaceList.get(position);
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
			convertView = mInflater.inflate(R.layout.custom_row_view, null);
			holder = new ViewHolder();
			
			holder.txtName = (TextView) convertView.findViewById(R.id.WorkspaceName);
			holder.txtAccuracy = (TextView) convertView.findViewById(R.id.WorkspaceAccuracy);
			holder.txtLastVisit = (TextView) convertView.findViewById(R.id.WorkspaceLastVisit);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.txtName.setText(workspaceList.get(position).getName());
		holder.txtAccuracy.setText(workspaceList.get(position).getAccuracy());
		holder.txtLastVisit.setText(workspaceList.get(position).getLastVisit());
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtName;
		TextView txtAccuracy;
		TextView txtLastVisit;
	}
}
