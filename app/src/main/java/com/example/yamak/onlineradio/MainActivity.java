/*Şimdi gelelim ana activity de gerçekleşen kodları incelemeye;
 initializeMediaPlayer() methodunda urli  stream ediyoruz.
 Eğer hatalı bir radyo url i stream etmeye çalıştığımızda try catch bloğunda hatayı yakaladığını göreceksiniz.
 btnCal butonuna bastığımızda Media Player hazır hale gelir gelmez ilgili radyo yayını çalmaya başlar.
 btnDur butonuna bastığımızda ise stopRadioPlayer() methodunda eğer Media Player çalışıyorsa onu durdurup
 tekrardan stream olayını gerçekleştiriyoruz. FLAG_KEEP_SCREEN_ON ile  uygulamanın uyku moduna geçmesini engelliyoruz.
 Uygulama arka plana alınıp tekrar açıldığında radyo çalmaya devam ediyorsa tekrardan çalma işlemlerinin devam etmesini sağlıyoruz.
  SeekBar yardımıyla da radyonun sesini artırma ve azaltma işlemlerini gerçekleştiriyoruz.
*/

package com.example.yamak.onlineradio;



import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//radyo fenemoen http://sc.powergroup.com.tr/RadyoFenomen/mpeg/128/tunein
public class MainActivity extends AppCompatActivity {
    String streamUrl=""; //bigfm
    private Button btnCal,btnDur;
  //  private ImageButton image_button;
    private MediaPlayer mediaPlayer;
    public  static boolean isAlreadyPlaying=false;
    private AudioManager audioManager;
    private SeekBar sesKontrol;
    Spinner spn;
    String [] x={"BİGFM","RADYO FENOMEN","RADYO ENERJI","RADYO FENOMEN TÜRK","FENOMEN AKUSTİK","AKTİF RADYO","POWER FM"
    ,"POWER AKUSTİK"};



    @Override
    protected void onResume(){
        super.onResume();
        if (isAlreadyPlaying)
            playRadioPlayer();
        else
            stopRadioPlayer();
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        audioManager=(AudioManager)getSystemService(getApplicationContext().AUDIO_SERVICE);
        sesKontrol=(SeekBar)findViewById(R.id.sesKontrol);
        sesKontrol.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        sesKontrol.setKeyProgressIncrement(10);
        sesKontrol.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnCal=(Button)findViewById(R.id.btnCal);
        btnDur=(Button)findViewById(R.id.btnDur);

        btnDur.setEnabled(false);


         // image_button=(ImageButton)findViewById(R.id.imageButton);
        /* image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAlreadyPlaying){
                    stopRadioPlayer();
                    image_button.setImageResource(R.drawable.play);

                }
                else{
                    playRadioPlayer();
                    image_button.setImageResource(R.drawable.pause);
                }
                isAlreadyPlaying=!isAlreadyPlaying;

            }
        }); */



        btnCal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAlreadyPlaying=true;
                playRadioPlayer();
            }
        });

        btnDur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAlreadyPlaying=false;
                stopRadioPlayer();
            }
        });
        doldur();
        initializeMediaPlayer();


        sesKontrol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int deger;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                deger=progress;
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);
                Toast.makeText(MainActivity.this,"VOLUME:"+String.valueOf(deger),Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    private void stopRadioPlayer(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            initializeMediaPlayer();

        }
        btnCal.setEnabled(true);
        btnDur.setEnabled(false);

    }
    private  void playRadioPlayer(){
        btnDur.setEnabled(true);
        btnCal.setEnabled(false);
        initializeMediaPlayer();

        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }
    private void initializeMediaPlayer(){
        mediaPlayer=new MediaPlayer();
        try {
            mediaPlayer.setDataSource(streamUrl);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
      catch (IllegalStateException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause(){
        super.onPause();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    public void doldur(){
        spn=(Spinner)findViewById(R.id.spn);
        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,R.layout.spinner_liste,x);
        spn.setAdapter(adp);




        spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String secilen=((TextView)view).getText().toString();
                Map<String,String> hmap=new HashMap<String, String>();
                hmap.put("BİGFM","http://streams.bigfm.de/turkey-128-mp3");
                hmap.put("RADYO FENOMEN","http://sc.powergroup.com.tr/RadyoFenomen/mpeg/128/tunein");
                hmap.put("RADYO ENERJI","http://webstream.mediaworks.com.tr/stream/2/");
                hmap.put("RADYO FENOMEN TÜRK","http://185.28.3.4/power/FenomenTurk_mpeg_128_tunein/icecast.audio?");
                hmap.put("FENOMEN AKUSTİK","http://195.142.3.50/fenomen/FenomenAkustik_mpeg_128_tunein/icecast.audio?");
                hmap.put("AKTİF RADYO","http://radyo.yayin.com.tr:8201/stream");
                hmap.put("POWER FM","http://195.142.3.50/power/PowerFM_mpeg_128_home/icecast.audio?");
                hmap.put("POWER AKUSTİK","http://195.142.3.50/power/PowerTurkAkustik_mpeg_128_home/icecast.audio?");
                Toast.makeText(MainActivity.this,secilen,Toast.LENGTH_SHORT).show();
                streamUrl=hmap.get(secilen);
                stopRadioPlayer();
                playRadioPlayer();






               // if (secilen.equals("RADYO FENOMEN"))
                 //streamUrl="http://sc.powergroup.com.tr/RadyoFenomen/mpeg/128/tunein";
                //if (secilen.equals("BİGFM"))
                  //  streamUrl="http://streams.bigfm.de/turkey-128-mp3";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
