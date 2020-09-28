package com.froggengo.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayOpenOperationOpenbizmockBizQueryModel;
import com.alipay.api.request.AlipayOpenOperationOpenbizmockBizQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayOpenOperationOpenbizmockBizQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;

public class Main {
    /**
     * 加签模式为公钥模式时
     * AppId、应用的私钥、应用的公钥、支付宝公钥
     */
    public static void main(String[] args) {
        try {
            //AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
             //       APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
            // 1. 创建AlipayClient实例
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipaydev.com/gateway.do",
                    AlipayConfig.app_id, AlipayConfig.app_private_key, "json", AlipayConfig.charset, AlipayConfig.alipay_public_key, "RSA2");
            //AlipayClient alipayClient = new DefaultAlipayClient(getClientParams());
            // 2. 创建使用的Open API对应的Request请求对象
            //AlipayOpenOperationOpenbizmockBizQueryRequest request = getRequest();
            AlipayTradeWapPayRequest request = getTradePagePayRequest();
            // 3. 发起请求并处理响应
           // AlipayOpenOperationOpenbizmockBizQueryResponse response = alipayClient.certificateExecute(request);
            //AlipayOpenOperationOpenbizmockBizQueryResponse response = alipayClient.execute(request);
            AlipayTradeWapPayResponse response = alipayClient.pageExecute(request);
            if (response.isSuccess()) {
                System.out.println("调用成功。");
                System.out.println(response.getTradeNo());
                System.out.println(response.getMerchantOrderNo());
                System.out.println(response.getBody());
/*                response.getParams().forEach((k,v)->{
                    System.out.println(k+"=="+v);
                });*/
            } else {
                System.out.println("调用失败，原因：" + response.getMsg() + "，" + response.getSubMsg());
            }
        } catch (Exception e) {
            System.out.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static CertAlipayRequest getClientParamsNoCert() {

        return null;

    }

    /**
     * 加签模式为公钥证书模式时（推荐）
     * AppID、应用的私钥、应用的公钥证书文件、支付宝公钥证书文件、支付宝根证书文件
     * @return
     */
    private static CertAlipayRequest getClientParams() {
        CertAlipayRequest certParams = new CertAlipayRequest();

        String privateKey="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCRketBCt5mHmH4qvAuryEPoscbray+USQ4n9U3u30bgYGtZf5vHVNh1Nd8iMwhQuvoTZIsohDwSU5yusKypigv70Q3Y8NBKlc4Lqw2aHLc/0IGzhtbzHGQ1lz79gP22jUlnupxVrRiwdcYMzjlRi9E18uXov/m+YNP2ER81RY6wrbfvs28DrXg8xnqnQ5n3Mk695+uwcp1DeYQ/EBCjst8CytZka6OxRTHVPXQVZMjKXYBEou3vrEawJOnNjQwvFngzhjL4/hbsULuq93M+rY/Dk9M0OG2q9jqrQSKaKWOEfF6rGvqtp1PcfcUZNCQ8IjfLBCilxYXqIKCRkZYh4hXAgMBAAECggEAGKIKnz0JmVo21e+HKT81ZwHyk3p2KtZsPLC/UekGKKtnmEIUJhVeUMXFmStE77P1W6AUxjy+hQrCnaOA4t4/Gq1wa7cUCWjyzW0Ic0SFe2QudhxWgNg9t3au0Ww2C4WtTKpxnbBAYh3PGidAz3tq8ElcCFLq3tkGIXLFVCJy2opqrnTYPDTsMaTJDLr4qRQ1mH+fTxe2DPomJimAsiOx4d6QG1doUeieyiP+pdg6Zv1niOUyYWRT8blESC6C+AyLkVU4M1nzTNymNiyjwNlTrd4QFnGyChD8vswNhR1etyw2T84zhPUNZwRbvw1v3c/cBMPt5gs/GIllw5GyCazwkQKBgQDbRBAbp8WDX3Jxx00Q5CJPkj+k8/1ZB8Y8+7D7FbfImfubOg/ezVkxHATDU7F/YVZNBFLtoEdVdpD8Z4YR7YEnaVj0Vx7mrQEPQfcvKzEvVtNlp7O+TTiP30A4Dkj+Y1WWM8yDm0xxdr5H4i8TsNjvG+gSkR3wtOOGk+fi7sf/ewKBgQCp9SqYBDFdkTkggiP12XzrB9eb1qENXZQKxFLqDF/9iaOraRA/LI4VX8+aF0hWEnAqFqG3s38s9u/JciGKlra0Yqn4E2YKdiJNvYmQLE/FQI4P0GDDS9VH2Wgst7Sj4GJjN6CgifE0G+GmWHSkp5bXwGeGua6U+mtF9jrNVhq11QKBgQDLex+Duq92WlZntnATc9NPisZbez4qqCdVIrI8YQw8MoInrLtbpa/W/kyBRCMdtiQTtyaKdUqdyjmEs0HdU06fV4mAv3Ti17x6/1m4QVoUt3vZA8evnBQYmDMNklw1D9Q2Xv21SKKpTWwKY6ISoPTA+WHmERv67LOViZ8Rn0ylDwKBgQCEyaxRwVTgigpcP9Hgz+AtsZ7ffz2tt6NnFiTCAJEvAk8CKcAr25/XS1hkSDSYKye/epc3c+K1Oun2NlnGciKlpUCvNljPa6U5oBDOuBBLSTi1WlV/wMwOquodphpYv4OfiWLSi3QNBouy8AGSc+4C+6znMkQY2d7edpdSvyd3UQKBgAiCLBKkKloPpH1mB7ABTa+ujZXkzuX/xZrPsQvvOW9ZfrYqWFnwlylqB43UbRooeaohxHBHDBShx5+vxDJ/X9EnYHVMqZI5tFpx3axPAlEujKOBUZ4ZpWB/yB/ZgLwAjGmJsM3qYBSzAAHFH72IlsLV35MbGUdE9mNGEwJtxNRm";
        certParams.setServerUrl("https://openapi.alipaydev.com/gateway.do");
        //请更换为您的AppId
        certParams.setAppId("2021000117625571");
        //请更换为您的PKCS8格式的应用私钥
        certParams.setPrivateKey(privateKey);

        //请更换为您使用的字符集编码，推荐采用utf-8
        certParams.setCharset("utf-8");
        certParams.setFormat("json");
        certParams.setSignType("RSA2");
        //请更换为您的应用公钥证书文件路径
        certParams.setCertPath("/home/foo/appCertPublicKey_2019091767145019.crt");
        //请更换您的支付宝公钥证书文件路径
        certParams.setAlipayPublicCertPath("/home/foo/alipayCertPublicKey_RSA2.crt");
        //更换为支付宝根证书文件路径
        certParams.setRootCertPath("/home/foo/alipayRootCert.crt");
        return certParams;
    }

    private static AlipayOpenOperationOpenbizmockBizQueryRequest getRequest() {
        // 初始化Request，并填充Model属性。实际调用时请替换为您想要使用的API对应的Request对象。
        AlipayOpenOperationOpenbizmockBizQueryRequest request = new AlipayOpenOperationOpenbizmockBizQueryRequest();
        AlipayOpenOperationOpenbizmockBizQueryModel model = new AlipayOpenOperationOpenbizmockBizQueryModel();
        model.setBizNo("test");
        request.setBizModel(model);
        return request;
    }
    private static AlipayTradeWapPayRequest getTradePagePayRequest(){
//        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent("{\"out_trade_no\":\""+ "100001" +"\","
                + "\"total_amount\":\""+ 99.99 +"\","//付款金额，必填
                + "\"subject\":\""+ "欧文7代支付订单" +"\","//订单名，必填
                + "\"body\":\""+ "球鞋" +"\","//商品描述，可空
                // 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天
                // （1c-当天的情况下，无论交易何时创建，都在0点关闭）。
                // 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
                + "\"timeout_express\":\""+ "1c" +"\","
               // + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
                + "\"product_code\":\"QUICK_WAP_PAY\"}");
        return request;
    }
}