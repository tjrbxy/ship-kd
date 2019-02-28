package com.alixlp.kd.fragment.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.PropertyID;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alixlp.kd.R;
import com.alixlp.kd.activity.order.OrderDetailActivity;
import com.alixlp.kd.bean.Order;
import com.alixlp.kd.biz.AgentBiz;
import com.alixlp.kd.biz.OrderBiz;
import com.alixlp.kd.config.Config;
import com.alixlp.kd.net.CommonCallback;
import com.alixlp.kd.util.SPUtils;
import com.alixlp.kd.util.T;

import java.util.HashMap;
import java.util.Map;

public class OrderFragment extends Fragment {

    private static final String TAG = "app-ActiveOrderFragment";
    private EditText mUserId;
    private Button mBtCheck, mBtSend;
    private AgentBiz mAgentBiz = new AgentBiz();
    private OrderBiz mOrderBiz = new OrderBiz();
    private TextView mConsigneeName;
    private TextView mAgentName;
    // 扫码声音
    private int soundid,cantFindSoundid,startSoundid, sendSuccessSoundid, inputSuccessSoundid, boxCodeRepeatSoundid,
            repeatedSweepCodeSoundid, scanOtherGoodsSoundid;
    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private String barcodeStr;
    private boolean isScaning = false;
    private EditText mScanGoods;
    private final static String SCAN_ACTION = ScanManager.ACTION_DECODE;
    private String ActivieScan;
    private String mImie;
    private String mUid;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(!getUserVisibleHint() )return;
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            mScanGoods.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra(ScanManager.DECODE_DATA_TAG);
            int barcodelen = intent.getIntExtra(ScanManager.BARCODE_LENGTH_TAG, 0);
            Log.d(TAG, "barcodelen: " + barcodelen);
            byte temp = intent.getByteExtra(ScanManager.BARCODE_TYPE_TAG, (byte) 0);
            barcodeStr = new String(barcode, 0, barcodelen);
            ActivieScan = (String) SPUtils.getInstance().get("active", "");
            Log.d(TAG, "onReceive: " + ActivieScan);
            // 根据快递单号查找订单
            Map params = new HashMap();
            params.put("kcode", barcodeStr);
            mOrderBiz.orderSearchOrderSn(params, new CommonCallback<Order>() {
                @Override
                public void onError(Exception e) {
                    soundpool.play(cantFindSoundid, 1, 1, 0, 0, 1);
                    // T.showToast(e.getMessage());
                }
                @Override
                public void onSuccess(Order response, String info) {
                    Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("OID", response.getId());
                    bundle.putInt("ORDERSTATUS", 0);
                    intent.putExtra("ORDER", bundle);
                    startActivity(intent);
                    Log.d(TAG, "onSuccess: " + info);
                    Log.d(TAG, "onSuccess: " + response.getId() );
                }
            });
            mScanGoods.setText(barcodeStr);
            return;
        }

    };

    public OrderFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // 创建视图
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        return view;
        // return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);
        mScanGoods = root.findViewById(R.id.id_et_order_sn);
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE); // 提示音
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScanManager != null) {
            mScanManager.stopDecode();
            isScaning = false;
        }
        getActivity().unregisterReceiver(mScanReceiver); // 取消
    }

    @Override
    public void onResume() {
        super.onResume();
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
        // 判断当前语言种类
        if ((Boolean) SPUtils.getInstance().get(Config.LANGUAGE, false)) {
            // 粤语
            sendSuccessSoundid = soundpool.load(getActivity(), R.raw.ctsendsuccesssoundid, 1); // 发货成功
            boxCodeRepeatSoundid = soundpool.load(getActivity(), R.raw.ctboxcoderepeatsoundid, 1); //外箱码重复
            inputSuccessSoundid = soundpool.load(getActivity(), R.raw.ctinputsuccesssoundid, 1); //录入成功
            repeatedSweepCodeSoundid = soundpool.load(getActivity(), R.raw.ctrepeatedsweepcodesoundid, 1);
            // 请勿重复扫码
            scanOtherGoodsSoundid = soundpool.load(getActivity(), R.raw.ctscanothergoodssoundid, 1); //
            // 扫入其他产品

            cantFindSoundid = soundpool.load(getActivity(), R.raw.ctcantfindsoundid, 1); //
            // 没找到订单
            startSoundid = soundpool.load(getActivity(), R.raw.ctstartsoundid, 1);
        } else {
            // 普通话
            sendSuccessSoundid = soundpool.load(getActivity(), R.raw.sendsuccesssoundid, 1); // 发货成功
            boxCodeRepeatSoundid = soundpool.load(getActivity(), R.raw.boxcoderepeatsoundid, 1); //外箱码重复
            inputSuccessSoundid = soundpool.load(getActivity(), R.raw.inputsuccesssoundid, 1); //录入成功
            repeatedSweepCodeSoundid = soundpool.load(getActivity(), R.raw.repeatedsweepcodesoundid, 1);
            // 请勿重复扫码
            scanOtherGoodsSoundid = soundpool.load(getActivity(), R.raw.scanothergoodssoundid, 1); //
            // 扫入其他产品
            cantFindSoundid = soundpool.load(getActivity(), R.raw.cantfindsoundid, 1); //
            // 没找到订单
            startSoundid = soundpool.load(getActivity(), R.raw.startsoundid, 1);
        }

        // 提示开始扫码
        soundpool.play(cantFindSoundid, 1, 1, 0, 0, 1);

        IntentFilter filter = new IntentFilter();
        int[] idbuf = new int[]{PropertyID.WEDGE_INTENT_ACTION_NAME, PropertyID
                .WEDGE_INTENT_DATA_STRING_TAG};
        String[] value_buf = mScanManager.getParameterString(idbuf);
        if (value_buf != null && value_buf[0] != null && !value_buf[0].equals("")) {
            filter.addAction(value_buf[0]);
        } else {
            filter.addAction(SCAN_ACTION);
        }
        getActivity().registerReceiver(mScanReceiver, filter); // 注册
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
