package com.kmitl.smartplug;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
	private Button buttonClear;

	private Button buttonSend;
	private ListView listViewDateTime;
	private ListView listViewTime;

	

	private Date date;
	private SlideDateTimeListener listener = new SlideDateTimeListener() {

		@Override
		public void onDateTimeSet(Date date) {
			AlarmActivity.this.date = date;
			editTextDateTime.setText(switchEveryday.isChecked() ? SharedValues.sdf_everyday.format(date) : SharedValues.sdf.format(date));
		}
	};

	private ImageView imageViewSwitch;
	private boolean switches;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		switchEveryday = (Switch) findViewById(R.id.switchEveryday);
		switchEveryday.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (date != null)
					editTextDateTime.setText(isChecked ? SharedValues.sdf_everyday.format(date) : SharedValues.sdf.format(date));
				
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
		editTextDateTime.setInputType(InputType.TYPE_NULL);
		
		buttonClear = (Button) findViewById(R.id.buttonClear);
		buttonClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextDateTime.setText("");
			}
		});
		

		imageViewSwitch = (ImageView) findViewById(R.id.imageViewSwitch);
		imageViewSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switches = !switches;
				imageViewSwitch.setImageResource(switches ? R.drawable.switch_on : R.drawable.switch_off);
			}
		});

		buttonSend = (Button) findViewById(R.id.buttonSend);
		buttonSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedValues.addDateTime(getApplicationContext(), switchEveryday.isChecked() ? SharedValues.KEY_EVERYDAY : SharedValues.KEY_ONETIME, new DateTimeItem(editTextDateTime.getText().toString(), switches));
				if (switchEveryday.isChecked())
					listViewTime.setAdapter(new ListViewRowAdapter(getApplicationContext(), SharedValues.getDateTimeList(getApplicationContext(), SharedValues.KEY_EVERYDAY)));
				else
					listViewDateTime.setAdapter(new ListViewRowAdapter(getApplicationContext(), SharedValues.getDateTimeList(getApplicationContext(), SharedValues.KEY_ONETIME)));
				
				editTextDateTime.setText("");
			}
		});
		
		editTextDateTime.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if (editTextDateTime.getText().toString().equals("")) {
					buttonSend.setVisibility(View.GONE);
					imageViewSwitch.setVisibility(View.GONE);
					switchEveryday.setVisibility(View.GONE);
				}
				else {
					buttonSend.setVisibility(View.VISIBLE);
					imageViewSwitch.setVisibility(View.VISIBLE);
					switchEveryday.setVisibility(View.VISIBLE);
				}
			}
		});

		listViewDateTime = (ListView) findViewById(R.id.listViewDateTime);
		listViewDateTime.setAdapter(new ListViewRowAdapter(getApplicationContext(), SharedValues.getDateTimeList(getApplicationContext(), SharedValues.KEY_ONETIME)));
		
		listViewTime = (ListView) findViewById(R.id.listViewTime);
		listViewTime.setAdapter(new ListViewRowAdapter(getApplicationContext(), SharedValues.getDateTimeList(getApplicationContext(), SharedValues.KEY_EVERYDAY)));
		
		buttonSend.setVisibility(View.GONE);
		imageViewSwitch.setVisibility(View.GONE);
		switchEveryday.setVisibility(View.GONE);
	}

	private class ListViewRowAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<DateTimeItem> datetime;

		public ListViewRowAdapter(Context context, ArrayList<DateTimeItem> datetime) {
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
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null)
				convertView = mInflater.inflate(R.layout.listview_row, parent, false);
			
			ImageView imageViewSwitch = (ImageView) convertView.findViewById(R.id.imageViewSwitch);
			imageViewSwitch.setImageResource(datetime.get(position).getState() ? R.drawable.switch_on : R.drawable.switch_off);
			
			TextView textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
			textViewTime.setText(datetime.get(position).getTime());
			
			TextView textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
			textViewDate.setText(datetime.get(position).getDate());
			
			final String KEY = datetime.get(position).getDate().equals("") ? SharedValues.KEY_EVERYDAY : SharedValues.KEY_ONETIME;
			final DateTimeItem ITEM = datetime.get(position);
			
			Button buttonDelete = (Button) convertView.findViewById(R.id.buttonDelete);
			buttonDelete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					SharedValues.removeDateTime(getApplicationContext(), KEY, ITEM);
					if (KEY.equals(SharedValues.KEY_EVERYDAY))
						listViewTime.setAdapter(new ListViewRowAdapter(getApplicationContext(), SharedValues.getDateTimeList(getApplicationContext(), SharedValues.KEY_EVERYDAY)));
					else
						listViewDateTime.setAdapter(new ListViewRowAdapter(getApplicationContext(), SharedValues.getDateTimeList(getApplicationContext(), SharedValues.KEY_ONETIME)));
				}
			});

			return convertView;
		}

	}
}
