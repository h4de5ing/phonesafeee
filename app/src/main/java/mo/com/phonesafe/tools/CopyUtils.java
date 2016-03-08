package mo.com.phonesafe.tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 复制工具类
 *
 * Created by Gh0st on 2015/9/7 20:20
 */


public class CopyUtils {
    public static void copy(InputStream in, OutputStream out) {
        byte[] bt = new byte[1024];
        int len = -1;
        try {
            while ((len = in.read(bt)) != -1) {
                out.write(bt,0,len);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(in);
            close(out);

        }
    }

    public static void close(Closeable stream) {
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
