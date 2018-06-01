package com.example.android.mygarden;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.mygarden.provider.PlantContract;
import com.example.android.mygarden.ui.PlantDetailActivity;
import com.example.android.mygarden.utils.PlantUtils;

import static com.example.android.mygarden.provider.PlantContract.BASE_CONTENT_URI;
import static com.example.android.mygarden.provider.PlantContract.PATH_PLANTS;


public class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    Context context;
    Cursor cursor;

    public GridRemoteViewsFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

        Uri Plant_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLANTS).build();
        if (cursor != null) cursor.close();
        cursor = context.getContentResolver().query(
                Plant_URI, null, null, null, PlantContract.PlantEntry.COLUMN_CREATION_TIME
        );
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (cursor != null) return cursor.getCount();
        return 0;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToPosition(position);
            int idIndex = cursor.getColumnIndex(PlantContract.PlantEntry._ID);
            int createTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_CREATION_TIME);
            int waterTimeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_LAST_WATERED_TIME);
            int plantTypeIndex = cursor.getColumnIndex(PlantContract.PlantEntry.COLUMN_PLANT_TYPE);

            long plantId = cursor.getLong(idIndex);
            long timeNow = System.currentTimeMillis();
            long wateredAt = cursor.getLong(waterTimeIndex);
            long createdAt = cursor.getLong(createTimeIndex);
            int plantType = cursor.getInt(plantTypeIndex);
            cursor.close();
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.plant_widget);
//            canWater = (timeNow - wateredAt) > PlantUtils.MIN_AGE_BETWEEN_WATER &&
//                    (timeNow - wateredAt) < PlantUtils.MAX_AGE_WITHOUT_WATER;
            int imgRes = PlantUtils.getPlantImageRes(context, timeNow - createdAt, timeNow - wateredAt, plantType);

            views.setImageViewResource(R.id.widget_plant_image, imgRes);
            views.setTextViewText(R.id.widget_plant_name, String.valueOf(plantId));
            views.setViewVisibility(R.id.widget_water_button, View.GONE);

            Bundle bundle = new Bundle();
            bundle.putLong(PlantDetailActivity.EXTRA_PLANT_ID,plantId);
            Intent fillIntent = new Intent();
            fillIntent.putExtras(bundle);
            views.setOnClickFillInIntent(R.id.widget_plant_image,fillIntent);
            return views;
        }
        return null;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
