package com.insthub.ecmobile.fragment;
//

//                       __
//                      /\ \   _
//    ____    ____   ___\ \ \_/ \           _____    ___     ___
//   / _  \  / __ \ / __ \ \    <     __   /\__  \  / __ \  / __ \
//  /\ \_\ \/\  __//\  __/\ \ \\ \   /\_\  \/_/  / /\ \_\ \/\ \_\ \
//  \ \____ \ \____\ \____\\ \_\\_\  \/_/   /\____\\ \____/\ \____/
//   \/____\ \/____/\/____/ \/_//_/         \/____/ \/___/  \/___/
//     /\____/
//     \/___/
//
//  Powered by BeeFramework
//

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.insthub.ecmobile.ECMobileAppConst;
import com.insthub.ecmobile.EcmobileApp;
import com.insthub.ecmobile.protocol.ApiInterface;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.external.androidquery.callback.AjaxStatus;
import com.external.maxwin.view.XListView;
import com.external.viewpagerindicator.PageIndicator;
import com.insthub.BeeFramework.fragment.BaseFragment;
import com.insthub.BeeFramework.model.BusinessResponse;
import com.insthub.BeeFramework.view.MyListView;
import com.insthub.ecmobile.EcmobileManager;
import com.insthub.ecmobile.EcmobileManager.RegisterApp;
import com.insthub.ecmobile.R;
import com.insthub.ecmobile.R.id;
import com.insthub.ecmobile.activity.B1_ProductListActivity;
import com.insthub.ecmobile.activity.B2_ProductDetailActivity;
import com.insthub.ecmobile.activity.BannerWebActivity;
import com.insthub.ecmobile.adapter.B0_IndexAdapter;
import com.insthub.ecmobile.adapter.Bee_PageAdapter;
import com.insthub.ecmobile.model.ConfigModel;
import com.insthub.ecmobile.model.HomeModel;
import com.insthub.ecmobile.model.LoginModel;
import com.insthub.ecmobile.model.ShoppingCartModel;
import com.insthub.ecmobile.protocol.FILTER;
import com.insthub.ecmobile.protocol.PLAYER;
import com.umeng.analytics.MobclickAgent;

public class B0_IndexFragment2 extends BaseFragment
		implements BusinessResponse, XListView.IXListViewListener, RegisterApp {
	private ViewPager bannerViewPager;
	private PageIndicator mIndicator;
	private MyListView mListView;
	// private B0_IndexAdapter listAdapter;

	XList_adapter listAdapter;

	private ArrayList<View> bannerListView;
	private Bee_PageAdapter bannerPageAdapter;
	FrameLayout bannerView;

	private View mTouchTarget;
	private ShoppingCartModel shoppingCartModel;

	private HomeModel dataModel;

	private ImageView back;
	private TextView title;
	private LinearLayout title_right_button;
	private TextView headUnreadTextView;

	private SharedPreferences shared;
	private SharedPreferences.Editor editor;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	View xlist_top;
	LinearLayout vp_content;
	ImageView top_image;
	ImageView two_left;
	ImageView two_right;
	ImageView good_left1;
	ImageView good_right1;
	ImageView good_left2;
	ImageView good_right2;
	ImageView patner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		View mainView = inflater.inflate(R.layout.b0_index, null);

		back = (ImageView) mainView.findViewById(R.id.top_view_back);
		back.setVisibility(View.GONE);
		title = (TextView) mainView.findViewById(R.id.top_view_text);
		Resources resource = this.getResources();
		String ecmobileStr = resource.getString(R.string.ecmobile);
		title.setText(ecmobileStr);

		headUnreadTextView = (TextView) mainView.findViewById(R.id.head_unread_num);

		if (null == dataModel) {
			dataModel = new HomeModel(getActivity());
			dataModel.fetchHotSelling();
			dataModel.fetchCategoryGoods();
		}

		if (null == ConfigModel.getInstance()) {
			ConfigModel configModel = new ConfigModel(getActivity());
			configModel.getConfig();
		}

		dataModel.addResponseListener(this);

		bannerView = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.b0_index_banner, null);

		bannerViewPager = (ViewPager) bannerView.findViewById(R.id.banner_viewpager);

		LayoutParams params1 = bannerViewPager.getLayoutParams();
		params1.width = getDisplayMetricsWidth();
		params1.height = (int) (params1.width * 1.0 / 484 * 200);

		bannerViewPager.setLayoutParams(params1);

		bannerListView = new ArrayList<View>();

		bannerPageAdapter = new Bee_PageAdapter(bannerListView);

		bannerViewPager.setAdapter(bannerPageAdapter);
		bannerViewPager.setCurrentItem(0);

		bannerViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			private int mPreviousState = ViewPager.SCROLL_STATE_IDLE;

			@Override
			public void onPageScrolled(int i, float v, int i2) {

			}

			@Override
			public void onPageSelected(int i) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// All of this is to inhibit any scrollable container from
				// consuming our touch events as the user is changing pages
				if (mPreviousState == ViewPager.SCROLL_STATE_IDLE) {
					if (state == ViewPager.SCROLL_STATE_DRAGGING) {
						mTouchTarget = bannerViewPager;
					}
				} else {
					if (state == ViewPager.SCROLL_STATE_IDLE || state == ViewPager.SCROLL_STATE_SETTLING) {
						mTouchTarget = null;
					}
				}

				mPreviousState = state;
			}
		});

		mIndicator = (PageIndicator) bannerView.findViewById(R.id.indicator);
		mIndicator.setViewPager(bannerViewPager);

		mListView = (MyListView) mainView.findViewById(R.id.home_listview);
		// vp_content.addView(bannerView);
		// mListView.addHeaderView(vp_content);

		mListView.addHeaderView(bannerView);
		mListView.bannerView = bannerView;

		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setXListViewListener(this, 0);
		mListView.setRefreshTime();

		getXListData();

		homeSetAdapter();

		ShoppingCartModel shoppingCartModel = new ShoppingCartModel(getActivity());
		shoppingCartModel.addResponseListener(this);
		shoppingCartModel.homeCartList();

		return mainView;
	}

	public boolean isActive = false;

	@Override
	public void onResume() {
		super.onResume();

		if (!isActive) {
			isActive = true;
			EcmobileManager.registerApp(this);
		}

		LoginModel loginModel = new LoginModel(getActivity());

		ConfigModel configModel = new ConfigModel(getActivity());
		configModel.getConfig();
		MobclickAgent.onPageStart("Home");
	}

	public void homeSetAdapter() {
		if (dataModel.homeDataCache() != null) {
			if (null == listAdapter) {
				// listAdapter = new B0_IndexAdapter(getActivity(), dataModel);
				listAdapter = new XList_adapter(getActivity(), xlist_data);

			}
			mListView.setAdapter(listAdapter);
			addBannerView();
		}

	}

	public void OnMessageResponse(String url, JSONObject jo, AjaxStatus status) {
		if (url.endsWith(ApiInterface.HOME_DATA)) {
			mListView.stopRefresh();
			mListView.setRefreshTime();

			if (null == listAdapter) {
				// listAdapter = new B0_IndexAdapter(getActivity(), dataModel);
				listAdapter = new XList_adapter(getActivity(), xlist_data);
			}
			mListView.setAdapter(listAdapter);
			addBannerView();
		} else if (url.endsWith(ApiInterface.HOME_CATEGORY)) {
			mListView.stopRefresh();
			mListView.setRefreshTime();

			if (null == listAdapter) {
				// listAdapter = new B0_IndexAdapter(getActivity(), dataModel);
				listAdapter = new XList_adapter(getActivity(), xlist_data);
			}
			mListView.setAdapter(listAdapter);
			addBannerView();
		} else if (url.endsWith(ApiInterface.CART_LIST)) {
			TabsFragment.setShoppingcartNum();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dataModel.removeResponseListener(this);
	}

	public void onRefresh(int id) {

		dataModel.fetchHotSelling();
		dataModel.fetchCategoryGoods();

	}

	@Override
	public void onLoadMore(int id) {
		// To change body of implemented methods use File | Settings | File
		// Templates.
	}

	public void addBannerView() {
		bannerListView.clear();
		for (int i = 0; i < dataModel.playersList.size(); i++) {
			PLAYER player = dataModel.playersList.get(i);
			ImageView viewOne = (ImageView) LayoutInflater.from(getActivity()).inflate(R.layout.b0_index_banner_cell,
					null);

			shared = getActivity().getSharedPreferences("userInfo", 0);
			editor = shared.edit();
			String imageType = shared.getString("imageType", "mind");

			if (imageType.equals("high")) {
				imageLoader.displayImage(player.photo.thumb, viewOne, EcmobileApp.options);
			} else if (imageType.equals("low")) {
				imageLoader.displayImage(player.photo.small, viewOne, EcmobileApp.options);
			} else {
				String netType = shared.getString("netType", "wifi");
				if (netType.equals("wifi")) {
					imageLoader.displayImage(player.photo.thumb, viewOne, EcmobileApp.options);
				} else {
					imageLoader.displayImage(player.photo.small, viewOne, EcmobileApp.options);
				}
			}

			try {
				viewOne.setTag(player.toJson().toString());
			} catch (JSONException e) {

			}

			bannerListView.add(viewOne);

			viewOne.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					String playerJSONString = (String) v.getTag();

					try {
						JSONObject jsonObject = new JSONObject(playerJSONString);
						PLAYER player1 = new PLAYER();
						player1.fromJson(jsonObject);
						if (null == player1.action) {
							if (null != player1.url) {
								Intent intent = new Intent(getActivity(), BannerWebActivity.class);
								intent.putExtra("url", player1.url);
								getActivity().startActivity(intent);
								getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							}
						} else {
							if (player1.action.equals("goods")) {
								Intent intent = new Intent(getActivity(), B2_ProductDetailActivity.class);
								intent.putExtra("good_id", player1.action_id + "");
								getActivity().startActivity(intent);
								getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							} else if (player1.action.equals("category")) {
								Intent intent = new Intent(getActivity(), B1_ProductListActivity.class);
								FILTER filter = new FILTER();
								filter.category_id = String.valueOf(player1.action_id);
								intent.putExtra(B1_ProductListActivity.FILTER, filter.toJson().toString());
								startActivity(intent);
								getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							} else if (null != player1.url) {
								Intent intent = new Intent(getActivity(), BannerWebActivity.class);
								intent.putExtra("url", player1.url);
								startActivity(intent);
								getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
							}
						}

					} catch (JSONException e) {

					}

				}
			});

		}

		mIndicator.notifyDataSetChanged();
		mIndicator.setCurrentItem(0);
		bannerPageAdapter.mListViews = bannerListView;
		bannerViewPager.setAdapter(bannerPageAdapter);

	}

	// 获取屏幕宽度
	public int getDisplayMetricsWidth() {
		int i = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		int j = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		return Math.min(i, j);
	}

	@Override
	public void onRegisterResponse(boolean success) {

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("Home");
	}

	@Override
	public void onStop() {

		super.onStop();
		if (!isAppOnForeground()) {
			// app 进入后台
			isActive = false;
		}
	}

	/**
	 * 程序是否在前台运行
	 *
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device

		ActivityManager activityManager = (ActivityManager) getActivity().getApplicationContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = getActivity().getApplicationContext().getPackageName();

		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

	List<Map<String, String>> xlist_data;
	Map<String, String> xmap_data;

	String jsonString = "{\"id\":\"1\",\"goods\":[{\"id\":\"1\",\"type\":\"0\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_top_image.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_top_image.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_top_image.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"type\":\"1\",\"imgs\":[{\"id\":\"40\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"41\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\"},\"link_url\":\"http://www.baidu.com/\"}]},{\"type\":\"2\",\"imgs\":[{\"id\":\"1\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"2\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\"},\"link_url\":\"http://www.baidu.com/\"}]},{\"type\":\"2\",\"imgs\":[{\"id\":\"3\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_good_left.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"4\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_good_right.png\"},\"link_url\":\"http://www.baidu.com/\"}]},{\"id\":\"6\",\"type\":\"3\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_patner.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_patner.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_patner.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"7\",\"type\":\"4\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_item_01.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_item_01.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_item_01.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"8\",\"type\":\"4\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_item_02.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_item_02.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_item_02.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"9\",\"type\":\"4\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_item_03.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_item_03.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_item_03.png\"},\"link_url\":\"http://www.baidu.com/\"},{\"id\":\"10\",\"type\":\"4\",\"img\":{\"thumb\":\"http://www.7slm.com/upload/ecdemo/m_item_04.png\",\"small\":\"http://www.7slm.com/upload/ecdemo/m_item_04.png\",\"url\":\"http://www.7slm.com/upload/ecdemo/m_item_04.png\"},\"link_url\":\"http://www.baidu.com/\"}]}";

	public List<Map<String, String>> getXListData() {
		try {
			JSONObject jsObject = new JSONObject(jsonString);

			xlist_data = new ArrayList<Map<String, String>>();

			JSONArray jaJsonArray = jsObject.getJSONArray("goods");
			for (int i = 0; i < jaJsonArray.length(); i++) {
				jsObject = jaJsonArray.getJSONObject(i);

				int type = Integer.parseInt(jsObject.getString("type"));
				xmap_data = new HashMap<String, String>();
				xmap_data.put("type", type + "");
				switch (type) {
				case 0:
					// 单个
					xmap_data.put("id", jsObject.getString("id"));
					xmap_data.put("link_url", jsObject.getString("link_url").toString());

					jsObject = new JSONObject(jsObject.getString("img"));

					xmap_data.put("thumb", jsObject.getString("thumb"));
					xmap_data.put("small", jsObject.getString("small"));
					xmap_data.put("url", jsObject.getString("url"));

					xmap_data.put("id1", "");
					xmap_data.put("link_url1", "");

					xmap_data.put("thumb1", "");
					xmap_data.put("small1", "");
					xmap_data.put("url1", "");

					break;

				case 1:
					// 横向两张图

					JSONArray two_item = jsObject.getJSONArray("imgs");
					JSONObject item = two_item.getJSONObject(0);

					xmap_data.put("id", item.getString("id"));
					xmap_data.put("link_url", item.getString("link_url"));

					jsObject = new JSONObject(item.getString("img"));

					xmap_data.put("thumb", jsObject.getString("thumb"));
					xmap_data.put("small", jsObject.getString("small"));
					xmap_data.put("url", jsObject.getString("url"));

					item = two_item.getJSONObject(1);
					xmap_data.put("id1", item.getString("id"));
					xmap_data.put("link_url1", item.getString("link_url"));

					jsObject = new JSONObject(item.getString("img"));

					xmap_data.put("thumb1", jsObject.getString("thumb"));
					xmap_data.put("small1", jsObject.getString("small"));
					xmap_data.put("url1", jsObject.getString("url"));

					break;

				case 2:

					// 横向liangge

					JSONArray fore_item = jsObject.getJSONArray("imgs");
					JSONObject items = fore_item.getJSONObject(0);

					xmap_data.put("id", items.getString("id"));
					xmap_data.put("link_url", items.getString("link_url"));

					jsObject = new JSONObject(items.getString("img"));

					xmap_data.put("thumb", jsObject.getString("thumb"));
					xmap_data.put("small", jsObject.getString("small"));
					xmap_data.put("url", jsObject.getString("url"));

					items = fore_item.getJSONObject(1);
					xmap_data.put("id1", items.getString("id"));
					xmap_data.put("link_url1", items.getString("link_url"));

					jsObject = new JSONObject(items.getString("img"));

					xmap_data.put("thumb1", jsObject.getString("thumb"));
					xmap_data.put("small1", jsObject.getString("small"));
					xmap_data.put("url1", jsObject.getString("url"));

					break;

				case 3:
					// 单个
					xmap_data.put("id", jsObject.getString("id"));
					xmap_data.put("link_url", jsObject.getString("link_url").toString());

					jsObject = new JSONObject(jsObject.getString("img"));

					xmap_data.put("thumb", jsObject.getString("thumb"));
					xmap_data.put("small", jsObject.getString("small"));
					xmap_data.put("url", jsObject.getString("url"));

					xmap_data.put("id1", "");
					xmap_data.put("link_url1", "");

					xmap_data.put("thumb1", "");
					xmap_data.put("small1", "");
					xmap_data.put("url1", "");

					break;

				case 4:

					xmap_data.put("id", jsObject.getString("id"));
					xmap_data.put("link_url", jsObject.getString("link_url"));

					jsObject = new JSONObject(jsObject.getString("img"));

					xmap_data.put("thumb", jsObject.getString("thumb"));
					xmap_data.put("small", jsObject.getString("small"));
					xmap_data.put("url", jsObject.getString("url"));

					xmap_data.put("thumb1", "");
					xmap_data.put("small1", "");
					xmap_data.put("url1", "");

				default:

					break;
				}

				xlist_data.add(xmap_data);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xlist_data;

	}

	private SharedPreferences shared1;
	private SharedPreferences.Editor editor1;

	class XList_adapter extends BaseAdapter {

		LayoutInflater inflater;

		protected ImageLoader imageLoader = ImageLoader.getInstance();
		String imageType;
		List<Map<String, String>> list;

		public XList_adapter(Context mContext, List<Map<String, String>> list) {
			list.size();

			shared1 = mContext.getSharedPreferences("userInfo", 0);
			editor1 = shared1.edit();
			imageType = shared1.getString("imageType", "mind");
			this.list = list;

			inflater = LayoutInflater.from(mContext);

		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			HoldeView hv;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.b0_index_category_cell_adapte_layout, null);
				hv = new HoldeView();

				hv.top_image = (ImageView) convertView.findViewById(R.id.top_image);

				hv.two_content = (LinearLayout) convertView.findViewById(R.id.two_content);
				hv.two_left = (ImageView) convertView.findViewById(R.id.two_left);
				hv.two_right = (ImageView) convertView.findViewById(R.id.two_right);

				// 两个一组
				hv.left_right_content = (LinearLayout) convertView.findViewById(R.id.left_right_content1);
				hv.good_left = (ImageView) convertView.findViewById(R.id.good_left1);
				hv.good_right = (ImageView) convertView.findViewById(R.id.good_right1);
				hv.pattner = (ImageView) convertView.findViewById(R.id.patner);

				hv.item = (ImageView) convertView.findViewById(R.id.item);

				convertView.setTag(hv);

			} else {
				hv = (HoldeView) convertView.getTag();
			}
			// case 0:
			// 0 单个//1 横向两张图 2合伙人
			switch (Integer.parseInt(list.get(position).get("type"))) {
			case 0:

				hv.top_image.setVisibility(View.VISIBLE);
				hv.two_content.setVisibility(View.GONE);
				hv.left_right_content.setVisibility(View.GONE);
				hv.pattner.setVisibility(View.GONE);
				hv.item.setVisibility(View.GONE);

				hv.top_image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url"));

					}
				});

				if (imageType.equals("high")) {
					imageLoader.displayImage(list.get(position).get("thumb"), hv.top_image, EcmobileApp.options);
				} else if (imageType.equals("low")) {
					imageLoader.displayImage(list.get(position).get("small"), hv.top_image, EcmobileApp.options);
				} else {
					String netType = shared1.getString("netType", "wifi");
					if (netType.equals("wifi")) {
						imageLoader.displayImage(list.get(position).get("thumb"), hv.top_image, EcmobileApp.options);
					} else {
						imageLoader.displayImage(list.get(position).get("small"), hv.top_image, EcmobileApp.options);
					}
				}

				break;

			case 1:
				hv.two_content.setVisibility(View.VISIBLE);
				hv.top_image.setVisibility(View.GONE);
				hv.left_right_content.setVisibility(View.GONE);
				hv.pattner.setVisibility(View.GONE);
				hv.item.setVisibility(View.GONE);

				if (imageType.equals("high")) {
					imageLoader.displayImage(list.get(position).get("thumb"), hv.two_left, EcmobileApp.options);
				} else if (imageType.equals("low")) {
					imageLoader.displayImage(list.get(position).get("small"), hv.two_left, EcmobileApp.options);
				} else {
					String netType = shared1.getString("netType", "wifi");
					if (netType.equals("wifi")) {
						imageLoader.displayImage(list.get(position).get("thumb"), hv.two_left, EcmobileApp.options);
					} else {
						imageLoader.displayImage(list.get(position).get("small"), hv.two_left, EcmobileApp.options);
					}
				}

				hv.two_left.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url"));

					}
				});

				if (imageType.equals("high")) {
					imageLoader.displayImage(list.get(position).get("thumb1"), hv.two_right, EcmobileApp.options);
				} else if (imageType.equals("low")) {
					imageLoader.displayImage(list.get(position).get("small1"), hv.two_right, EcmobileApp.options);
				} else {
					String netType = shared1.getString("netType", "wifi");
					if (netType.equals("wifi")) {
						imageLoader.displayImage(list.get(position).get("thumb1"), hv.two_right, EcmobileApp.options);
					} else {
						imageLoader.displayImage(list.get(position).get("small1"), hv.two_right, EcmobileApp.options);
					}
				}

				hv.two_right.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url1"));

					}
				});
				break;

			case 2:
				hv.two_content.setVisibility(View.GONE);
				hv.top_image.setVisibility(View.GONE);
				hv.left_right_content.setVisibility(View.VISIBLE);
				hv.pattner.setVisibility(View.GONE);
				hv.item.setVisibility(View.GONE);

				int id = Integer.parseInt(list.get(position).get("id"));
				switch (id) {
				case 1:

					hv.good_left.setImageResource(R.drawable.index_1);

					break;

				case 3:
					hv.good_left.setImageResource(R.drawable.index2_1);
					break;

				default:
					break;
				}

				// if (imageType.equals("high")) {
				// imageLoader.displayImage(list.get(position).get("thumb"),
				// hv.good_left, EcmobileApp.options);
				// } else if (imageType.equals("low")) {
				// imageLoader.displayImage(list.get(position).get("small"),
				// hv.good_left, EcmobileApp.options);
				// } else {
				// String netType = shared1.getString("netType", "wifi");
				// if (netType.equals("wifi")) {
				// imageLoader.displayImage(list.get(position).get("thumb"),
				// hv.good_left, EcmobileApp.options);
				// } else {
				// imageLoader.displayImage(list.get(position).get("small"),
				// hv.good_left, EcmobileApp.options);
				// }
				// }

				hv.good_left.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url"));

					}
				});
				
				id = Integer.parseInt(list.get(position).get("id1"));
				switch (id) {
				case 2:

					hv.good_right.setImageResource(R.drawable.index_2);

					break;

				case 4:
					hv.good_right.setImageResource(R.drawable.index2_2);
					break;

				default:
					break;
				}

				// if (imageType.equals("high")) {
				// imageLoader.displayImage(list.get(position).get("thumb1"),
				// hv.good_right, EcmobileApp.options);
				// } else if (imageType.equals("low")) {
				// imageLoader.displayImage(list.get(position).get("small1"),
				// hv.good_right, EcmobileApp.options);
				// } else {
				// String netType = shared1.getString("netType", "wifi");
				// if (netType.equals("wifi")) {
				// imageLoader.displayImage(list.get(position).get("thumb1"),
				// hv.good_right, EcmobileApp.options);
				// } else {
				// imageLoader.displayImage(list.get(position).get("small1"),
				// hv.good_right, EcmobileApp.options);
				// }
				// }

				hv.good_right.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url1"));

					}
				});
				break;

			case 3:

				hv.top_image.setVisibility(View.GONE);
				hv.two_content.setVisibility(View.GONE);
				hv.left_right_content.setVisibility(View.GONE);
				hv.pattner.setVisibility(View.VISIBLE);
				hv.item.setVisibility(View.GONE);
				hv.pattner.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url"));

					}
				});

				if (imageType.equals("high")) {
					imageLoader.displayImage(list.get(position).get("thumb"), hv.pattner, EcmobileApp.options);
				} else if (imageType.equals("low")) {
					imageLoader.displayImage(list.get(position).get("small"), hv.pattner, EcmobileApp.options);
				} else {
					String netType = shared1.getString("netType", "wifi");
					if (netType.equals("wifi")) {
						imageLoader.displayImage(list.get(position).get("thumb"), hv.pattner, EcmobileApp.options);
					} else {
						imageLoader.displayImage(list.get(position).get("small"), hv.pattner, EcmobileApp.options);
					}
				}

				break;

			case 4:

				hv.top_image.setVisibility(View.GONE);
				hv.two_content.setVisibility(View.GONE);
				hv.left_right_content.setVisibility(View.GONE);
				hv.pattner.setVisibility(View.GONE);
				hv.item.setVisibility(View.VISIBLE);

				hv.item.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {

						startAactivity(list.get(position).get("link_url"));

					}
				});

				if (imageType.equals("high")) {
					imageLoader.displayImage(list.get(position).get("thumb"), hv.item, EcmobileApp.options);
				} else if (imageType.equals("low")) {
					imageLoader.displayImage(list.get(position).get("small"), hv.item, EcmobileApp.options);
				} else {
					String netType = shared1.getString("netType", "wifi");
					if (netType.equals("wifi")) {
						imageLoader.displayImage(list.get(position).get("thumb"), hv.item, EcmobileApp.options);
					} else {
						imageLoader.displayImage(list.get(position).get("small"), hv.item, EcmobileApp.options);
					}
				}

				break;

			default:
				break;
			}

			return convertView;
		}

		public class HoldeView {

			// 单个item
			ImageView top_image;

			// 两个一组
			LinearLayout left_right_content;
			ImageView good_left;
			ImageView good_right;
			ImageView item;

			LinearLayout two_content;
			ImageView two_left;
			ImageView two_right;

			// 合作伙伴
			ImageView pattner;

		}

		public void startAactivity(String link_url) {
			Intent intent = new Intent(getActivity(), BannerWebActivity.class);
			intent.putExtra("url", link_url);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
		}
	}

	String imageType;

	public void initViewBG(String url_thumb, String url_small, ImageView v, final String link_url) {

		shared1 = getActivity().getSharedPreferences("userInfo", 0);
		editor1 = shared1.edit();
		imageType = shared1.getString("imageType", "mind");

		if (imageType.equals("high")) {
			imageLoader.displayImage(url_thumb, v, EcmobileApp.options);
		} else if (imageType.equals("low")) {
			imageLoader.displayImage(url_small, v, EcmobileApp.options);
		} else {
			String netType = shared1.getString("netType", "wifi");
			if (netType.equals("wifi")) {
				imageLoader.displayImage(url_thumb, v, EcmobileApp.options);
			} else {
				imageLoader.displayImage(url_small, v, EcmobileApp.options);
			}
		}

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				startAactivity(link_url);

			}
		});

	}

	public void startAactivity(String link_url) {
		Intent intent = new Intent(getActivity(), BannerWebActivity.class);
		intent.putExtra("url", link_url);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	}

}
