package com.example.middle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class MortgageActivity extends AppCompatActivity {

	private String[] yearArray = {"5", "10", "15", "20", "30"};
	private String[] ratioArray = {
			" 0.049",
			"0.0515",
			"0.025",
	};
	Spinner spinner1;
	Spinner spinner2;
	EditText row1edit;
	EditText row2edit;
	Button total;
	RadioGroup radioGroup;
	CheckBox checkBox1;
	CheckBox checkBox2;
	EditText row4edit;
	EditText row5edit;
	Button detail;
	TextView totalcal;
	TextView alldetail;
	double intotal,backtotal,extra,pertime;
	int month;
	String buytotal;
	String percent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mortgage);
		ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(this,
				R.layout.item_select, yearArray);
		yearAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spinner1 = (Spinner) findViewById(R.id.sp_year);
		spinner1.setPrompt("请选择贷款年限");
		spinner1.setAdapter(yearAdapter);
		spinner1.setSelection(0);

		ArrayAdapter<String> ratioAdapter = new ArrayAdapter<String>(this,
				R.layout.item_select, ratioArray);
		ratioAdapter.setDropDownViewResource(R.layout.item_dropdown);
		spinner2 = (Spinner) findViewById(R.id.sp_ratio);
		spinner2.setPrompt("请选择基准利率");
		spinner2.setAdapter(ratioAdapter);
		spinner2.setSelection(0);

		row1edit = (EditText) findViewById(R.id.et_price);
		row2edit = (EditText) findViewById(R.id.et_loan);
		total = (Button) findViewById(R.id.btn_loan);
		radioGroup = (RadioGroup) findViewById(R.id.rg_payment);
		checkBox1 = (CheckBox) findViewById(R.id.tv_business);
		checkBox2 = (CheckBox) findViewById(R.id.tv_fund);
		totalcal = (TextView) findViewById(R.id.tv_loan);
		detail = (Button) findViewById(R.id.btn_calculate);
		alldetail = (TextView) findViewById(R.id.tv_payment);
		row4edit = (EditText) findViewById(R.id.et_business);
		row5edit = (EditText) findViewById(R.id.et_fund);

		total.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				buytotal = row1edit.getText().toString();
				percent = row2edit.getText().toString();
				if (TextUtils.isEmpty(buytotal) || TextUtils.isEmpty(percent))//判非空
				{
					Toast.makeText(MortgageActivity.this, "购房总价和按揭部分信息填写完整", Toast.LENGTH_LONG).show();
				} else if (fangdaitext.isNum(buytotal) == false || fangdaitext.isNum(percent) == false) {//判数字
					Toast.makeText(MortgageActivity.this, "包含不合法的输入信息", Toast.LENGTH_LONG).show();
				} else if (Double.parseDouble(percent) > 100) {//判断百分比
					Toast.makeText(MortgageActivity.this, "按揭部分不能超过100%", Toast.LENGTH_LONG).show();
				} else if (fangdaitext.isNum(buytotal) && fangdaitext.isNum(percent)) {
					intotal = (Double.parseDouble(buytotal) * Double.parseDouble(percent) * 0.01);
					totalcal.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元");
				}
			}
		});

		detail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String first = row4edit.getText().toString();
				String second = row5edit.getText().toString();
				//firstrate和secondrate为商贷和公积金的年利率
				double firstrate = Double.parseDouble(spinner2.getSelectedItem().toString());
				double secondrate = Double.parseDouble(spinner2.getSelectedItem().toString());
				//获取下拉列表的年份求得月份
				String year = spinner1.getSelectedItem().toString();
				month = Integer.parseInt(year) * 12;
				//两种贷款的月利率
				double firstmonthrate = firstrate / 12;
				double secondmonthrate = secondrate / 12;
				if (totalcal.getText().toString().equals("其中贷款部分为：***万")) {//判断是否计算过贷款总额
					Toast.makeText(MortgageActivity.this, "请先计算贷款总额", Toast.LENGTH_LONG).show();
				} else if (row1edit.getText().toString().equals(buytotal) == false || row2edit.getText().toString().equals(percent) == false) {//监听贷款总额和按揭部分数值是否发生变化
					Toast.makeText(MortgageActivity.this, "检查到您的购房总价或按揭部分数据更改，请重新计算贷款总额", Toast.LENGTH_LONG).show();
				} else if (checkBox1.isChecked() == false && checkBox2.isChecked() == false)//监听勾选的多选框
				{
					Toast.makeText(MortgageActivity.this, "请勾选贷款种类", Toast.LENGTH_LONG).show();
				} else if (checkBox1.isChecked() && checkBox2.isChecked() == false) {
					//等额本息贷款算法
					if (radioGroup.getCheckedRadioButtonId() == R.id.btn_loan) {
						pertime = intotal * firstmonthrate * Math.pow((1 + firstmonthrate), month) / (Math.pow(1 + firstmonthrate, month) - 1);
						backtotal = pertime * month;
						extra = backtotal - intotal;
						alldetail.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元\n还款总额为" + String.format("%.2f", backtotal) + "万元\n其中利息总额为" + String.format("%.2f", extra) + "万元\n还款总时间为" + month + "月\n每月还款金额为" + String.format("%.2f", pertime * 10000) + "元");
					} else {//等额本金贷款算法
						double[] array = new double[month];
						double sum = 0;
						for (int i = 0; i < month; i++) {
							array[i] = intotal / month + (intotal - sum) * firstmonthrate;
							sum += array[i];
						}
						String text = "";
						for (int i = 0; i < month; i++) {
							text += "\n第" + (i + 1) + "个月应还金额为：" + String.format("%.2f", array[i] * 10000);
						}
						backtotal = sum;
						extra = backtotal - intotal;
						alldetail.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元\n还款总额为" + String.format("%.2f", backtotal) + "万元\n其中利息总额为" + String.format("%.2f", extra) + "万元\n还款总时间为" + month + "月\n每月还款金额如下：" + text);
					}

				} else if (checkBox1.isChecked() == false && checkBox2.isChecked()) {
					if (radioGroup.getCheckedRadioButtonId() == R.id.btn_loan) {
						pertime = intotal * secondmonthrate * Math.pow((1 + secondmonthrate), month) / (Math.pow(1 + secondmonthrate, month) - 1);
						backtotal = pertime * month;
						extra = backtotal - intotal;
						alldetail.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元\n还款总额为" + String.format("%.2f", backtotal) + "万元\n其中利息总额为" + String.format("%.2f", extra) + "万元\n还款总时间为" + month + "月\n每月还款金额为" + String.format("%.2f", pertime * 10000) + "元");
					} else {
						double[] array = new double[month];
						double sum = 0;
						for (int i = 0; i < month; i++) {
							array[i] = intotal / month + (intotal - sum) * secondmonthrate;
							sum += array[i];
						}
						String text = "";
						for (int i = 0; i < month; i++) {
							text += "\n第" + (i + 1) + "个月应还金额为：" + String.format("%.2f", array[i] * 10000) + "元";
						}
						backtotal = sum;
						extra = backtotal - intotal;
						alldetail.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元\n还款总额为" + String.format("%.2f", backtotal) + "万元\n其中利息总额为" + String.format("%.2f", extra) + "万元\n还款总时间为" + month + "月\n每月还款金额如下：" + text);
					}
				} else if (checkBox1.isChecked() && checkBox2.isChecked()) {
					if (radioGroup.getCheckedRadioButtonId() == R.id.btn_loan) {
						if (TextUtils.isEmpty(first) || TextUtils.isEmpty(second)) {
							Toast.makeText(MortgageActivity.this, "请将空信息填写完整", Toast.LENGTH_LONG).show();
						} else if (fangdaitext.isNum(first) == false || fangdaitext.isNum(second) == false) {
							Toast.makeText(MortgageActivity.this, "包含不合法的输入信息", Toast.LENGTH_LONG).show();
						} else if (Double.parseDouble(first) + Double.parseDouble(second) != Double.parseDouble(String.format("%.2f", intotal))) {
							Toast.makeText(MortgageActivity.this, "填写的两项贷款总额不等于初始贷款额度，请重新填写", Toast.LENGTH_LONG).show();
						} else {
							double p1 = Double.parseDouble(first);
							double p2 = Double.parseDouble(second);
							double pertime1 = p1 * firstmonthrate * Math.pow((1 + firstmonthrate), month) / (Math.pow(1 + firstmonthrate, month) - 1);
							double pertime2 = p2 * secondmonthrate * Math.pow((1 + secondmonthrate), month) / (Math.pow(1 + secondmonthrate, month) - 1);
							pertime = pertime1 + pertime2;
							backtotal = pertime * month;
							extra = backtotal - intotal;
							alldetail.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元\n还款总额为" + String.format("%.2f", backtotal) + "万元\n其中利息总额为" + String.format("%.2f", extra) + "万元\n还款总时间为" + month + "月\n每月还款金额为" + String.format("%.2f", pertime * 10000) + "元");
						}
					} else {
						if (first.equals("请输入商业贷款总额（单位万）") || second.equals("请输入公积金贷款总额（单位万）")) {
							Toast.makeText(MortgageActivity.this, "请将空信息填写完整", Toast.LENGTH_LONG).show();
						} else if (fangdaitext.isNum(first) == false || fangdaitext.isNum(second) == false) {
							Toast.makeText(MortgageActivity.this, "包含不合法的输入信息", Toast.LENGTH_LONG).show();
						} else if (Double.parseDouble(first) + Double.parseDouble(second) != Double.parseDouble(String.format("%.2f", intotal))) {
							Toast.makeText(MortgageActivity.this, "填写的两项贷款总额不等于初始贷款额度，请重新填写", Toast.LENGTH_LONG).show();
						} else {
							double p1 = Double.parseDouble(first);
							double p2 = Double.parseDouble(second);
							double[] array1 = new double[month];
							double[] array2 = new double[month];
							double sum1 = 0, sum2 = 0;
							for (int i = 0; i < month; i++) {
								array1[i] = p1 / month + (p1 - sum1) * firstmonthrate;
								array2[i] = p2 / month + (p2 - sum2) * secondmonthrate;
								Toast.makeText(MortgageActivity.this, array1[i] + " " + array2[i], Toast.LENGTH_LONG);
								sum1 += array1[i];
								sum2 += array2[i];
							}
							String text = "";
							for (int i = 0; i < month; i++) {
								text += "\n第" + (i + 1) + "个月应还金额为：" + String.format("%.2f", (array1[i] + array2[i]) * 10000) + "元";
							}
							backtotal = sum1 + sum2;
							extra = backtotal - intotal;
							alldetail.setText("您的贷款总额为" + String.format("%.2f", intotal) + "万元\n还款总额为" + String.format("%.2f", backtotal) + "万元\n其中利息总额为" + String.format("%.2f", extra) + "万元\n还款总时间为" + month + "月\n每月还款金额如下：" + text);
						}
					}
				}
			}
		});
	}
}
