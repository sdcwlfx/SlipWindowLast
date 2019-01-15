package com.example.slipwindow.View;

import android.widget.ImageView;
import android.widget.PopupWindow;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.slipwindow.R;

import org.w3c.dom.Text;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by asus on 2017-05-30.
 */

public class SelectPicPopupWnidow extends PopupWindow implements View.OnClickListener{
    private Button btn_cancel;
    private View mMenuView;
    private Activity con;
    private ImageView TXImage;
    private ImageView sinaImage;
    private TextView txTextView;
    private TextView sinaTextView;

    public SelectPicPopupWnidow(final Activity context, View.OnClickListener itemsOnclick){
        super(context);
        // LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.con=context;
        mMenuView=context.getLayoutInflater().inflate(R.layout.share,null);
        // mMenuView=inflater.inflate(R.layout.show,null);
        btn_cancel=(Button)mMenuView.findViewById(R.id.cancel_button);
        btn_cancel.setOnClickListener(new View.OnClickListener() {//取消分享
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TXImage=(ImageView)mMenuView.findViewById(R.id.share_tx_image_view);
        sinaImage=(ImageView)mMenuView.findViewById(R.id.share_sina_image_view);
        txTextView=(TextView)mMenuView.findViewById(R.id.share_tx_text_view);
        sinaTextView=(TextView)mMenuView.findViewById(R.id.share_sina_text_view);

        TXImage.setOnClickListener(this);
        sinaImage.setOnClickListener(this);
        txTextView.setOnClickListener(this);
        sinaTextView.setOnClickListener(this);

        this.setContentView(mMenuView);
        this.setWidth(WindowManager.LayoutParams.FILL_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);//点击空白处消失
        this.setAnimationStyle(R.anim.dialog_enter);
        ColorDrawable drawable=new ColorDrawable(0x00000000);
        //setBackgroundDrawable(new BitmapDrawable());
        setBackgroundDrawable(drawable);
        this.setOutsideTouchable(true);//设置控件外部可点击
        final WindowManager.LayoutParams params=context.getWindow().getAttributes();
        params.alpha=0.4f;
        context.getWindow().setAttributes(params);//设置活动背景变暗
        //this.setBackgroundDrawable(drawable);
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height=mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        WindowManager.LayoutParams params=context.getWindow().getAttributes();
                        params.alpha=1f;
                        context.getWindow().setAttributes(params);//恢复
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.share_sina_image_view:
                showShare();
                break;
            case R.id.share_sina_text_view:
                showShare();
                break;

            case R.id.share_tx_image_view:

                break;
            case R.id.share_tx_text_view:

                break;
        }
    }

    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
      //  oks.setTitle("标题");
        // titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
      //  oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
      //  oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
       // oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
       // oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
      //  oks.setSite("ShareSDK");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
     //   oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(con);
    }
}
