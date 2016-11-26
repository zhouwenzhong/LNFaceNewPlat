package com.lianyao.ftf;

import android.app.Application;
import android.content.IntentFilter;
import android.util.Log;

import com.lianyao.ftf.config.Constants;
import com.lianyao.ftf.receiver.RtcReceiver;
import com.lianyao.ftf.sdk.RtcClient;
import com.lianyao.ftf.sdk.config.RtcBroadcast;
import com.lianyao.ftf.sdk.inter.InitListener;
import com.lianyao.ftf.sdk.inter.RtcConnectionListener;
import com.lianyao.ftf.util.AppUtil;
import com.lianyao.ftf.util.ToastUtil;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

public class MainApplication extends Application {
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static DisplayImageOptions options;
	RtcReceiver mReceiver = new RtcReceiver();

	@Override
	public void onCreate() {
		super.onCreate();
		//来电广播处理
		IntentFilter intentFilter = new IntentFilter();
		//addAction
		intentFilter.addAction(RtcBroadcast.onIncomingCall);
		registerReceiver(mReceiver, intentFilter);

		if(!AppUtil.isNetWorkAvailable(MainApplication.this.getApplicationContext())) {
			ToastUtil.showShort(this.getApplicationContext(), "请检查网络连接");
			return;
		}
		imageLoader();
		initSdk();
	}

	@Override
	public void onTerminate(){
		super.onTerminate();
		unregisterReceiver(mReceiver);
	}

	public void initSdk() {

		final RtcClient client = RtcClient.getInstance();
		client.init(this, Constants.APPID, Constants.TICKET,
				new InitListener() {

					@Override
					public void onSuccess() {

					}

					@Override
					public void onError(String message) {

					}
				});
		client.rtcSetting.setRing(true, R.raw.call_bell);
		client.rtcSetting.setCallRing(true, R.raw.phone_ring);

		client.addConnectionListener(new RtcConnectionListener() {

			@Override
			public void onDisconnected(int error) {
				Log.e("MainApplication","网络断开。");
			}

			@Override
			public void onConnected() {
				Log.e("MainApplication","网络连接上。");
			}

		});
	}

	private void imageLoader() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "Sdm/Cache");
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.getApplicationContext())
//				.memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3) // 线程池内加载的数量
//				.threadPriority(Thread.NORM_PRIORITY - 2) //线程优先级
				.denyCacheImageMultipleSizesInMemory() //同一个地址如果有多个图片，只缓存一个
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) //缓存实现方式：如果超过缓存大小，先删除不常用的
				.memoryCacheSize(2 * 1024 * 1024) //缓存大小
				.diskCacheSize(50 * 1024 * 1024) //sd卡(本地)缓存的最大值
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()) // 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO) //设置图片加载和显示队列处理的类型
				.diskCacheFileCount(100) // 缓存的文件数量
				.diskCache(new UnlimitedDiskCache(cacheDir)) // 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) //缺省的图片显示的配置项
				.imageDownloader(new BaseImageDownloader(this.getApplicationContext(), 5 * 1000, 30 * 1000)) //图片文件输入流配置
				.writeDebugLogs() //打印调试信息
				.build();// 开始构建
		ImageLoader.getInstance().init(config);
		options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_launcher)// 设置图片加载/解码过程中错误时候显示的图片
				// .resetViewBeforeLoading(true)//在加载前是否重置
				.cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisk(true)// 是否緩存到sd卡上
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)// 设置 图片的缩放方式
				.build();
		/**
		 * 使用方式：
		 * MainApplication.imageLoader.displayImage(urlString, imageView, options);
		 */
	}
}
