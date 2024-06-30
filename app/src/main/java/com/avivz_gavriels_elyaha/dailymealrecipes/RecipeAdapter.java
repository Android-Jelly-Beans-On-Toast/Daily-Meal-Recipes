package com.avivz_gavriels_elyaha.dailymealrecipes;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avivz_gavriels_elyaha.dailymealrecipes.database.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {
    private final List<Recipe> recipeList;
    private final Context context;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.recipeTitle.setText(recipe.getTitle());
        holder.recipeCriteria.setText(getCriteria(recipe));
        holder.recipeImage.setImageBitmap(recipe.getFoodImage());
    }

    private String getCriteria(Recipe recipe) {
        // Initialize an empty string
        StringBuilder result = new StringBuilder();

        // Append corresponding strings based on boolean values
        if (recipe.isKosher()) {
            result.append("✅");
            result.append("Kosher");
        }
        if (recipe.isQuick()) {
            if (result.length() > 0) {
                result.append(" ✅");
            }
            result.append("Quick");
        }
        if (recipe.isLowCalories()) {
            if (result.length() > 0) {
                result.append(" ✅");
            }
            result.append("LowCalories");
        }

        return result.toString();
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeTitle;
        TextView recipeCriteria;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            recipeTitle = itemView.findViewById(R.id.recipe_title);
            recipeCriteria = itemView.findViewById(R.id.recipe_criteria);
        }
    }
}