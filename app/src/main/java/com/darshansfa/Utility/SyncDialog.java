package com.darshansfa.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import butterknife.ButterKnife;
import com.darshansfa.API.APIUtil;
import com.darshansfa.Adapters.SyncAdapter;
import com.darshansfa.Models.APIProgress;
import com.darshansfa.R;

/**
 * Created by Nikhil on 09-05-2017.
 */

public class SyncDialog {
    private NotifyListener listener;
    private ArrayList<APIProgress> progressArrayList;
    private Activity activity;
    private Dialog dialog;
    private RecyclerView recyclerView;
    private SyncAdapter syncAdapter;
    private int currentPosition;
    private CountDownTimer countDownTimer;
    private boolean isDone;

    public SyncDialog(Activity activity) {
        this.activity = activity;
    }


    public void show(ArrayList<Integer> arrayList, NotifyListener listener) {
        this.listener = listener;
        progressArrayList = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            addProgressAPI(arrayList.get(i));
        }
        dialog = new Dialog(activity, android.R.style.Theme_NoTitleBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_download);
        ButterKnife.bind(dialog);
        recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerSync);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        syncAdapter = new SyncAdapter(progressArrayList);
        recyclerView.setAdapter(syncAdapter);
        syncAdapter.notifyDataSetChanged();
        dialog.show();
        currentPosition = 0;
        if (progressArrayList.size() > 0) {
            startAPICalling();
        } else {
            dialog.dismiss();
        }
    }

    private void startAPICalling() {
        if (progressArrayList.size() <= currentPosition) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    countDownTimer.onFinish();
                    listener.onCompleted("");
                    dialog.dismiss();
                }
            }, 1000);
            return;
        }

        APIProgress p = null;
        try {
            p = progressArrayList.get(currentPosition);
        } catch (Exception e) {
            dialog.dismiss();
            e.printStackTrace();
        }
        isDone = false;
        final APIProgress progress = p;

        new APIUtil(progressArrayList.get(currentPosition).getAPI(), activity, new NotifyListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String s) {
                isDone = true;
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                    countDownTimer.onFinish();
                }
            }

            @Override
            public void onError(String error) {
                isDone = true;
                try {
                    progress.setProgress(100);
                    progress.setStatus(Constants.ERROR);
                    progressArrayList.set(currentPosition, progress);
                    syncAdapter.notifyDataSetChanged();
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                        countDownTimer.onFinish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                double a = 60 - (millisUntilFinished / 1000);
                double b = (a / 60.0) * 100;
                Log.e("Pro", "---A : " + a + "   B: " + b);
                Log.e("Progres: ", "seconds remaining: " + millisUntilFinished / 1000 + "    : " + ((60 - (millisUntilFinished / 1000)) / 60) * 100);
                try {
                    progress.setProgress((int) b);
                    progress.setStatus(Constants.DOWNLOADING);
                    progressArrayList.set(currentPosition, progress);
                    syncAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    countDownTimer.cancel();
                    this.cancel();
                }
            }

            public void onFinish() {
                try {
                    if (progressArrayList.size() > currentPosition && isDone) {
                        progress.setProgress((int) 100);
                        if (progress.getStatus() != Constants.ERROR)
                            progress.setStatus(Constants.DONE);
                        progressArrayList.set(currentPosition, progress);
                        syncAdapter.notifyDataSetChanged();
                        currentPosition = currentPosition + 1;
                        startAPICalling();
                    }
                } catch (Exception e) {
                    this.cancel();
                    dialog.dismiss();
                    listener.onCompleted("");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void openDownloadDialog() {

    }

    private void addProgressAPI(int i) {
        switch (i) {
            case Constants.API.PRODUCT:
                APIProgress progress = new APIProgress();
                progress.setAPI(i);
                progress.setProgress(0);
                progress.setStatus(Constants.WAITING);
                progress.setTitle("Getting parts...");
                progressArrayList.add(progress);
                break;
            case Constants.API.FOCUS_PARTS_DSR:
                APIProgress p1 = new APIProgress();
                p1.setAPI(i);
                p1.setProgress(0);
                p1.setStatus(Constants.WAITING);
                p1.setTitle("Getting Focus parts...");
                progressArrayList.add(p1);
                break;
            case Constants.API.RETAILERS:
                APIProgress p2 = new APIProgress();
                p2.setAPI(i);
                p2.setProgress(0);
                p2.setStatus(Constants.WAITING);
                p2.setTitle("Getting Customers...");
                progressArrayList.add(p2);
                break;
            case Constants.API.STOCK:
                APIProgress p3 = new APIProgress();
                p3.setAPI(i);
                p3.setProgress(0);
                p3.setStatus(Constants.WAITING);
                p3.setTitle("Updating product stock...");
                progressArrayList.add(p3);
                break;
            case Constants.API.ORDERS:
                APIProgress p4 = new APIProgress();
                p4.setAPI(i);
                p4.setProgress(0);
                p4.setStatus(Constants.WAITING);
                p4.setTitle("Getting Orders...");
                progressArrayList.add(p4);
                break;
            case Constants.API.OUTSTANDING:
                APIProgress p5 = new APIProgress();
                p5.setAPI(i);
                p5.setProgress(0);
                p5.setStatus(Constants.WAITING);
                p5.setTitle("Getting outstanding...");
                progressArrayList.add(p5);
                break;

            case Constants.API.PJP:
                APIProgress p6 = new APIProgress();
                p6.setAPI(i);
                p6.setProgress(0);
                p6.setStatus(Constants.WAITING);
                p6.setTitle("Getting PJPSchedule....");
                progressArrayList.add(p6);
                break;

            case Constants.API.COLLECTION:
                APIProgress p7 = new APIProgress();
                p7.setAPI(i);
                p7.setProgress(0);
                p7.setStatus(Constants.WAITING);
                p7.setTitle("Getting Collection....");
                progressArrayList.add(p7);
                break;
        }
    }


    public void syncDataWithoutProgress(ArrayList<Integer> arrayList, NotifyListener listener) {
        this.listener = listener;
        currentPosition = 0;
        getData(arrayList);
    }

    private void getData(final ArrayList<Integer> arrayList) {
        if (currentPosition == (arrayList.size() - 1)) {
            listener.onCompleted("");
            return;
        }
        try {
            new APIUtil(arrayList.get(currentPosition), activity, new NotifyListener() {
                @Override
                public void onStart() {
                }

                @Override
                public void onCompleted(String s) {
                    currentPosition++;
                    getData(arrayList);
                }

                @Override
                public void onError(String error) {
                    currentPosition++;
                    getData(arrayList);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onCompleted("");
            return;
        }
    }

}
