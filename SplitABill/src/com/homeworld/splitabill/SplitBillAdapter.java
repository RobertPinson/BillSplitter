package com.homeworld.splitabill;

import android.widget.ArrayAdapter;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SplitBillAdapter extends ArrayAdapter<BillItem> {
	private final Context context;
	private final ArrayList<BillItem> itemsArrayList;

	public SplitBillAdapter(Context context, ArrayList<BillItem> itemsArrayList) {
		super(context, R.layout.bill_row, itemsArrayList);

		this.context = context;
		this.itemsArrayList = itemsArrayList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 1. Create inflater
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 2. Get rowView from inflater
		View rowView = inflater.inflate(R.layout.bill_row, parent, false);

		// 3. Get the two text view from the rowView
		TextView billNameView = (TextView) rowView.findViewById(R.id.bill_name);
		TextView billAmountView = (TextView) rowView
				.findViewById(R.id.bill_ammount);

		// 4. Set the text for textView
		billNameView.setText(itemsArrayList.get(position).getName());
		billAmountView.setText(itemsArrayList.get(position).getAmount());
		
		return rowView;
	}
}
