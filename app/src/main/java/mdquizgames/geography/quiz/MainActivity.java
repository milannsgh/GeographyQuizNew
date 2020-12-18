package mdquizgames.geography.quiz;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class MainActivity extends AppCompatActivity implements RewardedVideoAdListener {

    private static final String appName = "Geography_Quiz";
    private static final String appVersion = "5.0";
    private static final String TAG = "Geography_Quiz_NEW";
    public static final String PREFS_NAME = "Geography_Quiz_NEW_PrefsFile";
    private SharedPreferences sharedPreferences;
    private TextView getHintsDailyBonus;
    private enum AppActivity {MENU, PLAY, START, LINKOVI}
    private TextView textViewMenuNavigationHint;
    private TextView tvQuestion;
    private AppActivity currentActivity;
    private List<UnifiedNativeAd> loadedNativeAds = new ArrayList<>();
    TemplateView templateView, menuTemplateView, hintTemplateView, useHintTemplate;
    private LinearLayout layoutLetters1;
    private LinearLayout layoutLetters2;
    private LinearLayout layoutLetters123;
    private int screenWidth, screenHeight, textViewYdimension, textViewXdimension, playerHints, playerPoints;
    private TextView textViewA11, textViewA12, textViewA13, textViewA14, textViewA15, textViewA16, textViewA17, textViewX, textViewY, textViewUseHints;
    private TextView textViewA31;
    private TextView textViewA32;
    private TextView textViewA33;
    private TextView textViewA34;
    private TextView textViewA35;
    private TextView textViewA36;
    private TextView textViewA37;
    private TextView textViewPlayNavigationTitle;
    private TextView textViewPlayNavigationHint;
    private TextView textViewPlayPrevious;
    private TextView textViewPlayNext;
    private TextView textViewPlayTemp1;
    private TextView textViewPlayTemp2;
    private ImageView imageViewQuestion;
    private ImageView imageViewPlayHintPicture;
    private ImageView imageViewMenuHintPicture;
    private List<TextView> questionTextViewList = new ArrayList<>();
    private List<TextView> letterButtonsList = new ArrayList<>();
    private List<TextView> kliknutaSlovaList = new ArrayList<>();
    private List<String> svaSlova = new ArrayList<>();
    private List<Pitanje> listaPitanja = new ArrayList<>();
    private List<Linkovi> listaLinkova = new ArrayList<>();
    private int currentQuestionIndex = 0, ukupanBrojPitanja;
    private Map<String, GrupaPitanja> mapaGrupa = new HashMap<>();
    private List<String> listaNazivaGrupa = new ArrayList<>();
    private LinearLayout layoutGlavniMeni, layoutLinkovi;
    private List<String> odgovorenaPitanja = new ArrayList<>();
    private Map<String, Integer> brojOdgovorenihPitanjaPoGrupi = new HashMap<>();
    private Map<String, Integer> pragoviOtkljucavanja = new HashMap<>();
    private Pitanje trenutnoPitanje;
    int counter=0;
    int randomCounter=0, navigateCounter=0;
    String question;
    String dailyBonus;
    String weekendBonus;
    private AlertDialog dialog;
    TextView buttonOK, titleTextView, messageTextView;
    private FirebaseAnalytics mFBAnalytics;
    Bundle params;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd mRewardedVideoAd;
    private ConsentForm consentForm;
    private boolean rateDialogShowed, gpRateVisited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    loadNativeAds();
                    interstitialAdInit();
                    rewardVideoAdInit();
                }
            });

            fillDataFromXML();
            readSharePref();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            screenHeight = displayMetrics.heightPixels;
            screenWidth = displayMetrics.widthPixels;

            textViewYdimension = (screenHeight*8)/100;
            textViewXdimension = (screenWidth/7);
            popuniSvaSlova();
            loadStartActivity();

            mFBAnalytics = FirebaseAnalytics.getInstance(this);
            params = new Bundle();

            consent();

            fillDataLinkoviFromXML();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void popuniSvaSlova(){
        try {
            svaSlova.add("Q");
            svaSlova.add("W");
            svaSlova.add("E");
            svaSlova.add("R");
            svaSlova.add("T");
            svaSlova.add("Y");
            svaSlova.add("U");
            svaSlova.add("I");
            svaSlova.add("O");
            svaSlova.add("P");
            svaSlova.add("A");
            svaSlova.add("S");
            svaSlova.add("D");
            svaSlova.add("F");
            svaSlova.add("G");
            svaSlova.add("H");
            svaSlova.add("J");
            svaSlova.add("K");
            svaSlova.add("L");
            svaSlova.add("Z");
            svaSlova.add("X");
            svaSlova.add("C");
            svaSlova.add("V");
            svaSlova.add("B");
            svaSlova.add("N");
            svaSlova.add("M");
            Collections.shuffle(svaSlova);
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }

    }

    private void loadMenuActivity(){
        try {
            setContentView(R.layout.activity_menu);
            currentActivity = AppActivity.MENU;
            initMenuControls();
            currentQuestionIndex=0;
            showNativeAd(menuTemplateView);
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void initStartControls(){
        try {
            TextView textViewPlay = findViewById(R.id.textViewPlay);
            TextView textViewPoints = findViewById(R.id.textViewPoints);
            TextView textViewQuestions = findViewById(R.id.textViewQuestions);
            TextView textViewOtherApps = findViewById(R.id.textViewOtherApps);

            textViewPlay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadMenuActivity();
                }
            });

            textViewOtherApps.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadLinkoviActivity();
                }
            });

            currentActivity = AppActivity.START;
            String tmp = "Questions: " + odgovorenaPitanja.size() + " / " + ukupanBrojPitanja;
            textViewQuestions.setText(tmp);
            tmp = "Points: " + playerPoints;
            textViewPoints.setText(tmp);

            Animation anim = new AlphaAnimation(0.5f, 1.0f);
            anim.setDuration(1000); //You can manage the time of the blink with this parameter
            anim.setStartOffset(0);
            anim.setRepeatMode(Animation.RESTART);
            anim.setRepeatCount(Animation.INFINITE);
            textViewOtherApps.startAnimation(anim);


        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void initPlayControls(){
        try {
            LinearLayout layoutNavigation = findViewById(R.id.layoutNavigation);
            LinearLayout layoutQuestion = findViewById(R.id.layoutQuestion);
            layoutLetters1 = findViewById(R.id.layoutLetters1);
            layoutLetters2 = findViewById(R.id.layoutLetters2);
            LinearLayout layoutHints = findViewById(R.id.layoutHints);
            LinearLayout layoutLetters3 = findViewById(R.id.layoutLetters3);
            LinearLayout layoutLetters4 = findViewById(R.id.layoutLetters4);
            LinearLayout layoutLetters5 = findViewById(R.id.layoutLetters5);
            layoutLetters123 = findViewById(R.id.layoutLetters123);
            LinearLayout layoutAd = findViewById(R.id.layoutNativeAd);

            textViewX = findViewById(R.id.textViewX);
            textViewY = findViewById(R.id.textViewY);
            textViewUseHints = findViewById(R.id.textViewUseHints);

            imageViewQuestion = findViewById(R.id.imageViewPlayQuestion);
            tvQuestion = findViewById(R.id.textViewPlayQuestion);
            textViewPlayPrevious = findViewById(R.id.textViewPlayPrevious);
            textViewPlayNext = findViewById(R.id.textViewPlayNext);
            textViewPlayTemp1 = findViewById(R.id.textViewPlayTemp1);
            textViewPlayTemp2 = findViewById(R.id.textViewPlayTemp2);

            TextView textViewPlayNavigationBack = findViewById(R.id.textViewPlayNavigationBack);
            textViewPlayNavigationTitle = findViewById(R.id.textViewPlayNavigationTitle);
            textViewPlayNavigationHint = findViewById(R.id.textViewPlayNavigationHint);
            imageViewPlayHintPicture = findViewById(R.id.textViewPlayNavigationHintPicture);

            textViewPlayPrevious.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    currentQuestionIndex--;
                    navigateCounter++;

                    if(navigateCounter>2) {
                        ucitajPitanje(true, currentQuestionIndex);
                        navigateCounter = 0;
                    }else{
                        ucitajPitanje(false, currentQuestionIndex);
                    }

                }
            });

            textViewPlayNext.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    currentQuestionIndex++;
                    navigateCounter++;

                    if(navigateCounter>2) {
                        ucitajPitanje(true, currentQuestionIndex);
                        navigateCounter = 0;
                    }else{
                        ucitajPitanje(false, currentQuestionIndex);
                    }

                    // todo
                    showRateDialog();

                }
            });

            textViewX.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    deleteLetter();
                }
            });
            textViewPlayNavigationHint.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showGetHintsDialog();
                }
            });
            String tmp = "hints\n" + playerHints;
            textViewPlayNavigationHint.setText(tmp);
            imageViewPlayHintPicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showGetHintsDialog();
                }
            });
            textViewY.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    deleteAllLetters();
                }
            });
            textViewUseHints.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showUseHintsDialog();
                }
            });

            textViewPlayNavigationBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadMenuActivity();
                }
            });

            textViewA11 = findViewById(R.id.textViewA11);
            fitLetterTextViewDimensiond(textViewA11, textViewXdimension, textViewYdimension);
            textViewA12 = findViewById(R.id.textViewA12);
            fitLetterTextViewDimensiond(textViewA12, textViewXdimension, textViewYdimension);
            textViewA13 = findViewById(R.id.textViewA13);
            fitLetterTextViewDimensiond(textViewA13, textViewXdimension, textViewYdimension);
            textViewA14 = findViewById(R.id.textViewA14);
            fitLetterTextViewDimensiond(textViewA14, textViewXdimension, textViewYdimension);
            textViewA15 = findViewById(R.id.textViewA15);
            fitLetterTextViewDimensiond(textViewA15, textViewXdimension, textViewYdimension);
            textViewA16 = findViewById(R.id.textViewA16);
            fitLetterTextViewDimensiond(textViewA16, textViewXdimension, textViewYdimension);
            textViewA17 = findViewById(R.id.textViewA17);
            fitLetterTextViewDimensiond(textViewA17, textViewXdimension, textViewYdimension);

            TextView textViewA21 = findViewById(R.id.textViewA21);
            fitLetterTextViewDimensiond(textViewA21, textViewXdimension, textViewYdimension);
            TextView textViewA22 = findViewById(R.id.textViewA22);
            fitLetterTextViewDimensiond(textViewA22, textViewXdimension, textViewYdimension);
            TextView textViewA23 = findViewById(R.id.textViewA23);
            fitLetterTextViewDimensiond(textViewA23, textViewXdimension, textViewYdimension);
            TextView textViewA24 = findViewById(R.id.textViewA24);
            fitLetterTextViewDimensiond(textViewA24, textViewXdimension, textViewYdimension);
            TextView textViewA25 = findViewById(R.id.textViewA25);
            fitLetterTextViewDimensiond(textViewA25, textViewXdimension, textViewYdimension);
            TextView textViewA26 = findViewById(R.id.textViewA26);
            fitLetterTextViewDimensiond(textViewA26, textViewXdimension, textViewYdimension);
            TextView textViewA27 = findViewById(R.id.textViewA27);
            fitLetterTextViewDimensiond(textViewA27, textViewXdimension, textViewYdimension);

            textViewA31 = findViewById(R.id.textViewA31);
            fitLetterTextViewDimensiond(textViewA31, textViewXdimension, textViewYdimension);
            textViewA32 = findViewById(R.id.textViewA32);
            fitLetterTextViewDimensiond(textViewA32, textViewXdimension, textViewYdimension);
            textViewA33 = findViewById(R.id.textViewA33);
            fitLetterTextViewDimensiond(textViewA33, textViewXdimension, textViewYdimension);
            textViewA34 = findViewById(R.id.textViewA34);
            fitLetterTextViewDimensiond(textViewA34, textViewXdimension, textViewYdimension);
            textViewA35 = findViewById(R.id.textViewA35);
            fitLetterTextViewDimensiond(textViewA35, textViewXdimension, textViewYdimension);
            textViewA36 = findViewById(R.id.textViewA36);
            fitLetterTextViewDimensiond(textViewA36, textViewXdimension, textViewYdimension);
            textViewA37 = findViewById(R.id.textViewA37);
            fitLetterTextViewDimensiond(textViewA37, textViewXdimension, textViewYdimension);

            layoutNavigation.getLayoutParams().height = (screenHeight * 8) / 100;
            layoutQuestion.getLayoutParams().height = ((screenHeight * 29) / 100);
            layoutLetters1.getLayoutParams().height = (screenHeight * 8) / 100;
            layoutLetters2.getLayoutParams().height = (screenHeight * 8) / 100;
            layoutHints.getLayoutParams().height = (screenHeight * 8) / 100;

            layoutLetters123.getLayoutParams().height = (screenHeight * 24) / 100;
            layoutAd.getLayoutParams().height = (screenHeight * 15) / 100;

            layoutNavigation.getLayoutParams().width = screenWidth;
            layoutQuestion.getLayoutParams().width = screenWidth;
            layoutLetters1.getLayoutParams().width = screenWidth;
            layoutLetters2.getLayoutParams().width = screenWidth;
            layoutHints.getLayoutParams().width = screenWidth;

            layoutLetters123.getLayoutParams().width = screenWidth;
            if (layoutLetters3 != null) {
                layoutLetters3.getLayoutParams().width = screenWidth;
            }
            if (layoutLetters4 != null) {
                layoutLetters4.getLayoutParams().width = screenWidth;
            }
            if (layoutLetters5 != null) {
                layoutLetters5.getLayoutParams().width = screenWidth;
            }

            layoutAd.getLayoutParams().width = screenWidth;

            letterButtonsList = new ArrayList<>();

            letterButtonsList.add(textViewA11);
            letterButtonsList.add(textViewA12);
            letterButtonsList.add(textViewA13);
            letterButtonsList.add(textViewA14);
            letterButtonsList.add(textViewA15);
            letterButtonsList.add(textViewA16);
            letterButtonsList.add(textViewA17);

            letterButtonsList.add(textViewA21);
            letterButtonsList.add(textViewA22);
            letterButtonsList.add(textViewA23);
            letterButtonsList.add(textViewA24);
            letterButtonsList.add(textViewA25);
            letterButtonsList.add(textViewA26);
            letterButtonsList.add(textViewA27);

            letterButtonsList.add(textViewA31);
            letterButtonsList.add(textViewA32);
            letterButtonsList.add(textViewA33);
            letterButtonsList.add(textViewA34);
            letterButtonsList.add(textViewA35);
            letterButtonsList.add(textViewA36);
            letterButtonsList.add(textViewA37);

            textViewUseHints.getLayoutParams().width = ((screenHeight * 8) / 100) * 3;
            textViewUseHints.getLayoutParams().height = (screenHeight * 8) / 100;

            textViewX.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewX.getLayoutParams().width = (screenHeight * 8) / 100;

            textViewY.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewY.getLayoutParams().width = (screenHeight * 8) / 100;

            imageViewQuestion.getLayoutParams().height = (screenHeight * 30) / 100;
            imageViewQuestion.getLayoutParams().width = screenWidth/2;

            tvQuestion.getLayoutParams().height = (screenHeight * 30) / 100;
            tvQuestion.getLayoutParams().width = screenWidth/2;
            int tempDimension = screenWidth/8;

         /*   if(imageViewQuestion.getLayoutParams().width>tvQuestion.getLayoutParams().width) {
                tempDimension = (screenWidth - imageViewQuestion.getLayoutParams().width) / 4;
            }else {
                tempDimension = (screenWidth - tvQuestion.getLayoutParams().width) / 4;
            } */

            textViewPlayPrevious.getLayoutParams().height = tempDimension;
            textViewPlayPrevious.getLayoutParams().width = tempDimension;

            textViewPlayNext.getLayoutParams().height = tempDimension;
            textViewPlayNext.getLayoutParams().width = tempDimension;

          /*  textViewPlayTemp1.getLayoutParams().height = tempDimension;
            textViewPlayTemp1.getLayoutParams().width = tempDimension;
            textViewPlayTemp2.getLayoutParams().height = tempDimension;
            textViewPlayTemp2.getLayoutParams().width = tempDimension; */

            if (currentQuestionIndex > 0) {
                textViewPlayPrevious.setEnabled(true);
                textViewPlayPrevious.setClickable(true);
                textViewPlayPrevious.setAlpha(1.0f);
            } else {
                textViewPlayPrevious.setEnabled(false);
                textViewPlayPrevious.setClickable(false);
                textViewPlayPrevious.setAlpha(0.2f);
            }

            if (currentQuestionIndex + 1 < listaPitanja.size()) {
                textViewPlayNext.setEnabled(true);
                textViewPlayNext.setClickable(true);
                textViewPlayNext.setAlpha(1.0f);
            } else {
                textViewPlayNext.setEnabled(false);
                textViewPlayNext.setClickable(false);
                textViewPlayNext.setAlpha(0.2f);
            }

            textViewPlayNavigationBack.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewPlayNavigationBack.getLayoutParams().width = screenWidth / 4;

            textViewPlayNavigationTitle.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewPlayNavigationTitle.getLayoutParams().width = screenWidth / 2;

            textViewPlayNavigationHint.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewPlayNavigationHint.getLayoutParams().width = screenWidth / 8;

            imageViewPlayHintPicture.getLayoutParams().height = (screenHeight * 8) / 100;
            imageViewPlayHintPicture.getLayoutParams().width = screenWidth / 8;
            if(dailyBonus()){
                imageViewPlayHintPicture.setImageResource(R.drawable.z_sijalicax);
            }else {
                imageViewPlayHintPicture.setImageResource(R.drawable.z_sijalica);
            }

            layoutLetters123.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ucitajSledeceNeodgovorenoPitanje();
                    if (odgovorenaSvaPitanjaIzGrupe()) {
                        showInfoDialog("Level completed\nReturn to main menu");
                    } else {
                        ucitajSledeceNeodgovorenoPitanje();
                        //todo
                        showRateDialog();
                    }
                }
            });
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }

    }

    private void fitLetterTextViewDimensiond(TextView tv, int w, int h) {
        try {
            if (tv != null) {
                if (w > h) {
                    tv.getLayoutParams().height = h;
                    tv.getLayoutParams().width = h;
                } else {
                    tv.getLayoutParams().height = w;
                    tv.getLayoutParams().width = w;
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void initMenuControls(){
        try {
            clearLists();
            TextView textViewMenuNavigationBack = findViewById(R.id.textViewMenuNavigationBack);
            textViewMenuNavigationHint = findViewById(R.id.textViewMenuNavigationHint);
            TextView textViewMenuNavigationTitle = findViewById(R.id.textViewMenuNavigationTitle);
            imageViewMenuHintPicture = findViewById(R.id.imageViewMenuHintPicture);
            layoutGlavniMeni = findViewById(R.id.layoutGlavniMeni);
            menuTemplateView = findViewById(R.id.menu_template);

            textViewMenuNavigationBack.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadStartActivity();
                }
            });
            textViewMenuNavigationHint.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showGetHintsDialog();
                }
            });

            String tmp = "hints\n" + playerHints;
            textViewMenuNavigationHint.setText(tmp);

            imageViewMenuHintPicture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showGetHintsDialog();
                }
            });

            currentActivity = AppActivity.MENU;

            textViewMenuNavigationBack.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewMenuNavigationBack.getLayoutParams().width = screenWidth / 4;

            textViewMenuNavigationTitle.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewMenuNavigationTitle.getLayoutParams().width = screenWidth / 2;

            textViewMenuNavigationHint.getLayoutParams().height = (screenHeight * 8) / 100;
            textViewMenuNavigationHint.getLayoutParams().width = screenWidth / 8;

            imageViewMenuHintPicture.getLayoutParams().height = (screenHeight * 8) / 100;
            imageViewMenuHintPicture.getLayoutParams().width = screenWidth / 8;

            if(dailyBonus()) {
                imageViewMenuHintPicture.setImageResource(R.drawable.z_sijalicax);
            }else{
                imageViewMenuHintPicture.setImageResource(R.drawable.z_sijalica);
            }


            //     layoutGlavniMeni.getLayoutParams().width = screenWidth;
            //     layoutGlavniMeni.getLayoutParams().height = screenHeight - textViewMenuNavigationHint.getLayoutParams().height - menuTemplateView.getLayoutParams().height;

            GrupaPitanja temp;
            for (int k = 0; k < listaNazivaGrupa.size(); k++) {
                temp = mapaGrupa.get(listaNazivaGrupa.get(k));
                kreirajGrupuUmeniju(temp);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void loadStartActivity(){
        try {
            setContentView(R.layout.activity_main);
            currentActivity = AppActivity.START;
            initStartControls();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        try {
            switch (currentActivity) {
                case MENU:
                    loadStartActivity();
                    break;
                case PLAY:
                    loadMenuActivity();
                    break;
                case LINKOVI:
                    loadStartActivity();
                    break;
                default:
                    super.onBackPressed();
            }
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    public void loadNativeAds(){
        try {
            AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.adMobNativeAdID));
            builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                @Override
                public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                    loadedNativeAds.add(unifiedNativeAd);
                    if (isDestroyed()) {
                        unifiedNativeAd.destroy();
                    }
                }
            });

            AdLoader adLoader = builder.build();
            AdRequest adRequest = new AdRequest.Builder().build();
            adLoader.loadAds(adRequest, 3);
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    public void showNativeAd(TemplateView templateView){
        try {
            if (loadedNativeAds.size() > 0) {

                templateView.setNativeAd(loadedNativeAds.get(0));

                loadedNativeAds.remove(0);
                if (loadedNativeAds.size() == 0) {
                    loadNativeAds();
                }

                params.putString("App", appName);
                params.putString("AppVersion", appVersion);
                mFBAnalytics.logEvent("NativeAd_SHOW", params);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void fitTextViewDimensiond(TextView textView, int w, int h){
        try {
            if (w < h) {
                textView.getLayoutParams().width = w;
                textView.getLayoutParams().height = w;
            } else {
                textView.getLayoutParams().width = h;
                textView.getLayoutParams().height = h;
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void fillQuestionTextViews(String string){
        try {
            if (!string.contains("_")) {
                TextView tv;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(5, 5, 5, 5);
                params.gravity = Gravity.CENTER;

                int txtViewW = ((screenWidth - string.length() * 6) - 70) / string.length();
                int txtViewH = (screenHeight * 7) / 100;

                for (int i = 0; i < string.length(); i++) {
                    tv = new TextView(MainActivity.this);
                    tv.setTextColor(Color.WHITE);
                    tv.setText("?");
                    tv.setBackgroundResource(R.drawable.z_letterbackground);
                    tv.setGravity(Gravity.CENTER);

                    TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    tv.setLayoutParams(params);
                    fitTextViewDimensiond(tv, txtViewW, txtViewH);
                    layoutLetters1.addView(tv);

                    questionTextViewList.add(tv);
                }
                layoutLetters2.getLayoutParams().height = 0;
                layoutLetters2.getLayoutParams().width = 0;
            } else {
                String[] separated = string.split("_");
                int length = Math.max(separated[0].length(), separated[1].length());

                int txtViewW = (screenWidth - length * 6) / length;
                int txtViewH = (screenHeight * 6) / 100;

                TextView tv;
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(2, 2, 2, 2);
                params.gravity = Gravity.CENTER;

                for (int i = 0; i < separated[0].length(); i++) {
                    tv = new TextView(MainActivity.this);
                    tv.setTextColor(Color.WHITE);
                    tv.setText("?");
                    tv.setBackgroundResource(R.drawable.z_letterbackground);
                    tv.setGravity(Gravity.CENTER);

                    TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    tv.setLayoutParams(params);
                    fitTextViewDimensiond(tv, txtViewW, txtViewH);
                    layoutLetters1.addView(tv);

                    questionTextViewList.add(tv);
                }

                for (int j = 0; j < separated[1].length(); j++) {
                    tv = new TextView(MainActivity.this);
                    tv.setTextColor(Color.WHITE);
                    tv.setText("?");
                    tv.setBackgroundResource(R.drawable.z_letterbackground);
                    tv.setGravity(Gravity.CENTER);

                    TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    tv.setLayoutParams(params);
                    fitTextViewDimensiond(tv, txtViewW, txtViewH);
                    layoutLetters2.addView(tv);

                    questionTextViewList.add(tv);
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void fitLettersSizes(String str){
        try {
            float density = this.getResources().getDisplayMetrics().density;
            float px = 70 * density;
            int w = (screenWidth - (int) px) / 7;
            int h;

            if (str.length() < 7) {
                h = (screenHeight * 24) / 100;
            } else if (str.length() < 14) {
                h = (screenHeight * 12) / 100;
            } else {
                h = (screenHeight * 8) / 100;
            }

            if (w < h) {
                h = w;
            } else {
                w = h;
            }

            for (int i = 0; i < letterButtonsList.size(); i++) {
                if (letterButtonsList.get(i) != null) {
                    letterButtonsList.get(i).getLayoutParams().width = w;
                    letterButtonsList.get(i).getLayoutParams().height = h;
                    setCustomOnClickListener(letterButtonsList.get(i));
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void ukloniDonjuCrtuIzStringa(String str){
        try {
            if (str.contains("_")) {
                str = str.replace("_", "");
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void fillLettersTextViews(String str){
        try {
            if (str.contains("_")) {
                str = str.replace("_", "");
            }
            List<TextView> tempLista;

            disableTextBoxs(str);

            if (str.length() < 7) {
                tempLista = letterButtonsList.subList(7, 13);
                Collections.shuffle(tempLista);
                for (int i = 7; i < 14; i++) {
                    if (i < str.length() + 7) {
                        if (letterButtonsList.get(i) != null)
                            letterButtonsList.get(i).setText(Character.toString(str.charAt(i - 7)));
                    } else {
                        if (letterButtonsList.get(i) != null)
                            letterButtonsList.get(i).setText(getRandomLetter());
                    }
                }
            } else if (str.length() < 14) {
                tempLista = letterButtonsList.subList(0, 13);
                Collections.shuffle(tempLista);
                for (int i = 0; i < 14; i++) {
                    if (i < str.length()) {
                        if (letterButtonsList.get(i) != null)
                            letterButtonsList.get(i).setText(Character.toString(str.charAt(i)));
                    } else {
                        if (letterButtonsList.get(i) != null)
                            letterButtonsList.get(i).setText(getRandomLetter());
                    }
                }
            } else if (str.length() <= 21) {
                tempLista = letterButtonsList.subList(0, 20);
                Collections.shuffle(tempLista);
                for (int i = 0; i < 21; i++) {
                    if (i < str.length()) {
                        if (letterButtonsList.get(i) != null)
                            letterButtonsList.get(i).setText(Character.toString(str.charAt(i)));
                    } else {
                        if (letterButtonsList.get(i) != null)
                            letterButtonsList.get(i).setText(getRandomLetter());
                    }
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void disableTextBoxs(String str){
        try {
            enableAllLetters();
            if (str.length() < 7) {
                if (textViewA11 != null) textViewA11.setEnabled(false);
                if (textViewA12 != null) textViewA12.setEnabled(false);
                if (textViewA13 != null) textViewA13.setEnabled(false);
                if (textViewA14 != null) textViewA14.setEnabled(false);
                if (textViewA15 != null) textViewA15.setEnabled(false);
                if (textViewA16 != null) textViewA16.setEnabled(false);
                if (textViewA17 != null) textViewA17.setEnabled(false);

                if (textViewA31 != null) textViewA31.setEnabled(false);
                if (textViewA32 != null) textViewA32.setEnabled(false);
                if (textViewA33 != null) textViewA33.setEnabled(false);
                if (textViewA34 != null) textViewA34.setEnabled(false);
                if (textViewA35 != null) textViewA35.setEnabled(false);
                if (textViewA36 != null) textViewA36.setEnabled(false);
                if (textViewA37 != null) textViewA37.setEnabled(false);

                if (textViewA11 != null) textViewA11.setVisibility(View.INVISIBLE);
                if (textViewA12 != null) textViewA12.setVisibility(View.INVISIBLE);
                if (textViewA13 != null) textViewA13.setVisibility(View.INVISIBLE);
                if (textViewA14 != null) textViewA14.setVisibility(View.INVISIBLE);
                if (textViewA15 != null) textViewA15.setVisibility(View.INVISIBLE);
                if (textViewA16 != null) textViewA16.setVisibility(View.INVISIBLE);
                if (textViewA17 != null) textViewA17.setVisibility(View.INVISIBLE);

                if (textViewA31 != null) textViewA31.setVisibility(View.INVISIBLE);
                if (textViewA32 != null) textViewA32.setVisibility(View.INVISIBLE);
                if (textViewA33 != null) textViewA33.setVisibility(View.INVISIBLE);
                if (textViewA34 != null) textViewA34.setVisibility(View.INVISIBLE);
                if (textViewA35 != null) textViewA35.setVisibility(View.INVISIBLE);
                if (textViewA36 != null) textViewA36.setVisibility(View.INVISIBLE);
                if (textViewA37 != null) textViewA37.setVisibility(View.INVISIBLE);

            } else if (str.length() < 14) {
                if (textViewA31 != null) textViewA31.setEnabled(false);
                if (textViewA32 != null) textViewA32.setEnabled(false);
                if (textViewA33 != null) textViewA33.setEnabled(false);
                if (textViewA34 != null) textViewA34.setEnabled(false);
                if (textViewA35 != null) textViewA35.setEnabled(false);
                if (textViewA36 != null) textViewA36.setEnabled(false);
                if (textViewA37 != null) textViewA37.setEnabled(false);

                if (textViewA31 != null) textViewA31.setVisibility(View.INVISIBLE);
                if (textViewA32 != null) textViewA32.setVisibility(View.INVISIBLE);
                if (textViewA33 != null) textViewA33.setVisibility(View.INVISIBLE);
                if (textViewA34 != null) textViewA34.setVisibility(View.INVISIBLE);
                if (textViewA35 != null) textViewA35.setVisibility(View.INVISIBLE);
                if (textViewA36 != null) textViewA36.setVisibility(View.INVISIBLE);
                if (textViewA37 != null) textViewA37.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void enableAllLetters(){
        try {
            for (int i = 0; i < letterButtonsList.size(); i++) {
                if (letterButtonsList.get(i) != null) {
                    letterButtonsList.get(i).setEnabled(true);
                    letterButtonsList.get(i).setVisibility(View.VISIBLE);
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void setCustomOnClickListener(final TextView tv){
        try {
            tv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!testNetwork()) {
                        InternetDialog id = new InternetDialog(MainActivity.this);
                        id.openDialog();
                    }else {
                        if (questionTextViewList.size() > counter) {
                            questionTextViewList.get(counter).setText(tv.getText());
                            questionTextViewList.get(counter).setTextColor(Color.BLACK);

                            tv.setEnabled(false);
                            tv.setVisibility(View.INVISIBLE);
                            kliknutaSlovaList.add(tv);
                            //  popunjenaSlovaList.add(questionTextViewList.get(counter));
                            counter++;
                        }
                        if (questionTextViewList.size() == counter) {
                            showResultDialog(checkCorrectAnswer());
                        }
                    }

                }
            });
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void deleteLetter(){
        try {
            if (kliknutaSlovaList.size() > 0) {
                kliknutaSlovaList.get(kliknutaSlovaList.size() - 1).setVisibility(View.VISIBLE);
                kliknutaSlovaList.get(kliknutaSlovaList.size() - 1).setEnabled(true);
                kliknutaSlovaList.remove(kliknutaSlovaList.size() - 1);

                questionTextViewList.get(counter - 1).setText("?");
                questionTextViewList.get(counter - 1).setTextColor(Color.WHITE);
                if (counter > 0) {
                    counter--;
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void deleteAllLetters(){
        try {
            int temp = kliknutaSlovaList.size();
            for (int i = 0; i < temp; i++) {
                deleteLetter();
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private boolean checkCorrectAnswer(){
        try {
            StringBuilder tmp = new StringBuilder();
            boolean result = false;
            for (int i = 0; i < questionTextViewList.size(); i++) {
                tmp.append(questionTextViewList.get(i).getText());
            }
            question = question.replace("_", "");
            if (question.equalsIgnoreCase(tmp.toString())) {
                updateHints(1);
                playerPoints = playerPoints + trenutnoPitanje.getBodovi();
                result = true;
            }
            return result;
        }catch (Exception e){
            printErrorMessage(e.getMessage());
            return false;
        }
    }

    private void loadPlayActivity(){
        try {
            setContentView(R.layout.activity_play);
            currentActivity = AppActivity.PLAY;
            ucitajPitanje(true, currentQuestionIndex);
            loadAdMobAds();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private boolean odgovorenaSvaPitanjaIzGrupe(){
        try {
            return listaPitanja.size() == brojOdgovorenihPitanjaPoGrupi.get(listaPitanja.get(0).getNazivGrupe());
        }catch (Exception e){
            printErrorMessage(e.getMessage());
            return false;
        }
    }

    private void ucitajSledeceNeodgovorenoPitanje(){
        try {
            String tempKey;
            for (int i = currentQuestionIndex; i < listaPitanja.size(); i++) {
                tempKey = listaPitanja.get(i).getNazivGrupe() + listaPitanja.get(i).getId();
                if (!odgovorenaPitanja.contains(tempKey)) {
                    currentQuestionIndex = i;
                    ucitajPitanje(true, currentQuestionIndex);
                    return;
                }
            }
            for (int j = 0; j < currentQuestionIndex; j++) {
                tempKey = listaPitanja.get(j).getNazivGrupe() + listaPitanja.get(j).getId();
                if (!odgovorenaPitanja.contains(tempKey)) {
                    currentQuestionIndex = j;
                    ucitajPitanje(true, currentQuestionIndex);
                    return;
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void ucitajPitanje(boolean showAd, int index){
        try {
            if (index < listaPitanja.size()) {
                trenutnoPitanje = listaPitanja.get(index);
                clearLists();
                question = trenutnoPitanje.getSlovaPitanja();

                String key = trenutnoPitanje.getNazivGrupe() + trenutnoPitanje.getId();
                String tekstPitanja = trenutnoPitanje.getTekstPitanja();

                initPlayControls();

                fitLettersSizes(question);
                fillQuestionTextViews(question);

                if(tekstPitanja.length()>0){
                    tvQuestion.setText(tekstPitanja);
                    tvQuestion.setEnabled(true);
                    tvQuestion.setVisibility(View.VISIBLE);
                    imageViewQuestion.setEnabled(false);
                    imageViewQuestion.setVisibility(View.INVISIBLE);
                    //   fitTextViewDimensions(tvQuestion);
                }else {
                    tvQuestion.setText("");
                    tvQuestion.setEnabled(false);
                    tvQuestion.setVisibility(View.INVISIBLE);
                    imageViewQuestion.setEnabled(true);
                    imageViewQuestion.setVisibility(View.VISIBLE);
                    imageViewQuestion.setImageResource(getImageIDFromName(trenutnoPitanje.getSlikaPitanja()));
                    //    fitImageViewDimensions(imageViewQuestion, getImageIDFromName(trenutnoPitanje.getSlikaPitanja()));
                }

                if (!odgovorenaPitanja.contains(key)) {
                    layoutLetters123.setClickable(false);
                    ukloniDonjuCrtuIzStringa(question);
                    fillLettersTextViews(question);
                    for (int i = 0; i < layoutLetters123.getChildCount(); i++) {
                        View view = layoutLetters123.getChildAt(i);
                        view.setVisibility(View.VISIBLE);
                    }
                    layoutLetters123.setBackgroundResource(0);
                    disableHintsAndButtons(false);
                } else {
                    popuniOdgovorenoPitanje();
                    disableHintsAndButtons(true);
                }

                templateView = findViewById(R.id.my_template);
                if (showAd) {
                    showNativeAd(templateView);
                }
                int tempIndeks = currentQuestionIndex + 1;
                String tmp = "Question " + tempIndeks + " / " + listaPitanja.size();
                textViewPlayNavigationTitle.setText(tmp);
            } else {
                currentQuestionIndex = 0;
                ucitajPitanje(showAd, currentQuestionIndex);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void disableHintsAndButtons(boolean disable){
        try {
            if (disable) {
                textViewUseHints.setAlpha(0.2f);
                textViewX.setAlpha(0.2f);
                textViewY.setAlpha(0.2f);
                textViewUseHints.setClickable(false);
                textViewX.setClickable(false);
                textViewY.setClickable(false);
            }else{
                textViewUseHints.setAlpha(1.0f);
                textViewX.setAlpha(1.0f);
                textViewY.setAlpha(1.0f);
                textViewUseHints.setClickable(true);
                textViewX.setClickable(true);
                textViewY.setClickable(true);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void fitImageViewDimensions(ImageView iv, int resource){
        try {
            int width = iv.getLayoutParams().width;
            int height = iv.getLayoutParams().height;
            Drawable drawable = ContextCompat.getDrawable(this, resource);
            assert drawable != null;
            int pictureWidth = drawable.getIntrinsicWidth();
            int pictureHeight = drawable.getIntrinsicHeight();
            double coef = (double)pictureWidth / (double)pictureHeight;
            int newWidth = (int) (coef * height);
            if(newWidth>iv.getLayoutParams().width){
                iv.getLayoutParams().width = width;
                iv.getLayoutParams().height = (int)((double)width/ coef);
            }else {
                iv.getLayoutParams().width = (int) (coef * height);
                iv.getLayoutParams().height = height;
            }

            // int tempDimension = Math.max();


            textViewPlayTemp1.getLayoutParams().height = screenHeight/4;
            textViewPlayTemp1.getLayoutParams().width = (screenWidth-iv.getLayoutParams().width - textViewPlayNext.getLayoutParams().width - textViewPlayPrevious.getLayoutParams().width)/2;
            textViewPlayTemp2.getLayoutParams().height = screenHeight/4;
            textViewPlayTemp2.getLayoutParams().width = textViewPlayTemp1.getLayoutParams().width;


        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void popuniOdgovorenoPitanje(){
        try {
            layoutLetters123.setClickable(true);
            layoutLetters123.setBackgroundResource(R.drawable.z_sledece);

            disableHintsAndButtons(true);
            for (int i = 0; i < layoutLetters123.getChildCount(); i++) {
                View view = layoutLetters123.getChildAt(i);
                view.setVisibility(View.INVISIBLE);
            }

            String tmp = question;
            if (tmp.contains("_")) {
                tmp = tmp.replace("_", "");
            }

            for (int k = 0; k < tmp.length(); k++) {
                questionTextViewList.get(k).setTextColor(Color.BLACK);
                String tmp1 = "" + tmp.charAt(k);
                questionTextViewList.get(k).setText(tmp1);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void solveQuestion(){
        try {
            updateHints(-20);
            upisiUmapuOdgovorenihPitanja(trenutnoPitanje);
            ucitajPitanje(true, currentQuestionIndex);
            if(odgovorenaPitanja.size()%5==0){
                showInterstitialAd();
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void updateHints(int number){
        try {
            playerHints = playerHints + number;
            if (textViewPlayNavigationHint != null) {
                String tmp = "hints\n" + playerHints;
                textViewPlayNavigationHint.setText(tmp);
            }
            if (textViewMenuNavigationHint != null) {
                String tmp1 = "hints\n" + playerHints;
                textViewMenuNavigationHint.setText(tmp1);
            }
            writeSharedPref();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    public void clearLists(){
        try {
            letterButtonsList.clear();
            questionTextViewList.clear();
            kliknutaSlovaList.clear();
            //  popunjenaSlovaList.clear();
            counter = 0;
            randomCounter = 0;
            Collections.shuffle(svaSlova);
            if (layoutLetters1 != null) {
                layoutLetters1.removeAllViews();
            }
            if (layoutLetters2 != null) {
                layoutLetters2.removeAllViews();
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private String getRandomLetter(){
        try {
            if (!question.contains(svaSlova.get(randomCounter))) {
                randomCounter++;
                return svaSlova.get(randomCounter - 1);
            } else {
                randomCounter++;
                return getRandomLetter();
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
            return "Q";
        }
    }

    private void removeExtraLetters(){
        try {
            updateHints(-10);
            deleteAllLetters();

            for (int i = 0; i < letterButtonsList.size(); i++) {
                if (!question.contains(letterButtonsList.get(i).getText())) {
                    letterButtonsList.get(i).setEnabled(false);
                    letterButtonsList.get(i).setVisibility(View.INVISIBLE);
                }
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    public void fillDataFromXML() {
        try {

            Pitanje pitanjeObj = new Pitanje();
            AssetManager assetManager = getAssets();
            InputStream inputStream;

            inputStream = assetManager.open("pitanja.xls");

            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int row = sheet.getRows();
            int col = sheet.getColumns();

            for (int i = 0; i < row; i++) {

                for (int j = 0; j < col; j++) {

                    Cell cell = sheet.getCell(j, i);
                    if (j == 0) {
                        // Kolona A - id pitanja
                        pitanjeObj.setId(Integer.parseInt(cell.getContents()));
                    } else if (j == 1) {
                        // Kolona B - slova pitanja
                        pitanjeObj.setSlovaPitanja(cell.getContents());
                    } else if (j == 2) {
                        // Kolona C - fotografija
                        pitanjeObj.setSlikaPitanja(cell.getContents());
                    } else if (j == 3) {
                        // Kolona D - bodovi
                        if (cell.getContents().equalsIgnoreCase("")) {
                            pitanjeObj.setBodovi(5);
                        } else {
                            pitanjeObj.setBodovi(Integer.parseInt(cell.getContents()));
                        }
                    } else if (j == 4) {
                        // Kolona E - prikazani naziv grupe
                        pitanjeObj.setPrikazaniNazivGrupe(cell.getContents());
                    }else if (j == 5) {
                        // Kolona F - naziv grupe
                        pitanjeObj.setNazivGrupe(cell.getContents());
                    }else if (j == 6) {
                        // Kolona G - slika grupe pitanja
                        pitanjeObj.setSlikaGrupe(cell.getContents());
                    }else if(j == 7){
                        // Kolona H - originalna slika pitanja
                        pitanjeObj.setOriginalnaSlika(cell.getContents());
                    }else if(j == 8){
                        // Kolona I - tekst pitanja
                        pitanjeObj.setTekstPitanja(cell.getContents());
                    }
                }

                listaPitanja.add(pitanjeObj);
                pitanjeObj = new Pitanje();
            }

            Pitanje iterator;
            for(int j=0; j<listaPitanja.size(); j++){
                iterator = listaPitanja.get(j);
                if(daLiPostojiGrupa(iterator.getNazivGrupe())){
                    mapaGrupa.get(iterator.getNazivGrupe()).pitanjaGrupe.add(iterator);
                }else{
                    GrupaPitanja novaGrupa = new GrupaPitanja();
                    novaGrupa.setNazivGrupe(iterator.getNazivGrupe());
                    novaGrupa.getPitanjaGrupe().add(iterator);
                    novaGrupa.setSlikaGrupe(iterator.getSlikaGrupe());
                    novaGrupa.setPrikazaniNazivGrupe(iterator.getPrikazaniNazivGrupe());
                    mapaGrupa.put(iterator.getNazivGrupe(), novaGrupa);
                    listaNazivaGrupa.add(iterator.getNazivGrupe());
                    brojOdgovorenihPitanjaPoGrupi.put(iterator.getNazivGrupe(), 0);

                    if(mapaGrupa.size()<=2){
                        pragoviOtkljucavanja.put(iterator.getNazivGrupe(),0);
                    }else{
                        pragoviOtkljucavanja.put(iterator.getNazivGrupe(),(j*8)/10);
                    }

                }
            }
            ukupanBrojPitanja = listaPitanja.size();
        } catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void kreirajGrupuUmeniju(final GrupaPitanja grupaPitanja){
        try {
            int brojOdgovorenihPitanja = odgovorenaPitanja.size();

            LinearLayout newLinearLayout = new LinearLayout(this);
            newLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSlikaGrupe = new TextView(this);

            boolean odgovorenaSvaPitanja = brojOdgovorenihPitanjaPoGrupi.get(grupaPitanja.getNazivGrupe())==grupaPitanja.getPitanjaGrupe().size();

            if(brojOdgovorenihPitanja>=pragoviOtkljucavanja.get(grupaPitanja.getNazivGrupe())){
                if(!odgovorenaSvaPitanja){
                    tvSlikaGrupe.setBackgroundResource(getImageIDFromName(grupaPitanja.getSlikaGrupe()));
                    String tmp = brojOdgovorenihPitanjaPoGrupi.get(grupaPitanja.getNazivGrupe()) + " / " + grupaPitanja.getPitanjaGrupe().size();
                    tvSlikaGrupe.setText(tmp);
                }else{
                    tvSlikaGrupe.setBackgroundResource(R.drawable.z_circlecorrect);
                    tvSlikaGrupe.setText("");
                }
            }else {
                tvSlikaGrupe.setBackgroundResource(R.drawable.z_circlelocked);
                tvSlikaGrupe.setText("");
            }

            tvSlikaGrupe.setLayoutParams(new ViewGroup.LayoutParams(
                    screenWidth/4,
                    screenWidth/4));

            tvSlikaGrupe.setTextAppearance(android.R.style.TextAppearance_Large);
            tvSlikaGrupe.setTypeface(null, Typeface.BOLD);
            tvSlikaGrupe.setTextColor(Color.WHITE);
            tvSlikaGrupe.setGravity(Gravity.CENTER);
            newLinearLayout.addView(tvSlikaGrupe);

            LinearLayout unutrasnjiLinearLayout = new LinearLayout(this);
            unutrasnjiLinearLayout.setLayoutParams(new LinearLayout.LayoutParams((screenWidth*3)/4, ViewGroup.LayoutParams.WRAP_CONTENT));
            unutrasnjiLinearLayout.setOrientation(LinearLayout.VERTICAL);

            LinearLayout gornjiLinearLayout = new LinearLayout(this);
            gornjiLinearLayout.setLayoutParams(new LinearLayout.LayoutParams((screenWidth*3)/4, screenWidth/4));
            gornjiLinearLayout.setOrientation(LinearLayout.VERTICAL);
            unutrasnjiLinearLayout.addView(gornjiLinearLayout);

            newLinearLayout.addView(unutrasnjiLinearLayout);

            TextView imeGrupe = new TextView(this);

            if(odgovorenaSvaPitanja){
                String tmp = grupaPitanja.getPrikazaniNazivGrupe() + " - Completed";
                imeGrupe.setText(tmp);
            }else{
                imeGrupe.setText(grupaPitanja.getPrikazaniNazivGrupe());
            }
            imeGrupe.setTextColor(Color.BLACK);
            imeGrupe.setLayoutParams(new ViewGroup.LayoutParams(
                    screenWidth-tvSlikaGrupe.getLayoutParams().width,
                    screenWidth/8));
            imeGrupe.setGravity(Gravity.BOTTOM);
            imeGrupe.setTextAppearance(android.R.style.TextAppearance_Medium);
            gornjiLinearLayout.addView(imeGrupe);

            if(brojOdgovorenihPitanja>=pragoviOtkljucavanja.get(grupaPitanja.getNazivGrupe())){
                // progress bar
                ProgressBar  progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
                progressBar.setIndeterminate(false);
                progressBar.setMax(100);
                progressBar.setProgress((brojOdgovorenihPitanjaPoGrupi.get(grupaPitanja.getNazivGrupe())*100)/grupaPitanja.getPitanjaGrupe().size());
                progressBar.setLayoutParams(new ViewGroup.LayoutParams(
                        gornjiLinearLayout.getLayoutParams().width-60,
                        screenWidth/8));
                gornjiLinearLayout.addView(progressBar);
                newLinearLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listaPitanja = mapaGrupa.get(grupaPitanja.getNazivGrupe()).getPitanjaGrupe();
                        loadPlayActivity();
                    }
                });
            }else {
                TextView message = new TextView(this);
                String tmp = "Answer " + (pragoviOtkljucavanja.get(grupaPitanja.getNazivGrupe())-brojOdgovorenihPitanja) + " questions to unlock";
                message.setText(tmp);
                message.setTextColor(Color.BLACK);
                message.setLayoutParams(new ViewGroup.LayoutParams(
                        screenWidth-tvSlikaGrupe.getLayoutParams().width,
                        screenWidth/8));
                message.setGravity(Gravity.TOP);
                message.setTextAppearance(android.R.style.TextAppearance_Small);
                gornjiLinearLayout.addView(message);
            }
            newLinearLayout.setBackgroundResource(R.drawable.z_buttonstroke);
            layoutGlavniMeni.addView(newLinearLayout);
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private boolean daLiPostojiGrupa(String naziv){
        try {
            return mapaGrupa.containsKey(naziv);
        }catch (Exception e){
            printErrorMessage(e.getMessage());
            return false;
        }
    }

    public static int getImageIDFromName(String imageName) {
        try {
            int imageID;
            if (imageName == null || imageName.equalsIgnoreCase("")) {
                return -555;
            }
            @SuppressWarnings("rawtypes")
            Class res = R.drawable.class;
            Field field = res.getField(imageName);
            imageID = field.getInt(null);
            return imageID;
        } catch (Exception e){
            printErrorMessage(e.getMessage());
            return 0;
        }
    }

    private void upisiUmapuOdgovorenihPitanja(Pitanje pitanje){
        try {
            String key = pitanje.getNazivGrupe() + "" + pitanje.getId();
            if (!odgovorenaPitanja.contains(key)) {
                odgovorenaPitanja.add(key);

                if (brojOdgovorenihPitanjaPoGrupi.containsKey(pitanje.getNazivGrupe())) {
                    brojOdgovorenihPitanjaPoGrupi.put(pitanje.getNazivGrupe(), brojOdgovorenihPitanjaPoGrupi.get(pitanje.getNazivGrupe()) + 1);
                } else {
                    brojOdgovorenihPitanjaPoGrupi.put(pitanje.getNazivGrupe(), 1);
                }
                writeSharedPref();
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }
//todo

    private void showRateDialog(){
        try{
            if(odgovorenaPitanja.size()==42 && !rateDialogShowed) {
                rateDialogShowed = true;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.rate_dialog, null);

                TextView textViewRateOkButton = mView.findViewById(R.id.textViewRateOkButton);
                TextView textViewRateNoButton = mView.findViewById(R.id.textViewRateNoButton);

                mBuilder.setView(mView);
                dialog = mBuilder.create();

                textViewRateOkButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.mdquizgames.logoquiz.car.brands")));
                            dialog.dismiss();
                        } catch (Exception e) {
                            dialog.dismiss();
                        }
                    }
                });

                textViewRateNoButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                writeSharedPref();
                dialog.show();
                mFBAnalytics.logEvent("RateDialog_SHOW", params);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void showInfoDialog(String info){
        try {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.info_dialog, null);

            TextView textViewInfoOkButton = mView.findViewById(R.id.textViewInfoOkButton);
            TextView textViewInfoMessage = mView.findViewById(R.id.textViewInfoMessage);

            textViewInfoMessage.setText(info);

            mBuilder.setView(mView);
            dialog = mBuilder.create();

            textViewInfoOkButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    loadMenuActivity();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void showResultDialog(final boolean taskCorrect){
        try {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.result_dialog, null);

            buttonOK = mView.findViewById(R.id.textViewIbutton);
            titleTextView = mView.findViewById(R.id.textViewItitle);
            messageTextView = mView.findViewById(R.id.textViewImessage);
            ImageView pictureImageView = mView.findViewById(R.id.imageViewIpicture);

            mBuilder.setView(mView);
            dialog = mBuilder.create();
            dialog.setCancelable(false);

            buttonOK.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (currentQuestionIndex + 1 <= listaPitanja.size()) {
                        if (taskCorrect) {
                            upisiUmapuOdgovorenihPitanja(trenutnoPitanje);
                            //currentQuestionIndex++;
                            popuniOdgovorenoPitanje();

                            showInterstitialAd();
                        }
                    }

                    dialog.dismiss();
                }
            });

            if (taskCorrect) {
                titleTextView.setText(R.string.correct);
                messageTextView.setText(R.string.correct_message);
                pictureImageView.setBackgroundResource(getImageIDFromName(listaPitanja.get(currentQuestionIndex).getOriginalnaSlika()));
                fitImageViewDimensions(pictureImageView, getImageIDFromName(listaPitanja.get(currentQuestionIndex).getOriginalnaSlika()));
            }
            dialog.show();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void showGetHintsDialog(){
        try {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.get_hints_dialog, null);

            // getHintsBonus = mView.findViewById(R.id.getHintsBonus);
            getHintsDailyBonus = mView.findViewById(R.id.getHintsDailyBonus);
            TextView getHintsWatchVideo = mView.findViewById(R.id.getHintsWatchVideo);
            TextView getHintsClose = mView.findViewById(R.id.getHintsClose);
            hintTemplateView = mView.findViewById(R.id.hint_template);

            mBuilder.setView(mView);
            dialog = mBuilder.create();
            dialog.setCancelable(false);

            getHintsClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            getHintsWatchVideo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showRewardVideoAd();
                    dialog.dismiss();
                }
            });
            getHintsDailyBonus.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    updateHints(+30);
                    dailyBonus = getCurrentDate();
                    writeSharedPref();
                    getHintsDailyBonus.setClickable(false);
                    updateBonusString();
                    if(imageViewPlayHintPicture!=null){
                        imageViewPlayHintPicture.setImageResource(R.drawable.z_sijalica);
                    }
                    if(imageViewMenuHintPicture!=null){
                        imageViewMenuHintPicture.setImageResource(R.drawable.z_sijalica);
                    }
                }
            });

            if(dailyBonus()){
                getHintsDailyBonus.setClickable(true);
            }else{
                getHintsDailyBonus.setClickable(false);
                updateBonusString();
            }

            if(mRewardedVideoAd!=null && mRewardedVideoAd.isLoaded()){
                getHintsWatchVideo.setAlpha(1.0f);
                getHintsWatchVideo.setClickable(true);
            }else{
                getHintsWatchVideo.setAlpha(0.2f);
                getHintsWatchVideo.setClickable(false);
            }
            getHintsWatchVideo.setVisibility(View.VISIBLE);

            showNativeAd(hintTemplateView);
            dialog.show();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void showUseHintsDialog(){
        try {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
            View mView = getLayoutInflater().inflate(R.layout.use_hints_dialog, null);

            TextView useHintsTitle = mView.findViewById(R.id.useHintsTitle);
            TextView useHintsHelpRemoveExtraLetters = mView.findViewById(R.id.useHintsHelpRemoveExtraLetters);
            TextView useHintsHelpSolve = mView.findViewById(R.id.useHintsHelpSolve);
            TextView useHintsWatchVideo = mView.findViewById(R.id.useHintsWatchVideo);
            TextView useHintsClose = mView.findViewById(R.id.useHintsClose);
            useHintTemplate = mView.findViewById(R.id.usehint_template);

            mBuilder.setView(mView);
            dialog = mBuilder.create();
            dialog.setCancelable(false);

            useHintsClose.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            useHintsHelpRemoveExtraLetters.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    removeExtraLetters();
                    dialog.dismiss();
                }
            });
            useHintsHelpSolve.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    solveQuestion();
                    dialog.dismiss();
                }
            });
            useHintsWatchVideo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showRewardVideoAd();
                    dialog.dismiss();
                }
            });

            if(mRewardedVideoAd!=null && mRewardedVideoAd.isLoaded()){
                useHintsWatchVideo.setAlpha(1.0f);
                useHintsWatchVideo.setClickable(true);
            }else{
                useHintsWatchVideo.setAlpha(0.2f);
                useHintsWatchVideo.setClickable(false);
            }
            useHintsWatchVideo.setVisibility(View.VISIBLE);

            if(canIupdateHints(-20)){
                useHintsHelpRemoveExtraLetters.setAlpha(1.0f);
                useHintsHelpRemoveExtraLetters.setClickable(true);
                useHintsHelpSolve.setAlpha(1.0f);
                useHintsHelpSolve.setClickable(true);
            }else if(canIupdateHints(-10)){
                useHintsHelpRemoveExtraLetters.setAlpha(1.0f);
                useHintsHelpRemoveExtraLetters.setClickable(true);
                useHintsHelpSolve.setAlpha(0.2f);
                useHintsHelpSolve.setClickable(false);
            }else{
                useHintsHelpRemoveExtraLetters.setAlpha(0.2f);
                useHintsHelpRemoveExtraLetters.setClickable(false);
                useHintsHelpSolve.setAlpha(0.2f);
                useHintsHelpSolve.setClickable(false);
            }

            String tmp1 = "You have " + playerHints + " hints";
            useHintsTitle.setText(tmp1);

            showNativeAd(useHintTemplate);

            dialog.show();
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    public void writeSharedPref() {
        try {
            sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String jsonText = gson.toJson(odgovorenaPitanja);
            editor.putString("key", jsonText);
            editor.putInt("playerHints", playerHints);
            editor.putInt("playerPoints", playerPoints);
            editor.putString("dailyBonus", dailyBonus);
            editor.putString("weekendBonus", weekendBonus);
            editor.putBoolean("rateDialogShowed", rateDialogShowed);

            for (Map.Entry<String, Integer> entry : brojOdgovorenihPitanjaPoGrupi.entrySet()) {
                editor.putInt(entry.getKey(), entry.getValue());
            }
            editor.apply();
        } catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    public void readSharePref() {
        try {
            sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
            Gson gson = new Gson();
            String jsonText = sharedPreferences.getString("key", null);
            String[] text = gson.fromJson(jsonText, String[].class);

            if(text!=null) {
                Collections.addAll(odgovorenaPitanja, text);
            }
            String tempKey;
            int tempValue;

            for(int j=0; j<listaNazivaGrupa.size(); j++){
                tempKey = listaNazivaGrupa.get(j);
                tempValue = sharedPreferences.getInt(tempKey, 0);
                brojOdgovorenihPitanjaPoGrupi.put(tempKey, tempValue);
            }
            playerHints = sharedPreferences.getInt("playerHints", 50);
            playerPoints = sharedPreferences.getInt("playerPoints", 0);
            dailyBonus = sharedPreferences.getString("dailyBonus", "");
            weekendBonus = sharedPreferences.getString("weekendBonus", "");
            rateDialogShowed = sharedPreferences.getBoolean("rateDialogShowed", false);
        } catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private static void printErrorMessage(String message){
        Log.println(Log.INFO, TAG, message);
        FirebaseCrashlytics.getInstance().log(message);
    }

    private void printMessage(String message){
        Log.println(Log.INFO, TAG, message);
    }

    public boolean testNetwork() {
        try {
            ConnectivityManager cm = (ConnectivityManager) MainActivity.this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            boolean networkType = false;
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    networkType = true;
                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    networkType = true;
                }
            }
            return networkType;
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
            return false;
        }
    }

    private String getCurrentDate(){
        try {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return "" + year + "" + month + "" + day;
        }catch (Exception e) {
            printErrorMessage(e.getMessage());
            return "";
        }
    }

    private boolean dailyBonus(){
        try {
            return !getCurrentDate().equalsIgnoreCase(dailyBonus);
        }catch (Exception e){
            printErrorMessage(e.getMessage());
            return false;
        }
    }

    private void updateBonusString(){
        try{
            Calendar future = Calendar.getInstance(); //future time
            // future.add(Calendar.DAY_OF_MONTH, 1);
            future.add(Calendar.DAY_OF_MONTH, 1);
            future.set(Calendar.HOUR_OF_DAY,0); // Set hours to 8'O clock
            future.set(Calendar.MINUTE,0);
            Calendar now = Calendar.getInstance(); //get current time
            long hoursDiff = (future.getTimeInMillis() - now.getTimeInMillis())/(60 * 60 * 1000);
            long minDiff = ((future.getTimeInMillis() - now.getTimeInMillis())/(60 * 1000))-(hoursDiff*60);

            String tmp = "Daily bonus +30\nNext in " + hoursDiff + "h and " + minDiff + "m";
            getHintsDailyBonus.setText(tmp);

        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void loadInterstitialAd() {
        try {
            if (mInterstitialAd != null && !mInterstitialAd.isLoaded()) {

                Bundle extras = new Bundle();
                ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
                if (consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
                    extras.putString("npa", "1");
                }

                mInterstitialAd.loadAd(new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build());
            }
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private void showInterstitialAd() {
        try {
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                if((odgovorenaPitanja.size()%5)==0) {
                    mInterstitialAd.show();
                    mFBAnalytics.logEvent("Interstitial_SHOW", params);
                }
            }
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private void interstitialAdInit() {
        try {
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.adMobInterstitialID));

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {

                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdClosed() {
                    loadInterstitialAd();
                }
            });
            loadInterstitialAd();
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private void consent() {
        try {
            ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
            consentInformation.requestConsentInfoUpdate(new String[]{getString(R.string.ADMOB_PUBLISHER_ID)}, new ConsentInfoUpdateListener() {
                @Override
                public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                    // User's consent status successfully updated. Display the consent form if Consent Status is UNKNOWN
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        displayConsentForm();
                    }
                }

                @Override
                public void onFailedToUpdateConsentInfo(String errorDescription) {

                }
            });
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private void displayConsentForm() {
        try {
            consentForm = new ConsentForm.Builder(MainActivity.this, getAppsPrivacyPolicy())
                    .withListener(new ConsentFormListener() {
                        @Override
                        public void onConsentFormLoaded() {
                            // Display Consent Form When Its Loaded
                            consentForm.show();
                        }

                        @Override
                        public void onConsentFormOpened() {
                        }

                        @Override
                        public void onConsentFormClosed(
                                ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                            // Consent form is closed. From this method you can decided to display PERSONLIZED ads or NON-PERSONALIZED ads based 				on consentStatus.
                        }

                        @Override
                        public void onConsentFormError(String errorDescription) {

                        }
                    })
                    .withPersonalizedAdsOption()
                    .withNonPersonalizedAdsOption()
                    .build();
            consentForm.load();
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private URL getAppsPrivacyPolicy() {
        URL mUrl;
        mUrl = null;
        try {
            //todo
            mUrl = new URL("https://carbrandslogoquiz.neocities.org/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            printErrorMessage(e.getMessage());
        }
        return mUrl;
    }

    private void loadAdMobAds() {
        loadRewardedVideoAd();
        loadInterstitialAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        Toast.makeText(getApplicationContext(), "You receive 100 hints.", Toast.LENGTH_LONG).show();
        updateHints(100);
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    private void loadRewardedVideoAd() {
        try {
            if (mRewardedVideoAd != null && !mRewardedVideoAd.isLoaded()) {
                Bundle extras = new Bundle();
                ConsentInformation consentInformation = ConsentInformation.getInstance(MainActivity.this);
                if (consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
                    extras.putString("npa", "1");
                }
                mRewardedVideoAd.loadAd(getString(R.string.adMobRewardVideoID),
                        new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build());
            }
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private void showRewardVideoAd() {
        try {
            if (mRewardedVideoAd != null && mRewardedVideoAd.isLoaded()) {
                mRewardedVideoAd.show();
                mFBAnalytics.logEvent("RewardVideo_SHOW", params);
            }
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private void rewardVideoAdInit() {
        try {
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener((RewardedVideoAdListener) this);
            loadRewardedVideoAd();
        } catch (Exception e) {
            printErrorMessage(e.getMessage());
        }
    }

    private boolean canIupdateHints(int number){
        try {
            return playerHints + number >= 0;
        }catch (Exception e) {
            printErrorMessage(e.getMessage());
            return false;
        }
    }

    public void fillDataLinkoviFromXML() {
        try {

            Linkovi linkoviObj = new Linkovi();
            AssetManager assetManager = getAssets();
            InputStream inputStream;
            inputStream = assetManager.open("linkovi.xls");
            Workbook workbook = Workbook.getWorkbook(inputStream);
            Sheet sheet = workbook.getSheet(0);
            int row = sheet.getRows();
            int col = sheet.getColumns();

            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {

                    Cell cell = sheet.getCell(j, i);
                    if (j == 0) {
                        // Kolona A - NASLOV
                        linkoviObj.setNaslov(cell.getContents());
                    } else if (j == 1) {
                        // Kolona B - FOTOGRAFIJA
                        linkoviObj.setFotografija(cell.getContents());
                    } else if (j == 2) {
                        // Kolona C - OPIS
                        linkoviObj.setOpis(cell.getContents());
                    } else if (j == 3) {
                        // Kolona D - URL
                        linkoviObj.setUrl(cell.getContents());
                    }
                }

                listaLinkova.add(linkoviObj);
                linkoviObj = new Linkovi();
            }
        } catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void kreirajLinkove(){
        try {

            Linkovi tempLink;

            for(int i=0; i<listaLinkova.size(); i++) {

                tempLink = listaLinkova.get(i);

                LinearLayout newLinearLayout = new LinearLayout(this);
                newLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                newLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

                TextView tvSlikaLinka = new TextView(this);
                tvSlikaLinka.setBackgroundResource(getImageIDFromName(tempLink.getFotografija()));
                tvSlikaLinka.setText("");
                LinearLayout.LayoutParams parametri = new LinearLayout.LayoutParams(screenWidth / 4, screenWidth / 4);
                parametri.setMargins(30,30,30,30);
                tvSlikaLinka.setLayoutParams(parametri);
                newLinearLayout.addView(tvSlikaLinka);

                LinearLayout unutrasnjiLinearLayout = new LinearLayout(this);
                unutrasnjiLinearLayout.setLayoutParams(new LinearLayout.LayoutParams((screenWidth * 3) / 4, ViewGroup.LayoutParams.WRAP_CONTENT));
                unutrasnjiLinearLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout gornjiLinearLayout = new LinearLayout(this);
                gornjiLinearLayout.setLayoutParams(new LinearLayout.LayoutParams((screenWidth * 3) / 4, screenWidth / 4));
                gornjiLinearLayout.setOrientation(LinearLayout.VERTICAL);
                unutrasnjiLinearLayout.addView(gornjiLinearLayout);

                newLinearLayout.addView(unutrasnjiLinearLayout);

                TextView tvNazivLinka = new TextView(this);
                tvNazivLinka.setText(tempLink.getNaslov());
                tvNazivLinka.setTextColor(Color.BLACK);
                tvNazivLinka.setLayoutParams(new ViewGroup.LayoutParams(screenWidth - tvSlikaLinka.getLayoutParams().width, screenWidth / 8));
                tvNazivLinka.setGravity(Gravity.BOTTOM);
                tvNazivLinka.setTextAppearance(android.R.style.TextAppearance_Large);
                gornjiLinearLayout.addView(tvNazivLinka);

                TextView tvOpis = new TextView(this);
                tvOpis.setText(tempLink.getOpis());
                tvOpis.setTextColor(Color.BLACK);
                tvOpis.setLayoutParams(new ViewGroup.LayoutParams(screenWidth - tvSlikaLinka.getLayoutParams().width, screenWidth / 8));
                tvOpis.setGravity(Gravity.TOP);
                tvOpis.setTextAppearance(android.R.style.TextAppearance_Medium);
                gornjiLinearLayout.addView(tvOpis);
                newLinearLayout.setBackgroundResource(R.drawable.z_buttonstroke);

                final Linkovi finalTempLink = tempLink;
                newLinearLayout.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(finalTempLink.getUrl())));
                    }
                });

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                layoutParams.setMargins(10, 50, 10, 50);

                layoutLinkovi.addView(newLinearLayout, layoutParams);
            }
        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

    private void loadLinkoviActivity(){
        try {

            setContentView(R.layout.activity_linkovi);
            currentActivity = AppActivity.LINKOVI;
            currentQuestionIndex=0;

            layoutLinkovi = findViewById(R.id.layoutLinkovi);

            kreirajLinkove();

            layoutLinkovi.setGravity(Gravity.BOTTOM);

        }catch (Exception e){
            printErrorMessage(e.getMessage());
        }
    }

}