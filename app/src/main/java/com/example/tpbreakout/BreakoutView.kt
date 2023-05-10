package com.example.tpbreakout

import ParticuleView
import android.content.Context
import android.graphics.Canvas
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import java.util.Random

class BreakoutView(context: Context) : View(context), SensorEventListener
{
    private lateinit var paddle: Paddle;
    private lateinit var ball: Ball;
    private lateinit var brick: Brick;

    private var screenWidth = 0;
    private var screenHeight = 0;

    private lateinit var sensorManager: SensorManager;
    private lateinit var accelerometer: Sensor;

    private var deltaTime: Float = 0f;
    private var lastUpdateTime: Long = 0;

    private var screenInit:Boolean = false;
    private var drawBall:Boolean = true;

    init
    {
        //initialise tout les composant de la view
        paddle = Paddle();
        ball = Ball();
        brick = Brick();

        //initailise le sensor manager
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)

        screenInit = false;
        drawBall = true;
    }

    //lorsque l'ecran est toucher appel le onTouchEvent de la paddle
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        if(screenInit)
        {
            paddle.onTouchEvent(event);
        }

        return true;
    }

    //lorsque l'ecran a changer, garde en memoir la grosseur appel la metode sur les composant et
    //dit que la screen est init au reste du code
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;

        paddle.onSizeChanged(w, h);
        ball.onSizeChanged(w, h);
        brick.onSizeChanged(w, h);

        screenInit = true;

        lastUpdateTime = System.currentTimeMillis();
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int)
    {
        super.onDetachedFromWindow();
    }

    //desenregistre le sensor manager de la view
    override fun onDetachedFromWindow()
    {
        super.onDetachedFromWindow()
        sensorManager.unregisterListener(this)
    }

    //lorsque le sensor de l'ecran a changer appel celui du paddle
    override fun onSensorChanged(event: SensorEvent)
    {
        if(screenInit)
        {
            paddle.onSensorChanged(event);
        }
    }

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas);

        //calcule le delta time
        val currentTime = System.currentTimeMillis();
        deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        //si la screen est init update les collider et va tout dessiner
        if(screenInit)
        {
            UpdateCollider();

            paddle.onDraw(canvas, deltaTime);
            if(drawBall)
            {
                ball.onDraw(canvas, deltaTime);
            }
            brick.onDraw(canvas);
        }

        invalidate();
    }

    //regarde les collision de la ball avec le reste des composant du jeu
    public fun UpdateCollider()
    {
        ball.CheckScreen();
        ball.CheckColliderWithPaddle(paddle.GetCollider());
        ball.CheckColliderWithBrick(brick.GetBricks());
    }

    //retourne si le jeu est win si oui fait jouer des particule partout dans l'écran
    public fun CheckWin() : Boolean
    {
        if(brick.GetBricks().isEmpty() && screenInit)
        {
            val random = Random();

            for(i in 0 until 50)
            {
                val posX = random.nextFloat() * screenWidth;
                val posY = random.nextFloat() * screenHeight;

                Breakout.instance.PlayParticle(posX, posY, 20f);
            }
            return true;
        }

        return false;
    }

    //retourne si le jeu est lose si oui fait jouer des particule dans le bas de l'ecran
    public fun CheckLose() : Boolean
    {
        if((ball.CheckOnGround() || brick.GetLowestBrickPosY() >= paddle.GetPosY()) && screenInit)
        {
            val random = Random();

            for(i in 0 until 30)
            {
                val posX = random.nextFloat() * screenWidth;
                val posY = screenHeight - 2f;

                Breakout.instance.PlayParticle(posX, posY, 20f);
            }

            drawBall = false;
            return true;
        }

        return false;
    }

    //update le composant brick pour le spawn d'une nouvelle rangé
    public fun UpdateSpawnBrick(dt:Float)
    {
        if(screenInit)
        {
            brick.UpdateSpawnBrick(dt);
        }
    }
}