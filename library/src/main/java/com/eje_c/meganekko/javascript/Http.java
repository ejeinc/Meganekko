/*
 * Copyright 2016 eje inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eje_c.meganekko.javascript;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eje_c.meganekko.MeganekkoApp;

/**
 * JavaScript <code>http</code> module.
 * <pre>
 *     // Get string
 *     http.get('http://yourhost.com/data.json', function(err, str) {
 *
 *        // Error handling
 *        if (err) {
 *            err.printStackTrace();
 *            return;
 *        }
 *
 *        // do something with data
 *        var data = JSON.parse(str);
 *
 *     });
 *
 *     // Get bitmap image
 *     http.getImage('http://yourhost.com/data.json', function(err, image) {
 *
 *        // Error handling
 *        if (err) {
 *            err.printStackTrace();
 *            return;
 *        }
 *
 *        // do something with image
 *        var obj = SceneObject.from(image);
 *        obj.position(new Vector3f(0, 0, -3));
 *        scene.add(obj);
 *
 *     });
 * </pre>
 */
public class Http {

    public interface JSCallback {
        void call(Exception err, Object result);
    }

    private final MeganekkoApp mApp;
    private RequestQueue mRequestQueue;

    public Http(MeganekkoApp app) {
        this.mApp = app;
        mRequestQueue = Volley.newRequestQueue(app.getContext());
    }

    private void reject(final JSCallback callback, final VolleyError error) {
        mApp.runOnGlThread(new Runnable() {
            @Override
            public void run() {
                callback.call(error, null);
            }
        });
    }

    private void resolve(final JSCallback callback, final Object data) {
        mApp.runOnGlThread(new Runnable() {
            @Override
            public void run() {
                callback.call(null, data);
            }
        });
    }

    public void get(String url, final JSCallback callback) {
        mRequestQueue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                resolve(callback, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                reject(callback, error);
            }
        }));
    }

    public void getImage(String url, final JSCallback callback) {
        getImage(url, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ALPHA_8, callback);
    }

    public void getImage(String url, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config config, final JSCallback callback) {
        mRequestQueue.add(new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(final Bitmap response) {
                resolve(callback, response);
            }
        }, maxWidth, maxHeight, scaleType, config, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(final VolleyError error) {
                reject(callback, error);
            }
        }));
    }
}
