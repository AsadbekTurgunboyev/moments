package com.example.lahza.ui.login

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.lahza.R
import com.example.lahza.databinding.FragmentUserNameBinding
import com.example.lahza.domain.models.LoginModel
import com.example.lahza.domain.preference.UserPreferenceManager
import com.example.lahza.ui.home.HomeActivity
import com.example.lahza.utils.CreateDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserNameFragment : Fragment(), TextWatcher {

    lateinit var viewBinding: FragmentUserNameBinding
    lateinit var databaseReference: DatabaseReference
    lateinit var model: LoginModel
    lateinit var preferenceManager: UserPreferenceManager
    lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewBinding = FragmentUserNameBinding.inflate(inflater, container, false)
        return viewBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString("name")
        val phone = arguments?.getString("phone")
        val birthday = arguments?.getString("birthday")
        dialog = CreateDialog.showDialog(requireContext())
        preferenceManager = UserPreferenceManager(requireContext())
        model = LoginModel(
            name = name, phone = phone, birthday = birthday
        )
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        viewBinding.userNameEditText.addTextChangedListener(this@UserNameFragment)

        viewBinding.buttonConfirm.setOnClickListener {
            viewBinding.userNameTextInput.isErrorEnabled = false
            viewBinding.userNameEditText.addTextChangedListener(null)

            dialog.show()
            val key = databaseReference.push().key
            model.key = key
            key?.let { it1 ->
                databaseReference.child(it1).setValue(model).addOnCompleteListener {
                    if (it.isSuccessful) {
                        dialog.dismiss()
                        preferenceManager.saveUserData(model)
                        val intent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(intent)
                    }
                }.addOnFailureListener {
                    dialog.dismiss()
                    Log.d("tekshirish", "onViewCreated: ${it.message}")
                }
            }
        }
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val model = ds.getValue(LoginModel::class.java)
                    if (model?.username == s.toString()) {
                        viewBinding.userNameTextInput.error = "Bunday foydalanuvchi mavjud"
                        viewBinding.buttonConfirm.isEnabled = false
                        return
                    } else {
                        viewBinding.userNameTextInput.isErrorEnabled = false
                        viewBinding.buttonConfirm.isEnabled = true

                        viewBinding.userNameTextInput.helperText = "Bunday nom bo'sh"
                        viewBinding.userNameTextInput.setHelperTextColor(
                            ColorStateList.valueOf(
                                ContextCompat.getColor(requireContext(), R.color.green)
                            )
                        )

                    }
                }
                model.username = s.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.userNameEditText.addTextChangedListener(null)

    }


}