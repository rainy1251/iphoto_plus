/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
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
package com.iphoto.plus.ptp.commands.eos;

import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;

import com.iphoto.plus.util.AppConfig;
import com.iphoto.plus.ptp.EosCamera;
import com.iphoto.plus.ptp.PtpCamera;
import com.iphoto.plus.ptp.PtpConstants;
import com.iphoto.plus.ptp.model.LiveViewData;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EosGetLiveViewPictureCommand extends EosCommand {

    private static final String TAG = EosGetLiveViewPictureCommand.class.getSimpleName();
    private static byte[] tmpStorage = new byte[0x4000];
    private final Options options;
    private LiveViewData data;

    public EosGetLiveViewPictureCommand(EosCamera camera, LiveViewData data) {
        super(camera);
        if (data == null) {
            this.data = new LiveViewData();
            this.data.histogram = ByteBuffer.allocate(1024 * 4);
            this.data.histogram.order(ByteOrder.LITTLE_ENDIAN);
        } else {
            this.data = data;
        }
        options = new Options();
        options.inBitmap = this.data.bitmap;
        options.inSampleSize = 1;
        options.inTempStorage = tmpStorage;
        this.data.bitmap = null;
    }

    @Override
    public void exec(PtpCamera.IO io) {
        io.handleCommand(this);
        if (responseCode == PtpConstants.Response.DeviceBusy) {
            camera.onDeviceBusy(this, true);
            return;
        }
        if (this.data.bitmap != null && responseCode == PtpConstants.Response.Ok) {
            camera.onLiveViewReceived(data);
        } else {
            camera.onLiveViewReceived(null);
        }
    }

    @Override
    public void encodeCommand(ByteBuffer b) {
        encodeCommand(b, PtpConstants.Operation.EosGetLiveViewPicture, 0x100000);
    }

    @Override
    protected void decodeData(ByteBuffer b, int length) {

        data.hasHistogram = false;
        data.hasAfFrame = false;

        if (length < 1000) {
            if (AppConfig.LOG) {
                Log.w(TAG, String.format("liveview data size too small %d", length));
            }
            return;
        }

        try {

            while (b.hasRemaining()) {
                int subLength = b.getInt();
                int type = b.getInt();

                if (subLength < 8) {
                    throw new RuntimeException("Invalid sub size " + subLength);
                }

                int unknownInt = 0;

                switch (type) {
                case 0x01:
                    data.bitmap = BitmapFactory.decodeByteArray(b.array(), b.position(), subLength - 8, options);
                    b.position(b.position() + subLength - 8);
                    break;
                case 0x04:
                    data.zoomFactor = b.getInt();
                    break;
                case 0x05:
                    // zoomfocusx, zoomfocusy
                    data.zoomRectRight = b.getInt();
                    data.zoomRectBottom = b.getInt();
                    if (AppConfig.LOG) {
                        Log.i(TAG, "header 5 " + data.zoomRectRight + " " + data.zoomRectBottom);
                    }
                    break;
                case 0x06:
                    // imagex, imagey (if zoomed should be non zero)
                    data.zoomRectLeft = b.getInt();
                    data.zoomRectTop = b.getInt();
                    if (AppConfig.LOG) {
                        Log.i(TAG, "header 6 " + data.zoomRectLeft + " " + data.zoomRectTop);
                    }
                    break;
                case 0x07:
                    unknownInt = b.getInt();
                    if (AppConfig.LOG) {
                        Log.i(TAG, "header 7 " + unknownInt + " " + subLength);
                    }
                    break;
                case 0x03:
                    data.hasHistogram = true;
                    b.get(data.histogram.array(), 0, 1024 * 4);
                    break;
                case 0x08: // faces if faces focus
                case 0x0e: // TODO original width, original height
                default:
                    b.position(b.position() + subLength - 8);
                    if (AppConfig.LOG) {
                        Log.i(TAG, "unknown header " + type + " size " + subLength);
                    }
                    break;
                }

                if (length - b.position() < 8) {
                    break;
                }
            }

        } catch (RuntimeException e) {
            Log.e(TAG, "" + e.toString());
            Log.e(TAG, "" + e.getLocalizedMessage());
        }
    }
}
