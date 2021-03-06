package com.example.wantm.android;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wantm.android.IQDeviceAdapter;
import com.garmin.android.connectiq.ConnectIQ;
import com.garmin.android.connectiq.ConnectIQ.ConnectIQListener;
import com.garmin.android.connectiq.ConnectIQ.IQConnectType;
import com.garmin.android.connectiq.ConnectIQ.IQDeviceEventListener;
import com.garmin.android.connectiq.ConnectIQ.IQSdkErrorStatus;
import com.garmin.android.connectiq.IQDevice;
import com.garmin.android.connectiq.IQDevice.IQDeviceStatus;
import com.garmin.android.connectiq.exception.InvalidStateException;
import com.garmin.android.connectiq.exception.ServiceUnavailableException;

import java.util.List;


public class AndroidComm {


    public class MainActivity extends ListActivity {

        private ConnectIQ mConnectIQ;
        private TextView mEmptyView;
        private IQDeviceAdapter mAdapter;
        private boolean mSdkReady = false;

        private ConnectIQ.IQDeviceEventListener mDeviceEventListener = new ConnectIQ.IQDeviceEventListener() {

            @Override
            public void onDeviceStatusChanged(IQDevice device, IQDevice.IQDeviceStatus status) {
                mAdapter.updateDeviceStatus(device, status);
            }

        };

        private ConnectIQ.ConnectIQListener mListener = new ConnectIQ.ConnectIQListener() {

            @Override
            public void onInitializeError(ConnectIQ.IQSdkErrorStatus errStatus) {
                if (null != mEmptyView)
                    mEmptyView.setText(R.string.initialization_error + errStatus.name());
                mSdkReady = false;
            }

            @Override
            public void onSdkReady() {
                loadDevices();
                mSdkReady = true;
            }

            @Override
            public void onSdkShutDown() {
                mSdkReady = false;
            }

        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            mAdapter = new IQDeviceAdapter(this);
            getListView().setAdapter(mAdapter);

            // Here we are specifying that we want to use a WIRELESS bluetooth connection.
            // We could have just called getInstance() which would by default create a version
            // for WIRELESS, unless we had previously gotten an instance passing TETHERED
            // as the connection type.
            mConnectIQ = ConnectIQ.getInstance(this, ConnectIQ.IQConnectType.WIRELESS);

            // Initialize the SDK
            mConnectIQ.initialize(this, true, mListener);

            mEmptyView = (TextView) findViewById(android.R.id.empty);

        }

        @Override
        public void onResume() {
            super.onResume();

            if (mSdkReady) {
                loadDevices();
            }
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            // It is a good idea to unregister everything and shut things down to
            // release resources and prevent unwanted callbacks.
            try {
                mConnectIQ.unregisterAllForEvents();
                mConnectIQ.shutdown(this);
            } catch (InvalidStateException e) {
                // This is usually because the SDK was already shut down
                // so no worries.
            }
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.load_devices) {
                loadDevices();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            IQDevice device = mAdapter.getItem(position);

            Intent intent = new Intent(this, DeviceActivity.class);
            intent.putExtra(DeviceActivity.IQDEVICE, device);
            startActivity(intent);
        }

        public void loadDevices() {
            // Retrieve the list of known devices
            try {
                List<IQDevice> devices = mConnectIQ.getKnownDevices();

                if (devices != null) {
                    mAdapter.setDevices(devices);

                    // Let's register for device status updates.  By doing so we will
                    // automatically get a status update for each device so we do not
                    // need to call getStatus()
                    for (IQDevice device : devices) {
                        mConnectIQ.registerForDeviceEvents(device, mDeviceEventListener);
                    }
                }

            } catch (InvalidStateException e) {
                // This generally means you forgot to call initialize(), but since
                // we are in the callback for initialize(), this should never happen
            } catch (ServiceUnavailableException e) {
                // This will happen if for some reason your app was not able to connect
                // to the ConnectIQ service running within Garmin Connect Mobile.  This
                // could be because Garmin Connect Mobile is not installed or needs to
                // be upgraded.
                if (null != mEmptyView)
                    mEmptyView.setText(R.string.service_unavailable);
            }
        }
    }

}