package com.jzj.simplelamp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

public class SimpleLamp extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.startService(new Intent(this, LightService.class));
		this.finish();
	}
}
