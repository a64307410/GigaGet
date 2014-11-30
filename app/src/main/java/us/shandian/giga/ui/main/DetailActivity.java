package us.shandian.giga.ui.main;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import us.shandian.giga.R;
import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.get.DownloadMission;
import us.shandian.giga.ui.common.BlockGraphView;
import us.shandian.giga.ui.common.ToolbarActivity;
import us.shandian.giga.util.Utility;

public class DetailActivity extends ToolbarActivity implements DownloadMission.MissionListener
{
	public static DownloadManager sManager;
	
	private static final int[] THEMES = new int[]{
		R.style.Theme_App_Blue,
		R.style.Theme_App_Red,
		R.style.Theme_App_Green,
		R.style.Theme_App_Orange,
		R.style.Theme_App_Gray,
		R.style.Theme_App_Purple
	};
	
	private DownloadMission mMission;
	
	private TextView mUrl;
	private TextView mPath;
	private TextView mDate;
	private TextView mTotal;
	private TextView mDone;
	private TextView mSpeed;
	private TextView mBlocks;
	private TextView mThreads;
	
	private BlockGraphView mGraph;
	
	private long mLastTime = 0;
	private long mLastDone = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Theme it up
		int colorId = getIntent().getIntExtra("colorId", 0);
		setTheme(THEMES[colorId]);
		
		super.onCreate(savedInstanceState);
		
		// Toolbar
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		
		if (sManager == null) {
			finish();
		} else {
			int id = getIntent().getIntExtra("id", 0);
			mMission = sManager.getMission(id);
			getSupportActionBar().setTitle(mMission.name);
		}
		
		// Views
		mUrl = Utility.findViewById(this, R.id.info_url);
		mPath = Utility.findViewById(this, R.id.info_path);
		mDate = Utility.findViewById(this, R.id.info_create);
		mTotal = Utility.findViewById(this, R.id.info_total);
		mDone = Utility.findViewById(this, R.id.info_done);
		mSpeed = Utility.findViewById(this, R.id.info_speed);
		mBlocks = Utility.findViewById(this, R.id.info_blocks);
		mThreads = Utility.findViewById(this, R.id.info_threads);
		mGraph = Utility.findViewById(this, R.id.info_graph);
		
		mGraph.setMission(mMission);
		
		initViews();
		
		mMission.addListener(this);
	}

	@Override
	protected int getLayoutResource() {
		return R.layout.detail;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		mMission.removeListener(this);
	}
	
	private void initViews() {
		mUrl.setText(mMission.url);
		mPath.setText(mMission.location + "/" + mMission.name);
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		mDate.setText(f.format(new Timestamp(mMission.timestamp)));
		
		mTotal.setText(Utility.formatBytes(mMission.length));
		mDone.setText(Utility.formatBytes(mMission.done));
		mBlocks.setText(String.valueOf(mMission.blocks));
		mThreads.setText(String.valueOf(mMission.threadCount));
		
		updateViews();
	}
	
	private void updateViews() {
		long now = System.currentTimeMillis();
		if (mLastTime > 0) {
			long deltaTime = now - mLastTime;
			
			if (deltaTime > 1000) {
				long deltaDone = mMission.done - mLastDone;
				mSpeed.setText(Utility.formatSpeed((float) deltaDone / deltaTime * 1000));
				mDone.setText(Utility.formatBytes(mMission.done));
				mLastTime = now;
				mLastDone = mMission.done;
			}
			
		} else {
			mSpeed.setText(Utility.formatSpeed(0));
			mLastTime = now;
			mLastDone = mMission.done;
		}
		
		mGraph.invalidate();
	}

	@Override
	public void onProgressUpdate(long done, long total) {
		updateViews();
	}

	@Override
	public void onFinish() {
		
	}
	
}