package com.example.tpbreakout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

data class BrickData(val rect: Rect, val collider: BoxCollider);

class Brick()
{
    private val paintBrick = Paint().apply { color = Color.GREEN }

    private lateinit var listBrick: MutableList<BrickData>;

    private var nbColumn: Int = 6;
    private var nbInitialLine: Int = 4;
    private var nbMaxLine: Int = 10;

    private var delaySpawnLine: Float = 40f;
    private var currTime: Float = 0f;

    private var offsetRatioBrick: Float = 100f;
    private var offsetRationScreen: Float = 30f;

    private var screenWidth = 0;
    private var screenHeight = 0;

    private var isFinish: Boolean = false;

    init
    {
        //initialise les valeur
        listBrick = mutableListOf<BrickData>();
    }

    //losrque la taille de l'ecran change garde en memoir les valeur et fait apparaitre les premiere
    //rangé de brick
    fun onSizeChanged(sW: Int, sH: Int)
    {
        screenWidth = sW;
        screenHeight = sH;

        for(i in 0..nbInitialLine)
        {
            AddLine();
        }
    }

    fun onDraw(canvas: Canvas)
    {
        //pour tout les brick deplace le canvas et va dessiner un rect
        for(brickData: BrickData in listBrick)
        {
            canvas.save();
            canvas.translate(brickData.rect.pX, brickData.rect.pY);
            canvas.scale(1f, 1f);
            canvas.drawRect(0f, 0f, brickData.rect.sX, brickData.rect.sY, paintBrick);
            canvas.restore();
        }
    }

    //ajoute une ranger de brick
    private fun AddLine()
    {
        var offsetScreen = screenWidth / offsetRationScreen;
        var offsetBrick = screenWidth / offsetRatioBrick;

        var distanceLine = screenWidth - (offsetScreen * 2);
        distanceLine -= offsetBrick * (nbColumn - 1);

        var sizeBrick = distanceLine / nbColumn;

        for(brickData: BrickData in listBrick)
        {
            brickData.rect.pY += 75f + offsetBrick;
            brickData.collider.UpdateCollider(brickData.rect);
        }

        for(i in 0..nbColumn - 1)
        {
            val rect = Rect(offsetScreen + (sizeBrick * i) + (offsetBrick * i), offsetScreen, sizeBrick, 75f);
            val collider = BoxCollider();
            collider.UpdateCollider(rect);
            val dataBrick = BrickData(rect, collider);
            listBrick.add(i, dataBrick);
        }
    }

    //update le timer pour le pawn de brick si celui a atteint le temp ajoute une ligne
    public fun UpdateSpawnBrick(dt: Float)
    {
        currTime += dt;
        if(currTime >= delaySpawnLine)
        {
            currTime = 0f;
            AddLine();
        }
    }

    public fun GetBricks() : MutableList<BrickData>
    {
        return listBrick;
    }

    //retourne la position la plus bas en y des bricks
    public fun GetLowestBrickPosY() : Float
    {
        if(listBrick.size > 0)
        {
            return listBrick[listBrick.size - 1].rect.pY + listBrick[listBrick.size - 1].rect.sY;
        }
        return 0.0f;
    }
}