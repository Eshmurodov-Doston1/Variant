package uz.gxteam.variant.ui.createStatement

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.constraint.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.extensions.getCancelUploadIntent
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import uz.gxteam.variant.BuildConfig
import uz.gxteam.variant.BuildConfig.BASE_URL
import uz.gxteam.variant.ListenerActivity
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.uploadAdapter.UploadAdapter
import uz.gxteam.variant.databinding.DialogCameraBinding
import uz.gxteam.variant.databinding.FragmentGenerateBinding
import uz.gxteam.variant.errors.errorInternet.errorNoClient
import uz.gxteam.variant.errors.errorInternet.messageError
import uz.gxteam.variant.errors.errorInternet.noInternet
import uz.gxteam.variant.errors.uploadError.ErrorUpload
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.models.uploadPhotos.UploadPhotos
import uz.gxteam.variant.resourse.applicationResourse.ApplicationResourse
import uz.gxteam.variant.resourse.uploadPhotos.UploadphotosResourse
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import uz.gxteam.variant.vm.statementVm.StatementVm
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "dataApplication"

/**
 * A simple [Fragment] subclass.
 * Use the [GenerateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class GenerateFragment : Fragment(R.layout.fragment_generate),CoroutineScope {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: DataApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getSerializable(ARG_PARAM3) as DataApplication
        }
    }

    /**
    1. passport old tomoni
    2. propiska
    3. passport bilan selfi
    4. Dagavor
     **/

    private val binding:FragmentGenerateBinding by viewBinding()
    lateinit var photoURI:Uri
    lateinit var imagePath:String
    private val authViewModel:AuthViewModel by viewModels()
    private val stateMentVm:StatementVm by viewModels()
    lateinit var listenerActivity:ListenerActivity
    lateinit var uploadAdapter:UploadAdapter

    private var actualImage: File? = null
    private var compressedImage: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

//            if (param3?.photo_status!! >= 4){
//                uploadBtn.isEnabled = false
//            }
            uploadBtn.setOnClickListener {
                PermissionX.init(activity)
                    .permissions(Manifest.permission.CAMERA)
                    .onExplainRequestReason { scope, deniedList ->
                        scope.showRequestReasonDialog(deniedList, getString(R.string.no_help), "OK", getString(R.string.cancel))
                    }
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted) {
                            var dialog = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                            val create = dialog.create()
                            val dialogCameraBinding = DialogCameraBinding.inflate(LayoutInflater.from(requireContext()), null, false)
                            create.setView(dialogCameraBinding.root)
                            dialogCameraBinding.camera.setOnClickListener {
                                var imageFile = createImageFile()
                                photoURI = FileProvider.getUriForFile(root.context,BuildConfig.APPLICATION_ID,imageFile)

                                getTakeImageContent.launch(photoURI)
                                create.dismiss()
                            }
                            dialogCameraBinding.gallery.setOnClickListener {
                                picImageForNewGallery()
                                create.dismiss()
                            }
                            dialogCameraBinding.close.setOnClickListener {
                                create.dismiss()
                            }
                            create.show()
                        } else {
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
                        }
                    }
            }
            status.text = param3?.status_title
            loadViewUpload()
        }
    }

    private fun loadViewUpload() {
        binding.apply {
            launch {
                stateMentVm.getUploadPhotos(SendToken(param3?.token.toString())).collect{
                    when(it){
                        is UploadphotosResourse.Loading->{
                            spinKit.visibility = View.VISIBLE
                        }
                        is UploadphotosResourse.SuccessUploadPhotos->{
                            spinKit.visibility = View.GONE
                            uploadAdapter = UploadAdapter(object:UploadAdapter.OnIemLongClick{
                                override fun onUploadClick(
                                    uploadPhotos: UploadPhotos,
                                    position: Int
                                ) {

                                }
                            })

                            if (it.uploadPhotos?.isEmpty() == true){
                                animationEmpty.visibility = View.VISIBLE
                            }else{
                                animationEmpty.visibility = View.GONE
                            }
                            uploadAdapter.submitList(it.uploadPhotos)
                            rvImage.adapter = uploadAdapter
                            rvImage.isNestedScrollingEnabled = false
                        }
                        is UploadphotosResourse.ErrorUploadPhotos->{
                            spinKit.visibility = View.GONE
                            if (it.internetConnection==true){
                                if (it.errorCode==401){
                                    var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false)
                                        .build()
                                    var bundle = Bundle()
                                    findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                }else{
                                    errorNoClient(requireContext(),it.errorCode?:0)
                                }
                            }else{
                                noInternet(requireContext())
                            }
                        }
                    }
                }
            }
        }
    }


    //Camera

    private val getTakeImageContent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {

            var openInputStream = activity?.contentResolver?.openInputStream(photoURI)

            var format = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
            var file = File(activity?.filesDir, "$format.jpg")



            var fileoutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileoutputStream)
            openInputStream?.close()
            fileoutputStream.close()



            var filAbsolutePath = file.absolutePath
            imagePath = filAbsolutePath
            uploadImage(imagePath)


        }
    }



    fun uriToFile(context: Context,uri: Uri,fileName:String):File?{
        activity?.contentResolver?.openInputStream(photoURI).let { inputStream ->
            var tempFile = createImageFile()
            val fileOutputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(fileOutputStream)
            inputStream?.close()
            fileOutputStream.close()
            return tempFile
        }
        return null
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$date",".jpg",externalFilesDir).apply { absolutePath }
    }


    fun compressImage(filePath:String,mb:Double=1.0){
        val decodeFile = BitmapFactory.decodeFile(filePath)
        val exifInterface = ExifInterface(filePath)
        var exiOrientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL)
        var exifDegree = exiOrientationDegree(exiOrientation)

        var image = rotateImage(decodeFile,exifDegree.toFloat())

        try {
            var file = File(filePath)
            var length = file.length()
            var fileSizInKB = (length/1024).toString().toDouble()
            var fileSizInMB = (fileSizInKB/1024).toString().toDouble()
            var quality = 100
            if (fileSizInMB>mb){
                quality=((mb/fileSizInMB)*100).toInt()
            }

            val fileOutputStream = FileOutputStream(filePath)
            image.compress(Bitmap.CompressFormat.JPEG,quality,fileOutputStream)
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun rotateImage(decodeFile: Bitmap, toFloat: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(toFloat)

        return Bitmap.createBitmap(decodeFile,0,0,decodeFile.width,decodeFile.height,matrix,true)
    }

    private fun exiOrientationDegree(exifPositon:Int):Int {
       return when(exifPositon){
            ExifInterface.ORIENTATION_ROTATE_90->{
              90
            }
            ExifInterface.ORIENTATION_ROTATE_180->{
                180
            }
            ExifInterface.ORIENTATION_ROTATE_270->{
                270
            }
           else->0
        }
    }

    //Gallery
    private fun picImageForNewGallery() {
        getImageContent.launch("image/*")
    }

    private var getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?:return@registerForActivityResult
       // fragmentAddZnakBinding.image.setImageURI(uri)
        var openInputStream =(activity)?.contentResolver?.openInputStream(uri)
        var filesDir = (activity)?.filesDir
        var format = SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
        var file = File(filesDir,"$format.jpg")
        val fileOutputStream = FileOutputStream(file)
        openInputStream!!.copyTo(fileOutputStream)
        openInputStream.close()
        fileOutputStream.close()
        var filAbsolutePath = file.absolutePath
        imagePath = filAbsolutePath
        uploadImage(imagePath)
//        var fileInputStream = FileInputStream(file)
//        val readBytes = fileInputStream.readBytes()
    }


    fun uploadImage(imagePath:String){
        GlobalScope.launch(Dispatchers.Main) {
            MultipartUploadRequest(context = requireContext(),serverUrl = "${BASE_URL}/api/chat/upload")
                .addHeader("Authorization","${authViewModel.getSharedPreference().tokenType} ${authViewModel.getSharedPreference().accessToken}")
                .addHeader("Accept","application/json")
                .setMethod("POST")
                .setNotificationConfig { context, uploadId ->
                    UploadNotificationAction(
                        icon = android.R.drawable.ic_menu_close_clear_cancel,
                        title = "Cancel",
                        intent = context.getCancelUploadIntent(uploadId))
                    UploadNotificationConfig(
                        notificationChannelId = UploadServiceConfig.defaultNotificationChannel!!,
                        isRingToneEnabled = false,
                        progress = UploadNotificationStatusConfig(
                            title = getString(R.string.please_wait),
                            message = ""
                        ),
                        success = UploadNotificationStatusConfig(
                            title = getString(R.string.photo_send_success),
                            message = ""
                        ),
                        error = UploadNotificationStatusConfig(
                            title = getString(R.string.error_app),
                            message = getString(R.string.no_data)
                        ),
                        cancelled = UploadNotificationStatusConfig(
                            title = "cancelled",
                            message = "some cancelled message"
                        )
                    )
                }
                .addParameter("token", param3?.token.toString()) //Adding text parameter to the request
                .addParameter("type", "${param3?.photo_status?.plus(1)}") //Adding text parameter to the request
                .addFileToUpload(imagePath, "photo") //Adding file
                .subscribe(requireContext(),viewLifecycleOwner, delegate = object:
                    RequestObserverDelegate {
                    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {}
                    @SuppressLint("LongLogTag")
                    override fun onCompletedWhileNotObserving() {
                        listenerActivity.showLoading()
                    }

                    override fun onError(
                        context: Context,
                        uploadInfo: UploadInfo,
                        exception: Throwable
                    ) {
                        when (exception) {
                            is UserCancelledUploadException -> {
                                Toast.makeText(requireContext(), "Xatolik:${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                            is UploadError -> {
                                listenerActivity.hideLoading()
                                val fromJson = Gson().fromJson(exception.serverResponse.bodyString, ErrorUpload::class.java)
                                messageError(fromJson.errors.message,requireContext())
                                //  getapplicationStatu()
                                // errorMain(requireActivity(), exception.serverResponse.bodyString,  exception.serverResponse.code,registerListener)
                            }
                            else -> {}
                        }
                    }




                    override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                        listenerActivity.showLoading()
                    }

                    override fun onSuccess(
                        context: Context,
                        uploadInfo: UploadInfo,
                        serverResponse: ServerResponse
                    ) {
                        if (serverResponse.isSuccessful ){
                            clearMyFiles()
                            getApplicationData()
                            loadViewUpload()
                            listenerActivity.hideLoading()
                        }
                    }

                })
        }
    }

    private fun getApplicationData() {
        launch {
            stateMentVm.getApplication(SendToken(param3?.token.toString())).
                    collect{
                        when(it){
                            is ApplicationResourse.SuccessApplication->{
                                it.application.let {
                                    param3 = DataApplication(param3?.status,param3?.level,it?.client_id?.toLong(), it?.contract_number,it?.photo_status?.toLong(), it?.token.toString(),param3?.status_title, it?.full_name)
                                    Log.e("Update_Param3", param3.toString())
                                   binding.status.text = param3?.status_title
                                }
                            }
                            is ApplicationResourse.ErrorApplication->{
                             if (it.internetConnection==true){
                                 if (it.errorCode==401){
                                     var navOpitions = NavOptions.Builder().setPopUpTo(R.id.authFragment,false)
                                         .build()
                                     var bundle = Bundle()
                                     findNavController().navigate(R.id.authFragment,bundle,navOpitions)
                                 }else{
                                     errorNoClient(requireContext(),it.errorCode?:0)
                                 }
                             }else{
                                 noInternet(requireContext())
                             }
                            }
                        }
                    }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listenerActivity = activity as ListenerActivity
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMyFiles()
    }


    fun clearMyFiles() {
        val files = activity?.filesDir?.listFiles()
        if (files != null) for (file in files) {
            file.delete()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GenerateFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GenerateFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}