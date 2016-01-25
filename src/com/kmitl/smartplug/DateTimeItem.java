package com.kmitl.smartplug;

public class DateTimeItem implements Comparable<DateTimeItem> {
	
	private String date;
	private String time;
	
	private boolean state;
	
	public DateTimeItem(String datetime, boolean state) {
		setDateTime(datetime);
		this.state = state;
	}

	public String getDateTime() {
		if (date.equals(""))
			return time;
		return date + " " + time;
	}

	public void setDateTime(String datetime) {
		String[] dt = datetime.split(" ");
		if (dt.length == 2) {
			this.date = dt[0];
			this.time = dt[1];
		}
		else {
			this.date = "";
			this.time = dt[0];
		}
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}
	
	public boolean getState() {
		return state;
	}

	@Override
	public String toString() {
		return getDateTime() + " " + state;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DateTimeItem))
			return false;
		DateTimeItem dt = (DateTimeItem) o;
		return toString().equals(dt.toString());
	}

	@Override
	public int compareTo(DateTimeItem arg0) {
		return getDateTime().compareTo(arg0.getDateTime());
	}

}
