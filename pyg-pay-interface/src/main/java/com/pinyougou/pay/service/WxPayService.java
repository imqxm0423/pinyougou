package com.pinyougou.pay.service;

import java.util.Map;

public interface WxPayService {

    Map createNative(String out_trade_no,String total_fee);

}
