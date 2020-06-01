/*
 * Created by 动脑科技-Tim on 17-8-18 下午9:19
 * Copyright (c) 2017. All rights reserved
 *
 * Last modified 17-8-18 下午9:19
 */

package com.ndk.so.generator;


public class BsPatch {

    public native static int patch(String oldfile, String newFile, String patchFile);

}
