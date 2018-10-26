package com.example.datvu.support;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.example.datvu.uschool.PostActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class SupportImage {
    private static Bitmap compressedImageFile;
    public static byte[] nenAnh(Uri imageUri, Context context) {
        File newImageFile = new File(imageUri.getPath());
        try {

            compressedImageFile = new Compressor(context)
                    .setMaxHeight(100)
                    .setMaxWidth(100)
                    .setQuality(1)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
