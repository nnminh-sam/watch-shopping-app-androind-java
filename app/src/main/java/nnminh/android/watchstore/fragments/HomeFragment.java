package nnminh.android.watchstore.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nnminh.android.watchstore.R;
import nnminh.android.watchstore.adapters.CategoryAdapter;
import nnminh.android.watchstore.adapters.ProductAdapter;
import nnminh.android.watchstore.models.*;
import nnminh.android.watchstore.network.ApiClient;
import nnminh.android.watchstore.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewProducts, recyclerViewCategories;
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private ProgressBar progressBar;
    private TextView textError;
    private EditText editTextSearch;
    private ImageButton buttonSearch, buttonClearFilters;
    private Spinner spinnerPrice, spinnerBrand;

    private List<Category> categoryList = new ArrayList<>();
    private List<Brand> brandList = new ArrayList<>();
    private String selectedCategoryName = null;
    private String selectedBrandName = null;
    private Long minPrice = null, maxPrice = null;

    private int currentPage = 1;
    private int totalPages = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewProducts = view.findViewById(R.id.recyclerViewProducts);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        progressBar = view.findViewById(R.id.progressBar);
        textError = view.findViewById(R.id.textError);
        editTextSearch = view.findViewById(R.id.editTextSearch);
        buttonSearch = view.findViewById(R.id.buttonSearch);
        spinnerPrice = view.findViewById(R.id.spinnerPrice);
        spinnerBrand = view.findViewById(R.id.spinnerBrand);
        buttonClearFilters = view.findViewById(R.id.buttonClearFilters);

        // Setup RecyclerViews
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), null, false, 1, 1,
                new ProductAdapter.PaginationListener() {
                    @Override public void onPrev() { goToPage(currentPage - 1); }
                    @Override public void onNext() { goToPage(currentPage + 1); }
                }
        );
        recyclerViewProducts.setAdapter(productAdapter);

        recyclerViewCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), (category, position) -> {
            if (position == 0) {
                selectedCategoryName = null;
            } else {
                selectedCategoryName = category.getName();
            }
            currentPage = 1;
            filterProducts();
        });
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Setup price filter spinner with small text size
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item_small,
                new String[]{"All", "Below 2M", "2M–5M", "5M–10M", "Above 10M"}
        );
        priceAdapter.setDropDownViewResource(R.layout.spinner_item_small);
        spinnerPrice.setAdapter(priceAdapter);

        spinnerPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: minPrice = null; maxPrice = null; break;
                    case 1: minPrice = null; maxPrice = 2000000L; break;
                    case 2: minPrice = 2000000L; maxPrice = 5000000L; break;
                    case 3: minPrice = 5000000L; maxPrice = 10000000L; break;
                    case 4: minPrice = 10000000L; maxPrice = null; break;
                }
                currentPage = 1;
                filterProducts();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Setup search button
        buttonSearch.setOnClickListener(v -> {
            currentPage = 1;
            filterProducts();
        });

        // Setup brand spinner (populated after brands are fetched)
        spinnerBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedBrandName = null;
                } else {
                    selectedBrandName = brandList.get(position - 1).getName();
                }
                currentPage = 1;
                filterProducts();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        buttonClearFilters.setOnClickListener(v -> clearFilters());

        fetchCategories();
        fetchBrands();
        filterProducts();
        return view;
    }

    private void fetchCategories() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        Call<CategoryResponse> call = apiService.getCategories();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<CategoryResponse> call, @NonNull Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = new ArrayList<>();
                    Category allCategory = new Category();
                    allCategory.setName("All");
                    categoryList.add(allCategory);
                    if (response.body().getCategories() != null) {
                        categoryList.addAll(response.body().getCategories());
                    }
                    categoryAdapter = new CategoryAdapter(categoryList, (category, position) -> {
                        if (position == 0) {
                            selectedCategoryName = null;
                        } else {
                            selectedCategoryName = category.getName();
                        }
                        currentPage = 1;
                        filterProducts();
                    });
                    recyclerViewCategories.setAdapter(categoryAdapter);
                }
            }
            @Override
            public void onFailure(@NonNull Call<CategoryResponse> call, @NonNull Throwable t) { }
        });
    }

    private void fetchBrands() {
        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        Call<BrandResponse> call = apiService.getBrands();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<BrandResponse> call, @NonNull Response<BrandResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    brandList = response.body().getBrands();
                    List<String> brandNames = new ArrayList<>();
                    brandNames.add("All");
                    for (Brand b : brandList) brandNames.add(b.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            R.layout.spinner_item_small,
                            brandNames
                    );
                    adapter.setDropDownViewResource(R.layout.spinner_item_small);
                    spinnerBrand.setAdapter(adapter);
                    spinnerBrand.setSelection(0);
                }
            }
            @Override
            public void onFailure(@NonNull Call<BrandResponse> call, @NonNull Throwable t) { }
        });
    }

    // Call the filtered products API
    private void filterProducts() {
        progressBar.setVisibility(View.VISIBLE);
        textError.setVisibility(View.GONE);

        String searchValue = editTextSearch.getText().toString().trim();
        FilterRequest request = new FilterRequest();
        if (!TextUtils.isEmpty(searchValue)) request.setName(searchValue);
        if (selectedCategoryName != null) request.setCategoryName(selectedCategoryName);
        if (selectedBrandName != null) request.setBrandName(selectedBrandName);
        if (minPrice != null) request.setMin_price(minPrice);
        if (maxPrice != null) request.setMax_price(maxPrice);
        request.setPage(currentPage);

        ApiService apiService = ApiClient.getClient(getContext()).create(ApiService.class);
        Call<ProductResponse> call = apiService.getProducts(FilterRequest.filterRequestToMap(request));

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ProductResponse> call, @NonNull Response<ProductResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && !response.body().getProducts().isEmpty()) {
                    // Pagination handling
                    Pagination pagination = response.body().getPagination();
                    if (pagination != null) {
                        currentPage = pagination.getPage();
                        totalPages = pagination.getTotal_pages();
                    } else {
                        currentPage = 1;
                        totalPages = 1;
                    }
                    boolean showFooter = totalPages > 1;
                    productAdapter.update(response.body().getProducts(), currentPage, totalPages, showFooter);
                    recyclerViewProducts.setVisibility(View.VISIBLE);
                    recyclerViewProducts.scrollToPosition(0);
                } else {
                    productAdapter.update(new ArrayList<>(), 1, 1, false);
                    recyclerViewProducts.scrollToPosition(0);
                    textError.setText("No products found");
                    textError.setVisibility(View.VISIBLE);
                    recyclerViewProducts.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProductResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                productAdapter.update(new ArrayList<>(), 1, 1, false);
                recyclerViewProducts.scrollToPosition(0);
                textError.setText("Failed to load products");
                textError.setVisibility(View.VISIBLE);
                recyclerViewProducts.setVisibility(View.GONE);
            }
        });
    }

    private void clearFilters() {
        editTextSearch.setText("");
        if (categoryAdapter != null) {
            recyclerViewCategories.smoothScrollToPosition(0);
            categoryAdapter.setSelectedPosition(0);
        }
        selectedCategoryName = null;
        if (spinnerBrand.getAdapter() != null && spinnerBrand.getAdapter().getCount() > 0) {
            spinnerBrand.setSelection(0);
        }
        selectedBrandName = null;
        spinnerPrice.setSelection(0);
        minPrice = null;
        maxPrice = null;
        currentPage = 1;
        filterProducts();
    }

    private void goToPage(int page) {
        if (page >= 1 && page <= totalPages && page != currentPage) {
            currentPage = page;
            filterProducts();
        }
    }
}