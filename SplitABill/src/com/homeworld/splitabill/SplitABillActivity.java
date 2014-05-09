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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.ads.*;

public class SplitABillActivity extends Activity {
	private AdView adView;
	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "ca-app-pub-7612725563518677/7747794144";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splitabill);

		// Create addview
		adView = new AdView(this);
		adView.setAdUnitId(AD_UNIT_ID);
		adView.setAdSize(AdSize.SMART_BANNER);

		// Lookup your linearLayout
		LinearLayout layout = (LinearLayout) findViewById(R.id.ad_banner);

		// add adView
		layout.addView(adView);

		// initiate generic request
		AdRequest adRequest = new AdRequest.Builder()
				.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice("958E03A368FB270183C598920E6AE442").build();

		// load add view with ad request
		adView.loadAd(adRequest);

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

		((RadioGroup) findViewById(R.id.toggleGroup))
				.setOnCheckedChangeListener(ToggleListener);

		final EditText serviceChargeValue = (EditText) findViewById(R.id.service_value);
		serviceChargeValue.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				ToggleButton btnPct = (ToggleButton) findViewById(R.id.btn_service_percent);
				boolean isPercent = btnPct.isChecked();
				EditText serviceChargeValue = (EditText) findViewById(R.id.service_value);

				Currency currency = Currency.getInstance(Locale.getDefault());
				String digits = s.toString().replaceAll(String.format("[%s,.]", currency.getSymbol()), "");
				digits = digits.replaceAll(String.format("[%s,.]", "%"),	"");

				if (digits.length() <= 0) {
					return;
				}

				if (isPercent) {
					// format value as percent
					String pctFormated = String.format("%.2f %%", Double.parseDouble(digits) / 100);

					serviceChargeValue.removeTextChangedListener(this);
					serviceChargeValue.setText(pctFormated);
					serviceChargeValue.setSelection(pctFormated.length());
					serviceChargeValue.addTextChangedListener(this);
				} else {
					// format as monetary value
					NumberFormat nf = NumberFormat.getCurrencyInstance();

					try {
						String formatted = nf.format(Double.parseDouble(digits) / 100);
						serviceChargeValue.removeTextChangedListener(this);
						serviceChargeValue.setText(formatted);
						serviceChargeValue.setSelection(formatted.length());
						serviceChargeValue.addTextChangedListener(this);
					} catch (NumberFormatException nfe) {
						// total.setText("");
					}

				}

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

		});

		serviceChargeValue.setText("20"); // set default service charge value
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	static final RadioGroup.OnCheckedChangeListener ToggleListener = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(final RadioGroup radioGroup, final int i) {
			for (int j = 0; j < radioGroup.getChildCount(); j++) {
				final ToggleButton view = (ToggleButton) radioGroup
						.getChildAt(j);
				view.setChecked(view.getId() == i);
			}
		}
	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		int splitValue = savedInstanceState.getInt("splitBy");
		TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
		splitBy.setText(String.format("%02d", splitValue));

		EditText billTotal = (EditText) findViewById(R.id.total_value);
		String total = billTotal.getText().toString();
		if (!total.isEmpty()) {
			BigDecimal totalValue = new BigDecimal(CurrencyStringClean(total));
			if (totalValue.signum() > 0)
				SplitBill(splitValue, totalValue);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		TextView splitBy = (TextView) findViewById(R.id.edit_splitby);
		int split = Integer.parseInt(splitBy.getText().toString());
		outState.putInt("splitBy", split);
	}

	@Override
	protected void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		adView.resume();
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

	public void onServiceTypeChange(View view) {
		((RadioGroup) view.getParent()).check(0);
		((RadioGroup) view.getParent()).check(view.getId());
		EditText serviceChargeValue = (EditText) findViewById(R.id.service_value);
		String scValue = serviceChargeValue.getText().toString();

		switch (view.getId()) {
		case R.id.btn_service_value:
			// calculate service charge by value

			// update UI
			serviceChargeValue.setText(scValue); // update service charge value
			break;
		case R.id.btn_service_percent:
			// calculate service charge by percent

			// update UI
			serviceChargeValue.setText(scValue); // set service charge
			break;
		}
	}

	private String CurrencyStringClean(String formatted) {
		if (formatted == null || formatted.isEmpty())
			return "";

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
