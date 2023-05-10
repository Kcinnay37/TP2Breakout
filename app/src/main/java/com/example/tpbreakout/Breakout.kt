package com.example.tpbreakout

import ParticuleView
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog

class Breakout : AppCompatActivity()
{
    companion object
    {
        init
        {
            //load les metode c++
            System.loadLibrary("tpbreakout");
        }

        public lateinit var instance: Breakout;
    }

    //fonction du c++
    external fun generateQrCodeBitmap(url: String): Bitmap

    private lateinit var relativeLayout: RelativeLayout;
    private lateinit var breakoutView: BreakoutView;

    private val UPDATE_INTERVAL = 16L;
    private val handler = Handler(Looper.getMainLooper());

    private var win: Boolean = false;
    private var lose: Boolean = false;

    private var deltaTime: Float = 0f;
    private var lastUpdateTime: Long = 0;

    lateinit var particuleView: ParticuleView;

    init
    {
        instance = this;
    }

    //runnable pour la fonction Update de breakout
    private val updateRunnable = object : Runnable
    {
        override fun run()
        {
            Update();
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        //cree le ralative layout
        relativeLayout = RelativeLayout(this);
        relativeLayout.setBackgroundColor(Color.WHITE);

        //ajoute la view du jeu breakout ainsi que la view pour jouer des particules qui sont 2
        //view personnalisé
        breakoutView = BreakoutView(this);
        particuleView = ParticuleView(this);

        //ajoute les view au layout
        relativeLayout.addView(breakoutView);
        relativeLayout.addView(particuleView);

        setContentView(relativeLayout);
    }

    override fun onResume() {
        super.onResume()
        //commence le runnable du Update
        handler.post(updateRunnable);
    }

    override fun onPause() {
        super.onPause()
        //enleve le runnable du Update
        handler.removeCallbacks(updateRunnable);
    }

    private fun Update()
    {
        if(lose) return;

        //calcule le DT
        val currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        if(win) return;

        //regarde si le joueur a gagné
        if(breakoutView.CheckWin())
        {
            PlayWinDialogue();
            win = true;
        }

        //regarde si le joueur a perdu
        if(breakoutView.CheckLose())
        {
            PlayLoseDialogue();
            lose = true;
        }

        //si le jeu est encore actif update le temp pour spawn de nouvelle brick
        if(!lose && !win)
        {
            breakoutView.UpdateSpawnBrick(deltaTime);
        }
    }

    //fais jouer des particle avec la view particule perso
    public fun PlayParticle(posX: Float, posY: Float, time: Float)
    {
        particuleView?.playParticulAt(posX, posY, time);
    }

    //fais jouer le dialogue de win
    private fun PlayWinDialogue()
    {
        //va chercher une image du code qr en c++ a un lien specique
        val url = "https://www.youtube.com/watch?v=d-dW4dOb3ko";
        val qrCodeBitmap = generateQrCodeBitmap(url);

        //met l'image du code qr dans une image view
        val imageView = ImageView(this);
        imageView.setImageBitmap(qrCodeBitmap);

        //j'utilise le frameLayout pour bien controller la taille de l'amge
        val frameLayout = FrameLayout(this);
        val layoutParams = FrameLayout.LayoutParams(500, 500, Gravity.CENTER);
        frameLayout.addView(imageView, layoutParams);

        //cree le dialoge et ajout tout les compsosant necessaire avant de le faire jouer
        val builder = AlertDialog.Builder(this);
        builder.setView(frameLayout)
            .setMessage("You win baby! Do you want to play again?")
            .setCancelable(false)
            .setPositiveButton("Yessir!") { dialog, id ->
                //fait rejouer le jeu
                val intent = Intent(this, Breakout::class.java);
                startActivity(intent);
            }
            .setNegativeButton("Nop!") { dialog, id ->
                //retourne a l'activity menu
                val intent = Intent(this, Menu::class.java);
                startActivity(intent);
            }
        val alert = builder.create();
        alert.show();
    }

    //fais jouer le dialogue de lose
    private fun PlayLoseDialogue()
    {
        //va chercher une image du code qr en c++ a un lien specique
        val url = "https://www.youtube.com/watch?v=wEWF2xh5E8s";
        val qrCodeBitmap = generateQrCodeBitmap(url);

        //met l'image du code qr dans une image view
        val imageView = ImageView(this);
        imageView.setImageBitmap(qrCodeBitmap);

        //j'utilise le frameLayout pour bien controller la taille de l'image
        val frameLayout = FrameLayout(this);
        val layoutParams = FrameLayout.LayoutParams(500, 500, Gravity.CENTER);
        frameLayout.addView(imageView, layoutParams);

        //cree le dialoge et ajout tout les compsosant necessaire avant de le faire jouer
        val builder = AlertDialog.Builder(this);
        builder.setView(frameLayout)
            .setMessage("You lost big! Do you want to play again?")
            .setCancelable(false)
            .setPositiveButton("Yessir!") { dialog, id ->
                //fait rejouer le jeu
                val intent = Intent(this, Breakout::class.java);
                startActivity(intent);
            }
            .setNegativeButton("Nop!") { dialog, id ->
                //retourne a l'activity menu
                val intent = Intent(this, Menu::class.java);
                startActivity(intent);
            }
        val alert = builder.create()
        alert.show()
    }
}