package com.homeworld.splitabill;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.math.BigDecimal;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		
		int splitValue = savedInstanceState.getInt("splitBy");
		TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
		splitBy.setText(String.format("%02d", splitValue));
		
		EditText billTotal = (EditText) findViewById(R.id.total_value);
		BigDecimal total = new BigDecimal(billTotal.getText().toString());
		
		SplitBill(splitValue, total);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
		int split = Integer.parseInt(splitBy.getText().toString());
		outState.putInt("splitBy", split);
	}

	public void splitMinus(View view) {
		try {
			TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
			int split = Integer.parseInt(splitBy.getText().toString());

			if (split > 0) {
				split--;
			}

			splitBy.setText(String.format("%02d", split));

			EditText billTotal = (EditText) findViewById(R.id.total_value);
			BigDecimal total = new BigDecimal(billTotal.getText().toString());
			
			SplitBill(split, total);			
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse " + nfe);
		}
	}

	public void splitPlus(View view) {
		try {
			TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
			int split = Integer.parseInt(splitBy.getText().toString());

			if (split < 100) {
				split++;
			}

			splitBy.setText(String.format("%02d", split));
			
			EditText billTotal = (EditText) findViewById(R.id.total_value);
			BigDecimal total = new BigDecimal(billTotal.getText().toString());
			
			SplitBill(split, total);
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse " + nfe);
		}
	}

	private void SplitBill(int split, BigDecimal total) {
		final SplitBillAdapter adapter;
		final ListView listview = (ListView) findViewById(R.id.listview_bills);
		
		ArrayList<BillItem> billList = new ArrayList<BillItem>();

		if (split <= 0) {

			adapter = new SplitBillAdapter(this, billList);
			listview.setAdapter(adapter);
			return;
		}		

		Money money = MoneyMaker.makeMoney(Currency.getInstance(Locale.UK),	total);
		Money[] bills = money.proRate(split);

		int index = 0;
		for (Money bill : bills) {
			index++;
			billList.add(new BillItem(String.format("Bill %01d", index), bill
					.value().toString()));
		}

		adapter = new SplitBillAdapter(this, billList);
		listview.setAdapter(adapter);
	}
}
