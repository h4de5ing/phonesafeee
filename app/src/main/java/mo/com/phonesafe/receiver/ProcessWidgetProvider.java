package mo.com.phonesafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import mo.com.phonesafe.service.ProcessService;

/**
 * 作者：MoMxMo on 2015/9/11 13:43
 * 邮箱：xxxx@qq.com
 *
 * 桌面小工具Widget，这里我们使用服务的方式实时更新数据
 */


public class ProcessWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        //开启服务实时更新显示widget的信息
        Intent intent = new Intent(context, ProcessService.class);
        context.startService(intent);
    }

}
