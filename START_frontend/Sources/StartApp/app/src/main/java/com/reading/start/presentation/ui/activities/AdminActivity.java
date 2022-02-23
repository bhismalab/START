package com.reading.start.presentation.ui.activities;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.reading.start.R;
import com.reading.start.data.DataServerManager;
import com.reading.start.databinding.ActivityAdminBinding;
import com.reading.start.tests.Server;

/**
 * Screen with setting of server url. Displaying when login as admin user.
 * username = admin
 * password = 123456
 */
public class AdminActivity extends BaseLanguageActivity {
    private ActivityAdminBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_admin);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.bg_small);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
        bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        mBinding.root.setBackground(bitmapDrawable);

        mBinding.actionBar.findViewById(R.id.back_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.back_button).setOnClickListener(v -> finish());

        mBinding.server.setText(Server.getServer(this));
        mBinding.actionBar.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        mBinding.actionBar.findViewById(R.id.save_button).setOnClickListener(v -> {
            String server = mBinding.server.getText().toString();
            Server.setServer(this, server);
            DataServerManager.reset();
            finish();
        });

        mBinding.actionBar.findViewById(R.id.text_actionbar).setVisibility(View.VISIBLE);
        TextView title = mBinding.actionBar.findViewById(R.id.text_actionbar);
        title.setText(R.string.admin_title);

        mBinding.actionBar.findViewById(R.id.home_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.checkbox_skip).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.language_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.delete_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.upload_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.logout_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.navigation_button).setVisibility(View.GONE);
        mBinding.actionBar.findViewById(R.id.next_button).setVisibility(View.GONE);
    }
}
