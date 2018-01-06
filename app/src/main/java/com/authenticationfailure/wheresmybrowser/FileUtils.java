/**
 *
 * Where's My Browser
 *
 * Copyright (C) 2017, 2018	   David Turco
 *
 * This program can be distributed under the terms of the GNU GPL.
 * See the file COPYING.
 *
 * WARNING: This code is VULNERABLE-BY-DESIGN and it is intended as a learning tool
 *          DO NOT USE THIS CODE IN YOUR PROJECTS!!!
 *
 */

package com.authenticationfailure.wheresmybrowser;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {
    public static String readAssetToString(Context context, String assetPath) throws IOException {
        InputStream iStream = context.getAssets().open(assetPath);
        String assetString = readInputStreamToString(iStream);
        iStream.close();
        return assetString;
    }

    public static String readInputStreamToString(InputStream iStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(iStream));
        final StringBuilder sBuilder = new StringBuilder();

        boolean done = false;
        String line;
        while (!done) {
            line = reader.readLine();
            if (line != null) {
                sBuilder.append(line + "\n");
            } else {
                done = true;
            }
        }
        return sBuilder.toString();
    }

    public static void writeFileFromString(String filePath,
                                           String fileContents,
                                           boolean overwriteIfExists) throws IOException {

        File file = new File(filePath);
        if (file.exists() && !overwriteIfExists) {
            Log.i("FILE_CREATE","File" + file.getAbsolutePath() + " already exists");
        } else {
            FileOutputStream fOut = new FileOutputStream(file);
            fOut.write(fileContents.getBytes());
            fOut.close();
        }
    }

    public static void copyAssetToFile(Context context, String assetPath, String filePath) throws IOException {
        String assetContents = readAssetToString(context,assetPath);
        writeFileFromString(filePath, assetContents, true);
    }
}
