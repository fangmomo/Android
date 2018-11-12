package com.example.lenovo.hm.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.lenovo.hm.MyApplication;
import com.example.lenovo.hm.R;
import com.youth.banner.loader.ImageLoader;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class homeFragment extends Fragment {

    private ArrayList<String> list_path = new ArrayList<>();
    private MyApplication myApplication = new MyApplication();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View v = inflater.inflate(R.layout.home_layout, container,false);

        /*Log.e("tag",getResourcesUri(myApplication.getContext(),getResourcesint("daohang")));
        list_path.add(getResourcesUri(myApplication.getContext(),getResourcesint("daohang")));
        list_path.add(getResourcesUri(myApplication.getContext(),getResourcesint("daohang0")));
        list_path.add(getResourcesUri(myApplication.getContext(),getResourcesint("daohang1")));

        Banner banner = (Banner) v.findViewById(R.id.banner);
        //设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        //设置图片集合
        banner.setImages(list_path);
        banner.setDelayTime(1500);
        //banner设置方法全部调用完毕时最后调用
        banner.start();*/

        return v;
    }
    public class GlideImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            /**
             注意：
             1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
             2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
             传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
             切记不要胡乱强转！
             */

            //Glide 加载图片简单用法
            Glide.with(context).load(path).into(imageView);

            /*//Picasso 加载图片简单用法
            Picasso.with(context).load(path).into(imageView);

            //用fresco加载图片简单用法，记得要写下面的createImageView方法
            Uri uri = Uri.parse((String) path);
            imageView.setImageURI(uri);*/
        }

        //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public ImageView createImageView(Context context) {
            /*//使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
            SimpleDraweeView simpleDraweeView=new SimpleDraweeView(context);
            return simpleDraweeView;*/
            return null;
        }
    }
    private String getResourcesUri(Context context,@DrawableRes int id) {
        Resources resources = context.getResources();
        String uriPath = ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }

    private int  getResourcesint(String path) {
        Class drawable = R.drawable.class;
        Field field = null;
        int res_ID = 0;
        try {
            field = drawable.getField(path);
            res_ID = field.getInt(field.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res_ID;
    }
}
