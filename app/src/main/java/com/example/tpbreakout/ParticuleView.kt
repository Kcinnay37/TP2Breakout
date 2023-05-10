import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.view.View
import kotlin.random.Random
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder

class ParticuleView(context: Context) : View(context) {

    private val animations = mutableListOf<Animation>();
    private val paint = Paint();

    //contient tout les information d'une particule
    private class Particle(x: Float, y: Float)
    {
        private val size = Random.nextInt(10, 50);
        private val speedX = Random.nextFloat() * 6 - 3;
        private val speedY = Random.nextFloat() * 6 - 3;

        val color = Color.argb(255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256));
        val rect = RectF(x, y, x + size, y + size);

        //effectu un deplacement de la particule
        fun update()
        {
            rect.offset(speedX, speedY);
        }
    }

    //contien une list de particle qui vont faire une effet de particles
    private class Animation(val particles: MutableList<Particle>);

    override fun onDraw(canvas: Canvas)
    {
        super.onDraw(canvas);

        //pour tout les animations de particules
        for (animation in animations)
        {
            //pour tout les particule dans l'animation, dessine la particle dans le canvas et update sont deplacement
            for (particule in animation.particles)
            {
                paint.color = particule.color;
                canvas.drawRect(particule.rect, paint);
                particule.update();
            }
        }

        postInvalidateOnAnimation();
    }

    //ajoute une animation de particule à l'emplacement entrer pour la duration X
    fun playParticulAt(x: Float, y: Float, duration: Float)
    {
        //cree tout les particule et l'ajoute dans une animation
        val particules = mutableListOf<Particle>();
        for (i in 0 until 50)
        {
            particules.add(Particle(x, y));
        }
        val animation = Animation(particules);
        animations.add(animation);

        // Convertir la durée en millisecondes et la passer au Handler
        val durationInMillis = (duration * 1000).toLong();

        // Supprimer l'animation après la durée spécifiée
        Handler(Looper.getMainLooper()).postDelayed({
            animations.remove(animation);
            invalidate();
        }, durationInMillis);

        invalidate();
    }
}