package com.example.tpbreakout

import ParticuleView
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class Menu : AppCompatActivity(), OnClickListener
{
    private lateinit var linearLayout: LinearLayout;
    private lateinit var playButton: Button;
    private lateinit var quitButton: Button;

    private var idButtonPlay: Int = -1;
    private var idButtonQuit: Int = -1;



    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState);

        //cree le linear layouyt
        linearLayout = LinearLayout(this);
        linearLayout.orientation = LinearLayout.VERTICAL;

        //set le bouton pour lancer le jeu
        playButton = Button(this);
        playButton.text = "Play";
        playButton.setOnClickListener(this);
        playButton.id = View.generateViewId();
        idButtonPlay = playButton.id;

        //set le bouton pour quitter l'application
        quitButton = Button(this);
        quitButton.text = "Quit";
        quitButton.setOnClickListener(this);
        idButtonQuit = quitButton.id;

        //ajoute les bouton dans la linear layout
        linearLayout.addView(playButton);
        linearLayout.addView(quitButton);

        setContentView(linearLayout);
    }

    override fun onClick(p0: View?)
    {
        //switch pour le id du bouton
        when(p0?.id)
        {
            //si bouton play lance l'activity breakout
            idButtonPlay ->
            {
                val intent = Intent(this, Breakout::class.java);
                startActivity(intent);
            }
            //si bouton quit l'activity, la metode finish occasionnait un bug
            idButtonQuit ->
            {
                val intent = Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP;
                startActivity(intent);
            }
        }
    }
}