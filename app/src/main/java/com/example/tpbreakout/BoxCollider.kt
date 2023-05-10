package com.example.tpbreakout
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.max

data class Rect(var pX: Float, var pY: Float, var sX: Float, var sY: Float)

class BoxCollider
{
    public lateinit var rect: Rect

    init
    {
        rect = Rect(0f, 0f,0f, 0f);
    }

    //Ajuste la position du collider
    public fun UpdateCollider(rectangle: Rect)
    {
        rect.pX = rectangle.pX;
        rect.pY = rectangle.pY;
        rect.sX = rectangle.sX;
        rect.sY = rectangle.sY;
    }

    //Retourne la direction de la collsion des deux box collider, le offsetCollider sert pour le mouvement
    //de la ball et eviter certain bug
    public fun CheckColliderWith(boxCollider: BoxCollider, offsetCollider: Float): Array<Int> {
        val result = arrayOf(0, 0)

        if (rect.pX + rect.sX > boxCollider.rect.pX &&
            boxCollider.rect.pX + boxCollider.rect.sX > rect.pX &&
            rect.pY + rect.sY > boxCollider.rect.pY &&
            boxCollider.rect.pY + boxCollider.rect.sY > rect.pY)
        {
            val distanceX = (boxCollider.rect.pX + boxCollider.rect.sX / 2) - (rect.pX + rect.sX / 2)
            val distanceY = (boxCollider.rect.pY +  boxCollider.rect.sY / 2) - (rect.pY + rect.sY / 2)

            if(abs(distanceX) >= ((rect.sX + boxCollider.rect.sX) / 2) - offsetCollider)
            {
                if(distanceX < 0)
                {
                    result[0] = -1;
                }
                else
                {
                    result[0] = 1;
                }
            }
            if(abs(distanceY) >= ((rect.sY + boxCollider.rect.sY) / 2) - offsetCollider)
            {
                if(distanceY < 0)
                {
                    result[1] = -1;
                }
                else
                {
                    result[1] = 1;
                }
            }
        }

        return result
    }

    //regarde la collision avec un point
    public fun CheckColliderWith(pX: Float, pY:Float): Boolean
    {
        if(pX >= rect.pX && pX <= rect.pX + rect.sX && pY >= rect.pY && pY <= rect.pY + rect.sY)
        {
            return true;
        }
        return false;
    }
}