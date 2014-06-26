package comet.com;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import util.Comments;
import util.TagsandComments;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TalkComments extends Activity {

	private Button add;
	private ListView lv;
	private SimpleAdapter adapter;
	private ArrayList<HashMap<String, Object>> comments;
	private ProgressDialog pd;
	private ProgressBar pb;
	private String id, uid, uname, server_return;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.talks_tags_and_comments);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			id = b.getString("id");
		}

		pb = (ProgressBar) getParent().findViewById(R.id.widget108);

		add = (Button) this.findViewById(R.id.add);
		add.setOnClickListener(addClick);

		lv = (ListView) this.findViewById(R.id.ListView01);
		comments = new ArrayList<HashMap<String, Object>>();

		if (isSignin()) {
			// showDialog("Loading data, please wait...");
			new CommentsLoadTask().execute(id);
		} else {
			new AlertDialog.Builder(TalkComments.this)
					.setMessage(
							"Showing and adding requires login, do you want to sign in first?")
					.setPositiveButton("yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialoginterface, int i) {
									dialoginterface.cancel();
									TalkComments.this.finish();
									Intent in = new Intent(TalkComments.this,
											Signin.class);
									in.putExtra("activityname", "TalkDetail");
									in.putExtra("id", id);

									startActivity(in);
								}
							})
					.setNegativeButton("no",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();
		}

	}

	OnClickListener addClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (isSignin()) {
				LayoutInflater inflater = (LayoutInflater) TalkComments.this
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				final LinearLayout layout = (LinearLayout) inflater.inflate(
						R.layout.email_dialog, null);
				EditText edit = (EditText) layout.findViewById(R.id.email);
				edit.setHint("");
				TextView text = (TextView) layout.findViewById(R.id.tvname);
				text.setText("Enter your comment below:");
				new AlertDialog.Builder(TalkComments.this)
						.setTitle("Add comment")
						.setView(layout)
						.setPositiveButton("yes",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {

										EditText inputStringr = (EditText) layout
												.findViewById(R.id.email);
										String str = inputStringr.getText()
												.toString();

										if (str == null || str.equals("")) {
											Toast.makeText(
													getApplicationContext(),
													"comments should not be blank",
													Toast.LENGTH_SHORT).show();
										} else {
											showDialog("adding comment...");
											new AddCommentTask().execute(str);

										}

									}
								})
						.setNegativeButton("cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int i) {
										dialog.cancel();
									}
								}).show();

			} else {
				new AlertDialog.Builder(TalkComments.this)
						.setMessage(
								"Showing and adding requires login, do you want to sign in first?")
						.setPositiveButton("yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										dialoginterface.cancel();
										TalkComments.this.finish();
										Intent in = new Intent(
												TalkComments.this, Signin.class);
										in.putExtra("activityname",
												"TalkDetail");
										in.putExtra("id", id);

										startActivity(in);
									}
								})
						.setNegativeButton("no",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								}).show();
			}

		}

	};

	public boolean isSignin() {
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin", false);
		uname = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;

	}

	public static boolean isConnect(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null) {

				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	public void showDialog(String s) {
		pd = ProgressDialog.show(this, "Synchronization", s, true, false);
	}

	public void dismissDialog() {
		pd.dismiss();
	}

	private class CommentsLoadTask extends AsyncTask<String, Integer, Integer> {
		TextView status;
		String id;
		TagsandComments tc = new TagsandComments();
		ArrayList<Comments> clist = new ArrayList<Comments>();

		@Override
		protected Integer doInBackground(String... args) {
			// TODO Auto-generated method stub
			if (isConnect(TalkComments.this)) {

				id = args[0];
				clist = tc.getComments(id);
				server_return = tc.server_return;

				publishProgress(4);
				if (clist.size() == 0 && server_return.compareTo("ok") == 0) {
					publishProgress(2);
				} else if (clist.size() == 0
						&& server_return.compareTo("ok") != 0) {
					publishProgress(0);
				} else
					publishProgress(1);
			} else
				publishProgress(3);
			return 0;
		}

		protected void onProgressUpdate(Integer... progress) {
			int command = progress[0];
			switch (command) {
			case 0:
				// dismissDialog();
				pb.setVisibility(View.GONE);
				status = (TextView) findViewById(R.id.status);
				status.setVisibility(View.VISIBLE);
				status.setText("Fail to connect to server, please check your internet connection and try again.");
				status.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.server_error, 0, 0, 0);
				init(clist);
				break;
			case 1:
				// dismissDialog();
				pb.setVisibility(View.GONE);
				status = (TextView) findViewById(R.id.status);
				status.setVisibility(View.GONE);
				init(clist);
				break;
			case 2:
				// dismissDialog();
				pb.setVisibility(View.GONE);
				status = (TextView) findViewById(R.id.status);
				status.setVisibility(View.VISIBLE);
				status.setText("There is no comments yet, you can add the first.");
				status.setCompoundDrawablesWithIntrinsicBounds(
						R.drawable.lightbulb, 0, 0, 0);
				init(clist);
				break;
			case 3:
				pb.setVisibility(View.GONE);
				Toast.makeText(
						getApplicationContext(),
						"Fail to connect to server, please check your internet connection and try again.",
						Toast.LENGTH_SHORT).show();
				break;
			case 4:
				pb.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}

		}
	}

	private class AddCommentTask extends AsyncTask<String, Integer, Integer> {

		String comment = "";
		String status = "";
		TagsandComments tc = new TagsandComments();

		@Override
		protected Integer doInBackground(String... args) {
			// TODO Auto-generated method stub

			comment = args[0];

			status = tc.addComment(id, uid, comment);
			if (status.compareTo("yes") == 0) {
				publishProgress(1);
			} else
				publishProgress(2);
			return 0;
		}

		protected void onProgressUpdate(Integer... progress) {
			int command = progress[0];
			switch (command) {
			case 0:
				dismissDialog();
				break;
			case 1:
				dismissDialog();
				TextView status_view = (TextView) findViewById(R.id.status);
				status_view.setVisibility(View.GONE);
				SimpleDateFormat formatter = new SimpleDateFormat(
						"EEEE, MMM dd, yyyy");
				Date curDate = new Date(System.currentTimeMillis());
				String date = formatter.format(curDate);

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("comment", comment);
				map.put("user", uname);
				map.put("date", date);
				comments.add(comments.size(), map);

				adapter.notifyDataSetChanged();
				break;
			case 2:
				dismissDialog();
				Toast.makeText(getApplicationContext(), status,
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}

		}
	}

	public void init(ArrayList<Comments> clist) {

		for (int i = 0; i < clist.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("comment", clist.get(i).cotent);
			map.put("user", clist.get(i).user_name);
			map.put("date", clist.get(i).date);
			comments.add(map);
		}

		adapter = new SimpleAdapter(getApplicationContext(), comments,
				R.layout.tags_and_comments_listitem, new String[] { "user",
						"date", "comment" }, new int[] { R.id.user, R.id.date,
						R.id.comment });
		lv.setAdapter(adapter);

	}
}
