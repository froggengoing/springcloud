package com.froggengo.alipay;

public class AlipayConfig {
    //发起请求的应用ID。沙箱与线上不同，请更换代码中配置;
    public static String app_id ="2021000117625571";
    //支付宝私匙
    public static String merchant_private_key = "";
    //支付宝公匙
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp7JyIy+VCMJLEw4JmjqItYiXvUu/0iJsZ+5lP6WCxreTZCRdqSHFgqzfMSb6y8K11ghQo0IR6vCAsfwhkpMk97Bfb0RC1HKQAPbD9yzr6Dykf5mfWKGTg82LablV3foZIfCF+vomlw8yNzOZ528ulNbp2Zlgl7aPhgGTeD7CpXaiR9gDv1L1R/LnOhnv31yUrcY2pOFbPm1wBeMTbeXiFLIoBoOMCvpvTk4dSZ/6yh3ZrZsH2Y3kYt9/9B0gh6lQsVPskFvF+FU9kkCrkHIoo2ucJ44GhjZ5nefMW+3dKyxXp42q9RdonqdHgw6GVVgWqLKtETLkb3oQnsEkqusaWwIDAQAB";
    //应用私钥
    public static String app_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCRketBCt5mHmH4qvAuryEPoscbray+USQ4n9U3u30bgYGtZf5vHVNh1Nd8iMwhQuvoTZIsohDwSU5yusKypigv70Q3Y8NBKlc4Lqw2aHLc/0IGzhtbzHGQ1lz79gP22jUlnupxVrRiwdcYMzjlRi9E18uXov/m+YNP2ER81RY6wrbfvs28DrXg8xnqnQ5n3Mk695+uwcp1DeYQ/EBCjst8CytZka6OxRTHVPXQVZMjKXYBEou3vrEawJOnNjQwvFngzhjL4/hbsULuq93M+rY/Dk9M0OG2q9jqrQSKaKWOEfF6rGvqtp1PcfcUZNCQ8IjfLBCilxYXqIKCRkZYh4hXAgMBAAECggEAGKIKnz0JmVo21e+HKT81ZwHyk3p2KtZsPLC/UekGKKtnmEIUJhVeUMXFmStE77P1W6AUxjy+hQrCnaOA4t4/Gq1wa7cUCWjyzW0Ic0SFe2QudhxWgNg9t3au0Ww2C4WtTKpxnbBAYh3PGidAz3tq8ElcCFLq3tkGIXLFVCJy2opqrnTYPDTsMaTJDLr4qRQ1mH+fTxe2DPomJimAsiOx4d6QG1doUeieyiP+pdg6Zv1niOUyYWRT8blESC6C+AyLkVU4M1nzTNymNiyjwNlTrd4QFnGyChD8vswNhR1etyw2T84zhPUNZwRbvw1v3c/cBMPt5gs/GIllw5GyCazwkQKBgQDbRBAbp8WDX3Jxx00Q5CJPkj+k8/1ZB8Y8+7D7FbfImfubOg/ezVkxHATDU7F/YVZNBFLtoEdVdpD8Z4YR7YEnaVj0Vx7mrQEPQfcvKzEvVtNlp7O+TTiP30A4Dkj+Y1WWM8yDm0xxdr5H4i8TsNjvG+gSkR3wtOOGk+fi7sf/ewKBgQCp9SqYBDFdkTkggiP12XzrB9eb1qENXZQKxFLqDF/9iaOraRA/LI4VX8+aF0hWEnAqFqG3s38s9u/JciGKlra0Yqn4E2YKdiJNvYmQLE/FQI4P0GDDS9VH2Wgst7Sj4GJjN6CgifE0G+GmWHSkp5bXwGeGua6U+mtF9jrNVhq11QKBgQDLex+Duq92WlZntnATc9NPisZbez4qqCdVIrI8YQw8MoInrLtbpa/W/kyBRCMdtiQTtyaKdUqdyjmEs0HdU06fV4mAv3Ti17x6/1m4QVoUt3vZA8evnBQYmDMNklw1D9Q2Xv21SKKpTWwKY6ISoPTA+WHmERv67LOViZ8Rn0ylDwKBgQCEyaxRwVTgigpcP9Hgz+AtsZ7ffz2tt6NnFiTCAJEvAk8CKcAr25/XS1hkSDSYKye/epc3c+K1Oun2NlnGciKlpUCvNljPa6U5oBDOuBBLSTi1WlV/wMwOquodphpYv4OfiWLSi3QNBouy8AGSc+4C+6znMkQY2d7edpdSvyd3UQKBgAiCLBKkKloPpH1mB7ABTa+ujZXkzuX/xZrPsQvvOW9ZfrYqWFnwlylqB43UbRooeaohxHBHDBShx5+vxDJ/X9EnYHVMqZI5tFpx3axPAlEujKOBUZ4ZpWB/yB/ZgLwAjGmJsM3qYBSzAAHFH72IlsLV35MbGUdE9mNGEwJtxNRm";
    //服务器异步通知路径
    //public static String notify_url = "http://127.0.01/alipay/alipayNotifyNotice.action";
    //服务器同步通知路径
   // public static String return_url = "http://127.0.0.1/alipay/alipayReturnNotice.action";
    //公匙类型/签名类型
    public static String sign_type = "RSA2";
    //编码格式
    public static String charset = "utf-8";
    //向支付宝发起请求的网关。沙箱与线上不同，请更换代码中配置;沙箱:https://openapi.alipaydev.com/gateway.do上线https://openapi.alipay.com/gateway.do
    //public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";
}