package com.sky.cookbooksa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.sky.cookbooksa.adapter.PageIndicator;
import com.sky.cookbooksa.entity.PictureInfo;
import com.sky.cookbooksa.utils.Constant;
import com.sky.cookbooksa.utils.FileUtils;
import com.sky.cookbooksa.utils.StringUtil;
import com.sky.cookbooksa.utils.ToastUtil;
import com.sky.cookbooksa.widget.HackyViewPager;

public class ImagePagerActivity extends BaseActivity{

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private static final String STATE_POSITION = "STATE_POSITION";
	private HackyViewPager pager;
	private ImageView goback, download;
	private int pagerPosition = 0;
	private int currentPos = 0;
	private ArrayList<PictureInfo> list;
	private TextView showNo;
	private String DOWNLOADDIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/cookbook/download/";

	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_page);

		Bundle bundle = getIntent().getExtras();
		list = (ArrayList<PictureInfo>)bundle.getSerializable("list");
		pagerPosition = bundle.getInt("currentPosition");

		if (savedInstanceState != null) {
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		init();
	}

	private void init() {

		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.photo_loading)
		.showImageOnFail(R.drawable.photo_loading)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.ARGB_8888)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();

		initImageLoader(this);

		showNo = (TextView)findViewById(R.id.show_no);
		showNo.setText((pagerPosition+1)+"/"+list.size());
		goback = (ImageView)this.findViewById(R.id.goback);
		download = (ImageView)this.findViewById(R.id.image_down);

		pager = (HackyViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(list,this));
		pager.setCurrentItem(pagerPosition);

		setListener();
	}

	private void setListener() {
		download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				FileUtils.mkdirs(DOWNLOADDIR);

				String imageStr = StringUtil.subString(list.get(currentPos).getPath(), "/");

				fh.download(Constant.DIR + imageStr, DOWNLOADDIR + imageStr, 
						false, new AjaxCallBack<File>() {

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, errorNo, strMsg);
						ToastUtil.toastLong(context, strMsg);
					}

					@Override
					public void onSuccess(File t) {
						// TODO Auto-generated method stub
						super.onSuccess(t);
						ToastUtil.toastLong(context, "下载成功，已保存在"+DOWNLOADDIR+"目录下");
					}
				});
			}
		});

		goback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((Activity) context).finish();
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});

		pager.setOnPageChangeListener(new PageIndicator() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				currentPos = arg0;
				showNo.setText((arg0+1)+"/"+list.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setViewPager(ViewPager view, int initialPosition) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setViewPager(ViewPager view) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setOnPageChangeListener(OnPageChangeListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setCurrentItem(int item) {
				// TODO Auto-generated method stub

			}

			@Override
			public void notifyDataSetChanged() {
				// TODO Auto-generated method stub

			}
		});
	}

	public void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.denyCacheImageMultipleSizesInMemory()
		.discCacheFileNameGenerator(new Md5FileNameGenerator())
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.writeDebugLogs() // Remove for release app
		.build();
		ImageLoader.getInstance().init(config);
	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<PictureInfo> pictures;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<PictureInfo> pictures,Context context) {
			this.pictures = pictures;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return pictures.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View imageLayout = inflater.inflate(R.layout.image_page_item, view, false);
			final TextView desc = (TextView)imageLayout.findViewById(R.id.image_desc);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			String descStr = StringUtil.isNullOrEmpty(pictures.get(position).getDesc())?"木有介绍"
					:pictures.get(position).getDesc();

			String descHtml = "<html><body><font color=\"#F5AE38\">"+(position+1)+". </font>"+descStr+"</body></html>";
			desc.setText(Html.fromHtml(descHtml));

			String imgStr = pictures.get(position).getPath();
			imgStr = imgStr.substring(imgStr.lastIndexOf("/")+1);

			imageLoader.displayImage(Constant.DIR+imgStr, imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
					download.setEnabled(false);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
					case IO_ERROR:
						message = "图片加载失败！";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "内存溢出了！";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
					}
					ToastUtil.toastLong(context, message);
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
					download.setEnabled(true);
				}
			});
			((ViewPager) view).addView(imageLayout, 0);

			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
		return super.onKeyDown(keyCode, event);
	}
}