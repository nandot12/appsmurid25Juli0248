package com.example22.lenovo.murid;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example22.lenovo.murid.helper.Constant;
import com.example22.lenovo.murid.helper.MyConstant;
import com.example22.lenovo.murid.helper.No_Internet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    @BindView(R.id.flipper)
    ViewFlipper flipper;
    @BindView(R.id.txtRunning)
    TextView txtRunning;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.frameDisplay)
    FrameLayout frameDisplay;
    Unbinder unbinder;

    AQuery aq;
    String teksRunning;


    public HomeFragment() {
    }

    SessionManager sesi;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 100;
    private String judulSlider, idSlider, gmbSlider, deskSlider;
    private boolean mIsAppFirstLaunched = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aq = new AQuery(getContext());
        sesi = new SessionManager(getContext());
        recyclerView = view.findViewById(R.id.paketrecyelrview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        txtRunning.setSelected(true);

        flipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
        flipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
        flipper.setFlipInterval(4000);
        flipper.startFlipping();
        if (!HeroHelper.isOnline(getActivity())) {
            startActivity(new Intent(getActivity(), No_Internet.class));
            getActivity().finish();
        } else {

            getDataMenu();
            getData();

        }


    }




    private void getData() {

        RetrofitClient.service.actionGetPacket().enqueue(new Callback<ResponsePaket>() {
            @Override
            public void onResponse(Call<ResponsePaket> call, Response<ResponsePaket> response) {

                if (response.isSuccessful()) {

                    Boolean status = response.body().isStatus();
                    String pesan = response.body().getPesan();

                    if (status) {
                        List<DataItem> data = response.body().getData();

                        tampil(data);


                    } else {

                        Toast.makeText(getContext(), pesan, Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponsePaket> call, Throwable t) {

            }
        });
    }
    private void tampil(List<DataItem> data) {
        recyclerView.setAdapter(new PaketAdapter(data, getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showData(List<DataItem> data) {
        recyclerView.setAdapter(new PaketAdapter(data, getContext()));
    }


    private void getDataMenu() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {


            //ambil data dari server
            String url = MyConstant.BASE_URL + "getSlider";
            Map<String, String> parampa = new HashMap<>();
            try {
                //mencari url dan parameter yang dikirimkan
                HeroHelper.pre("Url : " + url + ", params " + parampa.toString());
                //koneksi ke server meggunakan aquery
                aq.ajax(url, parampa, String.class,
                        new AjaxCallback<String>() {
                            @Override
                            public void callback(String url, String hasil, AjaxStatus status) {
                                //cek apakah hasilnya null atau tidak
                                if (hasil != null) {
                                    HeroHelper.pre("Responslider : " + hasil);
                                    //merubah string menjadi json
                                    try {
                                        JSONObject json = new JSONObject(hasil);
                                        String result = json.getString("status");
                                        String pesan = json.getString("pesan");
                                        // NurHelper.pesan(getActivity(), pesan);
                                        if (result.equalsIgnoreCase("true")) {
                                            JSONArray jsonArray = json.getJSONArray("data");
                                            for (int a = 0; a < jsonArray.length(); a++) {
                                                HashMap<String, String> dataMap = new HashMap<>();
                                                JSONObject object = jsonArray.getJSONObject(a);
                                                idSlider = object.getString("id_slider");
                                                judulSlider = object.getString("judul_slider");
//                                            hargaMenu = object.getString("harga_menu");
                                                deskSlider = object.getString("desc_slider");
                                                gmbSlider = object.getString("gbr_slider");
//

                                                if ((a < Constant.VALUE_SLIDESHOW) && mIsAppFirstLaunched) {
                                                    createSlideshow(idSlider, judulSlider, gmbSlider, deskSlider);
                                                }
                                            }
                                        } else {
//                                            KolakaHelper.pesan(getApplicationContext(), pesan);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
//                                        KolakaHelper.pesan(getApplicationContext(), "Error parsing data");
                                    }
                                }
                            }
                        });

            } catch (Exception e) {
//                KolakaHelper.pesan(getApplicationContext(), "Error get data");
                e.printStackTrace();
            }
        }
    }

    private void createSlideshow(String idSlider, String judulSlider, String imgSlider, String deskSlider) {
        View view = getLayoutInflater().inflate(R.layout.layout_slideshow, null);

        ImageView sliderImg = (ImageView) view.findViewById(R.id.imgMenu);
        TextView namaMenu = (TextView) view.findViewById(R.id.tvNamaMenu);
        TextView sliderJudul = (TextView) view.findViewById(R.id.tvNamaSlider);
        TextView deskripsiMenu = (TextView) view.findViewById(R.id.tvDesMenu);

        // menuImg.setId(Integer.parseInt(idMenu));
//        menuImg.setOnClickListener(this);

        namaMenu.setText(judulSlider);
        Log.d("slider", MyConstant.IMAGE_SLIDER_URL + imgSlider);


        Glide.with(getActivity())
                .load(MyConstant.IMAGE_SLIDER_URL + imgSlider)
                .placeholder(R.drawable.img_galeri)
                .override(175, 200)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.img_galeri)
                .skipMemoryCache(true)
                .into(sliderImg);

        // Add layout to flipper
        flipper.addView(view);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}