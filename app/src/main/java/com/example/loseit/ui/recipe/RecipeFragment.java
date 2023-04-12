package com.example.loseit.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import com.example.loseit.databinding.FragmentRecipeBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new MyRecipeFragment());
        fragments.add(new MyClollectFragment());
        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(this);
        adapter.setmFragments(fragments);
        binding.viewpager.setAdapter(adapter);
        String[] labels = new String[]{"My Recipes","My Collections"};
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tablayout,binding.viewpager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(labels[position]);
            }
        });
        binding.viewpager.setCurrentItem(0, true);
        mediator.attach();
        binding.viewpager.setUserInputEnabled(false);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class ViewPagerFragmentAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {
        List<Fragment> mFragments;

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        public ViewPagerFragmentAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        public void setmFragments(List<Fragment> mFragments) {
            this.mFragments = mFragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemCount() {
            return mFragments.size();
        }
    }
}