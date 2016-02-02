package com.temp.jhostelapp.ui;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DSM_ on 2/2/16.
 */
public class MessMeal {

    private String meal;
    private String menu;

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public MessMeal fromJSON(JSONObject jsonObject) {
        try {
            setMeal(jsonObject.getString("meal"));
            setMenu(jsonObject.getString("menu"));
            return this;
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("meal", getMeal());
            jsonObject.put("menu", getMenu());
            return jsonObject.toString();
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessMeal messMeal = (MessMeal) o;

        return !(meal != null ? !meal.equals(messMeal.meal) : messMeal.meal != null);

    }
}
