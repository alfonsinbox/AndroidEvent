package com.example.alfon.eventtest;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectCategoriesActivity extends AppCompatActivity {

    EditText editTextSearchCategories;
    ListView listViewCategories;
    Activity activity;
    MobileServiceClient mClient;

    List<Category> selectedCategories;
    ListenableFuture<ServiceFilterResponse> categoriesListenableFuture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_categories);

        activity = this;
        mClient = ((GlobalApplication)getApplication()).getmClient();
        editTextSearchCategories = (EditText)findViewById(R.id.edittext_search_categories);
        listViewCategories = (ListView)findViewById(R.id.listview_categories);

        selectedCategories = (List<Category>) getIntent().getSerializableExtra("categories");
        if(selectedCategories == null){
            selectedCategories = new ArrayList<>();
        }

        // It will be populated when initial request is done. Don't do it here
        // populateCategoryList(selectedCategories);

        editTextSearchCategories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(categoriesListenableFuture != null){
                    categoriesListenableFuture.cancel(true);
                }
                categoriesListenableFuture = CategoryUtilities.getCategoriesFuture(activity, s.toString(), mClient);
                Futures.addCallback(categoriesListenableFuture, new FutureCallback<ServiceFilterResponse>() {
                    @Override
                    public void onSuccess(ServiceFilterResponse result) {
                        List<Category> categoriesResult = new Gson().fromJson(result.getContent(), new TypeToken<List<Category>>() {
                        }.getType());
                        populateCategoryList(categoriesResult);
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                });
            }
        });

        categoriesListenableFuture = CategoryUtilities.getCategoriesFuture(activity, "", mClient);
        Futures.addCallback(categoriesListenableFuture, new FutureCallback<ServiceFilterResponse>() {
            @Override
            public void onSuccess(ServiceFilterResponse result) {
                System.out.println(result.getContent());
                List<Category> categoriesResult = new Gson().fromJson(result.getContent(), new TypeToken<List<Category>>() {
                }.getType());
                populateCategoryList(categoriesResult);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void populateCategoryList(List<Category> categories){

        for (Category category: categories) {
            for(Category selectedCategory: selectedCategories){
                if(selectedCategory.id.equals(category.id)){
                    category.category_is_selected = true;
                }
            }
        }

        System.out.println(selectedCategories.size());
        System.out.println(new Gson().toJson(selectedCategories));

        CategorySearchAdapter categorySearchAdapter = new CategorySearchAdapter(activity, R.layout.list_item_user_search, categories);
        listViewCategories.setAdapter(categorySearchAdapter);
        listViewCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);

                for (Category category: selectedCategories) {
                    if (category.id.equals(selectedCategory.id)) {
                        ((CheckBox) view.findViewById(R.id.category_is_selected)).setChecked(false);
                        selectedCategories.remove(category);
                        System.out.println(selectedCategories.size());
                        System.out.println(new Gson().toJson(selectedCategories));
                        return;
                    }
                }

                ((CheckBox) view.findViewById(R.id.category_is_selected)).setChecked(true);
                selectedCategories.add(selectedCategory);

                System.out.println(selectedCategories.size());
                System.out.println(new Gson().toJson(selectedCategories));
            }
        });
    }

    public void returnSelectedCategories(View view) {
        Intent result = new Intent();
        result.putExtra("categories", (Serializable) selectedCategories);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
}
