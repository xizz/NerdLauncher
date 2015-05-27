package xizz.nerdlauncher;

import android.app.ListFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends ListFragment {
	private static final String TAG = "NerdLauncherFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		Intent startupIntent = new Intent(Intent.ACTION_MAIN);
		startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		final PackageManager packageManager = getActivity().getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(startupIntent, 0);

		Log.d(TAG, "I've found " + activities.size() + " activities.");

		Collections.sort(activities, new Comparator<ResolveInfo>() {
			@Override
			public int compare(ResolveInfo lhs, ResolveInfo rhs) {
				return String.CASE_INSENSITIVE_ORDER.compare(lhs.loadLabel(packageManager)
						.toString(), rhs.loadLabel(packageManager).toString());
			}
		});
		setListAdapter(new ArrayAdapter<ResolveInfo>(getActivity(), android.R
				.layout.simple_list_item_1, activities) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView tv = (TextView) super.getView(position, convertView, parent);
				ResolveInfo ri = getItem(position);
				tv.setText(ri.loadLabel(packageManager));
				return tv;
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		ResolveInfo resolveInfo = (ResolveInfo) l.getAdapter().getItem(position);
		ActivityInfo activityInfo = resolveInfo.activityInfo;

		if (activityInfo == null)
			return;

		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		startActivity(i);
	}
}
