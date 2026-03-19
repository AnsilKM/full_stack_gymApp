package com.gym.gymapp.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.*
import platform.Foundation.*
import platform.PhotosUI.*
import platform.AVFoundation.*
import platform.Photos.*
import kotlinx.cinterop.*

@Composable
actual fun rememberImagePicker(onImagePicked: (ByteArray?) -> Unit): () -> Unit {
    val delegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                picker.dismissViewControllerAnimated(true, null)
                val itemProvider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
                if (itemProvider != null && itemProvider.canLoadObjectOfClass(UIImage.`class`()!!)) {
                    itemProvider.loadObjectOfClass(UIImage.`class`()!!) { image, error ->
                        if (image is UIImage) {
                            val data = UIImageJPEGRepresentation(image, 0.8)
                            onImagePicked(data?.let { nsData ->
                                ByteArray(nsData.length.toInt()).apply {
                                    usePinned { pinned ->
                                        memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                                    }
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    return {
        val configuration = PHPickerConfiguration()
        configuration.filter = PHPickerFilter.imagesFilter()
        configuration.selectionLimit = 1
        val picker = PHPickerViewController(configuration)
        picker.delegate = delegate
        UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, true, null)
    }
}

@Composable
actual fun rememberCameraLauncher(onImagePicked: (ByteArray?) -> Unit): () -> Unit {
    val delegate = remember {
        object : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
            override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<*, *>) {
                picker.dismissViewControllerAnimated(true, null)
                val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
                if (image != null) {
                    val data = UIImageJPEGRepresentation(image, 0.8)
                    onImagePicked(data?.let { nsData ->
                        ByteArray(nsData.length.toInt()).apply {
                            usePinned { pinned ->
                                memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                            }
                        }
                    })
                }
            }
            override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
                picker.dismissViewControllerAnimated(true, null)
            }
        }
    }

    return {
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            val picker = UIImagePickerController()
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(picker, true, null)
        }
    }
}

@Composable
actual fun rememberPermissionHandler(
    permission: PermissionType,
    onResult: (Boolean) -> Unit
): () -> Unit {
    return {
        when (permission) {
            PermissionType.CAMERA -> {
                val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
                when (status) {
                    AVAuthorizationStatusAuthorized -> onResult(true)
                    AVAuthorizationStatusNotDetermined -> {
                        AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                            onResult(granted)
                        }
                    }
                    else -> onResult(false)
                }
            }
            PermissionType.GALLERY -> {
                val status = PHPhotoLibrary.authorizationStatus()
                when (status) {
                    PHAuthorizationStatusAuthorized -> onResult(true)
                    PHAuthorizationStatusNotDetermined -> {
                        PHPhotoLibrary.requestAuthorization { newStatus ->
                            onResult(newStatus == PHAuthorizationStatusAuthorized)
                        }
                    }
                    else -> onResult(false)
                }
            }
        }
    }
}
