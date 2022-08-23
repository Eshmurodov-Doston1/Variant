package uz.gxteam.variant.ui.createStatement

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
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
import uz.gxteam.variant.R
import uz.gxteam.variant.adapters.category_adapter.CategoryAdapter
import uz.gxteam.variant.adapters.imageViewPagerAdapter.ViewPagerAdapter
import uz.gxteam.variant.adapters.uploadAdapter.UploadAdapter
import uz.gxteam.variant.databinding.CreateDocumentBinding
import uz.gxteam.variant.databinding.DialogCameraBinding
import uz.gxteam.variant.databinding.ImageDialogBinding
import uz.gxteam.variant.errors.errorInternet.messageError
import uz.gxteam.variant.errors.uploadError.ErrorUpload
import uz.gxteam.variant.models.appliction.Application
import uz.gxteam.variant.models.getApplication.reqApplication.SendToken
import uz.gxteam.variant.models.getApplications.DataApplication
import uz.gxteam.variant.models.uploaCategory.UploadCategory
import uz.gxteam.variant.models.uploaCategory.UploadCategoryItem
import uz.gxteam.variant.models.uploadPhotos.UploadPhotoData
import uz.gxteam.variant.models.uploadPhotos.UploadPhotos
import uz.gxteam.variant.ui.baseFragment.BaseFragment
import uz.gxteam.variant.utils.AppConstant.ACCEPT
import uz.gxteam.variant.utils.AppConstant.API_UPLOAD
import uz.gxteam.variant.utils.AppConstant.AUTH_STR
import uz.gxteam.variant.utils.AppConstant.DATAAPPLICATION
import uz.gxteam.variant.utils.AppConstant.DATE_FORMAT
import uz.gxteam.variant.utils.AppConstant.IMAGE_FORMAT
import uz.gxteam.variant.utils.AppConstant.ISUPDATE
import uz.gxteam.variant.utils.AppConstant.PHOTO
import uz.gxteam.variant.utils.AppConstant.POST
import uz.gxteam.variant.utils.AppConstant.TOKEN
import uz.gxteam.variant.utils.AppConstant.TYPE
import uz.gxteam.variant.utils.AppConstant.TYPETOKEN
import uz.gxteam.variant.utils.AppConstant.VALUEUPDATE
import uz.gxteam.variant.utils.fetchResult
import uz.gxteam.variant.utils.parseJsonInClass
import uz.gxteam.variant.vm.authViewModel.AuthViewModel
import uz.gxteam.variant.vm.statementVm.StatementVm
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


@AndroidEntryPoint
class GenerateFragment : BaseFragment(R.layout.create_document) {
    private var param3: DataApplication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param3 = it.getSerializable(DATAAPPLICATION) as DataApplication
        }
    }

    private val binding:CreateDocumentBinding by viewBinding()
    lateinit var photoURI:Uri
    lateinit var imagePath:String
    private var appId = -2
    private val authViewModel:AuthViewModel by viewModels()
    private val statementVm:StatementVm by viewModels()
    private lateinit var categoryAdapter:CategoryAdapter
    lateinit var uploadAdapter:UploadAdapter
    lateinit var viewPagerAdapter:ViewPagerAdapter
    var uploadPhotosApp:UploadPhotos?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
           dataCategory()

            swipeRefresh.setOnRefreshListener {
                loadViewUpload()
            }

            loadViewUpload()
        }
    }



    fun dataCategory(){
        binding.apply {
            statementVm.getUploadCategory((param3?.client_id?:0L).toInt())
            launch {
                statementVm.getUploadCategory.fetchResult(compositionRoot.uiControllerApp,{ result->
                    categoryAdapter = CategoryAdapter((result?: emptyList()) as ArrayList<UploadCategoryItem>){ item, position ->
                        if(item.is_uploaded==1){
                            compositionRoot.uiControllerApp.error(1001,item.title){ isClick -> }
                        }else{
                            appId = item.id
                            permissionAndUploadFile()
                        }
                    }
                    rvData.adapter = categoryAdapter
                },{isClick ->  })
            }

        }
    }

    fun permissionAndUploadFile(){
        binding.apply {
            PermissionX.init(activity)
                .permissions(Manifest.permission.CAMERA)
                .onExplainRequestReason { scope, deniedList ->
                    scope.showRequestReasonDialog(deniedList, getString(R.string.no_help), "OK", getString(R.string.cancel))
                }.request { allGranted, grantedList, deniedList ->
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
    }

    private fun loadViewUpload() {
        binding.apply {
            statementVm.getUploadPhotos(SendToken(param3?.token.toString()))
            launch {
                statementVm.getUploadPhotos.fetchResult(compositionRoot.uiControllerApp,{ result->
                    uploadAdapter(result?: emptyList())
                    swipeRefresh.isRefreshing = false
                    if (result?.isEmpty() == true){
                        animationEmpty.visibility = View.VISIBLE
                    }else{
                        animationEmpty.visibility = View.GONE
                    }
                    uploadAdapter.submitList(result)
                    rvFile.adapter = uploadAdapter
                    rvFile.isNestedScrollingEnabled = false
                    listenerActivity.uploadLoadingHide()
                },{isClick -> })

            }
        }
    }


    fun uploadAdapter(result:List<UploadPhotos>){
        binding.apply {
            uploadAdapter = UploadAdapter(object:UploadAdapter.OnIemLongClick{
                override fun onUploadClick(
                    uploadPhotos: UploadPhotos,
                    position: Int
                ) {
                    var alertDialog = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                    val create = alertDialog.create()
                    var imageDialogBinding = ImageDialogBinding.inflate(LayoutInflater.from(requireContext()),null,false)
                    create.setView(imageDialogBinding.root)
                    viewPagerAdapter = ViewPagerAdapter(object:ViewPagerAdapter.OnItemClickListener{
                        override fun onItemClickUpdate(
                            uploadPhotos: UploadPhotos,
                            position: Int
                        ) {
                            PermissionX.init(activity)
                                .permissions(Manifest.permission.CAMERA)
                                .onExplainRequestReason { scope, deniedList ->
                                    scope.showRequestReasonDialog(deniedList, getString(R.string.no_help), "OK", getString(R.string.cancel))
                                }.request { allGranted, grantedList, deniedList ->
                                    var dialogApp = AlertDialog.Builder(requireContext(),R.style.BottomSheetDialogThem)
                                    val create1 = dialogApp.create()
                                    var cameraDialogBinding = DialogCameraBinding.inflate(LayoutInflater.from(requireContext()),null,false)
                                    create1.setView(cameraDialogBinding.root)
                                    uploadPhotosApp = uploadPhotos
                                    cameraDialogBinding.camera.setOnClickListener {
                                        var imageFile = createImageFile()
                                        photoURI = FileProvider.getUriForFile(requireContext(),BuildConfig.APPLICATION_ID,imageFile)
                                        getTakeImageContentUpdate.launch(photoURI)
                                        create1.dismiss()
                                        create.dismiss()
                                    }
                                    cameraDialogBinding.gallery.setOnClickListener {
                                        picImageForNewGalleryUpdate()
                                        create1.dismiss()
                                        create.dismiss()
                                    }
                                    cameraDialogBinding.close.setOnClickListener {
                                        create1.dismiss()
                                    }
                                    create1.show()
                                }
                        }
                    }, result)

                    imageDialogBinding.viewPager2.setPageTransformer { page, position ->
                        if (position < -1){
                            page.alpha = 0F

                        }
                        else if (position <= 0) {
                            page.alpha = 1F
                            page.pivotX = page.width.toFloat()
                            page.rotationY = -90 * abs(position)

                        }
                        else if (position <= 1){
                            page.alpha = 1F
                            page.pivotX = 0F
                            page.rotationY = 90 * Math.abs(position)

                        }
                        else {
                            page.alpha = 0F

                        }
                    }

                    imageDialogBinding.viewPager2.adapter = viewPagerAdapter
                    imageDialogBinding.viewPager2.setCurrentItem(position,false)
                    create.show()
                }
            })
        }
    }


    //Camera Upload
    private val getTakeImageContent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            var openInputStream = activity?.contentResolver?.openInputStream(photoURI)

            var format = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).format(Date())
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


    private val getTakeImageContentUpdate = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            var openInputStream = activity?.contentResolver?.openInputStream(photoURI)

            var format = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).format(Date())
            var file = File(activity?.filesDir, "$format$IMAGE_FORMAT")

            var fileoutputStream = FileOutputStream(file)
            openInputStream?.copyTo(fileoutputStream)
            openInputStream?.close()
            fileoutputStream.close()
            var filAbsolutePath = file.absolutePath
            imagePath = filAbsolutePath
            updateImage(imagePath)
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val date = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_$date",IMAGE_FORMAT,externalFilesDir).apply { absolutePath }
    }

    //Gallery
    private fun picImageForNewGallery() {
        getImageContent.launch("image/*")
    }

    private var getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?:return@registerForActivityResult
        var openInputStream =(activity)?.contentResolver?.openInputStream(uri)
        var filesDir = (activity)?.filesDir
        var format = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).format(Date())
        var file = File(filesDir,"$format$IMAGE_FORMAT")
        val fileOutputStream = FileOutputStream(file)
        openInputStream!!.copyTo(fileOutputStream)
        openInputStream.close()
        fileOutputStream.close()
        var filAbsolutePath = file.absolutePath
        imagePath = filAbsolutePath
        uploadImage(imagePath)
    }

    //Gallery Update
    private fun picImageForNewGalleryUpdate() {
        getImageContentUpdate.launch("image/*")
    }

    private var getImageContentUpdate = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?:return@registerForActivityResult
        var openInputStream =(activity)?.contentResolver?.openInputStream(uri)
        var filesDir = (activity)?.filesDir
        var format = SimpleDateFormat(DATE_FORMAT,Locale.getDefault()).format(Date())
        var file = File(filesDir,"$format$IMAGE_FORMAT")
        val fileOutputStream = FileOutputStream(file)
        openInputStream!!.copyTo(fileOutputStream)
        openInputStream.close()
        fileOutputStream.close()
        var filAbsolutePath = file.absolutePath
        imagePath = filAbsolutePath
        updateImage(imagePath)
    }


    fun uploadImage(imagePath:String){
        launch(Dispatchers.Main) {
            MultipartUploadRequest(context = requireContext(),serverUrl = "${BASE_URL}${API_UPLOAD}")
                .addHeader(AUTH_STR,"${authViewModel.getSharedPreference().tokenType} ${authViewModel.getSharedPreference().accessToken}")
                .addHeader(ACCEPT,TYPETOKEN)
                .setMethod(POST)
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
                .addParameter(TOKEN, param3?.token.toString()) //Adding text parameter to the request
                .addParameter(TYPE, appId.toString()) //Adding text parameter to the request
                .addFileToUpload(imagePath, PHOTO) //Adding file
                .subscribe(requireContext(),viewLifecycleOwner, delegate = object:
                    RequestObserverDelegate {
                    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {

                    }
                    @SuppressLint("LongLogTag")
                    override fun onCompletedWhileNotObserving() {
                        listenerActivity.uploadLoadingShow()
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
                                listenerActivity.uploadLoadingHide()
                                val fromJson = Gson().fromJson(exception.serverResponse.bodyString, ErrorUpload::class.java)
                                messageError(fromJson.errors.message,requireContext())
                            }
                            else -> {}
                        }
                    }




                    override fun onProgress(context: Context, uploadInfo: UploadInfo) {

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
                        }
                    }

                })

        }
    }

    private fun getApplicationData() {
        statementVm.getApplication(SendToken(param3?.token.toString()))
        launch {
           statementVm.getApplication.fetchResult(compositionRoot.uiControllerApp,{ result->
               result.let {
                   param3 = DataApplication(param3?.status,param3?.level,it?.client_id?.toLong(), it?.contract_number,it?.photo_status?.toLong(), it?.token.toString(),param3?.status_title, it?.full_name)
               }
           },{isClick ->  })
        }
    }






    fun updateImage(imagePath:String){
        launch(Dispatchers.Main) {
            MultipartUploadRequest(context = requireContext(),serverUrl = "${BASE_URL}${API_UPLOAD}")
                .addHeader(AUTH_STR,"${authViewModel.getSharedPreference().tokenType} ${authViewModel.getSharedPreference().accessToken}")
                .addHeader(ACCEPT,TYPETOKEN)
                .setMethod(POST)
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
                .addParameter(TOKEN, param3?.token.toString()) //Adding text parameter to the request
                .addParameter(TYPE, "${uploadPhotosApp?.type}") //Adding text parameter to the request
                .addFileToUpload(imagePath, PHOTO) //Adding file
                .addParameter(ISUPDATE,VALUEUPDATE)
                .subscribe(requireContext(),viewLifecycleOwner, delegate = object:
                    RequestObserverDelegate {
                    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {}
                    @SuppressLint("LongLogTag")
                    override fun onCompletedWhileNotObserving() {
                        listenerActivity.uploadLoadingShow()
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
                                listenerActivity.uploadLoadingHide()
                                val fromJson = Gson().fromJson(exception.serverResponse.bodyString, ErrorUpload::class.java)
                                messageError(fromJson.errors.message,requireContext())

                            }
                            else -> {}
                        }
                    }




                    override fun onProgress(context: Context, uploadInfo: UploadInfo) {}

                    override fun onSuccess(
                        context: Context,
                        uploadInfo: UploadInfo,
                        serverResponse: ServerResponse
                    ) {
                        if (serverResponse.isSuccessful ){
                            clearMyFiles()
                            getApplicationData()
                            loadViewUpload()
                        }
                    }

                })
        }
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
}