package mo.com.phonesafe.tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 压缩和和解压的工具类
 *
 * 作者：MoMxMo on 2015/9/5 00:00
 * 邮箱：xxxx@qq.com
 *
 */


public class GZIPUtils {

    /**
     * 压缩
     * @param is    输入流
     * @param os    数据流
     */
    public static void zip(InputStream is, OutputStream os) {
        GZIPOutputStream gzip = null;
        try {
            gzip = new GZIPOutputStream(os);
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = is.read(buf)) != -1) {
                gzip.write(buf, 0, len);
                gzip.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeIO(is);
            closeIO(gzip);
        }
    }

    /**
     * 解压
     * @param is    输入流
     * @param os      输出流
     */
    public static void unzip(InputStream is, OutputStream os) {
        GZIPInputStream gzip = null;
        try {
            gzip = new GZIPInputStream(is);
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = gzip.read(buf)) != -1) {
                os.write(buf,0,len);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeIO(gzip);
            closeIO(os);
        }
    }

    /**
     * 关闭资源
     * @param stream
     */
    public static void closeIO(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stream = null;
        }
    }
}
