#include <jni.h>
#include <string>
#include "qrcodegen.hpp"

using qrcodegen::QrCode;

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_tpbreakout_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

//retourn le bit map d'un codeQR
extern "C" JNIEXPORT jobject JNICALL
Java_com_example_tpbreakout_Breakout_generateQrCodeBitmap(JNIEnv *env, jobject /* this */, jstring url)
{
    //Get le string Java(URL) et le convertie en chaine de caractères C.
    const char *urlC = env->GetStringUTFChars(url, nullptr);
    //Genere le code QR a partir du URL
    const QrCode qr = QrCode::encodeText(urlC, QrCode::Ecc::MEDIUM);
    //Libere la chaine de caractere C
    env->ReleaseStringUTFChars(url, urlC);

    //prend la taille du code QR
    int qrSize = qr.getSize();

    //Get la classe Bitmap de Android
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    //Get la metode static createBitmap de la class Bitmap
    jmethodID createBitmapMethod = env->GetStaticMethodID(bitmapCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    //Get la class interne Config de Bitmap
    jclass configCls = env->FindClass("android/graphics/Bitmap$Config");
    //Get l'attribut static 'ARGB_8888' de la class Config
    jfieldID argb8888Field = env->GetStaticFieldID(configCls, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
    //Get l'objet 'ARGB_8888' de la classa Config
    jobject argb8888 = env->GetStaticObjectField(configCls, argb8888Field);
    //Crée un objet Bitmap de la taille du QR code avec la config 'ARGB_8888'
    jobject bitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapMethod, qrSize, qrSize, argb8888);

    //Get la metode setPixel de la class Bitmap.
    jmethodID setPixelMethod = env->GetMethodID(bitmapCls, "setPixel", "(III)V");
    //Set couleurs noire et blanche pour les pixels du QR code
    int black = 0xFF000000;
    int white = 0xFFFFFFFF;

    //pour tout la size du code QR
    for (int y = 0; y < qrSize; y++)
    {
        for (int x = 0; x < qrSize; x++)
        {
            //Si le pixel du code QR est actif met met noir sinon blanc
            int color = qr.getModule(x, y) ? black : white;
            //Set la couleur du pixel dans le Bitmap.
            env->CallVoidMethod(bitmap, setPixelMethod, x, y, color);
        }
    }

    //Retourn l'image bitmap
    return bitmap;
}