//package com.AhmedNTS;
//
//import android.app.Activity;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.widget.ImageView;
//
//import com.AhmedNTS.AsyncCall.AsyncCall;
//import com.AhmedNTS.AsyncCall.AsyncCallback;
//
////import org.joda.time.DateTime;
//
//import java.io.InputStream;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.TimeZone;
//
//
///**
// * Created by AhmedNTS on 2015-08-22.
// */
//public class Utilities
//{
//    public static String getDateNew(String value)
//    {
////        DateTime dt = new DateTime(value);
//
//        long date = System.currentTimeMillis(); //current android time in epoch
//        //Converts epoch to "dd/MM/yyyy HH:mm:ss" dateformat
//        String NormalDate = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(date));
//
//        return null;
//    }
//
//    public static String getDate(String jsonDate)
//    {
//        String finalDate = "";
//        try
//        {
//            String dateTicks = jsonDate.replace("/Date(", "");
//            dateTicks = dateTicks.replace(")/", "");
//
//            String[] data = new String[2];
//            if (dateTicks.contains("-"))
//            {
//                data = dateTicks.split("\\-");
//            }
//            else
//            {
//                data = dateTicks.split("\\+");
//            }
//            String utcString = data[0];
////            String offsetString = data[1];
////            int offsetHours = Integer.parseInt(offsetString);
////            offsetHours = offsetHours / 100;
////            int offsetMinutes = offsetHours % 100;
//            Long utcTicks = Long.parseLong(utcString);
//
//            Date utcDate = new Date(utcTicks);
//
////            int offSetTime = TimeZone.getDefault().getOffset(utcDate.getTime());
////            int manualOffSetTime = (3600 * offsetHours * 1000);// + (60 * offsetMinutes);
//
//            Date localDate = new Date((utcDate.getTime()));// + offSetTime));
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");// hh:mm:ss
//            finalDate = sdf.format(localDate);
////
////
////
////            Calendar cal = Calendar.getInstance();
////            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
////            cal.setTimeInMillis(utcTicks);
////            Long ticks = cal.getTimeInMillis();
////            cal.add(Calendar.HOUR, 2);
////            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
////            finalDate = sdf2.format(cal.getTime());
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return finalDate;
//
//
//        /*String finalDate = "";
//        try
//        {
//            String dateTicks = jsonDate.replace("/Date(", "");
//            dateTicks = dateTicks.replace(")/", "");
//            String[] data = dateTicks.split("\\+");
//
//            String utcString = data[0];
//            String offsetString = data[1];
//            int offsetHours = Integer.parseInt(offsetString);
//            offsetHours = offsetHours / 100;
////            int offsetMinutes = offsetHours % 100;
//            Long utcTicks = Long.parseLong(utcString);
//
//            Date utcDate = new Date(utcTicks);
//
//            int offSetTime = TimeZone.getDefault().getOffset(utcDate.getTime());
//            int manualOffSetTime = (3600 * offsetHours * 1000);// + (60 * offsetMinutes);
//
//            Date localDate = new Date((utcDate.getTime() + offSetTime));
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//            finalDate = sdf.format(localDate);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return finalDate;*/
//    }
//
//    public static String getTime(String jsonTime)
//    {
//        String hh = "", mm = "";
//
//        String initDate = jsonTime.replace("PT", "");
//        if (initDate.toLowerCase().contains(("H").toLowerCase()))
//        {
//            String[] initDate2 = initDate.split("H");
//            hh = initDate2[0];
//            if (initDate2[1].toLowerCase().contains(("M").toLowerCase()))
//                mm = initDate2[1].replace(("M").toLowerCase(), "");
//        }
//        else
//        {
//            hh = "00";
//
//            if (initDate.toLowerCase().contains(("M").toLowerCase()))
//            {
////                String[] initDate2 = initDate.split("M");
////                mm = initDate2[0];
//
//                mm = initDate.replace(("M").toLowerCase(), "");
//            }
//            else
//            {
//                mm = "00";
//            }
//        }
//
//        return hh + ":" + mm;
//    }
//
//
//    //        Calendar cal = Calendar.getInstance();
////        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
////        cal.setTimeInMillis(Long.parseLong(utc));
//////        cal.add(Calendar.HOUR, offsetInt);
////        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd mm:hh:ss");
////        String dob = sdf.format(cal.getTime());
//
//
//    //    public static Date convertTimeZone(Date date, TimeZone fromTimeZone, TimeZone toTimeZone)
////    {
////        long fromTimeZoneOffset = getTimeZoneUTCAndDSTOffset(date, fromTimeZone);
////        long toTimeZoneOffset = getTimeZoneUTCAndDSTOffset(date, toTimeZone);
////
////        return new Date(date.getTime() + (toTimeZoneOffset - fromTimeZoneOffset));
////    }
////
////    private static long getTimeZoneUTCAndDSTOffset(Date date, TimeZone timeZone)
////    {
////        long timeZoneDSTOffset = 0;
////        if(timeZone.inDaylightTime(date))
////        {
////            timeZoneDSTOffset = timeZone.getDSTSavings();
////        }
////
////        return timeZone.getRawOffset() + timeZoneDSTOffset;
////    }
//
//
////    public static String getDate(long ticks)//if you get UTC format only
////    {
////        Date localTime = new Date(ticks);
////        Date fromGmt = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
////        String format = "yyyy/MM/dd";
////        SimpleDateFormat sdf = new SimpleDateFormat(format);
////        return sdf.format(fromGmt);
////    }
//
//
//    public static void GetImage(final String imageFolder, final String imageName, final ImageView imageView)
//    {
//        AsyncCall customerImage = new AsyncCall(null);
//        customerImage.callback = new AsyncCallback()
//        {
//            Bitmap bitmap;
//
//            @Override
//            public void onStart()
//            {
//
//            }
//
//            @Override
//            public Boolean onProgress()
//            {
//                try
//                {
//                    String url = "http://192.30.163.67/eko_wcf/" + imageFolder + "/" + imageName + ".png";
//                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
//
//                    if (bitmap == null)
//                        return false;
//
//                    return true;
//                }
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//                return false;
//            }
//
//            @Override
//            public void onFinish(Boolean isSuccess)
//            {
//                if (isSuccess)
//                {
//                    imageView.setImageBitmap(bitmap);
//                }
//                else
//                {
////                    Toast.makeText(getActivity(), "Set you photo in Profile", Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//        customerImage.execute((Void) null);
//    }
//
//    public static boolean isOnline(Activity context)
//    {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager != null)
//        {
//            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
//            for (NetworkInfo ni : networkInfos)
//            {
//                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                    if (ni.isConnected())
//                        return true;
//                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                    if (ni.isConnected())
//                        return true;
//            }
//        }
//        return false;
//    }
//}

package com.vivantor.examples;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.vivantor.mediaplayer.UI.VAudioPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by AhmedNTS on 2015-08-22.
 */
public class Utils
{
	private static final String TAG = Utils.class.getSimpleName();

	public static String GenerateFilePath(String folderName, int type) throws NullPointerException
	{
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.
		if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
			return null;

		String filePath;

		File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), folderName);

		if (!mediaStorageDir.exists())
		{
			if (!mediaStorageDir.mkdirs())
			{
				Log.d(TAG, "failed to create directory");
				return null;
			}
		}

		String timeStamp = SimpleDateFormat.getDateTimeInstance().format(new Date());
		if (type == 1)
		{
			filePath = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
		}
		else if (type == 2)
		{
			filePath = mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4";
		}
		else if (type == 3)
		{
			filePath = mediaStorageDir.getPath() + File.separator + "AUD_" + timeStamp + ".3gp";
		}
		else
		{
			return null;
		}

		return filePath;
	}

	public static Bitmap drawableToBitmap(Drawable drawable)
	{
		Bitmap bitmap = null;

		if (drawable instanceof BitmapDrawable)
		{
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			if (bitmapDrawable.getBitmap() != null)
			{
				return bitmapDrawable.getBitmap();
			}
		}

		if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0)
		{
			bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
		}
		else
		{
			bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		}

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public static String saveBitmapToFile(String folderName, Bitmap bm)
	{
		String imagePath = Utils.GenerateFilePath(folderName, 1);
		if (imagePath == null) return null;

		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(new File(imagePath));
			bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		}
		catch (IOException e)
		{
			Log.e(TAG, e.getMessage());
			if (fos != null)
			{
				try
				{
					fos.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}

		return imagePath;
	}

	public static boolean isOnline(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null)
		{
//            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (NetworkInfo ni : networkInfos)
			{
//                boolean isWiFi = ni.getType() == ConnectivityManager.TYPE_WIFI;

				if (ni.getTypeName().equalsIgnoreCase("WIFI"))
					if (ni.isConnected())
						return true;
				if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
					if (ni.isConnected())
						return true;
			}
		}
		return false;
	}
}