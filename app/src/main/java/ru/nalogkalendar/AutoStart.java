package ru.nalogkalendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by denis on 08.12.2017.
 */

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.startService(new Intent(context, NotifyService.class));//Запускаем сервис после перезапуска телефона
    }
}
