package com.lectek.android.lereader.net.openapi;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TokenService {
	
	/**
     * 请求参数封装类
     * @author 
     *
     */
    public static class TokenParam {

        private String name;

        private String value;

        public TokenParam(String name, String value){
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

    }
    
    /**
     * TokenParam Comparator
     */
    private final static Comparator<TokenService.TokenParam> TOKEN_COMPARATOR = new Comparator<TokenService.TokenParam>() {

        @Override
        public int compare(TokenParam o1,
                           TokenParam o2) {
            return o1.getName().compareTo(o2.getName());
        }

    };
    
    /**
     * 构建通用Token
     * @param tokenParams
     * @param clientSecret
     * @return
     */
    public static String buildToken(List<TokenParam> tokenParams, String clientSecret) {
    	//排序
    	Collections.sort(tokenParams, TOKEN_COMPARATOR);

        StringBuilder tokenBuf = new StringBuilder();

        for (TokenParam param : tokenParams) {
            tokenBuf.append(param.getName()).append("=").append(param.getValue()).append("&");
        }
        //加密前的字符串
        String myMd5 = tokenBuf.deleteCharAt(tokenBuf.length() - 1).append(clientSecret).toString();


        try {
        	//apach DigestUtils 工具类做md5加密
            //myMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(myMd5.getBytes("UTF-8"));
        	
        	myMd5 = myMD5(myMd5);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return myMd5;
    }
    
    /**
     * MD5简单实现
     * @param s
     * @return
     */
    private final static String myMD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};       

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            char[] str = encodeHex(md, hexDigits);
            
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将消息摘要转换成十六进制表示
     * @param data
     * @param toDigits
     * @return
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }


	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		List<TokenParam> paramList = new ArrayList<TokenParam>(4);
		paramList.add(new TokenParam("client_app_key","e26e816826584f3daca83f3a91151349"));
		paramList.add(new TokenParam("product_id","12785"));
		paramList.add(new TokenParam("timestamp","20131016135700033"));
		paramList.add(new TokenParam("call_back_url","http://wapread.189.cn"));
		
		System.out.println( buildToken(paramList, "secret"));
	}

}
