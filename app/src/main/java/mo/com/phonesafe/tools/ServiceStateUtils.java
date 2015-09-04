package mo.com.phonesafe.tools;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;

import java.util.List;

/**
 * 作者：MoMxMo on 2015/9/4 14:49
 * 邮箱：xxxx@qq.com
 */


public class ServiceStateUtils {

    /**
     * 获取服务的状态
     */
    public static boolean getServiceState(Context context, Class<? extends Service> clazz) {
        //获取服务管理对象
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取服务列表
        List<ActivityManager.RunningServiceInfo> listServer = am.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo service : listServer ) {
            String className = service.service.getClassName();

            if (className.equals(clazz.getName())) {
                return true;
            }

        }
        return false;
    }
}
