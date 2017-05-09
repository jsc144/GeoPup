package com.example.groupproject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.Model.Constants;
import com.example.groupproject.Model.Zone;
import com.example.groupproject.service.impl.ZoneService;

import java.util.List;
import java.util.Random;

import static com.example.groupproject.Model.Constants.MAX_HEALTH;
import static com.example.groupproject.Model.Constants.MAX_JOY;
import static com.example.groupproject.Model.Constants.MEDICINE_COST;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_ZONE = 0;
    private PendingIntent pi=null;
    private AlarmManager mgr=null;
    private ZoneService serv;
    private static long points;
    private Button food, heal, treat;
    private SharedPreferences prefs;
    private Context _context;
    private TextView points_txt, health_txt, joy_txt,hunger_txt;
    private long health, hunger, joy;
    private Random random = new Random();
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = this;

        serv = ServiceManager.getZoneService(getApplicationContext());

        List<Zone> zones = serv.getZones();

        if(zones.size()==0){
            setContentView(R.layout.activity_empty_main);
        }else{
            setContentView(R.layout.activity_main);

            food = (Button) findViewById(R.id.food_btn);
            heal = (Button) findViewById(R.id.heal_btn);
            treat = (Button) findViewById(R.id.treat_btn);
            prefs = getSharedPreferences("GeoCat",0);

            points = prefs.getLong("points",100);
            health = prefs.getLong("health",Constants.MAX_HEALTH);
            hunger = prefs.getLong("hunger",Constants.MAX_HUNGER);
            joy = prefs.getLong("joy",Constants.MAX_JOY);

            points_txt = (TextView)findViewById(R.id.points_textview);
            health_txt = (TextView)findViewById(R.id.health_textview);
            joy_txt = (TextView)findViewById(R.id.joy_textview);
            hunger_txt = (TextView)findViewById(R.id.hunger_textview);
            imageView = (ImageView)findViewById(R.id.pet_imageview);

            points_txt.setText("Points: " + points);
            health_txt.setText("Health: " + health);
            joy_txt.setText("Joy: " + joy);
            hunger_txt.setText("Hunger: " + hunger);

            food.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long points = prefs.getLong("points",100);
                    if(points >= Constants.FOOD_COST){
                        hunger = prefs.getLong("hunger",Constants.MAX_HUNGER);
                        if(hunger < Constants.MAX_HUNGER){
                            if(hunger + Constants.FOOD_INCREMENT <= Constants.MAX_HUNGER)
                                hunger = Constants.MAX_HUNGER;
                            else
                                hunger = hunger + Constants.FOOD_INCREMENT;
                        }else{
                            Toast.makeText(_context, "Already full",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        points = points - Constants.FOOD_COST;
                        editor.putLong("hunger",hunger);
                        editor.putLong("points",points);
                        editor.commit();
                        Toast.makeText(_context, "Food bought!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(_context, "Not enough points",
                                Toast.LENGTH_LONG).show();
                    }

                    update_stats();
                }
            });

            heal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long points = prefs.getLong("points",100);
                    //long points = 180;
                    if(points >= Constants.MEDICINE_COST){
                        health = prefs.getLong("health",MAX_HEALTH);
                        if(health >= MAX_HEALTH){
                            Toast.makeText(_context, "Already at full health",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }else{
                            points = points - MEDICINE_COST;
                            health = MAX_HEALTH;
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong("points",points);
                        editor.putLong("health",health);
                        editor.commit();
                        Toast.makeText(_context, "Medicine bought!",
                                Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(_context, "Not enough points",
                                Toast.LENGTH_LONG).show();
                    }
                    update_stats();
                }
            });

            treat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long points = prefs.getLong("points",100);
                    if(points >= Constants.TREAT_COST){
                        joy = prefs.getLong("joy",Constants.MAX_JOY);

                        if(joy < Constants.MAX_JOY)
                        {
                            points = points - Constants.TREAT_COST;
                            if(joy + Constants.TREAT_INCREMENT >= Constants.MAX_JOY)
                                joy = Constants.MAX_JOY;
                            else
                                joy = joy + Constants.TREAT_INCREMENT;
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putLong("points",points);
                            editor.putLong("joy",joy);
                            editor.commit();
                            Toast.makeText(_context, "Treat bought!",
                                    Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(_context, "Already completely joy-filled",
                                    Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(_context, "Not enough points",
                                Toast.LENGTH_LONG).show();
                    }
                    update_stats();
                }
            });
            select_pic();
        }

        ActionBar ab = getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.main_activity_color)));

    }

    private void update_stats(){

        points = prefs.getLong("points",100);
        health = prefs.getLong("health",Constants.MAX_HEALTH);
        hunger = prefs.getLong("hunger",Constants.MAX_HUNGER);
        joy = prefs.getLong("joy",Constants.MAX_JOY);


        points_txt.setText("Points: " + points);
        health_txt.setText("Health: " + health);
        joy_txt.setText("Joy: " + joy);
        hunger_txt.setText("Hunger: " + hunger);
        select_pic();
    }

    private void select_pic(){
        points = prefs.getLong("points",100);
        health = prefs.getLong("health",Constants.MAX_HEALTH);
        hunger = prefs.getLong("hunger",Constants.MAX_HUNGER);
        joy = prefs.getLong("joy",Constants.MAX_JOY);

        int i = random.nextInt(101-1);
        if(i > 50){
            imageView.setImageResource(R.drawable.happy_dog);
        }else{
            imageView.setImageResource(R.drawable.happy_dog_2);
        }

        if(hunger==0){
            if(i >50){
                imageView.setImageResource(R.drawable.sick_dog_1);
            }else{
                imageView.setImageResource(R.drawable.sick_dog2);
            }
        }

        if(joy < MAX_JOY/4){
            if(i <= 33){
                imageView.setImageResource(R.drawable.mad_dog);
            }else if(i > 66){
                imageView.setImageResource(R.drawable.mad_dog2);
            }else{
                imageView.setImageResource(R.drawable.mad_dog3);
            }
        }

        if(health == 0){
            imageView.setImageResource(R.drawable.animal_services);
            showAlert("A neighborhood vet saw your starving dog and took it to safety (away from you).");
        }

        if(!prefs.getBoolean("alive",true)){
            imageView.setImageResource(R.drawable.animal_services);

            showAlert("Your dog was so bummed, it ran away!");
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("alive",true);
            editor.commit();
        }
    }

    private void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainactivity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add_zone:
                Intent x = new Intent(getApplicationContext(), AddZoneActivity.class);
                startActivityForResult(x, REQUEST_CODE_ADD_ZONE);
                return true;
            case R.id.menu_item_zone_list:
                Intent y = new Intent(getApplicationContext(), ZoneListActivity.class);
                startActivity(y);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Toast.makeText(getApplicationContext(),"New zone was added", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Zone was not added", Toast.LENGTH_SHORT).show();
        }

        List<Zone> zones = serv.getZones();

        if(zones.size()==0){
            setContentView(R.layout.activity_empty_main);
        }else{
            setContentView(R.layout.activity_main);
        }
    }*/
}
