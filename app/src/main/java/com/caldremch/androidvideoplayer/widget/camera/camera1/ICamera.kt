package com.caldremch.androidvideoplayer.widget.camera.camera1

/**
 *
 * @author Caldremch
 *
 * @date 2019-06-11 16:56
 *
 * @email caldremch@163.com
 *
 * @describe
 *
 **/

interface ICamera {
    fun init();
    fun open(cameraId:Int);
    fun takePicture();
    fun swithCamera()
}