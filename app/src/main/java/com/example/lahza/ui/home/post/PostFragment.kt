package com.example.lahza.ui.home.post

import android.app.ActionBar.LayoutParams
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.lahza.R
import com.example.lahza.databinding.FragmentPostBinding
import com.example.lahza.domain.models.post.PostModel
import com.example.lahza.domain.preference.UserPreferenceManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID

class PostFragment : Fragment() {

    lateinit var viewBinding: FragmentPostBinding

    private lateinit var mProfileUri: Uri
    private lateinit var mLicenseUri: Uri

    val storage = Firebase.storage
    val storageRef = storage.reference
    lateinit var dialog: Dialog

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    mProfileUri = fileUri
                    viewBinding.mainImage.setImageURI(fileUri)
                }
                com.github.dhaval2404.imagepicker.ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(
                        requireContext(),
                        com.github.dhaval2404.imagepicker.ImagePicker.getError(data),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Toast.makeText(requireContext(), "Bekor qilindi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data!!
                // Use the uri to load the image
                // Only if you are not using crop feature:

                //////////////
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentPostBinding.inflate(inflater,container,false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sdf = SimpleDateFormat("HH:mm")
        val currentDate = sdf.format(Date())

        setDialog()
        viewBinding.timeShowTextView.text = "${currentDate}, Bugun"

        viewBinding.buttonSave.setOnClickListener {
            val comment = viewBinding.commentEditText.text.toString()
            val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm")
            val currentDate = sdf.format(Date())
            val model = PostModel(
                time =currentDate.toString(),
                comment = comment
            )
            uploadImageToFirebase(mProfileUri,model)
        }

        viewBinding.galleryImage.setOnClickListener {
            ImagePicker.with(requireActivity()).galleryOnly()
                .cropSquare().compress(1024)
                .maxResultSize(1080, 1080) //com.example.taxi.domain.model.history.com.example.taxi.domain.model.history.com.example.taxi.domain.model.history.User can only capture image using Camera
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
        viewBinding.buttonEnterCamera.setOnClickListener {
            com.github.dhaval2404.imagepicker.ImagePicker.with(requireActivity()).cameraOnly()
                .cropSquare().compress(1024)
                .maxResultSize(1080, 1080) //com.example.taxi.domain.model.history.com.example.taxi.domain.model.history.com.example.taxi.domain.model.history.User can only capture image using Camera
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private fun setDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_loading)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setGravity(Gravity.CENTER)
    }

    private fun uploadImageToFirebase(fileUri: Uri, model: PostModel) {
        val fileName = UUID.randomUUID().toString() // Generate a unique file name
        val ref = storageRef.child("images/$fileName")

        dialog.show()
        ref.putFile(fileUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {uri ->
                    val imageUrl = uri.toString()
                    model.urlImage = imageUrl
                    saveImageInfoToDatabase(model)
                }
                // Handle successful upload
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
            }
    }

    private fun saveImageInfoToDatabase(imageModel: PostModel) {
        val userPreferenceManager = UserPreferenceManager(requireContext())
        val databaseRef = userPreferenceManager.getUserKey()
            ?.let { FirebaseDatabase.getInstance().getReference("posts").child(it) }
        val imageId = databaseRef?.push()?.key ?: return
        databaseRef.child(imageId).setValue(imageModel).addOnCompleteListener {
            if (it.isSuccessful){
                dialog.dismiss()
                val navController = findNavController()
                navController.navigateUp()
            }
        }
    }
}