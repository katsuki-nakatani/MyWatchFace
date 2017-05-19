package org.gdgkobe.mywatchface;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.view.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by katsuki-nakatani on 2017/05/16.
 */

public class ComplicationConfigActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "CompSimpleConfig";

    private static final int PROVIDER_CHOOSER_REQUEST_CODE = 1;

    private WearableRecyclerView mWearableConfigListView;
    private ConfigurationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complication_config);

        mAdapter = new ConfigurationAdapter(getApplicationContext(), getComplicationItems(), this);
        mWearableConfigListView = (WearableRecyclerView) findViewById(R.id.recycler_view);
        mWearableConfigListView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PROVIDER_CHOOSER_REQUEST_CODE
                && resultCode == RESULT_OK) {
            ComplicationProviderInfo complicationProviderInfo =
                    data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onClick()");
        }

        int position = mWearableConfigListView.getChildAdapterPosition(view);
        ComplicationItem complicationItem = ((ConfigurationAdapter) mWearableConfigListView.getAdapter()).getItem(position);

        startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        getApplicationContext(),
                        complicationItem.watchFace,
                        complicationItem.complicationId,
                        complicationItem.supportedTypes),
                PROVIDER_CHOOSER_REQUEST_CODE);
    }

    private List<ComplicationItem> getComplicationItems() {
        ComponentName watchFace = new ComponentName(
                getApplicationContext(), MyWatchFace.class);

        String[] complicationNames =
                getResources().getStringArray(R.array.complication_simple_names);

        int[] complicationIds = MyWatchFace.COMPLICATION_IDS;

        TypedArray icons = getResources().obtainTypedArray(R.array.complication_simple_icons);

        List<ComplicationItem> items = new ArrayList<>();
        for (int i = 0; i < complicationIds.length; i++) {
            items.add(new ComplicationItem(watchFace,
                    complicationIds[i],
                    MyWatchFace.COMPLICATION_SUPPORTED_TYPES[i],
                    icons.getDrawable(i),
                    complicationNames[i]));
        }
        return items;
    }

    private final class ComplicationItem {
        ComponentName watchFace;
        int complicationId;
        int[] supportedTypes;
        Drawable icon;
        String title;

        public ComplicationItem(ComponentName watchFace, int complicationId, int[] supportedTypes,
                                Drawable icon, String title) {
            this.watchFace = watchFace;
            this.complicationId = complicationId;
            this.supportedTypes = supportedTypes;
            this.icon = icon;
            this.title = title;
        }
    }

    private static class ConfigurationAdapter extends WearableRecyclerView.Adapter<ConfigurationAdapter.ViewHolder> {

        private Context mContext;
        private List<ComplicationItem> mItems;

        public View.OnClickListener listener;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClick(v);

                }
            });
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(mItems.get(position).title);
            holder.imageView.setImageDrawable(mItems.get(position).icon);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends WearableRecyclerView.ViewHolder {

            TextView textView;
            ImageView imageView;

            public ViewHolder(View itemView) {
                super(itemView);

                textView = (TextView) itemView.findViewById(R.id.name);
                imageView = (ImageView) itemView.findViewById(R.id.icon);
            }
        }

        public ConfigurationAdapter(Context context, List<ComplicationItem> items, View.OnClickListener listener) {
            mContext = context;
            mItems = items;
            this.listener = listener;
        }

        public ComplicationItem getItem(int position) {
            return mItems.get(position);
        }

    }
}
