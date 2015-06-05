package com.example.administrador.myapplication.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.administrador.myapplication.R;
import com.example.administrador.myapplication.models.entities.ServiceOrder;
import com.example.administrador.myapplication.util.AppUtil;
import com.melnykov.fab.FloatingActionButton;

import org.apache.http.protocol.HTTP;

import java.util.List;

public class ServiceOrderListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    public static final int REQUEST_CODE_ADD = 1;
    public static final int REQUEST_CODE_EDIT = 2;

    private RecyclerView mServiceOrders;
    private ServiceOrderListAdapter mServiceOrdersAdapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_order_list_material);

        this.bindElements();
    }

    private void bindElements() {
        mServiceOrders = AppUtil.get(findViewById(R.id.recyclerViewServiceOrders));
        mServiceOrders.setHasFixedSize(true);
        mServiceOrders.setLayoutManager(new LinearLayoutManager(this));

        final FloatingActionButton fabAdd = AppUtil.get(findViewById(R.id.fabAdd));
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent goToAddActivity = new Intent(ServiceOrderListActivity.this, ServiceOrderActivity.class);
                startActivityForResult(goToAddActivity, REQUEST_CODE_ADD);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.updateRecyclerItens(true);
    }

    private void updateRecyclerItens(boolean activeFlag) {
        final List<ServiceOrder> serviceOrders = ServiceOrder.getAllByActiveFlag(activeFlag);
        if (mServiceOrdersAdapter == null) {
            mServiceOrdersAdapter = new ServiceOrderListAdapter(serviceOrders);
            mServiceOrders.setAdapter(mServiceOrdersAdapter);
        } else {
            mServiceOrdersAdapter.setItens(serviceOrders);
            mServiceOrdersAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_ADD) {
                Toast.makeText(this, R.string.msg_add_success, Toast.LENGTH_LONG).show();
                // Force onPrepareOptionsMenu call
                supportInvalidateOptionsMenu();
            } else if (requestCode == REQUEST_CODE_EDIT) {
                Toast.makeText(this, R.string.msg_edit_success, Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        final ServiceOrder serviceOrder = mServiceOrdersAdapter.getSelectedItem();
        switch (item.getItemId()) {
            case R.id.actionEdit:
                final Intent goToEditActivity = new Intent(ServiceOrderListActivity.this, ServiceOrderActivity.class);
                goToEditActivity.putExtra(ServiceOrderActivity.EXTRA_SERVICE_ORDER, serviceOrder);
                goToEditActivity.putExtra(ServiceOrderActivity.EXTRA_START_BENCHMARK, SystemClock.elapsedRealtime());
                super.startActivityForResult(goToEditActivity, REQUEST_CODE_EDIT);
                return true;
            case R.id.actionDelete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.lbl_confirm)
                        .setMessage((serviceOrder.isActive()) ? getString(R.string.msg_archived) : getString(R.string.msg_unArchiving))
                        .setPositiveButton(R.string.lbl_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Set attribute active
                                if (serviceOrder.isActive()) serviceOrder.setActive(false);
                                else serviceOrder.setActive(true);
                                // Update and show a message
                                serviceOrder.save();
                                Toast.makeText(ServiceOrderListActivity.this,
                                        (serviceOrder.isActive()) ? getString(R.string.msg_unArchiving_success) : getString(R.string.msg_archived_success),
                                        Toast.LENGTH_LONG).show();
                                // Update recycler view dataset
                                updateRecyclerItens(!serviceOrder.isActive());
                                // Force onPrepareOptionsMenu call
                                supportInvalidateOptionsMenu();
                            }
                        })
                        .setNeutralButton(R.string.lbl_no, null)
                        .create().show();
                return true;
            case R.id.actionCall:
                // Best Practices: http://stackoverflow.com/questions/4275678/how-to-make-phone-call-using-intent-in-android
                final Intent goToSOPhoneCall = new Intent(Intent.ACTION_CALL /* or Intent.ACTION_DIAL (no manifest permission needed) */);
                goToSOPhoneCall.setData(Uri.parse("tel:" + serviceOrder.getPhone()));
                startActivity(goToSOPhoneCall);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_service_order_list_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * @see <a href="http://developer.android.com/guide/components/intents-filters.html">Forcing an app chooser</a>
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionShare:
                // Create the text message with a string
                final Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, ServiceOrder.getAll().toString());
                sendIntent.setType(HTTP.PLAIN_TEXT_TYPE);

                // Create intent to show the chooser dialog
                final Intent chooser = Intent.createChooser(sendIntent, getString(R.string.lbl_share_option));

                // Verify the original intent will resolve to at least one activity
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(chooser);
                }
                return true;
            case R.id.actionOpen:
                this.optionSelected(AppUtil.ARCHIVED, item);
                return true;
            case R.id.actionArchived:
                this.optionSelected(AppUtil.OS_UNARCHIVING, item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenu = menu;
        this.activedShared();

        Integer itemActive = AppUtil.getMenuActive();
        switch (itemActive) {
            case AppUtil.ARCHIVED:
                menu.findItem(R.id.actionOpen).setChecked(true);
                break;
            case AppUtil.OS_UNARCHIVING:
                menu.findItem(R.id.actionArchived).setChecked(true);
                break;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    private void optionSelected(Integer option, MenuItem item) {
        switch (option) {
            case AppUtil.ARCHIVED:
                if (item.isChecked()) item.setChecked(true);
                else item.setChecked(false);
                AppUtil.changeMenuFilterOption(option);
                this.updateRecyclerItens(true);
                this.activedShared();
                break;
            case AppUtil.OS_UNARCHIVING:
                if (item.isChecked()) item.setChecked(true);
                else item.setChecked(false);
                AppUtil.changeMenuFilterOption(option);
                this.updateRecyclerItens(false);
                this.activedShared();
                break;
        }
    }

    private void activedShared() {
        final MenuItem menuShare = mMenu.findItem(R.id.actionShare);
        final boolean menuShareVisible = mServiceOrdersAdapter.getItemCount() > 0;
        menuShare.setEnabled(menuShareVisible).setVisible(menuShareVisible);
    }
}
