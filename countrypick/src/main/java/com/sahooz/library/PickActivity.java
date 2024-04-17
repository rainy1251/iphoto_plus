package com.sahooz.library;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PickActivity extends AppCompatActivity {

    private ArrayList<Country> selectedCountries = new ArrayList<>();
    private ArrayList<Country> allCountries = new ArrayList<>();
    private ArrayList<Country> hotCountries = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
        setContentView(R.layout.activity_pick);
        RecyclerView rvPick = (RecyclerView) findViewById(R.id.rv_pick);
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        SideBar side = (SideBar) findViewById(R.id.side);
        EditText etSearch = (EditText) findViewById(R.id.et_search);
        TextView tvLetter = (TextView) findViewById(R.id.tv_letter);
        allCountries.clear();
        allCountries.addAll(Country.getAll(this, null));
        selectedCountries.clear();
        hotCountries.add(new Country(86,"#a中国","CN",1));
        hotCountries.add(new Country(1,"#b美国","US",1));
        hotCountries.add(new Country(81,"#c日本","JP",1));
        hotCountries.add(new Country(44,"#d英国","GB",1));
        hotCountries.add(new Country(852,"#e中国香港","HK",1));
        hotCountries.add(new Country(853,"#f中国澳门","MO",1));
        hotCountries.add(new Country(886,"#g中国台湾","TW",1));
        allCountries.addAll(0, hotCountries);
        selectedCountries.addAll(allCountries);


        final CAdapter adapter = new CAdapter(selectedCountries);
        rvPick.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvPick.setLayoutManager(manager);
        rvPick.setAdapter(adapter);
       // rvPick.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override public void afterTextChanged(Editable s) {
                String string = s.toString();
                selectedCountries.clear();
                for (Country country : allCountries) {
                    if(country.name.toLowerCase().contains(string.toLowerCase()))
                        selectedCountries.add(country);
                }
                adapter.update(selectedCountries);
            }
        });


      //  side.addIndex("#", side.indexes.size());
        side.setOnLetterChangeListener(new SideBar.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                tvLetter.setVisibility(View.VISIBLE);
                int  position = adapter.getLetterPosition(letter);
                if (position==0){
                    tvLetter.setText("热门");
                }else{

                    tvLetter.setText(letter);
                }

                if (position != -1) {
                    manager.scrollToPositionWithOffset(position, 0);
                }
            }

            @Override
            public void onReset() {
                tvLetter.setVisibility(View.GONE);
            }
        });
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    class CAdapter extends PyAdapter<RecyclerView.ViewHolder> {

        public CAdapter(List<? extends PyEntity> entities) {
            super(entities);
        }

        @Override
        public RecyclerView.ViewHolder onCreateLetterHolder(ViewGroup parent, int viewType) {
            return new LetterHolder(getLayoutInflater().inflate(R.layout.item_letter, parent, false));
        }

        @Override
        public RecyclerView.ViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            return new VH(getLayoutInflater().inflate(R.layout.item_country_large_padding, parent, false));
        }

        @Override
        public void onBindHolder(RecyclerView.ViewHolder holder, PyEntity entity, int position) {
            VH vh = (VH)holder;
            final Country country = (Country)entity;
          //  vh.ivFlag.setImageResource(country.flag);
            vh.tvName.setText(country.name);
            vh.tvCode.setText("+" + country.code);
            holder.itemView.setOnClickListener(v -> {
                Intent data = new Intent();
                data.putExtra("country", country.toJson());
                setResult(Activity.RESULT_OK , data);
                finish();
            });
            if (position<8){
                if (country.name.contains("#")){

                    vh.tvName.setText(country.name.substring(2,country.name.length()));
                }
            }

        }

        @Override
        public void onBindLetterHolder(RecyclerView.ViewHolder holder, LetterEntity entity, int position) {
            ((LetterHolder)holder).textView.setText(entity.letter.toUpperCase());
            if (position==0){
                ((LetterHolder)holder).textView.setText("热门");
            }
        }

    }
}
