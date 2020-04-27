package com.darshansfa.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import java.util.List;

import butterknife.ButterKnife;
import com.darshansfa.Adapters.DistributorDialogAdapter;
import com.darshansfa.R;
import com.darshansfa.dbModel.Distributor;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class DistributorDialog {


    public static void show(final Activity activity, final List<Distributor> arrayList, final NotifyListener listener) {
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_download);
        ButterKnife.bind(dialog);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerSync);
        ((LinearLayout) dialog.findViewById(R.id.llHeader)).setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        DistributorDialogAdapter adapter = new DistributorDialogAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        (dialog.findViewById(R.id.ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        adapter.notifyDataSetChanged();
        dialog.show();

        adapter.SetOnItemClickListener(new DistributorDialogAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                dialog.dismiss();
                PreferencesManger.addStringFields(activity, Constants.Pref.DEPO_CODE, arrayList.get(position).getDepotCode());
                PreferencesManger.addStringFields(activity, Constants.Pref.DEPO_NAME, arrayList.get(position).getName());
                listener.onCompleted(arrayList.get(position).getDepotCode());
            }

            @Override
            public void onCallClick(View view, int position) {

            }
        });
    }


}
