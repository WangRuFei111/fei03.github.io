package com.example.read.controller.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.read.R;
import com.example.read.controller.adapter.BookBackAdapter;
import com.example.read.controller.fragment.BookDiscoverFragment;
import com.example.read.controller.fragment.BookmallFragment;
import com.example.read.controller.fragment.BookrackFragment;
import com.example.read.controller.fragment.MeFragment;
import com.example.read.controller.popupWindow.RewritePopwindow;
import com.example.read.model.bean.FruitImage;
import com.example.read.utils.Invisible;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private int currentIndex = 0;
    private RadioGroup mRg_main;
    private RewritePopwindow mPopwindow;
    private ScrollView mScrollView;
    private RelativeLayout mMain_introduction_top;
    //当前显示的fragment
    private static final String CURRENT_FRAGMENT = "STATE_FRAGMENT_SHOW";
    private FragmentManager fragmentManagerBook;
    private Fragment currentFragmentBook = new Fragment();
    private List<Fragment> fragmentsBook = new ArrayList<>();
    private List<FruitImage> mFruitImageBack = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ImageView mMain_title_all,mMain_title_seek;
    private TextView mTextView,me_name;
    private RelativeLayout mRelativeLayout;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mRg_main = findViewById(R.id.home_radiogroup);
        mRg_main.check(R.id.home_button_bookrack);
        mSwipeRefreshLayout = findViewById(R.id.refreshLayout);
        mMain_title_all = findViewById(R.id.main_title_all);
        mScrollView = findViewById(R.id.sv);
        mMain_title_seek = findViewById(R.id.main_title_seek);
        mMain_introduction_top = findViewById(R.id.main_introduction_top);
        mTextView = findViewById(R.id.main_introduction_btu_text);
        mRelativeLayout = findViewById(R.id.main_title);

        fragmentManagerBook = getSupportFragmentManager();
        if (savedInstanceState != null) { // “内存重启”时调用

            //获取“内存重启”时保存的索引下标
            currentIndex = savedInstanceState.getInt(CURRENT_FRAGMENT,0);

            //注意，添加顺序要跟下面添加的顺序一样！！！！
            fragmentsBook.removeAll(fragmentsBook);
            fragmentsBook.add(fragmentManagerBook.findFragmentByTag(0+""));
            fragmentsBook.add(fragmentManagerBook.findFragmentByTag(1+""));
            fragmentsBook.add(fragmentManagerBook.findFragmentByTag(2+""));
            fragmentsBook.add(fragmentManagerBook.findFragmentByTag(3+""));

            //恢复fragment页面
            restoreFragment();



        }else{      //正常启动时调用

            fragmentsBook.add(new BookrackFragment());
            fragmentsBook.add(new BookmallFragment());
            fragmentsBook.add(new BookDiscoverFragment());
            fragmentsBook.add(new MeFragment());

            showFragment();
        }


        //fragment页面进行替换的方法
        cutInitListener();

        //页面下拉刷新的方法
        //设置刷新的背景颜色，默认为白色
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorWhite);
        //设置刷新的背景颜色
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorOrange, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //模拟网络请求需要3000毫秒，请求完成，设置setRefreshing 为false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1200);
            }
        });

        //RecycleView的瀑布流形式
        RecyclerView mRecyBookrack = findViewById(R.id.refres_recycleView);
        StaggeredGridLayoutManager staggeredGridRevyBookrack = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        mRecyBookrack.setLayoutManager(staggeredGridRevyBookrack);
        BookBackAdapter bookBackAdapter = new BookBackAdapter(mFruitImageBack,this);
        mRecyBookrack.setNestedScrollingEnabled(false);
        mRecyBookrack.setAdapter(bookBackAdapter);
        initBookBackImage();
        setListeners();



        //解决SwipeRefreshLayout与ScrollView滑动冲突
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                mSwipeRefreshLayout.setEnabled(mScrollView.getScrollY()==0);
            }
        });

        mRelativeLayout.setBackgroundColor(Color.TRANSPARENT);
    }


    //判断是否登陆状态
    private void itInvisible() {
        if (Invisible.invisible){
            me_name = getLayoutInflater().inflate(R.layout.activity_main_bookme,null).findViewById(R.id.Me_name);
            String userName = getIntent().getStringExtra("userName");
            me_name.setText(userName);
        }else{
            me_name = getLayoutInflater().inflate(R.layout.activity_main_bookme,null).findViewById(R.id.Me_name);
            me_name.setText("点击登陆");
        }
        Log.i("这是itInvisible", "itInvisible: "+ Invisible.invisible);
    }


    //往书架中的RecycleView添加数据
    private void initBookBackImage(){
        for (int i = 0; i < 5; i++) {
            FruitImage fruitImage = new FruitImage(R.drawable.book1);
            mFruitImageBack.add(fruitImage);
            FruitImage fruitImage1 = new FruitImage(R.drawable.book2);
            mFruitImageBack.add(fruitImage1);
            FruitImage fruitImage2 = new FruitImage(R.drawable.book3);
            mFruitImageBack.add(fruitImage2);
            FruitImage fruitImage3 = new FruitImage(R.drawable.book4);
            mFruitImageBack.add(fruitImage3);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //“内存重启”时保存当前的fragment名字
        outState.putInt(CURRENT_FRAGMENT,currentIndex);
        super.onSaveInstanceState(outState);
    }

    //初始化RadioGroup选择监听事件
    private void cutInitListener() {
        mRg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    //初始化书架的首页
                    case R.id.home_button_bookrack:
                        currentIndex = 0;
                        break;
                    //初始化书城的列表
                    case R.id.home_button_bookmall:
                        currentIndex = 1;
                        break;
                    //初始化听书的列表
                    case R.id.home_button_booklisten:
                        currentIndex = 2;
                        break;
                    //初始化我的的列表
                    case R.id.home_button_bookme:
                        //在这里面判断一下是否登录，如果登录
                        currentIndex = 3;
                        //如果未登录跳转到本机登录中去
                        //待完成
                        break;
                }
                //实现fragment切换
                showFragment();
            }
        });
    }

    /**
     * 恢复fragment
     */
    private void restoreFragment(){

        FragmentTransaction BeginTreansactionBook = fragmentManagerBook.beginTransaction();
        for (int i = 0; i < fragmentsBook.size(); i++) {
            if(i == currentIndex){
                BeginTreansactionBook.show(fragmentsBook.get(i));
            }else{
                BeginTreansactionBook.hide(fragmentsBook.get(i));
            }
        }
        BeginTreansactionBook.commit();
        //把当前显示的fragment记录下来
        currentFragmentBook = fragmentsBook.get(currentIndex);
    }




    /**
     * 使用show() hide()切换页面
     * 显示fragment
     */
    private void showFragment(){

        FragmentTransaction transactionssBook = fragmentManagerBook.beginTransaction();

        //如果之前没有添加过
        if(!fragmentsBook.get(currentIndex).isAdded()){
            transactionssBook
                    .hide(currentFragmentBook)
                    .add(R.id.fl_bookrack,fragmentsBook.get(currentIndex),""+currentIndex);  //第三个参数为添加当前的fragment时绑定一个tag

        }else{
            transactionssBook
                    .hide(currentFragmentBook)
                    .show(fragmentsBook.get(currentIndex));
        }

        currentFragmentBook = fragmentsBook.get(currentIndex);

        transactionssBook.commit();

    }


    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            mPopwindow.dismiss();
            mPopwindow.setTouchable(true);
            mPopwindow.backgroundAlpha(MainActivity.this, 1f);
            switch (v.getId()) {
                case R.id.popup_local:
                    Toast.makeText(MainActivity.this, "点击本机导入", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.popup_wifi:
                    Toast.makeText(MainActivity.this, "点击WIFI传书", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.popup_me:
                    Toast.makeText(MainActivity.this, "点击我的书籍", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };





    @Override
    protected void onResume() {
        super.onResume();
        itInvisible();
    }



    //初始化单击方法
    private void setListeners() {
        MainActivity.OnClick onClick = new MainActivity.OnClick();
        mMain_title_all.setOnClickListener(onClick);
        mMain_title_seek.setOnClickListener(onClick);
        mMain_introduction_top.setOnClickListener(onClick);
        mTextView.setOnClickListener(onClick);
    }

    private class OnClick implements View.OnClickListener {
        public void onClick(View view) {
            Intent intent = null;
            switch (view.getId()) {
                //点击图片弹出分享页面
                case R.id.main_title_all:
                    mScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });
                    mPopwindow = new RewritePopwindow(MainActivity.this, itemsOnClick);
                    mPopwindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                    break;
                    //点击弹出搜索界面
                case R.id.main_title_seek:
                    intent = new Intent(MainActivity.this,SearchActivity.class);
                    startActivity(intent);
                    break;
                case R.id.main_introduction_top:
                    intent = new Intent(MainActivity.this,RecommendActivity.class);
                    startActivity(intent);
                    break;
                //跳转到 这不是真的女主页面
                case R.id.main_introduction_btu_text:
                    intent = new Intent(MainActivity.this,CrackDownActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }



}