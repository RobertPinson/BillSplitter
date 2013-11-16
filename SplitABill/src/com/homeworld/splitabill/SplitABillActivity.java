package com.homeworld.splitabill;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.math.BigDecimal;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class SplitABillActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splitabill);

		final EditText total = (EditText) findViewById(R.id.total_value);
		total.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Currency currency = Currency.getInstance(Locale.getDefault());
				String digits = s.toString().replaceAll(
						String.format("[%s,.]", currency.getSymbol()), "");

				if (digits.length() <= 0) {
					return;
				}

				NumberFormat nf = NumberFormat.getCurrencyInstance();

				try {
					String formatted = nf.format(Double.parseDouble(digits) / 100);
					total.removeTextChangedListener(this);
					total.setText(formatted);
					total.setSelection(formatted.length());
					total.addTextChangedListener(this);

					TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
					int split = Integer.parseInt(splitBy.getText().toString());

					BigDecimal totalValue = new BigDecimal(
							CurrencyStringClean(formatted));

					
						SplitBill(split, totalValue);
				} catch (NumberFormatException nfe) {
					// total.setText("");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}
		});
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
		BigDecimal totalValue = new BigDecimal(CurrencyStringClean(billTotal
				.getText().toString()));
		if (totalValue.signum() > 0)
			SplitBill(splitValue, totalValue);
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
			BigDecimal totalValue = new BigDecimal(
					CurrencyStringClean(billTotal.getText().toString()));
			if (totalValue.signum() > 0)
				SplitBill(split, totalValue);
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse " + nfe);
		}
	}

	public void splitPlus(View view) {
		try {
			TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
			int split = Integer.parseInt(splitBy.getText().toString());

			if (split < 99) {
				split++;
			}

			splitBy.setText(String.format("%02d", split));

			EditText billTotal = (EditText) findViewById(R.id.total_value);
			BigDecimal totalValue = new BigDecimal(
					CurrencyStringClean(billTotal.getText().toString()));
			if (totalValue.signum() > 0)
				SplitBill(split, totalValue);
		} catch (NumberFormatException nfe) {
			System.out.println("Could not parse " + nfe);
		}
	}

	private String CurrencyStringClean(String formatted) {
		Currency currency = Currency.getInstance(Locale.getDefault());
		String digits = formatted.replaceAll(
				String.format("[%s,.]", currency.getSymbol()), "");
		Double num = Double.parseDouble(digits) / 100;
		return num.toString();
	}

	private void SplitBill(int split, BigDecimal billTotal) {
		final SplitBillAdapter adapter;
		final ListView listview = (ListView) findViewById(R.id.listview_bills);

		ArrayList<BillItem> billList = new ArrayList<BillItem>();

		if (split <= 0 || billTotal.signum() < 1) {
			adapter = new SplitBillAdapter(this, billList);
			listview.setAdapter(adapter);
			return;
		}

		Money money = MoneyMaker.makeMoney(
				Currency.getInstance(Locale.getDefault()), billTotal);

		Money[] bills = money.proRate(split);

		int index = 0;
		for (Money bill : bills) {
			index++;
			billList.add(new BillItem(String.format("Payee %01d", index),
					NumberFormat.getCurrencyInstance().format(bill.value())));
		}

		adapter = new SplitBillAdapter(this, billList);
		listview.setAdapter(adapter);
	}
}
