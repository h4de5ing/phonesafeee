package mo.com.phonesafe.tools;

import java.io.InputStream;
import java.security.MessageDigest;

/**
 * 作者：MoMxMo on 2015/9/15 19:04
 * 邮箱：xxxx@qq.com
 */


public class MD5Utils {

    /**
     * 对流进行MD5加密
     * @param in
     * @return
     */
    public static String encode(InputStream in) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[8192];
            int byteCount;
            while ((byteCount = in.read(bytes)) > 0) {
                digester.update(bytes, 0, byteCount);
            }
            byte[] digest = digester.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int r = b & 0xff;//
                String hex = Integer.toHexString(r);//
                if (hex.length() == 1) {
                    hex = 0 + hex;
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
