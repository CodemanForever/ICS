package com.example.vakery.ics;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;
import com.example.vakery.ics.DB.DatabaseHandler;
import com.example.vakery.ics.PagerAdapters.SchedulePagerAdapter;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Badgeable;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import Entities.Lecturer;
import Entities.Mark;


public class MainActivity extends AppCompatActivity {
    private Drawer.Result drawerResult = null;
    final String myLog = "myLog";
    DatabaseHandler db;
    FragmentTransaction fragmentTransaction;
    ViewPager pager;
    private SchedulePagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //задаем контекст прилижения в переменную, чтоб можно было с ним работать из любого класса
        Vars.setContext(getApplicationContext());

        //проверка на наличие информации о пользователе, если ее нет, выводим диалоговое окно для ее добавления
        if(! LocalSettingsFile.isUserInfo()){
            Login myDialogFragment = new Login();
            FragmentManager manager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
            myDialogFragment.show(transaction, "dialog");
        }else {}

        // Инициализируем Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHandler(this);

        // Инициализируем Navigation Drawer
        drawerResult = new Drawer()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_notifications).withIcon(FontAwesome.Icon.faw_bell).withBadge("1").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_schedule).withIcon(FontAwesome.Icon.faw_tasks),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_marks).withIcon(FontAwesome.Icon.faw_file),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_visiting).withIcon(FontAwesome.Icon.faw_eye),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_lectors).withIcon(FontAwesome.Icon.faw_male),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_subjects).withIcon(FontAwesome.Icon.faw_university),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_programs).withIcon(FontAwesome.Icon.faw_keyboard_o),
                        new PrimaryDrawerItem(),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_exit).withIcon(FontAwesome.Icon.faw_sign_out),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_info).withIcon(FontAwesome.Icon.faw_question))
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Скрываем клавиатуру при открытии Navigation Drawer
                        InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    // Обработка клика
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof Nameable) {
                            if(getApplicationContext().getString(((Nameable) drawerItem).getNameRes()).equals(getString(R.string.drawer_item_lectors))){
                                Intent intent = new Intent(getApplicationContext(), LecturersActivity.class);
                                startActivity(intent);
                            }
                            if(getApplicationContext().getString(((Nameable) drawerItem).getNameRes()).equals(getString(R.string.drawer_item_schedule))){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                            if(getApplicationContext().getString(((Nameable) drawerItem).getNameRes()).equals(getString(R.string.drawer_item_exit))){
                                prepareForExit();
                            }
                            if(getApplicationContext().getString(((Nameable) drawerItem).getNameRes()).equals(getString(R.string.drawer_item_subjects))){
                                Intent intent = new Intent(getApplicationContext(), SubjectsActivity.class);
                                startActivity(intent);
                            }
                            if(getApplicationContext().getString(((Nameable) drawerItem).getNameRes()).equals(getString(R.string.drawer_item_marks))){
                                Intent intent = new Intent(getApplicationContext(), MarksActivity.class);
                                startActivity(intent);
                            }

 Toast.makeText(getApplicationContext(), getApplicationContext().getString(((Nameable) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        if (drawerItem instanceof Badgeable) {
                            Badgeable badgeable = (Badgeable) drawerItem;
                            if (badgeable.getBadge() != null) {
                                // учтите, не делайте так, если ваш бейдж содержит символ "+"
                                try {
                                    int badge = Integer.valueOf(badgeable.getBadge());
                                    if (badge > 0) {
                                        drawerResult.updateBadge(String.valueOf(badge - 1), position);
                                    }
                                } catch (Exception e) {
                                    Log.d(myLog, "Не нажимайте на бейдж, содержащий плюс! :)");
                                }
                            }
                        }
                    }
                })
                .withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {
                    @Override
                    // Обработка длинного клика, например, только для SecondaryDrawerItem
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        if (drawerItem instanceof SecondaryDrawerItem) {
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(((SecondaryDrawerItem) drawerItem).getNameRes()), Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }
                })
                .build();

        //настройка пейджера
        fragmentTransaction = getFragmentManager().beginTransaction();
        pager=(ViewPager)findViewById(R.id.pager);

        adapter = new SchedulePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        //устанавливаем анимацию перелистывания пейджера(доп библиотека)
        pager.setPageTransformer(true,new DepthPageTransformer());
        // определяем текущий день недели, чтоб поставить нужную стр
        Calendar calendar = Calendar.getInstance();
        // берем текущий день -1, так как календарь считает с воскресенья, а нам надо с понедельника
        int currentDayForPage = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //если наш день =0, то такого дна нет (вс = 1 - 1 = 0 ) то пишем что вс = 7 день
        if(currentDayForPage < 1){currentDayForPage = 7;}
        // задаем какую стр по порядку показывать. отнимаем -1 потому, что список дней начинается с 0
        pager.setCurrentItem(currentDayForPage - 1);

        //так как расписание пар по времени не меняется часто, то заполняем его только при включении приложения для экономии действий
        Vars.fillTimeList(this);

        checkForInformation();
    }


    public void prepareForExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.drawer_item_exit)
                .setMessage(R.string.asking_info_for_exit)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        LocalSettingsFile.clearUserInfo();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        AlertDialog alert = builder.create();
        alert.show();
    }


    public void checkForInformation(){
        final ArrayList<Lecturer> listOfLecturers = new ArrayList<Lecturer>();
        ArrayList<Integer> lecturersId = db.getLecturersId();

        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.d(myLog, "SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }

        //проверяем существует ли папка для хранения фото, берем ссылку на папку (File) из Vars
        if (! Vars.getImageFileDir().exists()){
            Log.d(myLog, "директории нет, создаем ее");
            // создаем каталог
            Vars.getImageFileDir().mkdirs();
        }else {}

        for (int i = 0; i < lecturersId.size(); i++) {
            //формируем имя файла для проверки его наличия
            String name = "lecturer_" + lecturersId.get(i).toString() + ".png";
            try {
                //проверяем наличие фотографии в папке
                if (!new File(Vars.getImageFileDir() + File.separator + name).exists()) {
                    //создаем экземпляр преподавателя с id и Photo_url
                    listOfLecturers.add(new Lecturer(db.getLecturer(lecturersId.get(i)).getmId(),db.getLecturer(lecturersId.get(i)).getmPhoto()));
                } else {
                }
            }catch (Exception e) {
                Log.d(myLog, "Ошибка проверки наличия фото");
            }
        }
        //если лист с преподавателями, которых надо скачать не пустой
        if(listOfLecturers.size() > 0) {
            //проверка на наличие интернет соединения
            if (checkInternetConnection(getApplicationContext())) {

                //создаем фоновый поток, чтоб основной не подвисал
                //Thread t = new Thread(new Runnable() {
                //    public void run() {
                DownloadInfo downloadInfo = new DownloadInfo(getApplicationContext());
                downloadInfo.loadImg(listOfLecturers);
                //     }
//            });
//            //запуск потока
//            t.start();
            } else {
                Log.d(myLog, "отсутствует интернет соединение");
                Toast.makeText(getApplicationContext(), R.string.no_internet_connection_info, Toast.LENGTH_LONG).show();
            }



        }
    }


    // метод для проверки подключения
    public static boolean checkInternetConnection(final Context context)
    {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (drawerResult.isDrawerOpen()) {
            drawerResult.closeDrawer();
        } else {
           // super.onBackPressed();
        }
    }


    //переопреднеленный метод(стандартный не работает), который обновляет данные в страницах пейджера
    public void notifyAdapter(int position) {
        adapter.notifyDataSetChanged(position);
    }

//    // Заглушка, работа с меню
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    // Заглушка, работа с меню
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}






