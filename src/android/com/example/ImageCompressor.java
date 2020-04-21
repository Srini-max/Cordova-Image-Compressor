
package com.cordova.imgcompressor;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.apache.cordova.PluginResult.Status;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;
import android.net.Uri;
import android.content.ContentUris;
import android.graphics.Bitmap;

import java.util.Date;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import android.os.Environment;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class ImageCompressor extends CordovaPlugin {
  private static final String TAG = "ImageCompressor";
      private static final String GET_COMPRESS_IMAGE = "getComprBase64";
  private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
   
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);

    Log.id(TAG, "Initializing ImageCompressor Plugin");
  }
    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        this.callback = callbackContext;

        // These actions require the key to be already set
        if (this.isInitialised()) {
            this.callback.error("ImageCompressor not initialised.");
        }

        if (action.equals(GET_COMPRESS_IMAGE)) {
            this.getComprBase64(args.optString(0),callbackContext);
        } else {
            Log.i(TAG, "This action doesn't exist");
            return false;
        }
        return true;
    }
  @Override
  
    public void getComprBase64(String base64,  CallbackContext callbackContext) {
         try {
        final Bitmap bitmapRes = ConvertBasetoBit(base64);
        Log.i(TAG,"bitmapRes ", new StringBuilder().append(bitmapRes.getHeight()).toString());
        final String compressedBase64 = BitMapToString(bitmapRes);
          if (callbackContext != null) {
                Log.i(TAG, "Sucess Plugin base64 compressed Image" + compressedBase64);
                callbackContext.success(compressedBase64);
                callbackContext = null;
            }
         } catch (JSONException ex) {
                callbackContext.error(ex.toString());
                Log.i(TAG, "failed to convert base64 :-", ex);
         }
       // return compressedBase64;
    }
    
    public static Bitmap ConvertBasetoBit(final String base64) {
     
        final String encodedImage = base64;
        final byte[] decodedString = Base64.decode(encodedImage, 0);
        final Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        Log.i(TAG,"Bitmap with args==>", new StringBuilder().append(decodedByte).toString());
        return decodedByte;
    }
    
    public static String BitMapToString(final Bitmap bitmap) {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, (OutputStream)baos);
        final byte[] b = baos.toByteArray();
        final String temp = new String(Base64.encode(b, 2));
        Log.i(TAG,"temp Bsde64==>", temp.toString());
        return temp;
    }
  
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    if (action.equals("compress")) {
      JSONObject jsonObject = args.getJSONObject(0);
      String originalFileUri = jsonObject.getString("uri");
      String destinationDirectoryPath = jsonObject.getString("folderName");
      String compressedFileName = jsonObject.getString("fileName");
      int quality = jsonObject.getInt("quality");
      int width = jsonObject.getInt("width");
      int height = jsonObject.getInt("height");

      File folder = new File(Environment.getExternalStorageDirectory() + "/" + destinationDirectoryPath);
      boolean success = true;
      if (!folder.exists()) {
        success = folder.mkdir();
      }
      
      return compressToFile(callbackContext, originalFileUri, folder.getPath() + File.separator + compressedFileName, width, height, this.compressFormat, quality);
    } else {
      return false;  
    }
  }

  public boolean compressToFile(CallbackContext callbackContext, String originalFileUri, String destinationFileUri, int width, int height, Bitmap.CompressFormat compressFormat, int quality) {
      boolean returnval = false;
      PluginResult result = null;
      try {
        String compressedFilePath = ImageUtil.compressImage(originalFileUri, width, height, compressFormat, quality, destinationFileUri);
        result = new PluginResult(PluginResult.Status.OK, (compressedFilePath));
        returnval = true;
      } catch (IOException e) {
        result = new PluginResult(PluginResult.Status.ERROR, (e.getMessage()));
      }
      callbackContext.sendPluginResult(result);
      return returnval;
  }
}
