package com.kmitl.smartplug;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmActivity extends FragmentActivity {

	private Switch switchEveryday;
	private EditText editTextDateTime;

	private Button buttonSend;
	private ListView listViewDateTime;
	private ListView listViewTime;

	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	private SimpleDateFormat sdf_everyday = new SimpleDateFormat("HH:mm:ss");

	private Date date;
	private SlideDateTimeListener listener = new SlideDateTimeListener() {

		@Override
		public void onDateTimeSet(Date date) {
			AlarmActivity.this.date = date;
			editTextDateTime.setText(switchEveryday.isChecked() ? sdf_everyday.format(date) : sdf.format(date));
		}
	};

	private ImageView[] imageViewSwitch;
	private boolean[] switches;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		switchEveryday = (Switch) findViewById(R.id.switchEveryday);
		switchEveryday.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (date != null)
					editTextDateTime.setText(isChecked ? sdf_everyday.format(date) : sdf.format(date));
			}
		});

		editTextDateTime = (EditText) findViewById(R.id.editTextDateTime);
		editTextDateTime.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new SlideDateTimePicker.Builder(getSupportFragmentManager()).setIs24HourTime(true).setListener(listener)
						.setInitialDate(new Date()).build().show();
			}
		});

		imageViewSwitch = new ImageView[4];
		switches = new boolean[4];

		imageViewSwitch[0] = (ImageView) findViewById(R.id.imageViewSwitch1);
		imageViewSwitch[0].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switches[0] = !switches[0];
				imageViewSwitch[0].setImageResource(switches[0] ? R.drawable.switch_on : R.drawable.switch_off);
			}
		});

		imageViewSwitch[1] = (ImageView) findViewById(R.id.imageViewSwitch2);
		imageViewSwitch[1].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switches[1] = !switches[1];
				imageViewSwitch[1].setImageResource(switches[1] ? R.drawable.switch_on : R.drawable.switch_off);
			}
		});

		imageViewSwitch[2] = (ImageView) findViewById(R.id.imageViewSwitch3);
		imageViewSwitch[2].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switches[2] = !switches[2];
				imageViewSwitch[2].setImageResource(switches[2] ? R.drawable.switch_on : R.drawable.switch_off);
			}
		});

		imageViewSwitch[3] = (ImageView) findViewById(R.id.imageViewSwitch4);
		imageViewSwitch[3].setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switches[3] = !switches[3];
				imageViewSwitch[3].setImageResource(switches[3] ? R.drawable.switch_on : R.drawable.switch_off);
			}
		});

		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});

		listViewDateTime = (ListView) findViewById(R.id.listViewDateTime);
		ArrayList<DateTime> dt = new ArrayList<AlarmActivity.DateTime>();
		dt.add(new DateTime("2015-11-24", "02:13"));
		dt.add(new DateTime("2015-11-25", "06:40"));
		listViewDateTime.setAdapter(new ListViewRoeAdapter(getApplicationContext(), dt));
		
		listViewTime = (ListView) findViewById(R.id.listViewTime);
		ArrayList<DateTime> d = new ArrayList<AlarmActivity.DateTime>();
		d.add(new DateTime("", "02:13"));
		d.add(new DateTime("", "06:40"));
		listViewTime.setAdapter(new ListViewRoeAdapter(getApplicationContext(), d));
	}

	private class ListViewRoeAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<DateTime> datetime;

		public ListViewRoeAdapter(Context context, ArrayList<DateTime> datetime) {
			this.context = context;
			this.datetime = datetime;
		}

		@Override
		public int getCount() {
			return datetime.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null)
				convertView = mInflater.inflate(R.layout.listview_row, parent, false);
			
			TextView textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
			textViewTime.setText(datetime.get(position).getTime());
			
			TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
			textViewDate.setText(datetime.get(position).getDate());
			
			Button buttonEdit = (Button) convertView.findViewById(R.id.buttonEdit);
			buttonEdit.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			Button buttonDelete = (Button) convertView.findViewById(R.id.buttonDelete);
			buttonDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			
			
			
			return convertView;
		}

	}

	private class DateTime {
		private String date;
		private String time;

		public DateTime(String date, String time) {
			this.date = date;
			this.time = time;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}
	}
}
