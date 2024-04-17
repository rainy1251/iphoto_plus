/**
 * Copyright 2013 Nils Assbeck, Guersel Ayaz and Michael Zoech
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iphoto.plus.ptp.commands;

import com.iphoto.plus.ptp.Camera;
import com.iphoto.plus.ptp.PtpAction;
import com.iphoto.plus.ptp.PtpCamera;
import com.iphoto.plus.ptp.PtpConstants;

import java.util.Arrays;

public class GetStorageInfosAction implements PtpAction {

    private final PtpCamera camera;
    private final Camera.StorageInfoListener listener;

    public GetStorageInfosAction(PtpCamera camera, Camera.StorageInfoListener listener) {
        this.camera = camera;
        this.listener = listener;
    }

    @Override
    public void exec(PtpCamera.IO io) {
//        GetStorageIdsCommand getStorageIds = new GetStorageIdsCommand(camera);
//        io.handleCommand(getStorageIds);
//
//        if (getStorageIds.getResponseCode() != PtpConstants.Response.Ok) {
//            listener.onAllStoragesFound();
//            return;
//        }
//
//        int ids[] = getStorageIds.getStorageIds();
//        for (int i = 0; i < ids.length; ++i) {
//            int storageId = ids[i];
//            GetStorageInfoCommand c = new GetStorageInfoCommand(camera, storageId);
//            io.handleCommand(c);
//
//            if (c.getResponseCode() != PtpConstants.Response.Ok) {
//                listener.onAllStoragesFound();
//                return;
//            }
//
//            StorageInfo info = c.getStorageInfo();
//
//            String label = c.getStorageInfo().volumeLabel.isEmpty() ? c.getStorageInfo().storageDescription : c
//                    .getStorageInfo().volumeLabel;
//            if (label == null || label.isEmpty()) {
//                label = "Storage " + i;
//            }
//            if (info.maxCapacity > 0 && info.freeSpaceInBytes>0) {
//                listener.onStorageFound(storageId, label);
//            }
//        }
        GetStorageIdsCommand getStorageIds = new GetStorageIdsCommand(camera);
        io.handleCommand(getStorageIds);
        if (getStorageIds.getResponseCode() != PtpConstants.Response.Ok) {
            listener.onAllStoragesFound("");
            return;
        }
        int[] ids = getStorageIds.getStorageIds();
        for (int storageId : ids) {
//            GetStorageInfoCommand c = new GetStorageInfoCommand(camera, storageId);
//            io.handleCommand(c);
//            StorageInfo info = c.getStorageInfo();
//            if (info.maxCapacity > 0 && info.freeSpaceInBytes > 0) {
//                listener.onStorageFound(storageId, ids);
//            }
           //  listener.onStorageFound(storageId, ids);

            listener.onStorageFound(storageId, Arrays.toString(ids));
        }
        listener.onAllStoragesFound(Arrays.toString(ids));
    }

    @Override
    public void reset() {
    }
}
